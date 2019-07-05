/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {

    private GraphElements.MyVertex v;
    private final ConcurrentHashMap<Integer, Integer> agentsInNode;
    private ArrayList<Agent> currentAgents;
    private double pfCreate;
    private int roundsWithOutVisit;
    private int roundsWithoutAck;
    private int nMsgSend;
    private int nMsgRecv;
    private int rounds;
    public boolean isProcessing = false;

    // private ArrayList<Integer> timeout;
    private ArrayList<Integer> nodeTimeoutsArrival;
    private HashMap<String, ArrayList> networkdata;
    private HashMap<Object, ArrayList> pending;
    private HashMap<String, Integer> respAgentsBkp;
//    private HashMap<Integer, String> prevLoc; // Stores <agentId, prevLoc> 
    private HashMap<Integer, Integer> followedAgentsCounter; // Stores <agentId, counter>
    private HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsInNeighbors;

    private ConcurrentHashMap<Integer, ReplicationStrategyInterface> repStrategy;
    private LinkedBlockingQueue<String[]> networkMessagebuffer;

    private HashMap<Integer, String> idCounter; //added by arles.rodriguez 12/12/2018

    private HashMap<GraphElements.MyVertex, Integer> distancesToNode;

    public HashMap<GraphElements.MyVertex, Integer> getDistancesToNode() {
        return distancesToNode;
    }

    public void setDistancesToNode(HashMap<GraphElements.MyVertex, Integer> distancesToNode) {
        this.distancesToNode = distancesToNode;
    }

    AtomicInteger c = new AtomicInteger(0);
    protected Hashtable<String, Object> properties = new Hashtable<>();

    public void incrementAgentCount() {
        c.incrementAndGet();
    }

    public void decrementAgentCount() {
        c.decrementAndGet();
    }

    public int getAgentCount() {
        return c.get();
    }

    public HashMap<Object, ArrayList> getPending() {
        return pending;
    }

    public void setPending(HashMap<Object, ArrayList> pending) {
        this.pending = pending;
    }

    //try to stimate pf locally 1/numberofagentcreated
    public Node(AgentProgram _program, GraphElements.MyVertex ve) {
        super(_program);
        this.pending = new HashMap();
        this.networkdata = new HashMap<>();
        this.v = ve;
        currentAgents = new ArrayList<>();
        rounds = 0;
        respAgentsBkp = new HashMap<>();
        nodeTimeoutsArrival = new ArrayList<>();
//        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
        agentsInNode = new ConcurrentHashMap<>();
        agentsInNeighbors = new HashMap<>();
        repStrategy = new ConcurrentHashMap<>();

        if (SimulationParameters.simMode.equals("chainv2")) {
            repStrategy.put(1, new ReplicationStrategyV2());
        } else if (!SimulationParameters.simMode.contains("chain")) {
            repStrategy.put(1, new ReplicationStrategyPAAMS());
        } else {
            for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
                repStrategy.put(i, new ReplicationStrategyPAAMS());
                repStrategy.get(i).setINITIAL_TIMEOUT(repStrategy.get(i).getINITIAL_TIMEOUT() * i);
            }
        }
        networkMessagebuffer = new LinkedBlockingQueue();
        idCounter = new HashMap<>();
    }

    public Node(AgentProgram _program, GraphElements.MyVertex ve, ConcurrentHashMap tout) {
        super(_program);
        this.pending = new HashMap();
        this.networkdata = new HashMap<>();
        this.v = ve;
        agentsInNode = new ConcurrentHashMap<>();
        rounds = 0;
        respAgentsBkp = new HashMap<>();
        nodeTimeoutsArrival = new ArrayList<>();
//        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
        agentsInNeighbors = new HashMap<>();
        repStrategy = new ConcurrentHashMap<>();

        if (SimulationParameters.simMode.equals("chainv2")) {
            repStrategy.put(1, new ReplicationStrategyV2());
        } else if (!SimulationParameters.simMode.contains("chain")) {
            repStrategy.put(1, new ReplicationStrategyPAAMS());
            repStrategy.get(1).setNodeTimeouts(tout);
        } else {
            setRepStrategy(tout);
            for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
                repStrategy.get(i).initialize();
            }
        }
        networkMessagebuffer = new LinkedBlockingQueue();
        idCounter = new HashMap<>();
    }

    public GraphElements.MyVertex getVertex() {
        return v;
    }

    public void addAgent(Agent a) {
        getCurrentAgents().add(a);
    }

    public void addAgentInNode(int agId, int fId) {
        //if (!agentsInNode.contains(agId)) {
        getAgentsInNode().put(agId, fId);
        //}
    }

    public void deleteAgentInNode(int agId) {
        getAgentsInNode().remove(agId);
    }

    public void deleteAgent(Agent a) {
        synchronized (Node.class) {
            for (Agent x : getCurrentAgents()) {
                if (x.equals(a)) {
                    getCurrentAgents().remove(x);
                }
            }
        }
    }

    /**
     * @return the pfCreate
     */
    public double getPfCreate() {
        return pfCreate;
    }

    /**
     * @param pfCreate the pfCreate to set
     */
    public void setPfCreate(double pfCreate) {
        this.pfCreate = pfCreate;
    }

    /**
     * @return the agentsInNode
     */
    public ArrayList<Agent> getCurrentAgents() {
        return currentAgents;
    }

    /**
     * @param currentAgents the currentAgents to set
     */
    public void setCurrentAgents(ArrayList<Agent> currentAgents) {
        this.currentAgents = currentAgents;
    }

    /**
     * @return the roundsWithOutVisit
     */
    public int getRoundsWithOutVisit() {
        return roundsWithOutVisit;
    }

    /**
     * @param roundsWithOutVisit the roundsWithOutVisit to set
     */
    public void setRoundsWithOutVisit(int roundsWithOutVisit) {
        this.roundsWithOutVisit = roundsWithOutVisit;
    }

    /**
     */
    public void addRoundsWithOutVisit() {
        roundsWithOutVisit++;
    }

    /**
     * @param hop
     * @return the responsibleAgents
     */
    public HashMap<Integer, Integer> getFollowedAgents(int hop) {
        return repStrategy.get(hop).getFollowedAgents();
    }

    /**
     * @param responsibleAgents the responsibleAgents to set
     */
    public void setResponsibleAgents(HashMap<Integer, Integer> responsibleAgents, int hop) {
        repStrategy.get(hop).setResponsibleAgents(responsibleAgents);
    }

    /**
     * @return the nMsgSend
     */
    public int getnMsgSend() {
        return nMsgSend;
    }

    /**
     * @param nMsgSend the nMsgSend to set
     */
    public void setnMsgSend(int nMsgSend) {
        this.nMsgSend = nMsgSend;
    }

    /**
     * @return the nMsgRecv
     */
    public int getnMsgRecv() {
        return nMsgRecv;
    }

    /**
     * @param nMsgRecv the nMsgRecv to set
     */
    public void setnMsgRecv(int nMsgRecv) {
        this.nMsgRecv = nMsgRecv;
    }

    public void incMsgSend() {
        synchronized (Node.class) {
            nMsgSend++;
        }
    }

    public void incMsgRecv() {
        synchronized (Node.class) {
            nMsgRecv++;
        }
    }

    /**
     * @return the roundsWithoutAck
     */
    public int getRoundsWithoutAck() {
        return roundsWithoutAck;
    }

    /**
     * @param roundsWithoutAck the roundsWithoutAck to set
     */
    public void setRoundsWithoutAck(int roundsWithoutAck) {
        this.roundsWithoutAck = roundsWithoutAck;
    }

    /**
     * increases rounds without ack
     */
    public void incRoundsWithoutAck() {
        roundsWithoutAck++;
    }

    /**
     * @return the rounds
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * @param rounds the rounds to set
     */
    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void incRounds() {
        synchronized (Node.class) {
            this.rounds++;
        }
    }

