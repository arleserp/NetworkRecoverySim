package environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;
import edu.uci.ics.jung.graph.*;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
public class NetworkEnvironmentNodeFailingMulticast extends NetworkEnvironment {


    public NetworkEnvironmentNodeFailingMulticast(Vector<Agent> _agents, SimpleLanguage _nlanguage, Graph gr) {
        super(_agents, _nlanguage, gr);
    }

    @Override
    public boolean isOccuped(MyVertex v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Percept sense(Agent agent) {
        Percept p = new Percept();
        return p;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof Node) {
            agent.sleep(50);
            Node n = (Node) agent;

            n.incRounds();

            String act = action.getCode();
            
            //Simple send protocol            
            ArrayList<String> topologyDatas = new ArrayList(this.getTopologyNames(n.getVertex()));
            
            for (String neigbour : topologyDatas) {
                String[] msgnet = new String[5];
                msgnet[0] = "networkdata";
                msgnet[1] = String.valueOf(n.getVertex().getName());
                StringSerializer s = new StringSerializer();
                msgnet[2] = s.serialize(n.getNetworkdata());
                if (!NetworkNodeMessageBuffer.getInstance().putMessage(neigbour, msgnet)) {
                    System.out.println("node is down: " + neigbour);
                }
            }

            //1. process messages from an agent
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        if (SimulationParameters.activateReplication.equals("replalgon")) {
                            //receives data
                            if (inbox[0].equals("networkdata")) {
                                //completes and updates data
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> recvData = (HashMap<String, ArrayList>) s.deserialize(inbox[2]);
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));
                                n.incMsgRecv();
                            }
                            if (inbox[0].equals("connect")) {
                                //message msgnodediff: connect|level|nodeid|nodetoconnect
                                int level = Integer.valueOf(inbox[1]);
                                //String ndet = String.valueOf(inbox[2]);
                                String nodetoConnect = inbox[3];
                                connect(n.getVertex(), nodetoConnect);
                            }
                        }
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
                ArrayList<String> topologyData = new ArrayList(this.getTopologyNames(n.getVertex())); // Get topology of the network
                
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
                                //min = getMoreInfoId(neigdiff, d);
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

}
