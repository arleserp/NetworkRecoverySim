package environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import networkrecoverysim.SimulationParameters;
import serialization.StringSerializer;
import staticagents.NetworkNodeMessageBuffer;
import staticagents.Node;
import util.HashMapOperations;

/**
 * This class defines an environment with the following assumption No mobile
 * agents only nodes nodes have information about the entire topology or partial
 * information given in terms of hops
 * @author arlese.rodriguezp
 */
public class NetworkEnvironmentNodeFailingAllInfo extends NetworkEnvironment {

    public NetworkEnvironmentNodeFailingAllInfo(Vector<Agent> _agents, SimpleLanguage _nlanguage, Graph gr) {
        super(_agents, _nlanguage, gr);
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

            //This part is primitive send to neigbors 
            String act = action.getCode();

            //1. process messages from others
            //System.out.println("action" + act + ", code:" + nodeLanguage.getActionIndex(act));
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {
                        if (SimulationParameters.activateReplication.equals("replalgon")) {                            
                            
                            //message networkdatanode: source | networkdata
                            //receives topological data from other node
                            if (SimulationParameters.simMode.equals("nhopsinfo") && inbox[0].equals("networkdatanode")) {
                                String source = inbox[1]; //source of message
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> ndata = (HashMap) s.deserialize(inbox[2]);
                                //System.out.println(n.getVertex().getName() + "recv networkdatanode from " + source + " nd:" + ndata);
                                //System.out.println("network data before: " + n.getNetworkdata());
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), ndata));
                                n.pruneInformation(SimulationParameters.nhops); //use nhops to prune data
                                //System.out.println(n.getVertex().getName() + "network data after: " + n.getNetworkdata());
                            }
                            
                            //receive connect message from other node
                            //message msgnodediff: connect|level|nodeid|nodetoconnect                                                            
                            if (inbox[0].equals("connect")) { 
                                //String ndet = String.valueOf(inbox[2]);
                                String nodetoConnect = inbox[3];
                                connect(n.getVertex(), nodetoConnect);

                                //When n connects to nodetoConnect n sends its network topology data
                                // if simulation mode is nhopsinfo
                                //this should be added to other experiments !
                                if (SimulationParameters.simMode.equals("nhopsinfo")) {
                                    String[] msgdatanode = new String[3];
                                    msgdatanode[0] = "networkdatanode";
                                    msgdatanode[1] = String.valueOf(n.getVertex().getName());
                                    StringSerializer s = new StringSerializer();
                                    msgdatanode[2] = s.serialize(n.getNetworkdata());
                                    NetworkNodeMessageBuffer.getInstance().putMessage(nodetoConnect, msgdatanode);
                                    System.out.println(n.getVertex().getName() + "send networkdatanode to " + nodetoConnect);
                                }
                            }
                        }
                    }
                    break;
                case 1: //what happens if a node dies?
                    //System.out.println("node " + n.getVertex().getName() + " n followed agents:" + n.getResponsibleAgents());
                    KillNode(n);
                    break;
                default:
                    System.out.println("acrtion not specified");
            }

            if (n.status != Action.DIE) {
                //System.out.println("node name" + n.getVertex().getName());
                //2. Compare topology data with cache given by other agents
                ArrayList<String> topologyData = new ArrayList(this.getTopologyNames(n.getVertex())); // Get topology of the network
                if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                    List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName())); 
                    
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
                        //System.out.println("node" + n.getVertex().getName() +" nd:" + nd + " vs  topologyData:" + topologyData);
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
                //Send topology data to others
                //evaporate pheromone
                n.getVertex().setPh((n.getVertex().getPh() - n.getVertex().getPh() * 0.001f));
            }
        }
        return false;
    }

    /**
     * Load all the topology in each node
     * @return hashMap of n_1={n_1={n_k,...n_l}, n_2={...},..., n={}}
     */
    public HashMap<String, ArrayList> loadAllTopology() {
        HashMap<String, ArrayList> networkInfo = new HashMap<>();
        for (MyVertex v : getTopology().getVertices()) {
            networkInfo.put(v.getName(), new ArrayList<>(getTopologyNames(v)));
        }
        return networkInfo;

    }

    /**
     * load neighobours using bfs
     * @param neighbours neighbours of a node 
     * @param nhopsChain number of hops
     * @param v node to load neighbour 
     * @param distances  //distances to a determined node
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
     * @param nhopsChain number of hops
     * @param n node
     * @return 
     */
    public HashMap<String, ArrayList> loadPartialNetwork(int nhopsChain, Node n) {
        HashMap<String, ArrayList> networkInfo = new HashMap<>();
        HashMap<MyVertex, Integer> distances = new HashMap<>();
        ArrayList<MyVertex> neighbours = new ArrayList<>();
        neighbours.add(n.getVertex());
        int initialPos = 0;
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
            networkInfo.put(v.getName(), new ArrayList<>(getTopologyNames(v)));
        }
        System.out.println("node" + n + "info = " + networkInfo);

        return networkInfo;
    }

    @Override
    public boolean isOccuped(MyVertex v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
