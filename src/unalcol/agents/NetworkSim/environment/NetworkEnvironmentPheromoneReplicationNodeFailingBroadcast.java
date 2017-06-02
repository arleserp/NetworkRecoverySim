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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
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

public class NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast extends NetworkEnvironmentReplication {

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

    public NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast(Vector<Agent> _agents, SimpleLanguage _language, SimpleLanguage _nlanguage, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents, _language, gr);
        nodeLanguage = _nlanguage;

        // agentsAlive = new 
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
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class) {
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
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class) {
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
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class) {
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
                Logger.getLogger(NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class.getName()).log(Level.SEVERE, null, ex);
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
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class) {
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
        return p;
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof Node) {
            agent.sleep(50);
            Node n = (Node) agent;

            /**
             * M.raynal Protocol: Apply for computing global functions, however
             * close the channel if no new information is received so it does
             * not apply in this case
             *
             *
             * //test of broadcast without diameter stimation if (n.getRounds()
             * == 0) { System.out.println("init data"); ArrayList<String>
             * ch_in_r = new ArrayList(); HashMap<String, HashMap> recv_ch = new
             * HashMap();
             *
             * HashMap<String, HashMap> outi = new HashMap<>();
             * HashMap<String, Integer> infi = new HashMap<>();
             * ArrayList<String> ch_in = new
             * ArrayList(getTopologyNames(n.getVertex())); ArrayList<String>
             * ch_out = new ArrayList(getTopologyNames(n.getVertex()));
             * n.setAttribute("ch_in", ch_in); n.setAttribute("ch_in_r",
             * ch_in_r); n.setAttribute("ch_out", ch_out);
             * n.setAttribute("recv_ch", recv_ch);
             *
             * n.setAttribute("outi", outi); n.setAttribute("infi", infi);
             * ArrayList<String> topologyDatas = new
             * ArrayList(this.getTopologyNames(n.getVertex()));
             * n.getNetworkdata().put(n.getVertex().getName(), topologyDatas);
             * HashMap<String, ArrayList> newi = new
             * HashMap<>(n.getNetworkdata()); n.setAttribute("newi", newi);
             *
             * System.out.println("newi" + n.getAttribute("newi")); }
             * //System.out.println("thread of node " + n.getVertex().getName()
             * + "ph: " + n.getVertex().getPh()); end of Raynal init *
             */
            n.incRounds();
            //This part is primitive send to neigbors 
            //System.out.println("node thread:" + n.getVertex().getName());
            String act = action.getCode();
            //Simple send protocol
            //if (n.getRounds() < SimulationParameters.nhops) {
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
            //}

            /**
             * Send part of Raynal Protocol if (((ArrayList)
             * n.getAttribute("ch_in_r")).isEmpty()) { HashMap<String, HashMap>
             * send = new HashMap(); ArrayList<String> chanlsout = new
             * ArrayList((ArrayList) n.getAttribute("ch_out"));
             * ArrayList<String> chanlsin = (ArrayList) n.getAttribute("ch_in");
             * HashMap<String, ArrayList> diff = new HashMap<>(); //if
             * (n.getRounds() < SimulationParameters.nhops) {
             * //if(!(((ArrayList) (n.getAttribute("ch_in"))).isEmpty()) ||
             * !(((ArrayList) (n.getAttribute("ch_out"))).isEmpty())){ //
             * ArrayList<String> topologyDatas = new
             * ArrayList(this.getTopologyNames(n.getVertex()));
             *
             * for (String neigbour : (ArrayList<String>)
             * n.getAttribute("ch_out")) { String[] msgnet = new String[3];
             * msgnet[0] = "networkdata"; msgnet[1] =
             * String.valueOf(n.getVertex().getName());
             * System.out.println("newi" + n.getAttribute("newi")); if
             * (chanlsin.contains(neigbour)) { diff =
             * HashMapOperations.calculateDifference((HashMap)
             * n.getAttribute("newi"), (HashMap) ((HashMap)
             * (n.getAttribute("recv_ch"))).get(neigbour)); StringSerializer s =
             * new StringSerializer(); send.put(neigbour, diff); msgnet[2] =
             * s.serialize(diff); } else { StringSerializer s = new
             * StringSerializer(); msgnet[2] =
             * s.serialize(n.getAttribute("newi")); send.put(neigbour, (HashMap)
             * n.getAttribute("newi")); }
             *
             * if (send.get(neigbour).isEmpty()) { System.out.println("nada que
             * enviar"); chanlsout.remove((String) neigbour); } if
             * (!NetworkNodeMessageBuffer.getInstance().putMessage(neigbour,
             * msgnet)) { System.out.println("node is down: " + neigbour); }
             * else { // System.out.println("message sent!" + ); } }
             * n.setAttribute("ch_out", chanlsout); n.setAttribute("send",
             * send); n.removeAttribute("newi"); n.setAttribute("newi", new
             * HashMap()); } end of send part
             */
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
                            //receives data
                            if (inbox[0].equals("networkdata")) {
                                //completes and updates data
                                StringSerializer s = new StringSerializer();
                                HashMap<String, ArrayList> recvData = (HashMap<String, ArrayList>) s.deserialize(inbox[2]);
                                n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(), recvData));
                                /**
                                 * * receive part of raynal protocol
                                 * ArrayList<String> chanin = new
                                 * ArrayList(((ArrayList)
                                 * n.getAttribute("ch_in"))); ArrayList<String>
                                 * chanout = new ArrayList(((ArrayList)
                                 * n.getAttribute("ch_out"))); ArrayList<String>
                                 * chanintmp = new ArrayList(((ArrayList)
                                 * n.getAttribute("ch_in_r")));
                                 *
                                 * String from = inbox[0];
                                 * chanintmp.remove(from);
                                 *
                                 * StringSerializer s = new StringSerializer();
                                 * HashMap<String, ArrayList> recvData =
                                 * (HashMap<String, ArrayList>)
                                 * s.deserialize(inbox[2]);
                                 *
                                 * if (chanout.contains(from) &&
                                 * n.getAttribute("send") != null) { HashMap
                                 * send = ((HashMap) ((HashMap)
                                 * n.getAttribute("send")).get(from));
                                 *
                                 * //System.out.println(a.getAttribute("ID") +
                                 * ", send: " + ((Hashtable)
                                 * a.getAttribute("send")) + "vs newh" + newh +
                                 * " ch: " + ch); if
                                 * (HashMapOperations.isContained(send,
                                 * recvData)) { System.out.println("sendi is
                                 * contained in recvi"); chanout.remove(from); }
                                 * if (HashMapOperations.isContained(recvData,
                                 * send)) { System.out.println("sendi is
                                 * contained in recvi"); chanin.remove(from); }
                                 * } //completes and updates data if
                                 * (recvData.isEmpty()) { chanin.remove(from); }
                                 *
                                 * n.setAttribute("ch_in", chanin);
                                 * n.setAttribute("ch_out", chanout);
                                 * n.setAttribute("ch_in_r", chanintmp);
                                 *
                                 * ((HashMap)
                                 * n.getAttribute("recv_ch")).put(from,
                                 * recvData);
                                 *
                                 * // newi = newi U aux HashMap temp =
                                 * HashMapOperations.DifferenceSets(recvData,
                                 * (HashMap) n.getAttribute("infi"));
                                 * n.setAttribute("newi",
                                 * HashMapOperations.JoinSets((HashMap)
                                 * n.getAttribute("newi"), temp));
                                 *
                                 * //inf = infi U newi n.setAttribute("infi",
                                 * HashMapOperations.JoinSets((HashMap)
                                 * n.getAttribute("infi"), (HashMap)
                                 * n.getAttribute("newi")));
                                 *
                                 * System.out.println("network data received");
                                 * n.setNetworkdata(HashMapOperations.JoinSets(n.getNetworkdata(),
                                 * recvData)); System.out.println("n" +
                                 * n.getNetworkdata()); end or recv part
                                 */
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
                    /*if (SimulationParameters.activateReplication.equals("replalgon")) {
                        //n.calculateTimeout();
                        //evaluateAgentCreation(n);
                        // n.calculateTimeoutArrival();
                        //evaluateAgentCreationArrival(n);
                    }*/
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
                //Send topology data to others
                //evaporate pheromone
                n.getVertex().setPh((n.getVertex().getPh() - n.getVertex().getPh() * 0.001f));
            }
        }
        return false;
    }

    public void evaporatePheromone() {
        synchronized (NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast.class) {
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

    @Override
    public int getAgentsAlive() {
        return agentsAlive.size();
    }

    @Override
    public int getNodesAlive() {
        return nodes.size();
    }
}
