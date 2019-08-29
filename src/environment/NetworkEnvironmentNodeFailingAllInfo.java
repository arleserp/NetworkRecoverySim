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
        //long actStartTime = System.currentTimeMillis();
        if (agent instanceof Node) {                                               
            Node n = (Node) agent;
            n.incRounds();
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
                        if (SimulationParameters.activateReplication.equals("replalgon")) {                                                        
                            //message msgnodediff: connect|level|nodeid|nodetoconnect                                                            
                            if (inbox[0].equals("connect")) { 
                                String nodetoConnect = inbox[3];
                                connect(n.getVertex(), nodetoConnect);
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
                n.evaluateNodeCreation(this);
            }            
            
            //long actStopTime = System.currentTimeMillis();
            //long timeTaken = actStopTime-actStartTime;            
            //System.out.println("env age: " + this.getAge() + " node:" + n.getName() + ", round: " + n.getRounds() + ", time taken act " + timeTaken);
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
    /**
     * Since there are not mobile agents always return false
     */
    public boolean isOccuped(MyVertex v) {
        return false;
    }
}
