package unalcol.agents.NetworkSim.environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphCreator;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;
import unalcol.agents.NetworkSim.MotionProgramSimpleFactory;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.SimulationParameters;
import static unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication.setTotalAgents;
import unalcol.agents.NetworkSim.programs.NodeFailingProgram;
import unalcol.agents.NetworkSim.util.StringSerializer;

public class NetworkEnvironmentPheromoneReplicationNodeFailing extends NetworkEnvironmentReplication {

    private static int falsePossitives = 0;
    private static SimpleLanguage nodeLanguage;
    private static int agentMovements = 0;
    private static int ACKAmount = 0;
    private static List<Node> nodes;
    private static List<MobileAgent> magents;

    public static List<Node> getNodes() {
        return nodes;
    }

    public static synchronized void incrementFalsePossitives() {
        falsePossitives++;
    }

    /**
     * @return the falsePossitives
     */
    public static int getFalsePossitives() {
        return falsePossitives;
    }

    public static synchronized void incrementAgentMovements() {
        agentMovements++;
    }

    /**
     * @return the falsePossitives
     */
    public static int getAgentMovements() {
        return agentMovements;
    }

    public static synchronized void incrementACKAmount() {
        ACKAmount++;
    }

    /**
     * @return the falsePossitives
     */
    public static int getACKAmount() {
        return ACKAmount;
    }

