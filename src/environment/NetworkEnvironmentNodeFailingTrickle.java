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
    public Percept sense(Agent agent) {
        Percept p = new Percept();
        return p;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof Node) {
            //agent.sleep(20);
            Node n = (Node) agent;

            n.incRounds();
            System.out.println("0-"+n.trickleInterval[0]+ " 1-"+n.trickleInterval[1]);
            n.trickleT = (n.trickleInterval[0] + n.trickleInterval[1]) / 2;
            String act = action.getCode();
            //https://www.researchgate.net/publication/318344774_Development_of_Thread-compatible_Open_Source_Stack/figures?lo=1
            Runnable sendData = () -> {
                //Step 4. Trickle Simple send protocol 
                if (n.getTrickleAlg().check()) {
                    //System.out.println("neeeeeeeeeeeeeeeeeeeeeeeeew");

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
                }
            };
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
            ses.schedule(sendData, n.trickleT, TimeUnit.MILLISECONDS); //Time t send data

            Runnable iExpired = () -> {
                n.trickleInterval = n.getTrickleAlg().next();
            };
            ScheduledExecutorService ses2 = Executors.newScheduledThreadPool(1);
            ses2.schedule(iExpired, n.trickleInterval[1], TimeUnit.MILLISECONDS); // i expired reset timer

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
                                HashMap<String, ArrayList> localData = n.getNetworkdata();
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));

                                //inconsistency detected step 6 trickle
                                if (!HashMapOperations.calculateDifference(n.getNetworkdata(), localData).isEmpty()) {
                                    n.getTrickleAlg().reset();

                                } else {
                                    //step 3: Whenever Trickle hears a transmission that is "consistent", it increments the counter c.
                                    n.getTrickleAlg().incr();
                                }
                                //double sizeRecv = inbox[0].length() + inbox[1].length() + inbox[2].length();
                            }
                            if (inbox[0].equals("connect")) {
                                //message msgnodediff: connect|level|nodeid|nodetoconnect                                
                                String nodetoConnect = inbox[2];
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
                n.evaluateNodeCreation(this);
            }
        }
        return false;
    }

}
