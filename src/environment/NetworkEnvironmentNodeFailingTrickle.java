package environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;
import edu.uci.ics.jung.graph.*;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import networkrecoverysim.SimulationParameters;
import serialization.StringSerializer;
import staticagents.NetworkNodeMessageBuffer;
import staticagents.Node;
import util.HashMapOperations;

/**
 * Nodes with failures Nodes use multicast to communicate its topology
 * information
 *
 * @author arlese.rodriguezp
 */
public class NetworkEnvironmentNodeFailingTrickle extends NetworkEnvironment {

    public NetworkEnvironmentNodeFailingTrickle(Vector<Agent> _agents, SimpleLanguage _nlanguage, Graph gr) {
        super(_agents, _nlanguage, null, gr);
    }

    @Override
    public boolean isOccuped(MyVertex v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof Node) {
            Node n = (Node) agent;
            try {
                available.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentNodeFailingAllInfo.class.getName()).log(Level.SEVERE, null, ex);
            }

            String act = action.getCode();
            n.trickleT = n.getTrickleAlg().getT(n.trickleInterval[0], n.trickleInterval[1]);

            //https://www.researchgate.net/publication/318344774_Development_of_Thread-compatible_Open_Source_Stack/figures?lo=1
            //Runnable sendData = () -> {
            //Step 4. Trickle Simple send protocol 
            if (n.getRounds() % n.trickleT == 0 && n.getTrickleAlg().check()) {
                System.out.println("Trickle [" + n.trickleInterval[0] + " ," + n.trickleInterval[1] + ", " + n.trickleT + "]" + "-" + n.getTrickleAlg().getCounter() + "-" + n.getName() + "-" + n.getRounds());
                //System.out.println("neeeeeeeeeeeeeeeeeeeeeeeeew");
                ArrayList<String> topologyDatas = new ArrayList(this.getTopologyNames(n.getVertex()));
                for (String neigbour : topologyDatas) {
                    String[] msgnet = new String[3];
                    msgnet[0] = "networkdata";
                    msgnet[1] = String.valueOf(n.getVertex().getName());
                    StringSerializer s = new StringSerializer();
                    msgnet[2] = s.serialize(n.getNetworkdata());
                    double msgnetSize = getMessageSize(msgnet);
                    n.increaseMessagesSentByRound(msgnetSize, 1);

                    if (!NetworkNodeMessageBuffer.getInstance().putMessage(neigbour, msgnet)) {
                        System.out.println("node is down: " + neigbour);
                    }
                }
            }

            //1. process messages from an agent
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {

                        //receives data
                        if (inbox[0].equals("networkdata")) {
                            double inboxSize = getMessageSize(inbox);
                            //increase number of messages received by round
                            n.increaseMessagesRecvByRound(inboxSize, 1);
                            //completes and updates data
                            StringSerializer s = new StringSerializer();
                            HashMap<String, ArrayList> recvData = (HashMap<String, ArrayList>) s.deserialize(inbox[2]);

                            //Prune data
                            if (SimulationParameters.simMode.equals("trickleP")) {
                                recvData = n.pruneReceivedData(SimulationParameters.nhopsPrune, recvData);
                            }

                            HashMap<String, ArrayList> localData = n.getNetworkdata();

                            n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));

                            //inconsistency detected step 6 trickle
                            //if (!HashMapOperations.calculateDifference(n.getNetworkdata(), localData).isEmpty()) { comment Jan 27
                            if (SimulationParameters.simMode.equals("trickleP") && HashMapOperations.isContained(recvData, localData)) {
                                //step 3: Whenever Trickle hears a transmission that is "consistent", it increments the counter c.
                                //System.out.println("is contained!");
                                n.getTrickleAlg().incr();
                            } else if (HashMapOperations.isContained(recvData, localData) && HashMapOperations.isContained(localData, recvData)) {
                                //step 3: Whenever Trickle hears a transmission that is "consistent", it increments the counter c.
                                //System.out.println("is contained!");
                                n.getTrickleAlg().incr();
                            } else {
                                n.trickleInterval = n.getTrickleAlg().reset();
                            }
                            //double sizeRecv = inbox[0].length() + inbox[1].length() + inbox[2].length();

                        }
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

                            if (inbox[0].equals("networkdatanode")) { //receive topology data from mobile agent or node
                                double inboxSize = getMessageSize(inbox);
                                if (inbox.length < 4) { // received by other node
                                    n.increaseMessagesRecvByRound(inboxSize, 1);
                                }
                                String source = inbox[1]; //origin of message
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> recvData = (HashMap) s.deserialize(inbox[2]); //networkdata
                                //Prune data
                                if (SimulationParameters.simMode.equals("trickleP")) {
                                    recvData = n.pruneReceivedData(SimulationParameters.nhopsPrune, recvData);
                                }
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));
                            }
                        }
                    }
                    break;
                case 1: //what happens if a node dies?
                    KillNode(n);
                    break;
                default:
                    System.out.println("action not specified");
            }
            if (n.status != Action.DIE) {
                if (SimulationParameters.activateReplication.equals("replalgon")) {
                    n.evaluateNodeCreation(this);
                }
            }
            if (n.getRounds() % n.trickleInterval[1] == 0) {
                n.trickleInterval = n.getTrickleAlg().iExpired();
            }
            // long end = System.currentTimeMillis();
            //System.out.println("node n " + (end-start)  + " age " + n.getRounds());
            available.release();
            //if (getSynsetNodesReported().contains(n.getName())) {
            getSynsetNodesReported().add(n.getName());
            try {
                synchronized (objBlock) {
                    objBlock.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentNodeFailingMobileAgents.class.getName()).log(Level.SEVERE, null, ex);
            }
            //}

        }
        return false;
    }

}