//    public HashMap<Integer, String> getPrevLoc() {
//        return prevLoc;
//    }
//
//    public void setPrevLoc(HashMap<Integer, String> prevLoc) {
//        this.prevLoc = prevLoc;
//    }
    /**
     * @param agentId
     * @return the lastAgentArrival
     */
    public int getLastAgentDeparting(int agentId, int hop) {
        return repStrategy.get(hop).getLastAgentDeparting().get(agentId);
    }

    /**
     * @param nodeAge current message of arrival
     * @param hop in the change
     * @param agentId
     */
    public void setLastAgentDeparting(int agentId, int nodeAge, int hop) {
        repStrategy.get(hop).getLastAgentDeparting().put(agentId, nodeAge);
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageFreeResp(int agentId, int nodeAge, String newLocation, int hop) {
        repStrategy.get(hop).setLastMessageFreeResp(agentId, nodeAge, newLocation);
    }

    public void calculateTimeout(int hop) {
        repStrategy.get(hop).calculateTimeout();
    }

    public void addTimeout(int timeout, int hop) {
        //System.out.println("add" + timeout);
        for (String key : repStrategy.get(hop).getNodeTimeouts().keySet()) {
            repStrategy.get(hop).getNodeTimeouts().get(key).add(timeout);
        }
    }

    public int estimateTimeout(int hop) {
        return repStrategy.get(hop).estimateTimeout();
    }

    /**
     * @return the lastAgentArrival
     */
    public HashMap<Integer, Integer> getLastAgentDeparting(int hop) {
        return repStrategy.get(hop).getLastAgentDeparting();
    }

    /**
     * @return the lastMessageArrival
     */
    public HashMap<String, Integer> getLastMessageFreeResp(int hop) {
        return repStrategy.get(hop).getLastMessageFreeResp();
    }

    /**
     * @param lastMessageFreeResp the lastMessageArrival to set
     */
    public void setLastMessageFreeResp(HashMap<String, Integer> lastMessageFreeResp, int hop) {
        repStrategy.get(hop).setLastMessageFreeResp(lastMessageFreeResp);
    }

    public double getStdDevTimeout(int hop) {
        return repStrategy.get(hop).getStdDevTimeout();
    }

    /**
     * @return the nodeTimeouts
     */
    public ConcurrentHashMap<String, ArrayList<Integer>> getNodeTimeouts(int hop) {
        return repStrategy.get(hop).getNodeTimeouts();
    }

    /**
     * @param nodeTimeouts the nodeTimeouts to set
     */
    public void setNodeTimeouts(ConcurrentHashMap<String, ArrayList<Integer>> nodeTimeouts, int hop) {
        repStrategy.get(hop).setNodeTimeouts(nodeTimeouts);
    }

    public void addCreationTime(int time, int hop) {
        for (String key : repStrategy.get(hop).getNodeTimeouts().keySet()) {
            repStrategy.get(hop).getNodeTimeouts().get(key).add(time);
        }
    }

    /**
     * @param hop
     * @return the responsibleAgentsLocation
     */
    public HashMap<Integer, String> getFollowedAgentsLocation(int hop) {
        return repStrategy.get(hop).getFollowedAgentsLocation();
    }

    /**
     * @param responsibleAgentsLocation the responsibleAgentsLocation to set
     */
    public void setResponsibleAgentsLocation(HashMap<Integer, String> responsibleAgentsLocation, int hop) {
        repStrategy.get(hop).setResponsibleAgentsLocation(responsibleAgentsLocation);
    }

    public int estimateExpectedTime(String nodeId, int hop) {
        return repStrategy.get(hop).estimateExpectedTime(nodeId);
    }

    public double getStdDevTimeout(String nodeName, int hop) {
        return repStrategy.get(hop).getStdDevTimeout();
    }

    /**
     * @return the networkdata
     */
    public HashMap<String, ArrayList> getNetworkdata() {
        return networkdata;
    }

    /**
     * @param networkdata the networkdata to set
     */
    public void setNetworkdata(HashMap<String, ArrayList> networkdata) {
        this.networkdata = networkdata;
    }

    /**
     * @param v the v to set
     */
    public void setVertex(GraphElements.MyVertex v) {
        this.v = v;
    }

    /**
     * @return the respAgentsBkp
     */
    public HashMap<String, Integer> getRespAgentsBkp() {
        return respAgentsBkp;
    }

    /**
     * @param respAgentsBkp the respAgentsBkp to set
     */
    public void setRespAgentsBkp(HashMap<String, Integer> respAgentsBkp) {
        this.respAgentsBkp = respAgentsBkp;
    }

    public void increaseFollowedAgentsCounter(int agentId) {
        if (!followedAgentsCounter.containsKey(agentId)) {
            followedAgentsCounter.put(agentId, 1);
        } else {
            followedAgentsCounter.put(agentId, followedAgentsCounter.get(agentId) + 1);
        }
    }

    public int getFollowedAgentsCounter(int agentId) {
        return followedAgentsCounter.get(agentId);
    }

    public boolean containsFollowedAgentsCounter(int agentId) {
        return followedAgentsCounter.containsKey(agentId);
    }

    public int deleteFollowedAgentsCounter(int agentId) {
        return followedAgentsCounter.remove(agentId);
    }

    /**
     * @return the agentsInNode
     */
    public ConcurrentHashMap<Integer, Integer> getAgentsInNode() {
        return agentsInNode;
    }

    public ArrayList<Integer> getDuplicatedAgents() {
        ArrayList<Integer> duplicatedAgents = new ArrayList<>();

        Iterator<Integer> it = agentsInNode.keySet().iterator();
        //System.out.print("Agents in node: " + agentsInNode + ", duplicated agents:");
        // 1. Rule to find if a process with original father has repeated agents
        while (it.hasNext()) {
            int agent = it.next();
            int father = agentsInNode.get(agent);
            if (father != -1) {
                if (agentsInNode.containsKey(father)) {
                    duplicatedAgents.add(agent);
                    System.out.print("1. " + agent);
                }
            }
        }

        // 2. Compare by now is bubble n*n-1 detection can be optimized
        Object[] keys = agentsInNode.keySet().toArray();
//        Arrays.sort(keys);        

        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                //System.out.println("2." + agentsInNode + "-" + agentsInNode.get(keys[i]) + " vs " + agentsInNode.get(keys[j]));
                if (agentsInNode.get(keys[i]) != -1 && (Objects.equals(agentsInNode.get(keys[i]), agentsInNode.get(keys[j])))) {
                    if (!duplicatedAgents.contains((int) keys[j])) {
                        duplicatedAgents.add((int) keys[j]);
                        System.out.print("," + keys[j]);
                    }
                }
            }
        }

        if (!duplicatedAgents.isEmpty()) {
            //System.out.print("Agents in node: " + agentsInNode + ", duplicated agents detected. ");
            //System.out.println("yayayayay");
            for (Integer r : duplicatedAgents) {
                agentsInNode.remove(r);
            }
            //System.out.println("After - Agents in node: " + agentsInNode + ".");
        }
        return duplicatedAgents;
    }

    /**
     * @return the agentsInNeighbors
     */
    public HashMap<String, ConcurrentHashMap<Integer, Integer>> getAgentsInNeighbors() {
        return agentsInNeighbors;
    }

    /**
     * @param agentsInNeighbors the agentsInNeighbors to set
     */
    public void setAgentsInNeighbors(HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsInNeighbors) {
        this.agentsInNeighbors = agentsInNeighbors;
    }

    public void setAttribute(String key, Object value) {
        properties.put(key, value);
    }

    public Object getAttribute(String key) {
        return properties.get(key);
    }

    public boolean removeAttribute(String key) {
        if (properties.remove(key) != null) {
            return true;
        } else {
            return (false);
        }
    }

    public void removeResponsibleAgentsPrevLocations(int agId, int hop) {
        repStrategy.get(hop).getResponsibleAgentsPrevLocations().remove(agId);
    }

    public ArrayList<String> getResponsibleAgentsPrevLocations(int agId, int hop) {
        return repStrategy.get(hop).getResponsibleAgentsPrevLocations().get(agId);
    }

    public void addFollowedAgentsPrevLocations(int agId, ArrayList<String> PrevLocations, int hop) {
        repStrategy.get(hop).getResponsibleAgentsPrevLocations().put(agId, PrevLocations);
    }

    public ConcurrentHashMap<Integer, ReplicationStrategyInterface> getRepStrategy() {
        return repStrategy;
    }

    public void setRepStrategy(ConcurrentHashMap<Integer, ReplicationStrategyInterface> repStrategy) {
        this.repStrategy = repStrategy;
    }

    public synchronized boolean putMessage(String[] msg) {
        networkMessagebuffer.add(msg);
        return true;
    }

    // Called by Consumer
    public synchronized String[] getMessage() {
        try {
            //if (pid.equals("p23")) {
            //System.out.println("Node id" + getVertex().getName() + ", network buffer size:" + networkMessagebuffer.size());
            //}
            if (!networkMessagebuffer.isEmpty()) {
                return networkMessagebuffer.poll();
            }
        } catch (NullPointerException ex) {
            //System.out.println("Error reading mbuffer for agent:" + pid + "buffer: " + mbuffer);            cs
            System.out.println("error reading networkmbuffer....");
            //createBuffer(pid);
            //mbuffer.put(pid, new LinkedBlockingQueue())
            //System.exit(1);
        }
        return null;
    }

    public boolean hasFollowedInNodeBefore(int agentId) {
        for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
            if (repStrategy.get(i).containsAgent(agentId)) {
                return true;
            }
        }
        return false;
    }

    public int countFollowedInNode(int agentId) {
        int count = 0;
        for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
            if (repStrategy.get(i).containsAgent(agentId)) {
                count++;
            }
        }
        return count;
    }

    public void deleteAllFollowedReferences(int agentId) {
        for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
            if (repStrategy.get(i).containsAgent(agentId)) {
                repStrategy.get(i).removeReferences(agentId);
            }
        }
