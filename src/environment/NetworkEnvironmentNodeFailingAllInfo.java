package environment;

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
public class NetworkEnvironmentNodeFailingAllInfo extends NetworkEnvironment {
    HashMap<String, ArrayList> networkInfo;
    boolean loaded;

    public NetworkEnvironmentNodeFailingAllInfo(Vector<Agent> _agents, SimpleLanguage _nlanguage, Graph gr) {
        super(_agents, _nlanguage, null, gr);
        networkInfo = new HashMap<>();
        loaded = false;
    }

    @Override
    public boolean act(Agent agent, Action action) {

//        long actStartTime = System.currentTimeMillis();
        if (agent instanceof Node) {
            try {
                available.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentNodeFailingAllInfo.class.getName()).log(Level.SEVERE, null, ex);
            }

            Node n = (Node) agent;

            //System.out.println(n.getName() + ":" + n.getRounds());
            //This part is primitive send to neigbors 
            String act = action.getCode();

            //1. process messages from others
            //System.out.println("action" + act + ", code:" + nodeLanguage.getActionIndex(act));
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        double inboxSize = getMessageSize(inbox);
                        //increase number of messages received by round
                        n.increaseMessagesRecvByRound(inboxSize, 1);

                        if (SimulationParameters.activateReplication.equals("replalgon")) {
                            //message msgnodediff: connect|nodeid|nodetoconnect                                                            
                            if (inbox[0].equals("connect")) {
                                //increase amount of messages received                                
                                String nodetoConnect = inbox[2];
                                connect(n.getVertex(), nodetoConnect);

                                //When n connects to nodetoConnect n also sends its network topology
                                // msg: networkdatainnode: networkdatanode|source|networkdata
                                if (SimulationParameters.simMode.equals("nhopsinfo")) {
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

                            if (SimulationParameters.simMode.equals("nhopsinfo") && inbox[0].equals("networkdatanode")) {
                                String source = inbox[1]; //origin of message
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> ndata = (HashMap) s.deserialize(inbox[2]); //networkdata
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), ndata));
                                n.pruneInformation(SimulationParameters.nhopsPrune); //use nhops to prune data
                            }
                        }
                    }
                    break;
                case 1: //what happens if a node dies?
                    //System.out.println("node " + n.getVertex().getName() + " n followed agents:" + n.getResponsibleAgents());
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
            //System.out.println("recv size:" + n.getSizeMessagesRecv() + ", sent size: " + n.getSizeMessagesSent());
//            long actStopTime = System.currentTimeMillis();
//            long timeTaken = actStopTime-actStartTime;            
//            System.out.println("env age: " + this.getAge() + " node:" + n.getName() + ", round: " + n.getRounds() + ", time taken act " + timeTaken);
            available.release();
            if (getSynsetNodesReported().contains(n.getName())) {
                try {
                    synchronized (objBlock) {
                        objBlock.wait();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(NetworkEnvironmentNodeFailingMobileAgents.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getSynsetNodesReported().add(n.getName());
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
     * @param nhops number of hops
     * @param v node to load neighbour
     * @param distances //distances to a determined node
     */
    private void loadNeighboursBFS(ArrayList<MyVertex> neighbours, int nhops, MyVertex v, HashMap<MyVertex, Integer> distances) {
        int lvls = 1;
        Deque<MyVertex> q = new LinkedList<>();
        distances.put(v, 0);
        if (nhops == 0) {
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

    @Override
    /**
     * Since there are not mobile agents always return false
     */
    public boolean isOccuped(MyVertex v) {
        return false;
    }
}
