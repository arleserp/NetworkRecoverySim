/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public abstract class ReplicationStrategyInterface implements Serializable {

    private HashMap<Integer, Integer> followedAgents; //store ids of agent and fatherId
    private HashMap<Integer, String> followedAgentsLocation; //current location of agent when departing message is received.
    private HashMap<Integer, ArrayList<String>> responsibleAgentsPrevLocations; // Store previous locations of agent to resend messages
    int INITIAL_TIMEOUT = 50;//50;//100;//better50;// 30;//50 //default timeout    

    /**
     * Used in PAAMS and chain Protocol
     *
     */
    private HashMap<Integer, Integer> lastAgentDeparting; // Last time of an agent departing <idAgent, nodeAge>
    private HashMap<String, Integer> lastMessageFreeResp; // Last time of reception of freeResp Message <agentId + "-" + newLocation, nodeAge>
    ConcurrentHashMap<String, ArrayList<Integer>> nodeTimeouts; // List of timeouts to an specific location <nodeId, timeouts>
    /**
     * End PAAMS
     */

    /**
     * *
     * Used in replication based on counter v2
     */
    private HashMap<Integer, Integer> firstDepartingMsgTime; // first time of an agent departing <idAgent, nodeAge>
    private HashMap<Integer, Integer> limitDepartingMsgTime; // used when is time to delete agent reference <idAgent, nodeAge>
    ArrayList<Integer> timeouts; // this version does not compute the timeout to an specific node

    public HashMap<Integer, Integer> getFirstDepartingMsgTime() {
        return firstDepartingMsgTime;
    }

    public void setFirstDepartingMsgTime(HashMap<Integer, Integer> firstDepartingMsgTime) {
        this.firstDepartingMsgTime = firstDepartingMsgTime;
    }

    public HashMap<Integer, Integer> getLimitDepartingMsgTime() {
        return limitDepartingMsgTime;
    }

    public void setLimitDepartingMsgTime(HashMap<Integer, Integer> limitDepartingMsgTime) {
        this.limitDepartingMsgTime = limitDepartingMsgTime;
    }
    /**
     * End v2
     */

    public int getINITIAL_TIMEOUT() {
        return INITIAL_TIMEOUT;
    }

    public void setINITIAL_TIMEOUT(int INITIAL_TIMEOUT) {
        this.INITIAL_TIMEOUT = INITIAL_TIMEOUT;
    }

    int WINDOW_SIZE = 5;

    public ReplicationStrategyInterface() {
        
        followedAgents = new HashMap<>();        
        followedAgentsLocation = new HashMap<>();                
        responsibleAgentsPrevLocations = new HashMap<>();
        
        //PAAMS
        lastAgentDeparting = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = new ConcurrentHashMap<>();

        //v2
        firstDepartingMsgTime = new HashMap<>();
        limitDepartingMsgTime = new HashMap<>();
        timeouts = new ArrayList<>();
    }

    public ReplicationStrategyInterface(ConcurrentHashMap tout) {
        followedAgents = new HashMap<>();
        followedAgentsLocation = new HashMap<>();
        responsibleAgentsPrevLocations = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = tout;
    }

    public void initialize() {
        followedAgents = new HashMap<>();
        followedAgentsLocation = new HashMap<>();
        //responsibleAgentsPrevLocations = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();

    }

    public abstract void calculateTimeout();

    public abstract void addTimeout(int timeout);

    public abstract int estimateTimeout();

    public HashMap<Integer, Integer> getLastAgentDeparting() {
        return lastAgentDeparting;
    }

    public HashMap<String, Integer> getLastMessageFreeResp() {
        return lastMessageFreeResp;
    }

    public HashMap<Integer, ArrayList<String>> getResponsibleAgentsPrevLocations() {
        return responsibleAgentsPrevLocations;
    }

    /*public void setResponsibleAgentsPrevLocations(HashMap<Integer, ArrayList<String>> responsibleAgentsPrevLocations) {
        this.responsibleAgentsPrevLocations = responsibleAgentsPrevLocations;
    }*/
    public void setLastMessageFreeResp(int agentId, int nodeAge, String newLocation) {
        String key = agentId + "-" + newLocation;
        getLastMessageFreeResp().put(key, nodeAge);
    }

    public abstract double getStdDevTimeout();

    public ConcurrentHashMap<String, ArrayList<Integer>> getNodeTimeouts() {
        return nodeTimeouts;
    }

    public abstract int estimateExpectedTime(String nodeId);

    /**
     * @return the responsibleAgents
     */
    public HashMap<Integer, Integer> getFollowedAgents() {
        return followedAgents;
    }

    /**
     * @param responsibleAgents the responsibleAgents to set
     */
    public void setResponsibleAgents(HashMap<Integer, Integer> responsibleAgents) {
        this.followedAgents = responsibleAgents;
    }

    /**
     * @return the responsibleAgentsLocation
     */
    public HashMap<Integer, String> getFollowedAgentsLocation() {
        return followedAgentsLocation;
    }

    /**
     * @param responsibleAgentsLocation the responsibleAgentsLocation to set
     */
    public void setResponsibleAgentsLocation(HashMap<Integer, String> responsibleAgentsLocation) {
        this.followedAgentsLocation = responsibleAgentsLocation;
    }

    /**
     * @param lastMessageFreeResp the lastMessageFreeResp to set
     */
    public void setLastMessageFreeResp(HashMap<String, Integer> lastMessageFreeResp) {
        this.lastMessageFreeResp = lastMessageFreeResp;
    }

    void setNodeTimeouts(ConcurrentHashMap<String, ArrayList<Integer>> nodeTimeouts) {
        this.nodeTimeouts = nodeTimeouts;
    }

    //determines if agent is contained in the followed agents vector
    public abstract boolean containsAgent(int agentId);

    public abstract void removeReferences(int agentId);

    public abstract void removeReferencesForCreation(int agentId);

    @Override
    public String toString() {
        return "ReplicationStrategyInterface{" + "followedAgents=" + followedAgents + ", followedAgentsLocation=" + followedAgentsLocation + ", lastAgentDeparting=" + lastAgentDeparting + ", lastMessageFreeResp=" + lastMessageFreeResp + ", nodeTimeouts=" + nodeTimeouts + ", INITIAL_TIMEOUT=" + INITIAL_TIMEOUT + ", WINDOW_SIZE=" + WINDOW_SIZE + '}';
    }
    /*
    public HashMap<Integer, Integer> getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(HashMap<Integer, Integer> idCounter) {
        this.idCounter = idCounter;
    }
     */
}
