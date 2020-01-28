package environment;

import agents.ActionParameters;
import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mobileagents.MobileAgent;
import networkrecoverysim.SimulationParameters;
import serialization.StringSerializer;
import staticagents.NetworkNodeMessageBuffer;
import staticagents.Node;
import util.HashMapOperations;

/**
 * This class defines an environment with the following assumption No mobile
 * agents only nodes nodes have information about the entire topology or partial
 * information given in terms of hops
 *
 * @author arlese.rodriguezp
 */
public class NetworkEnvironmentNodeFailingMobileAgents extends NetworkEnvironment {

    HashMap<String, ArrayList> networkInfo;
    boolean loaded;

    public NetworkEnvironmentNodeFailingMobileAgents(Vector<Agent> _agents, SimpleLanguage _nlanguage, SimpleLanguage _alanguage, Graph gr) {
        super(_agents, _nlanguage, _alanguage, gr);
        networkInfo = new HashMap<>();
        loaded = false;
    }

    @Override
    public boolean act(Agent agent, Action action) {

        try {
            if (agent instanceof MobileAgent) {
                try {
                    available.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NetworkEnvironmentNodeFailingMobileAgents.class.getName()).log(Level.SEVERE, null, ex);
                }

                MobileAgent a = (MobileAgent) agent;
                Node c = getNode(a.getLocation().getName());
                if (c == null) { //node failed                    
                    killMobileAgent(a);
                } else {
                    String[] msgdatanode = new String[4];
                    msgdatanode[0] = "networkdatanode";
                    msgdatanode[1] = String.valueOf(c.getName());
                    StringSerializer s = new StringSerializer();
                    msgdatanode[2] = s.serialize(a.getNetworkdata());
                    msgdatanode[3] = "mobileAgent";

                    NetworkNodeMessageBuffer.getInstance().putMessage(c.getName(), msgdatanode); //local communication
                    //a.increaseMessagesSentByRound(msgnetSize, 1);

                    HashMap<String, ArrayList> localData = a.getNetworkdata();
                    HashMap<String, ArrayList> recvData = c.getNetworkdata();
                    a.setNetworkdata(HashMapOperations.JoinSets(a.getNetworkdata(), recvData));

                    //inconsistency detected step 6 trickle
                    //if (HashMapOperations.calculateDifference(a.getNetworkdata(), localData).isEmpty()) {
                    if (HashMapOperations.isContained(recvData, localData) && HashMapOperations.isContained(localData, recvData)) {
                        //System.out.println("increase!");
                        a.incr();
                    } else {
                        a.restartCounter();
                    }
                    //
                    if (!a.check()) {
                        // System.out.println("kilin kiling lalalalal");
                        killMobileAgent(a);
                    }

                    //Action definition
                    ActionParameters ac = (ActionParameters) action;
                    String act = action.getCode();
                    switch (mobileAgentLanguage.getActionIndex(act)) {
                        case 0: // move
                            //get new location
                            MyVertex v = (MyVertex) ac.getAttribute("location");
                            a.setLocation(v); //move agent
                            Node n = getNode(v.getName());

                            if (n == null) { //node failed
                                killMobileAgent(a);
                            } else {
                                String[] msgMobileAgentArrived = new String[2];
                                msgMobileAgentArrived[0] = "mobileAgentArrived";
                                msgMobileAgentArrived[1] = a.getMemoryConsumption() + "";
                                NetworkNodeMessageBuffer.getInstance().putMessage(n.getName(), msgMobileAgentArrived);
                                a.increaseMessagesSentByRound(a.getMemoryConsumption(), 1);

                                n.setVisitedStatus("visited");      //to use in first and second neighbour               
                                a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone()))); //update pheromone amount
                                a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));
                            }
                            break;
                        case 1: //die
                            System.out.println("action: kill ");
                            killMobileAgent(a);
                        default:
                            String msg = "[Unknown action " + act
                                    + ". Action not executed]";
                            System.out.println(msg);
                            break;
                    }
                }
                //a.setRound(a.getRound() + 1);
                available.release();
                getSynsetAgentsReported().add(a.getId());
                synchronized (objBlock) {
                    try {
                        objBlock.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NetworkEnvironment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //return false;
            }
            //availableMa.release();
        } catch (Exception ex) {
            //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyyyyyy");
            //ex.printStackTrace();            
            System.exit(-1222222);
        }
