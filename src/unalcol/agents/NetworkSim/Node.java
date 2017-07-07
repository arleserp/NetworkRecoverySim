/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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

    // private ArrayList<Integer> timeout;
    private int amountRounds;

    private ArrayList<Integer> nodeTimeoutsArrival;
    private HashMap<String, ArrayList> networkdata;
    private HashMap<Object, ArrayList> pending;
    private HashMap<String, Integer> respAgentsBkp;
    private HashMap<Integer, String> prevLoc; // Stores <agentId, prevLoc> 
    private HashMap<Integer, Integer> followedAgentsCounter; // Stores <agentId, counter>
    private HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsInNeighbors;

    private HashMap<Integer, ReplicationStrategyInterface> repStrategy;


    AtomicInteger c = new AtomicInteger(0);
    protected Hashtable<String, Object> properties = new Hashtable<String, Object>();

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
        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
        agentsInNode = new ConcurrentHashMap<>();
        agentsInNeighbors = new HashMap<>();
        repStrategy = new HashMap<>();

        if (!SimulationParameters.simMode.equals("chain")) {
            repStrategy.put(1, new ReplicationStrategyPAAMS());
        } else {
            for (int i = 1; i <= SimulationParameters.nhops; i++) {
                repStrategy.put(i, new ReplicationStrategyPAAMS());
                repStrategy.get(i).setINITIAL_TIMEOUT(repStrategy.get(i).getINITIAL_TIMEOUT());
            }
        }
    }

    public Node(AgentProgram _program, GraphElements.MyVertex ve, HashMap tout) {
        super(_program);
        this.pending = new HashMap();
        this.networkdata = new HashMap<>();
        this.v = ve;
        agentsInNode = new ConcurrentHashMap<>();
        rounds = 0;
        respAgentsBkp = new HashMap<>();
        nodeTimeoutsArrival = new ArrayList<>();
        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
        agentsInNeighbors = new HashMap<>();

        if (!SimulationParameters.simMode.equals("chain")) {
            repStrategy.put(1, new ReplicationStrategyPAAMS());
            repStrategy.get(1).setNodeTimeouts(tout);
        } else {
            setRepStrategy(tout);
            for (int i = 1; i <= SimulationParameters.nhops; i++) {
                repStrategy.get(i).initialize();
            }
        }

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
     * @return the responsibleAgents
     */
    public HashMap<Integer, Integer> getResponsibleAgents(int hop) {

        return repStrategy.get(hop).getResponsibleAgents();
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

    public HashMap<Integer, String> getPrevLoc() {
        return prevLoc;
    }

    public void setPrevLoc(HashMap<Integer, String> prevLoc) {
        this.prevLoc = prevLoc;
    }

    /**
     * @param agentId
     * @return the lastAgentArrival
     */
    public int getLastAgentDeparting(int agentId, int hop) {
        return repStrategy.get(hop).getLastAgentDeparting().get(agentId);
    }

    /**
     * @param lastAgentArrival the lastAgentArrival to set
     * @param agentId
     */
    public void setLastAgentDeparting(int agentId, int nodeAge, int hop) {
        repStrategy.get(hop).getLastAgentDeparting().put(agentId, nodeAge);
    }

    /**
     * @param agentId
     * @return the lastMessageArrival
     */
    public int getLastMessageFreeResp(int agentId, int hop) {
        if (repStrategy.containsKey(hop) && repStrategy.get(hop).getLastMessageFreeResp().containsKey(agentId)) {
            return repStrategy.get(hop).getLastMessageFreeResp().get(agentId);
        }
        //System.out.println("no last free messages free!");
        return -1;
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
    public HashMap<String, ArrayList<Integer>> getNodeTimeouts(int hop) {
        return repStrategy.get(hop).getNodeTimeouts();
    }

    /**
     * @param nodeTimeouts the nodeTimeouts to set
     */
    public void setNodeTimeouts(HashMap<String, ArrayList<Integer>> nodeTimeouts, int hop) {
        repStrategy.get(hop).setNodeTimeouts(nodeTimeouts);
    }

    public void addCreationTime(int time, int hop) {
        for (String key : repStrategy.get(hop).getNodeTimeouts().keySet()) {
            repStrategy.get(hop).getNodeTimeouts().get(key).add(time);
        }
    }

    /**
     * @return the responsibleAgentsLocation
     */
    public HashMap<Integer, String> getResponsibleAgentsLocation(int hop) {
        return repStrategy.get(hop).getResponsibleAgentsLocation();
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
    
    public void addResponsibleAgentsPrevLocations(int agId, ArrayList<String> PrevLocations, int hop) {
        repStrategy.get(hop).getResponsibleAgentsPrevLocations().put(agId, PrevLocations);
    }
    
    
    public HashMap<Integer, ReplicationStrategyInterface> getRepStrategy() {
        return repStrategy;
    }

    public void setRepStrategy(HashMap<Integer, ReplicationStrategyInterface> repStrategy) {
        this.repStrategy = repStrategy;
    }

}
