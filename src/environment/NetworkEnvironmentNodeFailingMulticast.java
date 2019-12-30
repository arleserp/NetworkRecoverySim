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

                                double sizeRecv = inbox[0].length() + inbox[1].length() + inbox[2].length();

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
                addLocalConsumptionNode(n);
            }
        }
        return false;
    }

}