//      long actStartTime = System.currentTimeMillis();
        if (agent instanceof Node) {
            Node n = (Node) agent;

            try {
                available.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentNodeFailingMobileAgents.class.getName()).log(Level.SEVERE, null, ex);
            }
            String act = action.getCode();

            //1. process messages from others
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    //boolean created = false;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        if (inbox[0].equals("mobileAgentArrived")) {
                            n.increaseMessagesRecvByRound(Double.valueOf(inbox[1]), 1);
                        } else {
                            if (SimulationParameters.activateReplication.equals("replalgon")) {
                                if (inbox[0].equals("connect")) {
                                    double inboxSize = getMessageSize(inbox);
                                    //increase number of messages received by round
                                    n.increaseMessagesRecvByRound(inboxSize, 1);

                                    //increase amount of messages received                                
                                    String nodetoConnect = inbox[2];
                                    connect(n.getVertex(), nodetoConnect);

                                    //When n connects to nodetoConnect n also sends its network topology
                                    // msg: networkdatainnode: networkdatanode|source|networkdata
                                    String[] msgdatanode = new String[3];
                                    msgdatanode[0] = "networkdatanode";
                                    msgdatanode[1] = String.valueOf(n.getVertex().getName());
                                    StringSerializer s = new StringSerializer();
                                    msgdatanode[2] = s.serialize(n.getNetworkdata());
                                    //increase message size                                    
                                    double msgdatanodeSize = getMessageSize(msgdatanode);
                                    n.increaseMessagesSentByRound(msgdatanodeSize, 1);

                                    NetworkNodeMessageBuffer.getInstance().putMessage(nodetoConnect, msgdatanode);
                                    System.out.println(n.getVertex().getName() + "send networkdatanode to " + nodetoConnect);
                                }
                            }

                            if (inbox[0].equals("networkdatanode")) { //receive topology data from mobile agent or node
                                double inboxSize = getMessageSize(inbox);
                                if (inbox.length < 4) { // received by other node
                                    n.increaseMessagesRecvByRound(inboxSize, 1);
                                }
                                String source = inbox[1]; //origin of message
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> recvData = (HashMap) s.deserialize(inbox[2]); //networkdata
                                HashMap<String, ArrayList> localData = n.getNetworkdata();
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));
                            }
                        }
                    }
                    break;
                case 1: //what happens if a node dies?
                    //System.out.println("node " + n.getVertex().getName() + " n followed agents:" + n.getResponsibleAgents());
                    //System.out.println("kiill");
                    KillNode(n);
                    break;
                default:
                    System.out.println("action not specified");
            }

            //2. Compare topology data with information obtained
            if (n.status != Action.DIE) {
                if (SimulationParameters.activateReplication.equals("replalgon")) {
                    n.evaluateNodeCreation(this);
                }
            }
            available.release();
            getSynsetNodesReported().add(n.getName());
            try {
                synchronized (objBlock) {
                    objBlock.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentNodeFailingMobileAgents.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Load all the topology in each node
     *
     * @return hashMap of n_1={n_1={n_k,...n_l}, n_2={...},..., n={}}
     */
    public HashMap<String, ArrayList> loadAllTopology() {
        if (loaded) {
            return networkInfo;
        } else {
            for (MyVertex v : getTopology().getVertices()) {
                networkInfo.put(v.getName(), new ArrayList<>(getTopologyNames(v)));
            }
            loaded = true;
            return networkInfo;
        }
    }

    /**
     * load neighobours using bfs
     *
     * @param neighbours neighbours of a node
     * @param nhopsChain number of hops
     * @param v node to load neighbour
     * @param distances //distances to a determined node
     */
    private void loadNeighboursBFS(ArrayList<MyVertex> neighbours, int nhopsChain, MyVertex v, HashMap<MyVertex, Integer> distances) {
        //System.out.println("nopsChain:" + nhopsChain);
        int lvls = 1;
        Deque<MyVertex> q = new LinkedList<>();
        distances.put(v, 0);
        if (nhopsChain == 0) {
            return;
        }
        while (v != null) {
            List<MyVertex> list = new ArrayList<>(getTopology().getNeighbors(v));
            Iterator<MyVertex> itr = list.iterator();
            while (itr.hasNext()) {
                MyVertex ne = itr.next();
                if (!distances.containsKey(ne)) {
                    distances.put(ne, distances.get(v) + 1);
                    q.add(ne);
                }
            }
            v = q.poll();
        }
    }

    /**
     * For each node load the neighbourhood in hops nhopsChain of n
     *
     * @param nhopsChain number of hops
     * @param n node
     * @return
     */
    public HashMap<String, ArrayList> loadPartialNetwork(int nhopsChain, Node n) {
        HashMap<String, ArrayList> localNetworkInfo = new HashMap<>();
        HashMap<MyVertex, Integer> distances = new HashMap<>();
        ArrayList<MyVertex> neighbours = new ArrayList<>();
        neighbours.add(n.getVertex());

        //loadNeighboursRecursively(neighbours, nhopsChain, n.getVertex()); //slow!
        loadNeighboursBFS(neighbours, nhopsChain, n.getVertex(), distances);

        n.setDistancesToNode(distances);
        for (MyVertex v : distances.keySet()) {
            if (!neighbours.contains(v) && distances.get(v) <= nhopsChain) {
                neighbours.add(v);
            }
        }
        System.out.println("node" + n + "neigh: " + neighbours + " neigh size:" + neighbours.size());
        System.out.println("");
        for (MyVertex v : neighbours) {
            localNetworkInfo.put(v.getName(), new ArrayList<>(getTopologyNames(v)));
        }
        System.out.println("node" + n + "info = " + localNetworkInfo);

        return localNetworkInfo;
    }

    @Override
    /**
     * Since there are not mobile agents always return false
     */
    public boolean isOccuped(MyVertex v) {
        return false;
    }
}
