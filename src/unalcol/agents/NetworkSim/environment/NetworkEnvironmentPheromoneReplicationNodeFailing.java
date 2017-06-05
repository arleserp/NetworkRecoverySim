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
import java.util.concurrent.ConcurrentHashMap;
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
    private static final List<Node> nodes = Collections.synchronizedList(new ArrayList());
    //private static ConcurrentHashMap<String, GraphElements.MyVertex> mapVertex;
    public List<MobileAgent> agentsAlive = Collections.synchronizedList(new ArrayList());
    int[][] adyacenceMatrix;
    HashMap<String, Integer> nametoAdyLocation = new HashMap<>();
    HashMap<Integer, String> locationtoVertexName = new HashMap<>();

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
        int size = gr.getVertexCount();
        adyacenceMatrix = new int[size][size];
        //mapVertex =  new ConcurrentHashMap<>();T
        ArrayList<GraphElements.MyVertex> av = new ArrayList<>(gr.getVertices());
        Collections.sort(av, new CustomComparator());
        Iterator<GraphElements.MyVertex> it = av.iterator();
        //HashMap<String, GraphElements.MyVertex> namesB = new HashMap<>();

        int i = 0;
        while (it.hasNext()) {
            GraphElements.MyVertex va = it.next();
            nametoAdyLocation.put(va.getName(), i);
            locationtoVertexName.put(i, va.getName());
            i++;
        }
        System.out.println("ba" + nametoAdyLocation);
        for (int k = 0; k < adyacenceMatrix.length; k++) {
            for (int l = 0; l < adyacenceMatrix.length; l++) {
                adyacenceMatrix[k][l] = 0;
                adyacenceMatrix[k][l] = 0;
            }
        }

        it = gr.getVertices().iterator();
        while (it.hasNext()) {
            GraphElements.MyVertex va = it.next();
            ArrayList<GraphElements.MyVertex> na = new ArrayList<>(gr.getNeighbors(va));
            Iterator<GraphElements.MyVertex> itn = na.iterator();
            while (itn.hasNext()) {
                adyacenceMatrix[nametoAdyLocation.get(va.getName())][nametoAdyLocation.get(itn.next().getName())] = 1;
            }
        }

    }

    GraphElements.MyVertex findVertex(String nodename) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            GraphElements.MyVertex v = null;
            synchronized (nodes) {
                Iterator<Node> itr = nodes.iterator();
                while (itr.hasNext()) {
                    Node n = itr.next();
                    if (n.getVertex().getName().equals(nodename) && !n.getVertex().getStatus().equals("failed")) {
                        return n.getVertex();
                    }
                }
            }
            return v;
        }
    }

    private void connect(GraphElements.MyVertex vertex, String nodetoConnect) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            GraphElements.MyVertex nodeTo = findVertex(nodetoConnect);
            if (nodeTo != null) {
                if (!getTopology().isNeighbor(vertex, nodeTo)) {
                    if (getTopology().containsEdge("e" + vertex.getName() + nodeTo.getName())) {
                        System.out.println("creating extra name while cleaning vertex");
                        getTopology().addEdge("eb" + vertex.getName() + nodeTo.getName(), vertex, nodeTo);
                    } else {
                        getTopology().addEdge("e" + vertex.getName() + nodeTo.getName(), vertex, nodeTo);
                    }
                }
                adyacenceMatrix[nametoAdyLocation.get(vertex.getName())][nametoAdyLocation.get(nodetoConnect)] = 1;
                adyacenceMatrix[nametoAdyLocation.get(nodetoConnect)][nametoAdyLocation.get(vertex.getName())] = 1;
            } else {
                System.out.println("node to connect is null:" + nodetoConnect);
            }
        }
    }

    public void addNodes(List<Node> nds) {
        synchronized (nodes) {
            for (Node n : nds) {
                nodes.add(n);
            }
        }
    }

    private void createnewAgent(Node n, int fatherId) {
        AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
        int newAgentID = agents.size();
        MobileAgent a = new MobileAgent(program, newAgentID);

        System.out.println("creating agent id: " + newAgentID + ", father id:" + fatherId);
        NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

        a.setId(newAgentID);
        a.setIdFather(fatherId);
        a.setData(new ArrayList(n.getVertex().getData()));
        a.setRound(super.getAge());
        this.agents.add(a);
        a.live();
        Thread t = new Thread(a);
        a.setThread(t);
        a.setLocation(n.getVertex());
        //a.setPrevLocation(n.getVertex());
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

    private void createNewAgents(Integer number, Node n, int fatherId) {
        System.out.println("create new " + number + " agents in node" + n.getVertex().getName());
        for (int i = 0; i < number; i++) {
            createnewAgent(n, fatherId);
        }
    }

    void addConnection(GraphElements.MyVertex dvertex, Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            if (getTopology().containsVertex(dvertex) && getTopology().containsVertex(n.getVertex()) && !getTopology().isNeighbor(dvertex, n.getVertex())) {
                // if (topology.containsEdge("e" + dvertex.getName() + n.getVertex().getName())) {
                //      topology.removeEdge("e" + dvertex.getName() + n.getVertex().getName());
                // }
                if (getTopology().containsEdge("e" + dvertex + n.getVertex().getName())) {
                    System.out.println("creating extra name while cleaning vertex");
                    getTopology().addEdge("eb" + dvertex.getName() + n.getVertex().getName(), n.getVertex(), n.getVertex());
                } else {
                    getTopology().addEdge("e" + dvertex.getName() + n.getVertex().getName(), dvertex, n.getVertex());
                }
            }
            adyacenceMatrix[nametoAdyLocation.get(n.getVertex().getName())][nametoAdyLocation.get(dvertex.getName())] = 1;
            adyacenceMatrix[nametoAdyLocation.get(dvertex.getName())][nametoAdyLocation.get(n.getVertex().getName())] = 1;
        }
    }

    void createNewNode(Node n, String d) {
        System.out.print("creating new node " + d);
        GraphElements.MyVertex dvertex = findVertex(d);
        if (dvertex != null) { //can ping node and discover
            System.out.println("Node " + d + " is alive connecting instead create...[" + dvertex + "," + n.getVertex() + "]");
            addConnection(dvertex, n);
        } else {
            GraphCreator.VertexFactory v = new GraphCreator.VertexFactory();
            //GraphCreator.EdgeFactory e = new GraphCreator.EdgeFactory();
            //System.out.println(n.getVertex().getName() + " is creating " + d + ":");
            GraphElements.MyVertex vv = v.create();
            vv.setName(d);
            vv.setStatus("alive");
            // vv.setPh(0.5F);
            synchronized (TopologySingleton.getInstance()) {
                getTopology().addVertex(vv);
            }
            //if (topology.containsEdge("e" + vv.getName() + n.getVertex().getName())) {
            //     topology.removeEdge("e" + vv.getName() + n.getVertex().getName());
            //}
            addConnection(vv, n);
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
            //nodes.add(nod);
            nodes.add(nod);
            t.start();

            //System.out.println("adding data to node" + nod.getVertex().getName() + ":" + n.getNetworkdata());
            nod.setNetworkdata(new HashMap(n.getNetworkdata()));
            //System.out.println("adding data to node" + nod.getVertex().getName() + ":" + n.getRespAgentsBkp());
            nod.setAgentsInNeighbors(new HashMap(n.getAgentsInNeighbors()));
            nod.getAgentsInNeighbors().remove(d);
            if (n.getAgentsInNeighbors().containsKey(d)) {
                System.out.println("creating " + n.getAgentsInNeighbors().get(d).size() + " agents in node:" + nod.getVertex().getName());
                ConcurrentHashMap<Integer, Integer> i = n.getAgentsInNeighbors().get(d);
                Iterator<Integer> it = i.keySet().iterator();
                while (it.hasNext()) {
                    int id = it.next();
                    if (i.get(id) == -1) {
                        createnewAgent(nod, id);
                    } else {
                        createnewAgent(nod, i.get(id));
                    }
                }
            }
            /* nod.setRespAgentsBkp(new HashMap(n.getRespAgentsBkp()));
           nod.getRespAgentsBkp().remove(d);
            if (n.getRespAgentsBkp().containsKey(d)) {
               int i = n.getRespAgentsBkp().get(d);
                System.out.println("in node" + d + " creating " + i + " agents...");
                //rule to create new agents???
                createNewAgents(i, nod, -1);
            }*/
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

    private void killAgent(MobileAgent a, boolean isMyself) {
        System.out.println("kill agent " + a.getId() + " is myself: " + isMyself);
        increaseAgentsDie();
        agentsAlive.remove(a);
        a.die();
        a.setLocation(null);
        setChanged();
        notifyObservers();
        //System.out.println(" agents alive after removal " + agentsAlive.size());
        if (!isMyself) {
            try {
                a.getThread().join();
                //a.setLocation(null);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkEnvironmentPheromoneReplicationNodeFailing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean removeVertex(GraphElements.MyVertex vertex) {
        synchronized (TopologySingleton.getInstance()) {
            //copy to avoid concurrent modification in removeEdge
            //Set<String> incident = new HashSet<>(topology.getInEdges(vertex));
            //incident.addAll(topology.getOutEdges(vertex));
            //for (String edge : incident) {
            //    topology.removeEdge(edge);
            //}
            for (int i = 0; i < adyacenceMatrix.length; i++) {

                //System.out.println("name" + nametoAdyLocation.get(vertex.getName()) + "v:" + vertex);
                adyacenceMatrix[nametoAdyLocation.get(vertex.getName())][i] = 0;
                adyacenceMatrix[i][nametoAdyLocation.get(vertex.getName())] = 0;
            }
            if (!getTopology().removeVertex(vertex)) {
                System.out.println("cannot remove vertex " + vertex);
            }
            return true;
        }
    }

    /*  public ArrayList<MobileAgent> getAgentsInNode(Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            ArrayList<MobileAgent> agtoDel = new ArrayList();
            Iterator<MobileAgent> it = agentsAlive.iterator();
            while (it.hasNext()) {
                MobileAgent a = it.next();
                if (a.getLocation().getName().equals(n.getVertex().getName())) {
                    agtoDel.add(a);
                }
            }
            return agtoDel;
        }
    }
     */
    private void KillNode(Node n) {
        System.out.println("Node " + n.getVertex().getName() + " has failed." + " resp agents:" + n.getAgentCount());
        //System.out.println("killed: " + killAgentsInlocation(n.getVertex().getName()));
        NetworkNodeMessageBuffer.getInstance().deleteBuffer(n.getVertex().getName());
        //System.out.println("node fail?");
        //Send kill signal to agents
        Iterator<Integer> it = n.getAgentsInNode().keySet().iterator();//getAgentsInNode(n).iterator();
        while (it.hasNext()) {
            MobileAgent i = getMobileAgent(it.next());
            if (i != null) {
                System.out.println("node n" + n.getVertex().getName() + " is removing" + i.getId());
                killAgent(i, false);
            }
        }
        //not sure if this is necessary

        synchronized (TopologySingleton.getInstance()) {
            if (nodes.remove(n)) {
                System.out.println("removed: " + n.getVertex().getName());
            }
            removeVertex(n.getVertex());
        }
        n.getVertex().setName(n.getVertex().getName() + " failed");
        n.getVertex().setStatus("failed");
        n.die();
        setChanged();
        notifyObservers();
    }

    private void removeAgent(int id) {
        MobileAgent a = getMobileAgent(id);
        if (a != null) {
            killAgent(a, false);
        }
    }

    private MobileAgent getMobileAgent(int id) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<MobileAgent> itr = agentsAlive.iterator();
            while (itr.hasNext()) {
                MobileAgent tmp = itr.next();
                if (tmp.getId() == id) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public void validateNodesAlive() {
        synchronized (nodes) {
            Iterator<Node> it = nodes.iterator();
            while (it.hasNext()) {
                Node t = it.next();
                if (t.getVertex().getStatus().equals("failed")) {
                    System.out.println("wat???" + t.getVertex().getName());
                }
            }

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
        for (int i = 0; i < adyacenceMatrix.length; i++) {
            if (adyacenceMatrix[nametoAdyLocation.get(node.getName())][i] == 1) {
                names.add(locationtoVertexName.get(i));
            }
        }
        /*
        List<String> names = new ArrayList();
        if (getTopology().containsVertex(node)) {
            synchronized (TopologySingleton.getInstance()) {
                List<GraphElements.MyVertex> list = new ArrayList<>(getTopology().getNeighbors(node));
                Iterator<GraphElements.MyVertex> itr = list.iterator();
                while (itr.hasNext()) {
                    names.add(itr.next().getName());
                }
            }
        }*/
        return names;
    }

    Node getNode(String name) {
        //System.out.print("getNode" + name + ":");
        //ArrayList<Node> copy = new ArrayList(nodes);
        //System.out.println("getNode nodes size " + nodes.size());
        if (name == null) {
            return null;
        }
        synchronized (nodes) {
            for (Node nod : nodes) {
                if (nod.status != Action.DIE && nod.getVertex().getName().equals(name) && getTopology().containsVertex(nod.getVertex())) {
                    return nod;
                }
            }
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

    public class CustomComparatorNodeN implements Comparator<Node> {

        String neig;

        public CustomComparatorNodeN(String n) {
            neig = n;
        }

        @Override
        public int compare(Node f1, Node f2) {
            int n1, n2;
            if (!f1.getNetworkdata().containsKey(neig)) {
                n1 = 0;
            } else {
                n1 = f1.getNetworkdata().get(neig).size();
            }
            if (!f2.getNetworkdata().containsKey(neig)) {
                n2 = 0;
            } else {
                n2 = f2.getNetworkdata().get(neig).size();
            }
            if (n1 == n2) {
                return 0;
            } else if (n1 > n2) {
                return 1;
            }
            return -1;
        }

    }

    public String getMinimumId(List<String> neigdiff) {
        List<String> nodesAlive = new ArrayList();
        for (String s : neigdiff) {
            if (getNode(s) != null) {
                nodesAlive.add(s);
            }
        }
        if (nodesAlive.isEmpty()) {
            return "none";
        }
        String min = Collections.min(nodesAlive, new CustomComparatorNode());
        return min;
    }

    public String getMoreInfoId(List<String> neigdiff, String neig) {
        List<Node> nodesAlive = new ArrayList();
        for (String s : neigdiff) {
            Node x = getNode(s);
            if (x != null) {
                nodesAlive.add(x);
            }
        }
        Node maxN = Collections.max(nodesAlive, new CustomComparatorNodeN(neig));
        return maxN.getVertex().getName();
    }

    public ArrayList<MobileAgent> getRepeatedAgents(MobileAgent x) {
        ArrayList<MobileAgent> todelete = new ArrayList<>();
        synchronized (NetworkEnvironmentReplication.class) {
            for (MobileAgent a : agentsAlive) {
                if ((a.getId() != x.getId() && a.getIdFather() == x.getId()) && x.getLocation() != null && x.getLocation().equals(a.getLocation())) {
                    if (a.getId() > x.getId()) {
                        todelete.add(a);
                    }
                }
            }
        }
        return todelete;
    }

    @Override
    public Percept sense(Agent agent) {
        Percept p = new Percept();
        if (agent instanceof MobileAgent) {
            MobileAgent a = (MobileAgent) agent;
            try {
                //Validate that agent is not death 
                //System.out.println("a location" + a.getLocation() + "status" + a.getLocation().getStatus());
                if (a.status != Action.DIE && !a.getLocation().getStatus().equals("failed")) {
                    p.setAttribute("neighbors", getTopology().getNeighbors(a.getLocation()));
                    if (a.status != Action.DIE) {
                        a.getData().removeAll(a.getLocation().getData());
                        a.getData().addAll(a.getLocation().getData());
                        a.getLocation().saveAgentInfo(a.getData(), a.getId(), a.getRound(), getAge());
                    }
                    //    throw new NullPointerException("Agent arrived to a node recently death killing a: " + a.getId() + " father:" + a.getIdFather() + " " + a.getLocation() + " " + ex.getLocalizedMessage());
                    //}
                } else {
                    p.setAttribute("nodedeath", true);

                }
            } catch (NullPointerException ex) {
                System.out.println("Killing agent sensing null node: " + a.getId());
                killAgent(a, true);
            }
        }
        return p;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof MobileAgent) {
            agent.sleep(50);
            boolean flag = (action != null);
            MobileAgent a = (MobileAgent) agent;

            try {
                if (a.status == Action.DIE) {
                    System.out.println("death: " + a.getId());
                    return false;
                }
                //System.out.println("1.");
                String act = action.getCode();

                if (SimulationParameters.activateReplication.equals("replalgon")) {
                    if (a.getPrevLocation() != null) {
                        String[] msgnoder = new String[3];
                        msgnoder[0] = "freeresp";
                        msgnoder[1] = String.valueOf(a.getId());
                        msgnoder[2] = a.getLocation().getName();
                        NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), msgnoder);
                    }
                }

                if (language.getActionIndex(act) == 2) {
                    System.out.println("informfailure" + a.getId());
                    setChanged();
                    notifyObservers();
                    return false;
                } else {
                    if (a.status == Action.DIE) {
                        System.out.println("death before send freeresp:" + a.getId());
                        return false;
                    }

                    currentNode = a.getLocation();
                    //System.out.println("2.");
                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                        /*if (a.status == Action.DIE) {
                            System.out.println("death before getNode" + a.getId());
                            return false;
                        }*/

                        // Here is when agents die and failure is not detected!
                        /*if (a.getLocation().getStatus().equals("failed")) {
                            System.out.println("Kill a id: " + a.getId() + " location failed " + a.getLocation());
                            killAgent(a, true);
                            return false;
                        }*/
                        Node c = getNode(a.getLocation().getName());
                        HashMap<String, Object> nodeNet = new HashMap<>();
                        nodeNet.put(a.getLocation().getName(), getTopologyNames(a.getLocation()));
                        HashMap<String, Integer> agentsinNodeNet = new HashMap<>();
                        agentsinNodeNet.put(c.getVertex().getName(), c.getAgentCount());

                        HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsinNodeNetMap = new HashMap<>();
                        agentsinNodeNetMap.put(c.getVertex().getName(), c.getAgentsInNode());
                        a.getLocalAgentsInNetworkHmap().add(agentsinNodeNetMap);
                        a.getLocalAgentsInNetwork().add(agentsinNodeNet);
                        a.getLocalNetwork().add(nodeNet);

                        //}
                        //let only nhops as local information by agent
                        if (a.getLocalNetwork().size() > SimulationParameters.nhops) {
                            List nl = new ArrayList(a.getLocalNetwork().subList(a.getLocalNetwork().size() - SimulationParameters.nhops, a.getLocalNetwork().size()));
                            a.setLocalNetwork(nl);
                        }

                        if (a.getLocalAgentsInNetwork().size() > SimulationParameters.nhops) {
                            List aln = new ArrayList(a.getLocalAgentsInNetwork().subList(a.getLocalAgentsInNetwork().size() - SimulationParameters.nhops, a.getLocalAgentsInNetwork().size()));
                            a.setLocalAgentsInNetwork(aln);
                        }

                        if (a.getLocalAgentsInNetwork().size() > SimulationParameters.nhops) {
                            List aln2 = new ArrayList(a.getLocalAgentsInNetwork().subList(a.getLocalAgentsInNetworkHmap().size() - SimulationParameters.nhops, a.getLocalAgentsInNetworkHmap().size()));
                            a.setLocalAgentsInNetworkHmap(aln2);
                        }

                        //Sh Send neighbour data to node ex in HashMap format: {p51={p21, p22, p23}, p22={p1, p2}}
                        if (a.status == Action.DIE) {
                            System.out.println("death before network data" + a.getId());
                            return false;
                        }

                        String[] msgnet = new String[5];
                        msgnet[0] = "networkdata";
                        msgnet[1] = String.valueOf(a.getId());
                        StringSerializer s = new StringSerializer();
                        msgnet[2] = s.serialize(a.getLocalAgentsInNetwork());
                        msgnet[3] = s.serialize(a.getLocalNetwork());
                        msgnet[4] = s.serialize(a.getLocalAgentsInNetworkHmap());
                        if (a.status == Action.DIE) {
                            System.out.println("death before network data" + a.getId());
                            return false;
                        }
                        NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnet);
                    }

                    if (a.status == Action.DIE) {
                        System.out.println("death before put data in node" + a.getId());
                        return false;
                    }
                    //getLocationAgents().put(a, a.getLocation());
                    ActionParameters ac = (ActionParameters) action;

                    if (a.status == Action.DIE) {
                        System.out.println("death before getData" + a.getId());
                        return false;
                    }
                    synchronized (a.getLocation().getData()) {
                        //Get data from agent and put information in node
                        for (Object data : a.getData()) {
                            if (a.status == Action.DIE) {
                                System.out.println("death before getData" + a.getId());
                                return false;
                            }
                            if (!a.getLocation().getData().contains(data)) {
                                a.getLocation().getData().add(data);
                                a.setPheromone(1.0f); //??? maybe this is not here
                            }
                        }
                    }
                    if (flag) {
                        //String act = action.getCode();
                        String msg = null;

                        if (a.status == Action.DIE) {
                            System.out.println("death before switch" + a.getId());
                            return false;
                        }
                        /**
                         * 0- "move"
                         */
                        /* @TODO: Detect Stop Conditions for the algorithm */
                        switch (language.getActionIndex(act)) {
                            case 0: // move
                                //System.out.println("a despues" + a.getLocation());
                                boolean complete = false;
                                if (a.getData().size() == getTopology().getVertexCount()) {
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

                                if (v.getStatus().equals("failed")) {
                                    System.out.println("agent " + a.getId() + ", ojo! aca no me puedo mover ni enviar departing!" + v.getName());
                                    return false;
                                } else //Send a message to current node before moving to new destination v
                                //msgnode: "departing"|agentId|FatherId|newDest
                                {
                                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        String[] msgnode = new String[4];
                                        msgnode[0] = "departing";
                                        msgnode[1] = String.valueOf(a.getId());
                                        msgnode[2] = String.valueOf(a.getIdFather());
                                        msgnode[3] = v.getName();
                                        NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                                    }
                                }
                                //Agent Fail when moving
                                if (Math.random() < pf) {
                                    //System.out.println("Agent " + a.getId() + "has failed");
                                    killAgent(a, true);
                                    return false;
                                }
                                if (!SimulationParameters.nodeDelay.equals("NODELAY")) {
                                    int nodeDelay = Integer.valueOf(SimulationParameters.nodeDelay);
                                    a.sleep(nodeDelay);
                                }

                                if (a.status == Action.DIE) {
                                    System.out.println("death before send arrive and move" + a.getId());
                                    return false;
                                }
                                //Move agent to a location
                                //Send message arrived to node arrived|id|getPrevLocation
                                if (v.getStatus().equals("failed")) {
                                    System.out.println("ojo! aca no me puedo mover!");
                                    return false;
                                } else {
                                    a.setPrevLocation(a.getLocation());
                                    a.setLocation(v);
                                    a.getLocation().setStatus("visited");
                                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        String[] msgarrived = new String[3];
                                        msgarrived[0] = "arrived";
                                        msgarrived[1] = String.valueOf(a.getId());
                                        msgarrived[2] = String.valueOf(a.getIdFather());
                                        if (NetworkNodeMessageBuffer.getInstance().putMessage(v.getName(), msgarrived) == false) {
                                            System.out.println("Agent" + a.getId() + " cannot move to node " + v.getName());
                                            killAgent(a, true);
                                            return false;
                                        }
                                    }
                                    //agent.sleep(50);
                                    // getLocationAgents().put(a, a.getLocation());
                                    a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                                    // System.out.println("a" + a.getId() + " pheromone " + a.getPheromone());
                                    a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));

                                    a.setRound(a.getRound() + 1);
                                    incrementAgentMovements();

                                    if (a.status == Action.DIE) {
                                        System.out.println("death before send freeresp:" + a.getId());
                                        return false;
                                    }

                                    /*if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        if (a.getPrevLocation() != null) {
                                            String[] msgnoder = new String[3];
                                            msgnoder[0] = "freeresp";
                                            msgnoder[1] = String.valueOf(a.getId());
                                            msgnoder[2] = a.getLocation().getName();
                                            NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), msgnoder);
                                        }
                                    }*/
                                    currentNode = v;
                                }
                                //visitedNodes.add(currentNode);
                                break;
                            case 1: //die
                                System.out.println("action: kill ");
                                killAgent(a, true);
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
            } catch (NullPointerException ex) {
                System.out.println("Killing agent by null exception: " + a.getId() + " message: ");
                //ex.printStackTrace();
                killAgent(a, true);
                return false;
            }
        }
        if (agent instanceof Node) {
            agent.sleep(50);
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
                                int father = Integer.valueOf(inbox[2]);
                                //n.setLastAgentArrive(agentId, n.getRounds());
                                //n.incMsgRecv();
                                //n.getResponsibleAgentsArrival().put(agentId, n.getRounds());
                                //n.calculateTimeoutArrival();
                                n.addAgentInNode(agentId, father);
                                n.incrementAgentCount();
                                ArrayList<Integer> repeatedAgents = n.getDuplicatedAgents();
                                for (int id : repeatedAgents) {
                                    removeAgent(id);
                                }
                                //System.out.println("Agents in node: " + n.getAgentsInNode());
                                //System.out.println(n.getVertex().getName() + "arrived:" + agentId);
                            }
                            //msgnode: "departing"|agentId|FatherId|newDest
                            if (inbox[0].equals("departing")) {
                                int agentId = Integer.valueOf(inbox[1]);
                                int father = Integer.valueOf(inbox[2]);
                                n.setLastAgentDeparting(agentId, n.getRounds());
                                
                                n.incMsgRecv();
                                n.getResponsibleAgents().put(agentId, father);
                                n.getResponsibleAgentsLocation().put(agentId, inbox[3]);
                                n.calculateTimeout();
                                // System.out.println(n.getVertex().getName() + "departing:" + agentId);
                                //n.calculateTimeoutArrival();
                                //if (n.getResponsibleAgentsArrival().containsKey(agentId)) {
                                //    n.getResponsibleAgentsArrival().remove(agentId);
                                //}else {
                                //System.out.println("delete!!!!");
                                //   incrementFalsePossitives();
                                //deleteNextReplica(n);
                                //}
                                //evaluateAgentCreationArrival(n);
                                n.decrementAgentCount();
                                n.deleteAgentInNode(agentId);

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
                                    //deleteNextReplica(n);
                                }
                            }

                            if (inbox[0].equals("networkdata")) {
                                //completes and updates data
                                StringSerializer s = new StringSerializer();
                                ArrayList<HashMap<String, Integer>> agentList = (ArrayList<HashMap<String, Integer>>) s.deserialize(inbox[2]);
                                Iterator<HashMap<String, Integer>> itr = agentList.iterator();

                                while (itr.hasNext()) {
                                    HashMap<String, Integer> agentBkp = itr.next();
                                    //Maybe update this?
                                    for (String key : agentBkp.keySet()) {
                                        n.getRespAgentsBkp().put(key, agentBkp.get(key));
                                        //System.out.println("node " + n.getVertex().getName() + ", Responsible Agents Info:" + n.getRespAgentsBkp());
                                    }
                                }
                                StringSerializer sx = new StringSerializer();
                                ArrayList<HashMap<String, ConcurrentHashMap<Integer, Integer>>> agentInNeigborsList = (ArrayList<HashMap<String, ConcurrentHashMap<Integer, Integer>>>) s.deserialize(inbox[4]);
                                Iterator<HashMap<String, ConcurrentHashMap<Integer, Integer>>> itr2 = agentInNeigborsList.iterator();

                                while (itr2.hasNext()) {
                                    HashMap<String, ConcurrentHashMap<Integer, Integer>> agentBkp = itr2.next();
                                    //Maybe update this?
                                    for (String key : agentBkp.keySet()) {
                                        n.getAgentsInNeighbors().put(key, agentBkp.get(key));
                                        //System.out.println("node " + n.getVertex().getName() + ", Responsible Agents Info:" + n.getRespAgentsBkp());
                                    }
                                }

                                //System.out.println("res" + n.getRespAgentsBkp());
                                StringSerializer ss = new StringSerializer();
                                ArrayList<HashMap> agentLoc = (ArrayList) ss.deserialize(inbox[3]);
                                //System.out.print(n.getVertex().getName() + " n antes" + n.getNetworkdata());
                                for (HashMap< String, ArrayList> agentData : agentLoc) {
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
                    }
                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                        n.calculateTimeout();
                        evaluateAgentCreation(n);
                        // n.calculateTimeoutArrival();
                        //evaluateAgentCreationArrival(n);
                    }
                    break;
                case 1: //what happens if a node dies?
                    System.out.println("node " + n.getVertex().getName() + " n followed agents:" + n.getResponsibleAgents());
                    KillNode(n);
                    /*if (n.getRounds() > 100) {
                        KillNode(n);
                    } else {
                        System.out.println("not die for first 100 rounds");
                    }*/
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
                //evaporate pheromone
                n.getVertex().setPh((n.getVertex().getPh() - n.getVertex().getPh() * 0.001f));
            }
        }
        return false;
    }

    public void evaporatePheromone() {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<Node> itr = nodes.iterator();
            //ArrayList<GraphElements.MyVertex> evaporateClone = new ArrayList(topology.getVertices());
            while (itr.hasNext()) {
                //System.out.println(v.toString() + "before:" + v.getPh());
                GraphElements.MyVertex v = itr.next().getVertex();
                v.setPh(v.getPh() - v.getPh() * 0.001f);
                //System.out.println(v.toString() + "after:" + v.getPh());
            }
        }
    }


    //Example: It is better handshake protocol. J. Gomez
    public void evaluateAgentCreation(Node n) {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailing.class) {
            Iterator<Map.Entry<Integer, Integer>> iter = n.getResponsibleAgents().entrySet().iterator();
            Iterator<Map.Entry<Integer, String>> iterLoc = n.getResponsibleAgentsLocation().entrySet().iterator();
            /*if (!n.getResponsibleAgents().isEmpty()) {
                System.out.println("evaluateAgentCreation node" + n.getVertex().getName() + " is responsible for: " + n.getResponsibleAgents());
            }*/
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

                    if (n.getResponsibleAgents().get(k) == -1) {
                        createNewAgents(1, n, k);
                    } else {
                        createNewAgents(1, n, n.getResponsibleAgents().get(k));
                    }

                    //System.out.println("Get 0" + );
/*                    AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);

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
                    t.start();*/
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

    @Override
    public int getAgentsAlive() {
        return agentsAlive.size();
    }

    @Override
    public int getNodesAlive() {
        return nodes.size();
    }

}