    public NetworkEnvironmentPheromoneReplicationNodeFailing(Vector<Agent> _agents, SimpleLanguage _language, SimpleLanguage _nlanguage, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents, _language, gr);
        nodeLanguage = _nlanguage;
        nodes = new ArrayList<>();
        magents = new ArrayList<>();
    }

    GraphElements.MyVertex findVertex(String nodename) {
        GraphElements.MyVertex v = null;
        for (GraphElements.MyVertex vv : topology.getVertices()) {
            if (vv.getName().equals(nodename)) {
                return vv;
            }
        }
        return v;
    }

    private void connect(GraphElements.MyVertex vertex, String nodetoConnect) {
        GraphElements.MyVertex nodeTo = findVertex(nodetoConnect);
        topology.addEdge("e" + vertex.getName() + nodeTo.getName(), vertex, nodeTo);
    }

    public void setNodes(List<Node> nds) {
        nodes = nds;
    }

    private void createNewAgents(Integer number, Node n) {
        System.out.println("create new agents");
        for (int i = 0; i < number; i++) {
            AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
            int newAgentID = agents.size();
            MobileAgent a = new MobileAgent(program, newAgentID);

            //System.out.println("creating agent id" + newAgentID);
            NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

            //getLocationAgents().add(new GraphElements.MyVertex("null"));
            a.setId(newAgentID);
            a.setData(new ArrayList(n.getVertex().getData()));
            a.setRound(super.getAge());
            this.agents.add(a);

            a.live();
            Thread t = new Thread(a);
            a.setThread(t);
            a.setLocation(n.getVertex());
            a.setPrevLocation(n.getVertex());
            a.setArchitecture(this);
            setTotalAgents(getTotalAgents() + 1);
            t.start();
        }
    }

    private int killAgentsInlocation(String name) {
        int killedAgents = 0;
        ArrayList<Agent> agentsCopy = new ArrayList(getAgents());
        for (Agent a : agentsCopy) {
            if (a instanceof MobileAgent) {
                MobileAgent ma = (MobileAgent) a;
                if (ma.getLocation() != null && ma.getLocation().getName().equals(name)) {
                    increaseAgentsDie();
                    getLocationAgents().put(ma, null);
                    ma.setLocation(null);
                    killedAgents++;
                    ma.die();
                }
            }
        }
        return killedAgents;
    }

    void createNewNode(Node n, String d) {
        if (findVertex(d) != null) { //can ping node and discover
            System.out.println("already created." + d);
        } else {

            GraphCreator.VertexFactory v = new GraphCreator.VertexFactory();
            //GraphCreator.EdgeFactory e = new GraphCreator.EdgeFactory();
            System.out.println(n.getVertex().getName() + " is creating " + d + ":");
            GraphCreator g = new GraphCreator();
            GraphElements.MyVertex vv = v.create();
            vv.setName(d);
            topology.addVertex(vv);
            topology.addEdge("e" + vv.getName() + n.getVertex().getName(), vv, n.getVertex());
            NodeFailingProgram np = new NodeFailingProgram(SimulationParameters.npf);
            NetworkNodeMessageBuffer.getInstance().createBuffer(d);

            Node nod;
            nod = new Node(np, vv);
            nod.setVertex(vv);
            nod.setArchitecture(this);
            this.agents.add(nod);
            nod.live();
            Thread t = new Thread(nod);
            nod.setThread(t);

            if (n.getRespAgentsBkp().containsKey(d)) {
                int i = n.getRespAgentsBkp().get(d);
                System.out.println("creating " + i + " agents");
                createNewAgents(i, nod);
            }
            createNewAgents(3, nod); //we cannot create agents yet! maybe after network is connected.
            t.start();
            nod.setNetworkdata(new HashMap(n.getNetworkdata()));
            nodes.add(nod);
            setChanged();
            notifyObservers();
        }
    }

    public class CustomComparator implements Comparator<GraphElements.MyVertex> {

        @Override
        public int compare(GraphElements.MyVertex f1, GraphElements.MyVertex f2) {
            int v1 = Integer.valueOf(f1.getName().substring(1));
            int v2 = Integer.valueOf(f2.getName().substring(1));
            //System.out.println("v1" + v1 + ", v2" + v2);
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    List<String> getTopologyNames(GraphElements.MyVertex node) {
        List<String> names = new ArrayList();

        if (topology.containsVertex(node)) {
            for (GraphElements.MyVertex v : topology.getNeighbors(node)) {
                names.add(v.getName());
            }
        }
        return names;
    }

    Node getNode(String name) {
        for (Node n : nodes) {
            if (n.status != Action.DIE && n.getVertex().getName().equals(name)) {
                // System.out.println("found d");
                return n;
            }
        }
        //System.out.println("retun nul");
        return null;
    }

    public class CustomComparatorNode implements Comparator<String> {

        @Override
        public int compare(String f1, String f2) {
            int v1 = Integer.valueOf(f1.substring(1));
            int v2 = Integer.valueOf(f2.substring(1));
//            System.out.println("v1" + v1 + ", v2" + v2);
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public String getMinimumId(List<String> neigdiff) {
        List<String> nodesAlive = new ArrayList();
        for(String s: neigdiff){
            if(getNode(s) != null){
                nodesAlive.add(s);
            }
        }
        System.out.println("nodes alive size " + nodesAlive.size());
        String min = Collections.min(neigdiff, new CustomComparatorNode());
        System.out.println(" min " + min);
        return min;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        agent.sleep(30);
        if (agent instanceof MobileAgent) {
            boolean flag = (action != null);
            MobileAgent a = (MobileAgent) agent;

            //System.out.println("thread:" + a.getId());
            if (a.status == Action.DIE || a.getLocation() == null) {
                return false;
            }
            String act = action.getCode();

            //New: informfailure
            if (language.getActionIndex(act) == 2) {
                //System.out.println("Agent " + a.getId() + " detect that something happened, sending noneighdetected to: " + a.getLocation().getName());
                //Sh Send neighbour data to node ex in HashMap format: {p51={p21, p22, p23}, p22={p1, p2}}
                String[] msgnet = new String[3];
                msgnet[0] = "noneighdetected";
                msgnet[1] = StringSerializer.serialize(a.getLocalNetwork());
                msgnet[2] = StringSerializer.serialize(a.getRespAgentsBkp());
                //System.out.println("hey");
                if (a.getLocation() != null) {
                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnet);
                }
                currentNode = a.getLocation();
            } else {
                currentNode = a.getLocation();

                //Sh Add neighbor data to agent as an array p51={p21, p22, p23}|p22={p1, p2}
                HashMap<String, Object> nodeNet = new HashMap<>();
                nodeNet.put(a.getLocation().getName(), getTopologyNames(a.getLocation()));

                //Add resposible agents v
                Node c = getNode(a.getLocation().getName());
                /*if (!c.getResponsibleAgents().isEmpty()) {
                    a.getRespAgentsBkp().put(a.getLocation().getName(), c.getResponsibleAgents().size());
                }*/
                a.getLocalNetwork().add(nodeNet);

                //let only nhops as local information by agent
                if (a.getLocalNetwork().size() > SimulationParameters.nhops) {
                    List nl = new ArrayList(a.getLocalNetwork().subList(a.getLocalNetwork().size() - SimulationParameters.nhops, a.getLocalNetwork().size()));
                    a.setLocalNetwork(nl);
                }

                //Sh Send neighbour data to node ex in HashMap format: {p51={p21, p22, p23}, p22={p1, p2}}
                String[] msgnet = new String[3];
                msgnet[0] = "networkdata";
                msgnet[1] = StringSerializer.serialize(a.getLocalNetwork());
                msgnet[2] = StringSerializer.serialize(a.getRespAgentsBkp());

                if (a.getLocation() != null) {
                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnet);
                }

                visitedNodes.add(currentNode);
                getLocationAgents().put(a, a.getLocation());
                ActionParameters ac = (ActionParameters) action;

                synchronized (a.getLocation().getData()) {
                    //Get data from agent and put information in node
                    for (Object data : a.getData()) {
                        if (!a.getLocation().getData().contains(data)) {
                            a.getLocation().getData().add(data);
                        }
                    }
                }

                // Communication among agents 
                //detects other agents in network
                ArrayList<Integer> agentNeighbors = getAgentNeighbors(a);

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

                int old_size = a.getData().size();
                int new_size = 0;

                //inbox: id | infi 
                if (inbox != null) {
                    a.incMsgRecv();
                    ArrayList senderInf = (ArrayList) ObjectSerializer.deserialize(inbox[1]);

                    // Join ArrayLists
                    a.getData().removeAll(senderInf);
                    a.getData().addAll(senderInf);
                    new_size = a.getData().size();
                    if (old_size < new_size) {
                        a.setPheromone(1.0f);
                    }
                }

                if (flag) {
                    //String act = action.getCode();
                    String msg = null;
                    /**
                     * 0- "move"
                     */
                    /* @TODO: Detect Stop Conditions for the algorithm */
                    switch (language.getActionIndex(act)) {
                        case 0: // move
                            //System.out.println("a despues" + a.getLocation());
                            boolean complete = false;
                            if (a.getData().size() == topology.getVertexCount()) {
                                complete = true;
                            }

                            if (getRoundComplete() == -1 && complete) {
                                System.out.println("complete! round" + a.getRound());
                                setRoundComplete(a.getRound());
                                setIdBest(a.getId());
                                //updateWorldAge();
                            }

                            //get new location
                            GraphElements.MyVertex v = (GraphElements.MyVertex) ac.getAttribute("location");
                            float pf = (float) ac.getAttribute("pf");
                            //Send a message to current node before moving to new destination v
                            //msgnode: "departing"|agentId|FatherId|newDest

                            if (SimulationParameters.activateReplication.equals("replalgon")) {
                                String[] msgnode = new String[4];
                                msgnode[0] = "departing";
                                msgnode[1] = String.valueOf(a.getId());
                                msgnode[2] = String.valueOf(a.getIdFather());
                                msgnode[3] = v.getName();
                                if (a.getLocation() != null) {
                                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                                }
                            }

                            //Agent Fail when moving
                            if (Math.random() < pf) {
                                //System.out.println("Agent " + a.getId() + "has failed");
                                a.die();
                                increaseAgentsDie();
                                getLocationAgents().put(a, null);
                                a.setLocation(null);
                                setChanged();
                                notifyObservers();
                                return false;
                            }

                            if (!SimulationParameters.nodeDelay.equals("NODELAY")) {
                                int nodeDelay = Integer.valueOf(SimulationParameters.nodeDelay);
                                a.sleep(nodeDelay);
                            }

                            a.setPrevLocation(a.getLocation());
                            a.setLocation(v);
                            getLocationAgents().put(a, a.getLocation());
                            a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                            a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));
                            a.setRound(a.getRound() + 1);
                            incrementAgentMovements();

                            if (SimulationParameters.activateReplication.equals("replalgon")) {
                                if (a.getPrevLocation() != null) {
                                    String[] msgnoder = new String[3];
                                    msgnoder[0] = "freeresp";
                                    msgnoder[1] = String.valueOf(a.getId());
                                    msgnoder[2] = a.getLocation().getName();
                                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), msgnoder);
                                }
                            }
                            currentNode = v;
                            visitedNodes.add(currentNode);
                            break;
                        case 1: //die
                            System.out.println("Agent " + a.getId() + "has failed");
                            Scanner sc = new Scanner(System.in);
                            String cont = sc.next();
                            a.die();
                            increaseAgentsDie();
                            getLocationAgents().put(a, null);
                            a.setLocation(null);
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
            }
            //updateWorldAge();
            setChanged();
            notifyObservers();
            //System.out.println("wat" + a.getId());
            return flag;
        }
        if (agent instanceof Node) {
            Node n = (Node) agent;
            //System.out.println("thread of node " + n.getVertex().getName());
            n.incRounds();
            //System.out.println("node thread:" + n.getVertex().getName());
            String act = action.getCode();

            //Node has no neighbours
            if (topology.getNeighbors(n.getVertex()).isEmpty()) {
                System.out.println("empty");
                if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                    System.out.println("entra");

                    List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName()));
                    List<String> dif = new ArrayList(nd);

                    if (!dif.isEmpty()) {
                        System.out.println(n.getVertex().getName() + ", diff nd" + nd);
                        int level = 0;
                        level++;
                        n.getPending().put(dif.toString(), new ArrayList<>());

                        for (String d : dif) {
                            List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);
                            String min;
                            min = getMinimumId(neigdiff);
                            // System.out.println("neighdiff size" + neigdiff.size());
                            //I'm minimum, I create node
                            if (min.equals(n.getVertex().getName()) ) {
                                System.out.println("create node because node does not detect");
                                createNewNode(n, d);
                                //Send message to node neigbours.
                                //can be no nd but all agentData
                                for (String neig : neigdiff) {
                                    //message msgnodediff: connect|level|nodeid|nodetoconnect
                                    //System.out.println(n.getVertex().getName() + "is sending diff " + dif + "to" + neig);
                                    String[] msgnodediff = new String[5];
                                    msgnodediff[0] = "connect";
                                    msgnodediff[1] = String.valueOf(level);
                                    msgnodediff[2] = n.getVertex().getName();
                                    msgnodediff[3] = d;
                                    NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                    //n.getPending().get(dif.toString()).add(neig);
                                }
                            }
                        }
                    }
                }
            }

            //System.out.println("action" + act + ", code:" + nodeLanguage.getActionIndex(act));
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        if (SimulationParameters.activateReplication.equals("replalgon")) {
                            //Send a message to current node before moving to new destination v
                            //msgnode: "departing"|agentId|FatherId|newDest
                            if (inbox[0].equals("departing")) {
                                int agentId = Integer.valueOf(inbox[1]);
                                n.setLastAgentArrival(agentId, n.getRounds());
                                n.incMsgRecv();
                                n.getResponsibleAgents().put(agentId, Integer.valueOf(inbox[2]));
                                n.getResponsibleAgentsLocation().put(agentId, inbox[3]);
                                n.calculateTimeout();
                            }
                            if (inbox[0].equals("freeresp")) {
                                incrementACKAmount();
                                n.incMsgRecv();
                                int agentId = Integer.valueOf(inbox[1]);
                                String newLocation = inbox[2];
                                n.setLastMessageArrival(agentId, n.getRounds(), newLocation);
                                n.calculateTimeout();
                                if (n.getResponsibleAgents().containsKey(agentId)) {
                                    n.getResponsibleAgents().remove(agentId);
                                } else {
                                    incrementFalsePossitives();
                                    deleteNextReplica(n);
                                }
                            }
                            n.calculateTimeout();
                            evaluateAgentCreation(n);
                        }
                        if (inbox[0].equals("noneighdetected")) {
                            if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                                List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName()));
                                List<String> dif = new ArrayList(nd);
                                   
                                
                                if (!dif.isEmpty()) {
                                    //System.out.println(n.getVertex().getName() + ", diff nd" + nd + " vs " + "ad" + ad);
                                    int level = 0;
                                    level++;
                                    n.getPending().put(dif.toString(), new ArrayList<>());

                                    for (String d : dif) {
                                        List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);
                                        String min;
                                        min = getMinimumId(neigdiff);
                                        // System.out.println("neighdiff size" + neigdiff.size());
                                        //I'm minimum, I create node
                                        if (min.equals(n.getVertex().getName())) {
                                            System.out.println("Create node method:" + inbox[0]);
                                            createNewNode(n, d);
                                            //Send message to node neigbours.
                                            //can be no nd but all agentData
                                            for (String neig : neigdiff) {
                                                //message msgnodediff: connect|level|nodeid|nodetoconnect
                                                //System.out.println(n.getVertex().getName() + "is sending diff " + dif + "to" + neig);
                                                String[] msgnodediff = new String[5];
                                                msgnodediff[0] = "connect";
                                                msgnodediff[1] = String.valueOf(level);
                                                msgnodediff[2] = n.getVertex().getName();
                                                msgnodediff[3] = d;
                                                NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                                //n.getPending().get(dif.toString()).add(neig);
                                            }
                                        } else {
                                            //Send message to node neigbours.
                                            //can be no nd but all agentData
                                            for (String neig : neigdiff) {
                                                //message msgnodediff: diffound|level|nodeid|dif|neigdif
                                                //System.out.println("sending diff " + dif + "to" + neig);
                                                String[] msgnodediff = new String[6];
                                                msgnodediff[0] = "diffound";
                                                msgnodediff[1] = String.valueOf(level);
                                                msgnodediff[2] = n.getVertex().getName();
                                                msgnodediff[3] = StringSerializer.serialize(dif);
                                                msgnodediff[4] = StringSerializer.serialize(neigdiff);
                                                msgnodediff[5] = inbox[2];
                                                NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                                n.getPending().get(dif.toString()).add(neig);
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println("No neigbours.");
                            }
                        }
                        if (inbox[0].equals("networkdata")) {
                            //completes and updates data
                            HashMap<String, Integer> agentBkp = (HashMap) StringSerializer.deserialize(inbox[2]);

                            for (String key : agentBkp.keySet()) {
                                n.getRespAgentsBkp().put(key, agentBkp.get(key));
                            }
                            //System.out.println("res" + n.getRespAgentsBkp());

                            ArrayList<HashMap> agentLoc = (ArrayList) StringSerializer.deserialize(inbox[1]);
                            HashMap<String, Integer> nagentsresp = (HashMap<String, Integer>) StringSerializer.deserialize(inbox[2]);
                            for (HashMap< String, Object> agentData : agentLoc) {
                                //System.out.print(agentData + ",");
                                if (!n.getNetworkdata().equals(agentData)) {
                                    if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                                        List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName()));

                                        if (agentData.containsKey(n.getVertex().getName())) {
                                            List<String> ad = new ArrayList((Collection) agentData.get(n.getVertex().getName()));
                                            List<String> dif = new ArrayList(nd);
                                            dif.removeAll(ad);

                                            if (!dif.isEmpty()) {
                                                //System.out.println(n.getVertex().getName() + ", diff nd" + nd + " vs " + "ad" + ad);
                                                int level = 0;
                                                level++;
                                                n.getPending().put(dif.toString(), new ArrayList<>());

                                                for (String d : dif) {
                                                    List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);
                                                    String min;
                                                    min = getMinimumId(neigdiff);
                                                    System.out.println("neighdiff size" + neigdiff.size());
                                                    //I'm minimum, I create node
                                                    if (neigdiff.size() == 1 || min.equals(n.getVertex().getName())) {
                                                        System.out.println("Create node method:" + inbox[0]);
                                                        createNewNode(n, d);
                                                        //Send message to node neigbours.
                                                        //can be no nd but all agentData
                                                        for (String neig : neigdiff) {
                                                            //message msgnodediff: connect|level|nodeid|nodetoconnect
                                                            //System.out.println(n.getVertex().getName() + "is sending diff " + dif + "to" + neig);
                                                            String[] msgnodediff = new String[5];
                                                            msgnodediff[0] = "connect";
                                                            msgnodediff[1] = String.valueOf(level);
                                                            msgnodediff[2] = n.getVertex().getName();
                                                            msgnodediff[3] = d;
                                                            NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                                            //n.getPending().get(dif.toString()).add(neig);
                                                        }
                                                    } else {
                                                        //Send message to node neigbours.
                                                        //can be no nd but all agentData
                                                        for (String neig : neigdiff) {
                                                            //message msgnodediff: diffound|level|nodeid|dif|neigdif
                                                            //System.out.println("sending diff " + dif + "to" + neig);
                                                            String[] msgnodediff = new String[6];
                                                            msgnodediff[0] = "diffound";
                                                            msgnodediff[1] = String.valueOf(level);
                                                            msgnodediff[2] = n.getVertex().getName();
                                                            msgnodediff[3] = StringSerializer.serialize(dif);
                                                            msgnodediff[4] = StringSerializer.serialize(neigdiff);
                                                            msgnodediff[5] = inbox[2];
                                                            NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                                            n.getPending().get(dif.toString()).add(neig);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    for (String key : agentData.keySet()) {
                                        if (!n.getNetworkdata().containsKey(key)) {
                                            n.getNetworkdata().put(key, agentData.get(key));
                                        }
                                    }
                                }
                            }

                        }
                        if (inbox[0].equals("connect")) {
                            //message msgnodediff: connect|level|nodeid|nodetoconnect
                            int level = Integer.valueOf(inbox[1]);
                            //String ndet = String.valueOf(inbox[2]);
                            String nodetoConnect = inbox[3];
                            connect(n.getVertex(), nodetoConnect);
                            //createNewAgents(5, n);
                        }
                        if (inbox[0].equals("diffound")) {
                            //msgnodediff: diffound|level|nodeid|dif|neigdif
                            int level = Integer.valueOf(inbox[1]);
                            String nodeid = String.valueOf(inbox[2]);
                            List<String> dif = (List) StringSerializer.deserialize(inbox[3]);
                            //HashMap<String, Integer> nagentsresp = (HashMap<String, Integer>) StringSerializer.deserialize(inbox[5]);
                            //List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName()));
                            //List nemiss = (List) StringSerializer.deserialize(inbox[4]);
                            //message msgnodediff: diffound|level|nodeid|dif|neigdif

                            for (String d : dif) {
                                List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);

                                String min;
                                min = getMinimumId(neigdiff);

                                //I'm minimum, I create node
                                if (min.equals(n.getVertex().getName())) {
                                    System.out.println("Create node method:" + inbox[0]);
                                    createNewNode(n, d);
                                    //Send message to node neigbours.
                                    //can be no nd but all agentData
                                    for (String neig : neigdiff) {
                                        //message msgnodediff: connect|level|nodeid|nodetoconnect
                                        // System.out.println("sending diff " + dif + "to" + neig);
                                        String[] msgnodediff = new String[4];
                                        msgnodediff[0] = "connect";
                                        msgnodediff[1] = String.valueOf(level);
                                        msgnodediff[2] = n.getVertex().getName();
                                        msgnodediff[3] = d;
                                        NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                        //n.getPending().get(dif.toString()).add(neig);
                                    }
                                } /*else {
                                    //Send message to node neigbours.
                                    //can be no nd but all agentData
                                    for (String neig : neigdiff) {
                                        //message msgnodediff: diffound|level|nodeid|dif|neigdif
                                        //System.out.println("sending diff " + dif + "to" + neig);
                                        String[] msgnodediff = new String[5];
                                        msgnodediff[0] = "diffound";
                                        msgnodediff[1] = String.valueOf(level);
                                        msgnodediff[2] = n.getVertex().getName();
                                        msgnodediff[3] = StringSerializer.serialize(dif);
                                        msgnodediff[4] = StringSerializer.serialize(neigdiff);

                                        NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                        n.getPending().get(dif.toString()).add(neig);
                                    }
                                }*/
                            }
                        }
                    }
                    break;

                case 1: //what happens if a node dies?
                    System.out.println("Node" + n.getVertex().getName() + " has failed.");
                    System.out.println("killed: " + killAgentsInlocation(n.getVertex().getName()));
                    NetworkNodeMessageBuffer.getInstance().deleteBuffer(n.getVertex().getName());
                    //System.out.println("node fail?");
                    topology.removeVertex(n.getVertex());
                    nodes.remove(n);
                    n.die();
                    break;
                default:
                    System.out.println("acrtion not specified");
            }
        }
        setChanged();
        notifyObservers();

        return false;
    }

    public void evaporatePheromone() {
        for (GraphElements.MyVertex v : topology.getVertices()) {
            //System.out.println(v.toString() + "before:" + v.getPh());
            v.setPh(v.getPh() - v.getPh() * 0.001f);
            //System.out.println(v.toString() + "after:" + v.getPh());
        }
    }

    //Example: It is better handshake protocol. J. Gomez
    public void evaluateAgentCreation(Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<Map.Entry<Integer, Integer>> iter = n.getResponsibleAgents().entrySet().iterator();
            Iterator<Map.Entry<Integer, String>> iterLoc = n.getResponsibleAgentsLocation().entrySet().iterator();
            ///if (!n.getResponsibleAgents().isEmpty()) {
            // System.out.println(n.getVertex().getName() + " hashmap " + n.getResponsibleAgents());
            /*}*/

            //System.out.println(n.getVertex().getName() + " hashmap " + n.getResponsibleAgents());
            int estimatedTimeout = 0;
            int stdDevTimeout = 0;
            while (iter.hasNext()) {
                //Key: agentId|roundNumber
                Map.Entry<Integer, Integer> Key = iter.next();
                int k = Key.getKey();
                estimatedTimeout = n.estimateExpectedTime(n.getResponsibleAgentsLocation().get(k));
                stdDevTimeout = (int) n.getStdDevTimeout(n.getResponsibleAgentsLocation().get(k));

                if (n.getLastAgentArrival().containsKey(k) && Math.abs((n.getRounds() - n.getLastAgentArrival(k))) > (estimatedTimeout + 3 * stdDevTimeout)) { //this is not the expresion
                    /*if (n.getResponsibleAgentsLocation().containsKey(k) && n.getNodeTimeouts().containsKey(n.getResponsibleAgentsLocation().get(k))) {
                        n.getNodeTimeouts().get(n.getResponsibleAgentsLocation().get(k)).add(estimatedTimeout);
                        //n.addTimeout(estimatedTimeout);
                    }*/
                    //System.out.println("node" + n.getVertex().getName() + "," + estimatedTimeout);
                    //System.out.println("create new agent instance..." + n.getVertex().getName());
                    AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);

                    int newAgentID = agents.size();
                    MobileAgent a = new MobileAgent(program, newAgentID);

                    //System.out.println("creating agent id" + newAgentID);
                    NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

                    //getLocationAgents().add(new GraphElements.MyVertex("null"));
                    a.setId(newAgentID);
                    a.setData(new ArrayList(n.getVertex().getData()));

                    if (n.getResponsibleAgents().get(k) == -1) {
                        a.setIdFather(k);
                    } else {
                        a.setIdFather(n.getResponsibleAgents().get(k));
                    }
                    a.setRound(super.getAge());
                    this.agents.add(a);

                    a.live();
                    Thread t = new Thread(a);
                    a.setThread(t);
                    a.setLocation(n.getVertex());
                    a.setPrevLocation(n.getVertex());
                    a.setArchitecture(this);
                    setTotalAgents(getTotalAgents() + 1);

                    String[] msgnode = new String[4];
                    msgnode[0] = "arrived";
                    msgnode[1] = String.valueOf(a.getId());
                    msgnode[2] = String.valueOf(a.getIdFather());
                    msgnode[3] = String.valueOf(-1);
                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                    t.start();
                    //System.out.println("replica created:" + a.getId());
                    //System.out.println("add creation time" + (n.getRounds() - n.getLastAgentArrival(k)));
                    //n.addCreationTime(n.getRounds() - n.getLastAgentArrival(k));
                    //System.out.println("node before: " + n.getVertex().getName() + " - " + n.getResponsibleAgents());
                    iter.remove();
                    //System.out.println("node after: " + n.getVertex().getName() + " - " + n.getResponsibleAgents());
                    //System.out.println("end creation of agent" + newAgentID);
                }
            }
        }
    }

    private void deleteNextReplica(Node n) {
        Vector<Agent> copy = (Vector) agents.clone();
        for (Agent a : copy) {
            if (a instanceof MobileAgent) {
                MobileAgent t = (MobileAgent) a;
                if (t.getLocation() != null && t.getLocation().getName().equals(n.getVertex().getName())) {
                    if (t.getPrevLocation() != null) {
                        String[] msgnoder = new String[3];
                        msgnoder[0] = "freeresp";
                        msgnoder[1] = String.valueOf(t.getId());
                        msgnoder[2] = t.getLocation().getName();
                        NetworkNodeMessageBuffer.getInstance().putMessage(t.getPrevLocation().getName(), msgnoder);
                    }
                    a.die();
                    increaseAgentsDie();
                    getLocationAgents().put(t, null);

                    t.setLocation(null);

                    setChanged();
                    notifyObservers();
                    //System.out.println("delete replica!");
                    return;
                }
            }
        }
    }
}
