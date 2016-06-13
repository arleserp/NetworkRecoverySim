package unalcol.agents.NetworkSim.environment;

import java.util.Hashtable;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;
import unalcol.agents.simulate.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;

public class NetworkEnvironment extends Environment {

    public static String msg = null;
    public int[][] structure = null;
    public SimpleLanguage language = null;
    Date date;
    public Graph<GraphElements.MyVertex, String> topology;
    GraphElements.MyVertex currentNode = null;
    String currentEdge = null;
    String lastactionlog;
    public List<GraphElements.MyVertex> visitedNodes = Collections.synchronizedList(new ArrayList());
    ;
    public ArrayList<GraphElements.MyVertex> locationAgents = null;
    HashMap<Integer, ConcurrentLinkedQueue> mbuffer;
    private int roundComplete = -1;
    private int idBest = -1;
    private boolean finished = false;
    private int age;
    public static int agentsDie = 0;

    /**
     * @return the idBest
     */
    public int getIdBest() {
        return idBest;
    }

    /**
     * @param aIdBest the idBest to set
     */
    public void setIdBest(int aIdBest) {
        idBest = aIdBest;
    }

    public int getRowsNumber() {
        return structure.length;
    }

    public int getColumnsNumber() {
        return structure[0].length;
    }

    public boolean act(Agent agent, Action action) {
        boolean flag = (action != null);
        MobileAgent a = (MobileAgent) agent;
        ActionParameters ac = (ActionParameters) action;
        currentNode = a.getLocation();
        visitedNodes.add(currentNode);

        getLocationAgents().set(a.getId(), a.getLocation());

        //detect other agents in network
        ArrayList<Integer> agentNeighbors = getAgentNeighbors(a);
        //System.out.println(a.getId() + "agentNeigbors" + agentNeighbors);

        //serialize messages 
        String[] message = new String[2]; //msg: [from|msg]
        message[0] = String.valueOf(a.getId());
        message[1] = ObjectSerializer.serialize(a.getData());

        //for each neighbor send a message
        for (Integer idAgent : agentNeighbors) {
            NetworkMessageBuffer.getInstance().putMessage(idAgent, message);
            a.incMsgSend();
        }

        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(a.getId());

        //inbox: id | infi 
        if (inbox != null) {
            a.incMsgRecv();
            //System.out.println("my " + a.getData().size());
            ArrayList senderInf = (ArrayList) ObjectSerializer.deserialize(inbox[1]);
            //System.out.println("received" + senderInf.size());
            // Join ArrayLists
            a.getData().removeAll(senderInf);
            a.getData().addAll(senderInf);
            //System.out.println("joined" + a.getData().size());
        }

        if (flag) {
            //Agents can be put to Sleep for some ms
            //sleep is good is graph interface is on
            agent.sleep(3);
            String act = action.getCode();
            String msg = null;

            /**
             * 0- "move"
             */
            /* @TODO: Detect Stop Conditions for the algorithm */
            switch (language.getActionIndex(act)) {
                case 0: // move
                    GraphElements.MyVertex v = (GraphElements.MyVertex) ac.getAttribute("location");
                    a.setLocation(v);
                    a.setRound(a.getRound() + 1);
                    boolean complete = false;
                    if (a.getData().size() == topology.getVertexCount()) {
                        complete = true;
                    }

                    if (getRoundComplete() == -1 && complete) {
                        //System.out.println("complete! round" + a.getRound());
                        setRoundComplete(a.getRound());
                        setIdBest(a.getId());
                        updateWorldAge();
                    }
                    break;
                case 1: //die
                    a.die();
                    a.setLocation(null);
                    getLocationAgents().set(a.getId(), null);
                    increaseAgentsDie();
                    setChanged();
                    notifyObservers();
                    return false;
                default:
                    msg = "[Unknown action " + act
                            + ". Action not executed]";
                    System.out.println(msg);
                    break;
            }
        }
        updateWorldAge();
        setChanged();
        notifyObservers();
        return flag;
    }

    @Override
    public Percept sense(Agent agent) {
        MobileAgent anAgent = (MobileAgent) agent;
        Percept p = new Percept();
        //System.out.println("sense - topology " + topology);
        //Load neighbors 
        p.setAttribute("neighbors", getTopology().getNeighbors(anAgent.getLocation()));
        //System.out.println("agent" + anAgent.getId() + "- neighbor: " +  getTopology().getNeighbors(anAgent.getLocation()));
        //Load data in Agent
        //clone ArrayList
        ArrayList<Object> copy = new ArrayList<>(anAgent.getLocation().getData());
        //System.out.println("copy" + copy);
        Iterator<Object> it = copy.iterator();
        while (it.hasNext()) {
            Object x = it.next();
            if (x == null) {
                System.out.println("error 2!");
            }
            if (!anAgent.getData().contains(x)) {
                anAgent.getData().add(x);
            }
        }

        //System.out.println("agent info size:" + anAgent.getData().size());
        return p;
    }

