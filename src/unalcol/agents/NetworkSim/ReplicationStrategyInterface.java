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

    private HashMap<Integer, Integer> responsibleAgents;
    private HashMap<Integer, Integer> responsibleAgentsArrival;
    private HashMap<Integer, String> responsibleAgentsLocation;
    private HashMap<Integer, ArrayList<String>> responsibleAgentsPrevLocations;

    private HashMap<Integer, Integer> lastAgentDeparting;
    private HashMap<Integer, Integer> lastAgentArrival;
    private HashMap<String, Integer> lastMessageFreeResp;
    ConcurrentHashMap<String, ArrayList<Integer>> nodeTimeouts;

    int INITIAL_TIMEOUT = 50;//50;//100;//better50;// 30;//50

    public int getINITIAL_TIMEOUT() {
        return INITIAL_TIMEOUT;
    }

    public void setINITIAL_TIMEOUT(int INITIAL_TIMEOUT) {
        this.INITIAL_TIMEOUT = INITIAL_TIMEOUT;
    }
    int WINDOW_SIZE = 5;

    public ReplicationStrategyInterface() {
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        responsibleAgentsPrevLocations = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = new ConcurrentHashMap<>();
    }

    public ReplicationStrategyInterface(ConcurrentHashMap tout) {
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        responsibleAgentsPrevLocations = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = tout;
    }

    public void initialize() {
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        responsibleAgentsPrevLocations = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        lastAgentArrival = new HashMap<>();
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

    public void setResponsibleAgentsPrevLocations(HashMap<Integer, ArrayList<String>> responsibleAgentsPrevLocations) {
        this.responsibleAgentsPrevLocations = responsibleAgentsPrevLocations;
    }

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
    public HashMap<Integer, Integer> getResponsibleAgents() {
        return responsibleAgents;
    }

    /**
     * @param responsibleAgents the responsibleAgents to set
     */
    public void setResponsibleAgents(HashMap<Integer, Integer> responsibleAgents) {
        this.responsibleAgents = responsibleAgents;
    }

    /**
     * @return the responsibleAgentsLocation
     */
    public HashMap<Integer, String> getResponsibleAgentsLocation() {
        return responsibleAgentsLocation;
    }

    /**
     * @param responsibleAgentsLocation the responsibleAgentsLocation to set
     */
    public void setResponsibleAgentsLocation(HashMap<Integer, String> responsibleAgentsLocation) {
        this.responsibleAgentsLocation = responsibleAgentsLocation;
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

}