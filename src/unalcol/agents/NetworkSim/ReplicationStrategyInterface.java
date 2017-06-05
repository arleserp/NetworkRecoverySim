/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public abstract class ReplicationStrategyInterface {

    private HashMap<Integer, Integer> responsibleAgents;
    private HashMap<Integer, Integer> responsibleAgentsArrival;
    private HashMap<Integer, String> responsibleAgentsLocation;
    private HashMap<Integer, Integer> lastAgentDeparting;
    private HashMap<Integer, Integer> lastAgentArrival;
    private HashMap<String, Integer> lastMessageFreeResp;
    HashMap<String, ArrayList<Integer>> nodeTimeouts;

    int INITIAL_TIMEOUT = 30;//50
    int WINDOW_SIZE = 10;//5

    public ReplicationStrategyInterface() {
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = new HashMap();
    }

    public ReplicationStrategyInterface(HashMap tout) {
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        nodeTimeouts = tout;
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

    public void setLastMessageFreeResp(int agentId, int nodeAge, String newLocation) {
        String key = agentId + "-" + newLocation;
        getLastMessageFreeResp().put(key, nodeAge);
    }

    public abstract double getStdDevTimeout();

    public HashMap<String, ArrayList<Integer>> getNodeTimeouts(){
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

    void setNodeTimeouts(HashMap<String, ArrayList<Integer>> nodeTimeouts) {
        this.nodeTimeouts = nodeTimeouts;
    }

}
