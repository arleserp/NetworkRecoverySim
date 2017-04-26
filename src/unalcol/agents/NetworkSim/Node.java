/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.print.attribute.HashAttributeSet;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.util.BoxPlotACKAmount.CustomComparator;
import unalcol.agents.NetworkSim.util.StatisticsNormalDist;

/**
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {

    private GraphElements.MyVertex v;
    private ArrayList<Agent> currentAgents;
    private HashMap<Integer, Integer> responsibleAgents;
    private HashMap<Integer, Integer> responsibleAgentsArrival;
    private HashMap<Integer, String> responsibleAgentsLocation;
    private double pfCreate;
    private int roundsWithOutVisit;
    private int roundsWithoutAck;
    private int nMsgSend;
    private int nMsgRecv;
    private int rounds;
    private HashMap<Integer, Integer> lastAgentDeparting;
    private HashMap<Integer, Integer> lastAgentArrival;

    private HashMap<Integer, Integer> lastStartDeparting;

    private HashMap<String, Integer> lastMessageFreeResp;
    // private ArrayList<Integer> timeout;
    private int amountRounds;
    private HashMap<String, ArrayList<Integer>> nodeTimeouts;
    private ArrayList<Integer> nodeTimeoutsArrival;

    private int INITIAL_TIMEOUT = 10;
    private int WINDOW_SIZE = 10;
    private HashMap<String, Object> networkdata;
    private HashMap<Object, ArrayList> pending;
    private HashMap<String, Integer> respAgentsBkp;
    private HashMap<Integer, String> prevLoc; // Stores <agentId, prevLoc> 
    private HashMap<Integer, Integer> followedAgentsCounter; // Stores <agentId, counter> 

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
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        rounds = 0;
        nodeTimeouts = new HashMap();
        respAgentsBkp = new HashMap<>();
        lastStartDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        nodeTimeoutsArrival = new ArrayList<>();
        lastAgentArrival = new HashMap<>();
        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
    }

    public Node(AgentProgram _program, GraphElements.MyVertex ve, HashMap tout) {
        super(_program);
        this.pending = new HashMap();
        this.networkdata = new HashMap<>();
        this.v = ve;
        currentAgents = new ArrayList<>();
        responsibleAgents = new HashMap<>();
        responsibleAgentsLocation = new HashMap<>();
        lastAgentDeparting = new HashMap<>();
        lastMessageFreeResp = new HashMap<>();
        rounds = 0;
        nodeTimeouts = tout;
        respAgentsBkp = new HashMap<>();
        lastStartDeparting = new HashMap<>();
        responsibleAgentsArrival = new HashMap<>();
        nodeTimeoutsArrival = new ArrayList<>();
        lastAgentArrival = new HashMap<>();
        prevLoc = new HashMap<>();
        followedAgentsCounter = new HashMap<>();
    }

    public GraphElements.MyVertex getVertex() {
        return v;
    }

    public void addAgent(Agent a) {
        getCurrentAgents().add(a);
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
     * @return the currentAgents
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
    public int getLastAgentDeparting(int agentId) {
        return getLastAgentDeparting().get(agentId);
    }

    /**
     * @param lastAgentArrival the lastAgentArrival to set
     * @param agentId
     */
    public void setLastAgentDeparting(int agentId, int nodeAge) {
        this.getLastAgentDeparting().put(agentId, nodeAge);
    }

    /**
     * @param agentId
     * @return the lastMessageArrival
     */
    public int getLastMessageFreeResp(int agentId) {
        return getLastMessageFreeResp().get(agentId);
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageFreeResp(int agentId, int nodeAge, String newLocation) {
        String key = agentId + "-" + newLocation;
        getLastMessageFreeResp().put(key, nodeAge);
    }

    /*
    public void calculateTimeout() {
        Iterator<Map.Entry<Integer, Integer>> iter = getResponsibleAgents().entrySet().iterator();
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageArrival().entrySet().iterator();
        
        while (iter.hasNext()) {
            //Key: agentId|roundNumber
            ArrayList<Integer> times = new ArrayList<>();
            Map.Entry<Integer, Integer> Key = iter.next();
            int k = Key.getKey();

            while (iterM.hasNext()) {
                Map.Entry<String, Integer> KeyM = iterM.next();
                String[] dataKey = KeyM.getKey().split("-");
                int agentId = Integer.valueOf(dataKey[0]);
                String nodeId = dataKey[1];
                
                if (agentId == k && getLastAgentArrival().containsKey(k) && getLastMessageArrival().containsKey(KeyM.getKey())) {

                    int diff = Math.abs(getLastMessageArrival().get(KeyM.getKey()) - getLastAgentArrival().get(k));

                    if (diff != 0) {
                        if (!nodeTimeouts.containsKey(nodeId)) {
                            getNodeTimeouts().put(nodeId, new ArrayList());
                            getNodeTimeouts().get(nodeId).add(INITIAL_TIMEOUT);
                        }
                        getNodeTimeouts().get(nodeId).add(diff);
                    }
                    //System.out.println("node:" + getVertex().getName() + ", antes:" + getLastMessageArrival());
                    iterM.remove();
                    getLastAgentArrival().remove(k);
                }
            }
            //Add median to the history to have more stable 
        }
    }
     */
    public void calculateTimeout() {
        Iterator<Map.Entry<Integer, Integer>> iter = getResponsibleAgents().entrySet().iterator();
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageFreeResp().entrySet().iterator();

        while (iter.hasNext()) {
            //Key: agentId|roundNumber
            ArrayList<Integer> times = new ArrayList<>();
            Map.Entry<Integer, Integer> Key = iter.next();
            int k = Key.getKey();

            while (iterM.hasNext()) {
                Map.Entry<String, Integer> KeyM = iterM.next();
                String[] dataKey = KeyM.getKey().split("-");
                int agentId = Integer.valueOf(dataKey[0]);
                String nodeId = dataKey[1];

                if (agentId == k && getLastAgentDeparting().containsKey(k) && getLastMessageFreeResp().containsKey(KeyM.getKey())) {
                    int diff = Math.abs(getLastMessageFreeResp().get(KeyM.getKey()) - getLastAgentDeparting().get(k));
                    //System.out.println("diff" +  diff);
                    //if (diff != 0) {
                    if (!nodeTimeouts.containsKey(nodeId)) {
                        getNodeTimeouts().put(nodeId, new ArrayList());
                        getNodeTimeouts().get(nodeId).add(INITIAL_TIMEOUT);
                    }

                    if (getNodeTimeouts().get(nodeId).size() >= WINDOW_SIZE) {
                        nodeTimeouts.put(nodeId, new ArrayList<>(nodeTimeouts.get(nodeId).subList(nodeTimeouts.get(nodeId).size() - WINDOW_SIZE, nodeTimeouts.get(nodeId).size() - 1)));
                    }
                    getNodeTimeouts().get(nodeId).add(diff);
                    //System.out.println("calculatetimeout size getNodeTimeOuts():" + nodeId + "+" + getNodeTimeouts().get(nodeId).size());
                    //}
                    //System.out.println("node:" + getVertex().getName() + ", antes:" + getLastMessageArrival());
                    iterM.remove();
                    getLastAgentDeparting().remove(k);
                }
            }
        }
    }

    public void addTimeout(int timeout) {
        //System.out.println("add" + timeout);
        for (String key : getNodeTimeouts().keySet()) {
            getNodeTimeouts().get(key).add(timeout);
        }
    }

    public int estimateTimeout() {

        int maxMedianTimeout = Integer.MIN_VALUE;
        System.out.println("nodeTimeouts" + getNodeTimeouts());

        if (getNodeTimeouts().isEmpty()) {
            return INITIAL_TIMEOUT;
        }

        for (String key : getNodeTimeouts().keySet()) {
            ArrayList<Double> dtimeout = new ArrayList();
            for (Integer d : getNodeTimeouts().get(key)) {
                dtimeout.add(d.doubleValue());
            }
            if (dtimeout.size() >= WINDOW_SIZE) {
                dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size() - 1));

            }
            //if (dtimeout.size() > 1) {
            StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
            if (st.getMedian() > maxMedianTimeout) {
                maxMedianTimeout = (int) st.getMedian();
            }
        }
        //System.out.println("Max median timeout" + maxMedianTimeout);
        return maxMedianTimeout;
        /*ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : timeout) {
            dtimeout.add(d.doubleValue());
        }

        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getMedian();*/
        //} 
        //return Collections.max(getTimeout());*/
    }

    /**
     * @return the lastAgentArrival
     */
    public HashMap<Integer, Integer> getLastAgentDeparting() {
        return lastAgentDeparting;
    }

    /**
     * @return the lastMessageArrival
     */
    public HashMap<String, Integer> getLastMessageFreeResp() {
        return lastMessageFreeResp;
    }

    /**
     * @param lastMessageFreeResp the lastMessageArrival to set
     */
    public void setLastMessageFreeResp(HashMap<String, Integer> lastMessageFreeResp) {
        this.lastMessageFreeResp = lastMessageFreeResp;
    }

    public double getStdDevTimeout() {
        int maxMedianStdTimeout = Integer.MIN_VALUE;
        //System.out.println("nodeTimeouts" + getNodeTimeouts());

        if (getNodeTimeouts().isEmpty()) {
            return 0;
        }

        for (String key : getNodeTimeouts().keySet()) {
            ArrayList<Double> dtimeout = new ArrayList();
            for (Integer d : getNodeTimeouts().get(key)) {
                dtimeout.add(d.doubleValue());
            }

            if (dtimeout.size() >= WINDOW_SIZE) {
                dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size() - 1));
            }

            //if (dtimeout.size() > 1) {
            StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
            if (st.getStdDev() > maxMedianStdTimeout) {
                maxMedianStdTimeout = (int) st.getStdDevMedian();
            }
        }
        //System.out.println("Max std timeout" + maxMedianStdTimeout);
        return maxMedianStdTimeout;
    }

    /**
     * @return the nodeTimeouts
     */
    public HashMap<String, ArrayList<Integer>> getNodeTimeouts() {
        return nodeTimeouts;
    }

    /**
     * @param nodeTimeouts the nodeTimeouts to set
     */
    public void setNodeTimeouts(HashMap<String, ArrayList<Integer>> nodeTimeouts) {
        this.nodeTimeouts = nodeTimeouts;
    }

    public void addCreationTime(int time) {
        for (String key : getNodeTimeouts().keySet()) {
            getNodeTimeouts().get(key).add(time);
        }
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

    public int estimateExpectedTime(String nodeId) {
        //System.out.println("nodeTimeouts" + getNodeTimeouts());
        if (!getNodeTimeouts().containsKey(nodeId)) {
            return INITIAL_TIMEOUT;
        }
        //for (String key : getNodeTimeouts().keySet()) {
        ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : getNodeTimeouts().get(nodeId)) {
            dtimeout.add(d.doubleValue());
        }
        if (dtimeout.size() >= WINDOW_SIZE) {
            dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size() - 1));
        }
        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getMedian();

    }

    public double getStdDevTimeout(String nodeName) {
        if (!getNodeTimeouts().containsKey(nodeName)) {
            return 0;
        }
        ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : getNodeTimeouts().get(nodeName)) {
            dtimeout.add(d.doubleValue());
        }
        if (dtimeout.size() >= WINDOW_SIZE) {
            dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size() - 1));
        }
        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getStdDevMedian();
    }

    /**
     * @return the networkdata
     */
    public HashMap<String, Object> getNetworkdata() {
        return networkdata;
    }

    /**
     * @param networkdata the networkdata to set
     */
    public void setNetworkdata(HashMap<String, Object> networkdata) {
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

    /*
      @param
     */
    public void setLastAgentArrive(int agentId, int rounds) {
        getLastAgentArrive().put(agentId, rounds);
    }

    /**
     * @return the responsibleAgentsArrival
     */
    public HashMap<Integer, Integer> getResponsibleAgentsArrival() {
        return responsibleAgentsArrival;
    }

    /**
     * @param responsibleAgentsArrival the responsibleAgentsArrival to set
     */
    public void setResponsibleAgentsArrival(HashMap<Integer, Integer> responsibleAgentsArrival) {
        this.responsibleAgentsArrival = responsibleAgentsArrival;
    }

    public void calculateTimeoutArrival() {
        Iterator<Map.Entry<Integer, Integer>> iter = responsibleAgentsArrival.entrySet().iterator();

        while (iter.hasNext()) {
            ArrayList<Integer> times = new ArrayList<>();
            Map.Entry<Integer, Integer> Key = iter.next();
            int k = Key.getKey();
            if (responsibleAgentsArrival.containsKey(k) && lastStartDeparting.containsKey(k)) {
                int diff = Math.abs(responsibleAgentsArrival.get(k) - lastStartDeparting.get(k));

                if (nodeTimeoutsArrival.size() >= WINDOW_SIZE) {
                    nodeTimeoutsArrival = new ArrayList<>(nodeTimeoutsArrival.subList(nodeTimeoutsArrival.size() - WINDOW_SIZE, nodeTimeoutsArrival.size()));
                }

                nodeTimeoutsArrival.add(diff);
                //System.out.println("nodeTimeoutsArrival.size=" + nodeTimeoutsArrival.size());
                iter.remove();
                lastStartDeparting.remove(k);
            }

        }
    }

    public void setLastStartDeparting(int agentId, int rounds) {
        lastStartDeparting.put(agentId, rounds);
    }

    /**
     * @return the lastStartDeparting
     */
    public HashMap<Integer, Integer> getLastStartDeparting() {
        return lastStartDeparting;
    }

    /**
     * @param lastStartDeparting the lastStartDeparting to set
     */
    public void setLastStartDeparting(HashMap<Integer, Integer> lastStartDeparting) {
        this.lastStartDeparting = lastStartDeparting;
    }

    public int estimateExpectedTimeArrival() {
        //System.out.println("nodeTimeouts" + getNodeTimeouts());
        if (nodeTimeoutsArrival.isEmpty()) {
            return INITIAL_TIMEOUT;
        }
        //for (String key : getNodeTimeouts().keySet()) {
        ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : nodeTimeoutsArrival) {
            dtimeout.add(d.doubleValue());
        }
        if (dtimeout.size() >= WINDOW_SIZE) {
            dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size()));
        }
        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getMedian();
    }

    public double getStdDevTimeoutArrival() {
        if (nodeTimeoutsArrival.isEmpty()) {
            return 0;
        }
        ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : nodeTimeoutsArrival) {
            dtimeout.add(d.doubleValue());
        }
        if (dtimeout.size() >= WINDOW_SIZE) {
            dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size() - 1));
        }
        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getStdDevMedian();
    }

    /**
     * @return the lastAgentArrival
     */
    public HashMap<Integer, Integer> getLastAgentArrive() {
        return lastAgentArrival;
    }

    public int getLastAgentArrive(int agentId) {
        return lastAgentArrival.get(agentId);
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
}