//        prevLoc.remove(agentId);

    }

    public void printReplicationHops() {
        System.out.println("Node: " + getVertex().getName() + " Dictionary{idagent=counter}: " + idCounter);
        if (SimulationParameters.simMode.equals("chainv2")) {
            System.out.println("Node" + getVertex().getName() + " - hop " + 1 + " repl: " + repStrategy.get(1));
        } else {

            for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
                System.out.println("Node" + getVertex().getName() + " - hop " + i + " repl: " + repStrategy.get(i));
            }
        }
    }

    public void printReplicationHop(int i) {
        System.out.println("Node" + getVertex().getName() + " - hop " + i + " repl: " + repStrategy.get(i));
    }

    public void deleteAgentFromRep(int hop, int agentId) {
        repStrategy.get(hop).removeReferencesForCreation(agentId);
    }

    public HashMap<Integer, String> getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(HashMap<Integer, String> idCounter) {
        this.idCounter = idCounter;
    }

    //used in v2
    public void setFirstDepartingMsgTime(int agentId, int rounds) {
        ((ReplicationStrategyV2) repStrategy.get(1)).getFirstDepartingMsgTime().put(agentId, rounds);
    }

    //used in v2
    public void setLimitDepartingMsgTime(int agentId, int rounds) {
        ((ReplicationStrategyV2) repStrategy.get(1)).getLimitDepartingMsgTime().put(agentId, rounds);
    }

    //used in v2
    public int getFirstDepartingMsgTime(int agentId) {
        return ((ReplicationStrategyV2) repStrategy.get(1)).getFirstDepartingMsgTime().get(agentId);
    }

    //used in v2
    public boolean containsFirstDepartingMsgTime(int agentId) {
        return ((ReplicationStrategyV2) repStrategy.get(1)).getFirstDepartingMsgTime().containsKey(agentId);
    }

    @Override
    public String toString() {
        return "Node{" + "v=" + v + ", rounds=" + rounds + '}';

    }

    private void getListNeighboursHop(ArrayList<String> neighbours, int nhopsChain, String nodeName, HashMap<String, Integer> distances) {
        int lvls = 1;
        Deque<String> q = new LinkedList<>();
        distances.put(nodeName, 0);
        if (nhopsChain == 0) {
            return;
        }

        while (nodeName != null) {
            if (getNetworkdata().containsKey(nodeName) && getNetworkdata().get(nodeName) != null) {
                List<String> list = new ArrayList<>(getNetworkdata().get(nodeName));
                Iterator<String> itr = list.iterator();
                while (itr.hasNext()) {
                    String ne = itr.next();
                    //System.out.println(ne);
                    if (!distances.containsKey(ne)) {
                        distances.put(ne, distances.get(nodeName) + 1);
                        q.add(ne);
                    }
                    //loadNeighboursRecursively(neighbours, nhopsChain - 1, ne);
                }
            }
            nodeName = q.poll();
            //System.out.println("leveeeel" + lvls);
            //Scanner sc = new Scanner(System.in);
            //sc.nextInt();
        }

        /*
        if (nhopsChain == 0) {
            return;
        }
        if (getNetworkdata().containsKey(nodeName) && getNetworkdata().get(nodeName) != null) {
            List<String> list = new ArrayList<>(getNetworkdata().get(nodeName));
            Iterator<String> itr = list.iterator();
            while (itr.hasNext()) {
                String ne = itr.next();
                if (!neighbours.contains(ne)) {
                    neighbours.add(ne);
                }
                getListNeighboursHop(neighbours, nhopsChain - 1, ne);
            }
        }*/
    }

    public void pruneInformation(int nhops) {
        System.out.println("begin prune" + this.getVertex().getName() + "hops" + nhops);
        //System.out.println("prune!");
        //System.out.println("nhops" + nhops);
        ArrayList<String> neighbours = new ArrayList<>();
        neighbours.add(this.getVertex().getName());
        HashMap<String, Integer> distances = new HashMap<>();
        getListNeighboursHop(neighbours, nhops, this.getVertex().getName(), distances);
        
        for (String neig : distances.keySet()) {
            if (!neighbours.contains(neig) && distances.get(neig) <= nhops) {
                neighbours.add(neig);
            }
        }
        //getListNeighboursHop(neighbours, nhops, this.getVertex().getName());
        System.out.println(this.getVertex().getName() + " neighbours size:" + neighbours.size() + " networkdata.size=" + networkdata.size() + " data=" + networkdata);
        if (neighbours.size() != networkdata.size()) {
            HashMap<String, ArrayList> networkDatatmp = new HashMap<>();
            for (String s : neighbours) {
                if (networkdata.containsKey(s)) {
                    networkDatatmp.put(s, networkdata.get(s));
                }
            }
            networkdata = networkDatatmp;
        }
        System.out.println(this.getVertex().getName() + " after prune neighbours size=" + neighbours.size() + " networkdata.size=" + networkdata.size() + " data=" + networkdata);
        
        System.out.println("end prune" + this.getVertex().getName());
    }

}