    public NetworkEnvironment(Vector<Agent> _agents, SimpleLanguage _language, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents);
        this.mbuffer = new HashMap<>();
        int n = _agents.size();
        locationAgents = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            MobileAgent ag = (MobileAgent) _agents.get(i);
            locationAgents.add(new GraphElements.MyVertex("null"));
            //System.out.println("creating buffer id" + ag.getAttribute("ID"));
            mbuffer.put(i, new ConcurrentLinkedQueue());
        }
        language = _language;
        date = new Date();
        topology = gr;
    }

    public Vector<Action> actions() {
        Vector<Action> acts = new Vector<Action>();
        int n = language.getActionsNumber();
        for (int i = 0; i < n; i++) {
            acts.add(new Action(language.getAction(i)));
        }
        return acts;
    }

    /* @param agentsDie set the number of agents with failures
     */
    public void setAgentsDie(int agentsDie) {
        NetworkEnvironment.agentsDie = agentsDie;
    }

    /**
     * increases the number of agents with failures
     */
    public void increaseAgentsDie() {
        synchronized (NetworkEnvironment.class) {
            NetworkEnvironment.agentsDie++;
        }
    }

    /**
     * increases the number of agents with failures
     *
     * @return number of agents with failures
     */
    public int getAgentsDie() {
        synchronized (NetworkEnvironment.class) {
            return agentsDie;
        }
    }

    @Override
    public void init(Agent agent) {
        MobileAgent sim_agent = (MobileAgent) agent;
        //@TODO: Any special initialization processs of the environment
    }

    public String getLog() {
        return lastactionlog;
    }

    public void updateLog(String event, String log) {
        Date datenow = new Date();
        long diff = datenow.getTime() - date.getTime();
        //long diffSeconds = diff / 1000 % 60;
        lastactionlog = event + (String.valueOf(diff / 1000)) + " " + log;
        setChanged();
        notifyObservers();
    }

    private void returnOutput(String pid, Hashtable out) {
        controlBoard.getInstance().addOutput(pid, out);
    }

    /**
     * @return the topology
     */
    public Graph<GraphElements.MyVertex, String> getTopology() {
        return topology;
    }

    /**
     * @param topology the topology to set
     */
    public void setTopology(Graph<GraphElements.MyVertex, String> topology) {
        this.topology = topology;
    }

    /**
     * @return the visitedNodes
     */
    public List<GraphElements.MyVertex> getVisitedNodes() {
        return visitedNodes;
    }

    /**
     * @param visitedNodes the visitedNodes to set
     */
    public void setVisitedNodes(ArrayList<GraphElements.MyVertex> visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    public void not() {
        setChanged();
        notifyObservers();
    }

    /**
     * @return the locationAgents
     */
    public ArrayList<GraphElements.MyVertex> getLocationAgents() {
        return locationAgents;
    }

    /**
     * @param locationAgents the locationAgents to set
     */
    public void setLocationAgents(ArrayList<GraphElements.MyVertex> locationAgents) {
        this.locationAgents = locationAgents;
    }

    public ArrayList<Integer> getAgentNeighbors(MobileAgent x) {
        ArrayList n = new ArrayList();
        for (int i = 0; i < getLocationAgents().size(); i++) {
            if (i != x.getId() && x.getLocation().equals(getLocationAgents().get(i))) {
                n.add(i);
            }
        }
        return n;
    }

    /**
     * @return the roundComplete
     */
    public int getRoundComplete() {
        return roundComplete;
    }

    /**
     * @param roundComplete the roundComplete to set
     */
    public void setRoundComplete(int roundComplete) {
        this.roundComplete = roundComplete;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Function used to calculate the intersection of all the information that
     * agent have collected in a determined time.
     * @return 
     */
    /* public void calculateGlobalInfo() {
        if (!isCalculating) {
            isCalculating = true;
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    states[i][j].globalInfo = false;
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < this.getAgents().size(); k++) {
                        MobileAgent t = (MobileAgent)this.getAgent(k);
                        if (t.status != Action.DIE) {
                            String loc = i + "-" + j;
                            if (((Hashtable) t.getAttribute("inf_i")).containsKey(loc)) {
                                states[i][j].globalInfo = true;
                                break;
                            }
                        }
                    }
                }
            }
            isCalculating = false;
        } else {
            System.out.println("entra!");
        }
    }
    
     */
    public Double getAmountGlobalInfo() {
        Double amountGlobalInfo = 0.0;
        for (GraphElements.MyVertex v : topology.getVertices()) {
            ArrayList<Object> vertex_info = new ArrayList<>(v.getData());
            //System.out.println("copy" + copy);
            Iterator<Object> it = vertex_info.iterator();
            while (it.hasNext()) {
                Object x = it.next();
                if (x == null) {
                    System.out.println("error 2!");
                }
                for (Agent m : this.getAgents()) {
                    MobileAgent n = (MobileAgent) m;
                    if (n.status != Action.DIE) {
                        if (n.getData().contains(x)) {
                            amountGlobalInfo++;
                            break;
                        }
                    }
                }
            }

        }
        return amountGlobalInfo/topology.getVertexCount();
    }

    public void updateWorldAge() {
        int average = 0;
        int agentslive = 0;
        for (int k = 0; k < this.getAgents().size(); k++) {
            if ((this.getAgent(k)).status != Action.DIE) {
                average += ((MobileAgent) this.getAgent(k)).getRound();
                agentslive++;
            }
        }
        if (agentslive != 0) {
            average /= agentslive;
            //System.out.println("age:" + average);
            this.setAge(average);
        }
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

}
