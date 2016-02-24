package unalcol.agents.NetworkSim.environment;

import java.util.Hashtable;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;
import unalcol.agents.simulate.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.collections15.Transformer;
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
    public ArrayList<GraphElements.MyVertex> visitedNodes = new ArrayList();
    public ArrayList<GraphElements.MyVertex> locationAgents = null;
    HashMap<Integer, ConcurrentLinkedQueue> mbuffer;
    
    
    
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
        }
        
        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(a.getId());

        //inbox: id | infi 
        if (inbox != null) {
            System.out.println("my "+ a.getData().size());
            ArrayList senderInf = (ArrayList) ObjectSerializer.deserialize(inbox[1]);
            System.out.println("received" + senderInf.size());
            // Join ArrayLists
            a.getData().removeAll(senderInf);
            a.getData().addAll(senderInf);
            System.out.println("joined" + a.getData().size());
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
                    if(a.getData().size() == getTopology().getVertexCount()){
                        System.out.println("complete" + a.getRound());
                        a.die();
                    }
                    break;
                default:
                    msg = "[Unknown action " + act
                            + ". Action not executed]";
                    System.out.println(msg);
                    break;
            }
        }
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
        
        //Load data in Agent
        //clone ArrayList
        ArrayList<Object> copy = new ArrayList<>(anAgent.getLocation().getData());
        //System.out.println("copy" + copy);
        Iterator<Object> it = copy.iterator();
        while(it.hasNext()){
            Object x = it.next();
            if(x == null){
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
    public ArrayList<GraphElements.MyVertex> getVisitedNodes() {
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
        for(int i=0; i < getLocationAgents().size(); i++){
            if(i != x.getId() && x.getLocation().equals(getLocationAgents().get(i))){
                n.add(i);
            }
        }
        return n;
    }

}
