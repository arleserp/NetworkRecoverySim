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
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphCreator;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;
import unalcol.agents.NetworkSim.MotionProgramSimpleFactory;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.SimulationParameters;
import static unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication.setTotalAgents;
import unalcol.agents.NetworkSim.programs.NodeFailingProgram;
import unalcol.agents.NetworkSim.util.HashMapOperations;
import unalcol.agents.NetworkSim.util.StringSerializer;

public class NetworkEnvironmentPheromoneReplicationNodeFailing extends NetworkEnvironmentReplication {

    private static int falsePossitives = 0;
    private static SimpleLanguage nodeLanguage;
    private static int agentMovements = 0;
    private static int ACKAmount = 0;
    private static List<Node> nodes = Collections.synchronizedList(new ArrayList());
    ;
    public List<MobileAgent> agentsAlive = Collections.synchronizedList(new ArrayList());

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

        // agentsAlive = new 
        for (Agent a : _agents) {
            if (a instanceof MobileAgent) {
                agentsAlive.add((MobileAgent) a);
            }
        }
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
        if (nodeTo != null) {
            topology.addEdge("e" + vertex.getName() + nodeTo.getName(), vertex, nodeTo);
        } else {
            System.out.println("node to connect is null:" + nodetoConnect);
        }
    }

    public void setNodes(List<Node> nds) {
        nodes = nds;
    }

    private void createNewAgents(Integer number, Node n) {
        System.out.println("create new " + number + " agents in node" + n.getVertex().getName());
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
            agentsAlive.add(a);

            String[] msgnode = new String[4];
            msgnode[0] = "arrived";
            msgnode[1] = String.valueOf(a.getId());
            msgnode[2] = String.valueOf(a.getIdFather());
            NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
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
                    killAgent(ma);
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
            //System.out.println(n.getVertex().getName() + " is creating " + d + ":");
            GraphElements.MyVertex vv = v.create();
            vv.setName(d);
            topology.addVertex(vv);
            topology.addEdge("e" + vv.getName() + n.getVertex().getName(), vv, n.getVertex());
            //NodeFailingProgram np = new NodeFailingProgram(SimulationParameters.npf);
            NodeFailingProgram np = new NodeFailingProgram((float) SimulationParameters.npf);
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

            //createNewAgents(10, nod); //we cannot create agents yet! maybe after network is connected.
            nodes.add(nod);
            t.start();
            //System.out.println("adding data to node" + nod.getVertex().getName() + ":" + n.getNetworkdata());
            nod.setNetworkdata(new HashMap(n.getNetworkdata()));
            //System.out.println("adding data to node" + nod.getVertex().getName() + ":" + n.getRespAgentsBkp());
            nod.setRespAgentsBkp(new HashMap(n.getRespAgentsBkp()));

            setChanged();
            notifyObservers();
        }
    }

    public boolean isOccuped(GraphElements.MyVertex v) {
        synchronized (NetworkEnvironmentReplication.class) {
            Iterator itr = agentsAlive.iterator();
            // System.out.println("isOccuped size agents" + this.agentsAlive.size());
            while (itr.hasNext()) {
                Agent a = (Agent) itr.next();
                if (a instanceof MobileAgent) {
                    if (a.status != Action.DIE && ((MobileAgent) a).getLocation() != null && (((MobileAgent) a).getLocation()).equals(v)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void killAgent(MobileAgent a) {
        System.out.println("Method Kill Agent:" + a.getId());
        a.die();
        increaseAgentsDie();
        getLocationAgents().put(a, null);
        a.setLocation(null);
        agentsAlive.remove(a);
        setChanged();
        notifyObservers();
        try {
            a.getThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(NetworkEnvironmentPheromoneReplicationNodeFailing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void KillNode(Node n) {
        //System.out.println("Node" + n.getVertex().getName() + " has failed.");
        //System.out.println("killed: " + killAgentsInlocation(n.getVertex().getName()));
        NetworkNodeMessageBuffer.getInstance().deleteBuffer(n.getVertex().getName());
        //System.out.println("node fail?");
        topology.removeVertex(n.getVertex());
        if (nodes.remove(n)) {
            //System.out.println("removed!" + n.getVertex().getName());
        }
        /*System.out.println("visited nodes before" + visitedNodes);
                    if (visitedNodes.remove(n.getVertex())) {
                        System.out.println("removed visited nodes!" + n.getVertex().getName());
                        System.out.println("visited nodes after " + visitedNodes);
                    }*/
        n.die();
        setChanged();
        notifyObservers();
        try {
            n.getThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(NetworkEnvironmentPheromoneReplicationNodeFailing.class.getName()).log(Level.SEVERE, null, ex);
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
        //System.out.print("getNode" + name + ":");
        //ArrayList<Node> copy = new ArrayList(nodes);
        System.out.println("getNode nodes size " + nodes.size());
        for (Node nod : nodes) {
            //if (nod.status != Action.DIE && nod.getVertex().getName().equals(name) && topology.containsVertex(nod.getVertex())) {
            //      System.out.println("found");
            return nod;
            //}
        }
        //System.out.println("return nul");
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
        // System.out.print("getMinimum id called, ");
        /*if(neigdiff.isEmpty()){
            return 
        }*/
        List<String> nodesAlive = new ArrayList();
        for (String s : neigdiff) {
            if (getNode(s) != null) {
                nodesAlive.add(s);
            }
        }
        //if (nodesAlive.isEmpty()) {
        //System.out.println("nodes alive size " + nodesAlive.size());
        String min = Collections.min(nodesAlive, new CustomComparatorNode());
        //}
        //System.out.println(" min " + min);
        return min;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof MobileAgent) {
            agent.sleep(10);
            boolean flag = (action != null);
            MobileAgent a = (MobileAgent) agent;

            System.out.println("thread:" + a.getId());
            /*if (a.status == Action.DIE || a.getLocation() == null) {
                System.out.println("return false " + a.getId());
                return false;
            }*/
            String act = action.getCode();

            //New: informfailure
            if (language.getActionIndex(act) == 2) {
                System.out.println("really?");
                //killAgent(a);
                //can report a failure because node loss neigbours.
            } else {
                currentNode = a.getLocation();
                a.getLocation().setStatus("visited");
                //Sh Add neighbor data to agent as an array p51={p21, p22, p23}|p22={p1, p2}
                HashMap<String, Object> nodeNet = new HashMap<>();
                nodeNet.put(a.getLocation().getName(), getTopologyNames(a.getLocation()));

                //Add resposible agents v
                Node c = getNode(a.getLocation().getName());

                if (c != null) {
                   /* ArrayList respAg = new ArrayList();
                    ArrayList respAr = new ArrayList();
                    if (c.getResponsibleAgents() != null) {
                        
                        respAg = new ArrayList(((HashMap<Integer,Integer>)c.getResponsibleAgents().clone()).values());
                    }
                    if (c.getResponsibleAgentsArrival() != null) {
                        respAr = new ArrayList(((HashMap) c.getResponsibleAgentsArrival().clone()).values());
                    }
                    respAg.removeAll(respAr);
                    respAg.addAll(respAr);*/
                    a.getRespAgentsBkp().put(a.getLocation().getName(), c.getResponsibleAgentsArrival().size());
                    a.getLocalNetwork().add(nodeNet);
                }
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

                /*if (!visitedNodes.contains(currentNode)) {
                    a.getLocation().setStatus("visited");
                    visitedNodes.add(currentNode);
                    System.out.println("visitedNodes: " + visitedNodes);
                }*/
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
                                killAgent(a);
                                return false;
                            }

                            if (!SimulationParameters.nodeDelay.equals("NODELAY")) {
                                int nodeDelay = Integer.valueOf(SimulationParameters.nodeDelay);
                                a.sleep(nodeDelay);
                            }

                            //Move agent to a location
                            a.setPrevLocation(a.getLocation());
                            a.setLocation(v);

                            //Send message arrived to node arrived|id|getPrevLocation
                            if (SimulationParameters.activateReplication.equals("replalgon")) {
                                String[] msgarrived = new String[3];
                                msgarrived[0] = "arrived";
                                msgarrived[1] = String.valueOf(a.getId());
                                msgarrived[2] = a.getPrevLocation().getName();
                                NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgarrived);
                            }

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
                            //visitedNodes.add(currentNode);
                            break;
                        case 1: //die
                            killAgent(a);
                            return false;
                        default:
                            msg = "[Unknown action " + act
                                    + ". Action not executed]";
                            System.out.println(msg);
                            break;
                    }
                }
                //updateWorldAge();
                setChanged();
                notifyObservers();
            }
            //System.out.println("wat" + a.getId());
            return flag;
        }
        if (agent instanceof Node) {
            agent.sleep(30);
            Node n = (Node) agent;
            //System.out.println("thread of node " + n.getVertex().getName() + "ph: " + n.getVertex().getPh());
            n.incRounds();
            //System.out.println("node thread:" + n.getVertex().getName());
            String act = action.getCode();
            //System.out.println("thread " + n.getVertex().getName());
            //Node has no neighbours
            //System.out.println("to" + (topology.getNeighbors(n.getVertex())));

            //1. process messages from an agent
            //System.out.println("action" + act + ", code:" + nodeLanguage.getActionIndex(act));
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        if (SimulationParameters.activateReplication.equals("replalgon")) {
                            //Send a message to current node before moving to new destination v
                            //Send message arrived to node arrived|id|getPrevLocation
                            if (inbox[0].equals("arrived")) {
                                int agentId = Integer.valueOf(inbox[1]);
                                n.setLastAgentArrive(agentId, n.getRounds());
                                n.incMsgRecv();
                                n.getResponsibleAgentsArrival().put(agentId, n.getRounds());
                                //n.getResponsibleAgentsLocation().put(agentId, inbox[3]);
                                n.calculateTimeoutArrival();
                            }
                            //msgnode: "departing"|agentId|FatherId|newDest
                            if (inbox[0].equals("departing")) {
                                int agentId = Integer.valueOf(inbox[1]);
                                n.setLastAgentDeparting(agentId, n.getRounds());
                                n.setLastStartDeparting(agentId, n.getRounds());
                                n.incMsgRecv();
                                n.getResponsibleAgents().put(agentId, -1);
                                n.getResponsibleAgentsLocation().put(agentId, inbox[3]);
                                n.calculateTimeout();
                                n.calculateTimeoutArrival();
                                if (n.getResponsibleAgentsArrival().containsKey(agentId)) {
                                    n.getResponsibleAgentsArrival().remove(agentId);
                                }
                                /*else {
                                    //System.out.println("delete!!!!");
                                    incrementFalsePossitives();
                                    deleteNextReplica(n);
                                }*/
                                evaluateAgentCreationArrival(n);
                            }
                            if (inbox[0].equals("freeresp")) {
                                incrementACKAmount();
                                n.incMsgRecv();
                                int agentId = Integer.valueOf(inbox[1]);
                                String newLocation = inbox[2];
                                n.setLastMessageFreeResp(agentId, n.getRounds(), newLocation);
                                n.calculateTimeout();
                                if (n.getResponsibleAgents().containsKey(agentId)) {
                                    n.getResponsibleAgents().remove(agentId);
                                } else {
                                    //System.out.println("delete!!!!");
                                    incrementFalsePossitives();
                                   // deleteNextReplica(n);
                                }
                            }
                        }
                        if (inbox[0].equals("networkdata")) {
                            //completes and updates data
                            HashMap<String, Integer> agentBkp = (HashMap) StringSerializer.deserialize(inbox[2]);
                            //Maybe update this?
                            for (String key : agentBkp.keySet()) {
                                n.getRespAgentsBkp().put(key, agentBkp.get(key));
                            }
                            //System.out.println("res" + n.getRespAgentsBkp());
                            ArrayList<HashMap> agentLoc = (ArrayList) StringSerializer.deserialize(inbox[1]);
                            //System.out.print(n.getVertex().getName() + " n antes" + n.getNetworkdata());
                            for (HashMap< String, Object> agentData : agentLoc) {
                                //Compare node data with agent data
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), agentData));
                            }
                            //System.out.println(", n despues" + n.getNetworkdata());
                        }
                        if (inbox[0].equals("connect")) {
                            //message msgnodediff: connect|level|nodeid|nodetoconnect
                            int level = Integer.valueOf(inbox[1]);
                            //String ndet = String.valueOf(inbox[2]);
                            String nodetoConnect = inbox[3];
                            connect(n.getVertex(), nodetoConnect);
                            //createNewAgents(5, n);
                        }
                    }
                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                        n.calculateTimeout();
                        evaluateAgentCreation(n);
                        n.calculateTimeoutArrival();
                        evaluateAgentCreationArrival(n);
                    }
                    break;
                case 1: //what happens if a node dies?
                    KillNode(n);
                    break;
                default:
                    System.out.println("acrtion not specified");
            }

            if (n.status != Action.DIE) {
                //System.out.println("node name" + n.getVertex().getName());
                //2. Compare topology data with cache given by agents.
                List<String> topologyData = new ArrayList(this.getTopologyNames(n.getVertex())); // Get topology of the network
                if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                    List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName())); //Store data given by agents

                    //System.out.println("node" + n.getVertex().getName() +" nd:" + nd + "vs  topologyData:" + topologyData);
                    //dif = nd - topologyData
                    List<String> dif = new ArrayList<>(nd);
                    dif.removeAll(topologyData);

                    //dif = topologyData - nd
                    List<String> dif2 = new ArrayList<>(topologyData);
                    dif2.removeAll(nd);

                    dif.removeAll(dif2);
                    dif.addAll(dif2);

                    //System.out.println("node 2 nd" + nd);
                    if (!dif.isEmpty()) {
                        int level = 0;
                        level++;
                        for (String d : dif) {
                            //without neigbor data of d is impossible create d ?
                            if (n.getNetworkdata().containsKey(d)) {
                                List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);

                                String min;
                                // System.out.println(n.getVertex().getName() + "-d:" + d + ", data:" + n.getNetworkdata());
                                //System.out.println("ne:" + neigdiff + ", " + n.getVertex().getName());
                                min = getMinimumId(neigdiff);
                                //I'm minimum, I create node
                                //System.out.println("min" + min + " vs " + n.getVertex().getName());
                                if (min.equals(n.getVertex().getName())) {
                                    //System.out.println("create node because node does not detect");
                                    createNewNode(n, d);
                                    //Send message to node neigbours.
                                    //can be no nd but all agentData
                                    if (neigdiff != null && !neigdiff.isEmpty()) {
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
                } else if (n.getNetworkdata().isEmpty()) {

                    n.getNetworkdata().put(n.getVertex().getName(), topologyData);
                }
            }
        }
        return false;
    }

    public void evaporatePheromone() {
        ArrayList<GraphElements.MyVertex> evaporateClone = new ArrayList(topology.getVertices());
        for (GraphElements.MyVertex v : evaporateClone) {
            //System.out.println(v.toString() + "before:" + v.getPh());
            v.setPh(v.getPh() - v.getPh() * 0.001f);
            //System.out.println(v.toString() + "after:" + v.getPh());

        }
    }

    private void evaluateAgentCreationArrival(Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<Map.Entry<Integer, Integer>> iter = n.getResponsibleAgentsArrival().entrySet().iterator();
            //System.out.println(n.getVertex().getName() + " hashmap " + n.getResponsibleAgents());
            int estimatedTimeoutArrival = 0;
            int stdDevTimeoutArrival = 0;
            while (iter.hasNext()) {
                //Key: agentId|roundNumber
                Map.Entry<Integer, Integer> Key = iter.next();
                int k = Key.getKey();
                estimatedTimeoutArrival = n.estimateExpectedTimeArrival();
                stdDevTimeoutArrival = (int) n.getStdDevTimeoutArrival();

                if (n.getLastAgentDeparting().containsKey(k) && Math.abs((n.getRounds() - n.getLastAgentArrive(k))) > (estimatedTimeoutArrival + 3 * stdDevTimeoutArrival)) { //this is not the expresion
                    AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
                    System.out.println("create new agent arrival!!!!");
                    int newAgentID = agents.size();
                    MobileAgent a = new MobileAgent(program, newAgentID);

                    NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

                    a.setId(newAgentID);
                    a.setData(new ArrayList(n.getVertex().getData()));
                    /*
                    if (n.getResponsibleAgentsArrival().get(k) == -1) {
                        a.setIdFather(k);
                    } else {
                        a.setIdFather(n.getResponsibleAgents().get(k));
                    }*/
                    a.setIdFather(-1);
                    a.setRound(super.getAge());
                    this.agents.add(a);

                    a.live();
                    Thread t = new Thread(a);
                    a.setThread(t);
                    a.setLocation(n.getVertex());
                    a.setPrevLocation(n.getVertex());
                    a.setArchitecture(this);
                    setTotalAgents(getTotalAgents() + 1);

                    /*String[] msgnode = new String[4];
                    msgnode[0] = "arrived";
                    msgnode[1] = String.valueOf(a.getId());
                    msgnode[2] =- 

                    NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                     */
                    t.start();
                    iter.remove();
                }
            }
        }
    }

    //Example: It is better handshake protocol. J. Gomez
    public void evaluateAgentCreation(Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<Map.Entry<Integer, Integer>> iter = n.getResponsibleAgents().entrySet().iterator();
            Iterator<Map.Entry<Integer, String>> iterLoc = n.getResponsibleAgentsLocation().entrySet().iterator();
            if (!n.getResponsibleAgents().isEmpty()) {
                System.out.println("evaluateAgentCreation node" + n.getVertex().getName() + " is responsible for: " + n.getResponsibleAgents());
            }
            //System.out.println(n.getVertex().getName() + " n resp " + n.getResponsibleAgents());

            //System.out.println(n.getVertex().getName() + " hashmap " + n.getResponsibleAgents());
            int estimatedTimeout = 0;
            int stdDevTimeout = 0;
            while (iter.hasNext()) {
                //Key: agentId|roundNumber
                Map.Entry<Integer, Integer> Key = iter.next();
                int k = Key.getKey();
                estimatedTimeout = n.estimateExpectedTime(n.getResponsibleAgentsLocation().get(k));
                stdDevTimeout = (int) n.getStdDevTimeout(n.getResponsibleAgentsLocation().get(k));
                if (n.getLastAgentDeparting().containsKey(k) && Math.abs((n.getRounds() - n.getLastAgentDeparting(k))) > (estimatedTimeout + 3 * stdDevTimeout)) { //this is not the expresion
                    /*if (n.getResponsibleAgentsLocation().containsKey(k) && n.getNodeTimeouts().containsKey(n.getResponsibleAgentsLocation().get(k))) {
                        n.getNodeTimeouts().get(n.getResponsibleAgentsLocation().get(k)).add(estimatedTimeout);
                        //n.addTimeout(estimatedTimeout);
                    }*/
                    System.out.println("nodep: " + n.getVertex().getName() + ", estimatedTimeout: " + estimatedTimeout + ", lastAgentDep: " + n.getLastAgentDeparting(k) + ", node rounds: " + n.getRounds());
                    System.out.println("create new agent instance..." + n.getVertex().getName());
                    //System.out.println("Get 0" + );
                    AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);

                    int newAgentID = agents.size();
                    MobileAgent a = new MobileAgent(program, newAgentID);

                    System.out.println("creating agent id" + newAgentID);
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
                    agentsAlive.add(a);

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
                    killAgent(t);
                    //System.out.println("delete replica!");
                    return;
                }
            }
        }
    }

    @Override
    public int getAgentsAlive() {
        return agentsAlive.size();
    }

    @Override
    public int getNodesAlive() {
        return nodes.size();
    }

}
