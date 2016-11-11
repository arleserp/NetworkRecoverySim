/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.util.StatisticsNormalDist;

/**
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {

    private GraphElements.MyVertex v;
    private ArrayList<Agent> currentAgents;
    private HashMap<Integer, Integer> responsibleAgents;
    private double pfCreate;
    private int roundsWithOutVisit;
    private int roundsWithoutAck;
    private int nMsgSend;
    private int nMsgRecv;
    private int rounds;
    private HashMap<Integer, Integer> lastAgentArrival;
    private HashMap<String, Integer> lastMessageArrival;
    // private ArrayList<Integer> timeout;
    private int amountRounds;
    private HashMap<String, ArrayList<Integer>> nodeTimeouts;
    private int INITIAL_TIMEOUT = 50;
    private int WINDOW_SIZE = 50;
    //try to stimate pf locally 1/numberofagentcreated
    
    
    public Node(AgentProgram _program, GraphElements.MyVertex ve) {
        super(_program);
        this.v = ve;
        currentAgents = new ArrayList<>();
        responsibleAgents = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageArrival = new HashMap<>();
        rounds = 0;
        nodeTimeouts = new HashMap();
    }

    public Node(AgentProgram _program, GraphElements.MyVertex ve, HashMap tout) {
        super(_program);
        this.v = ve;
        currentAgents = new ArrayList<>();
        responsibleAgents = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageArrival = new HashMap<>();
        rounds = 0;
        nodeTimeouts = tout;
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

    /**
     * @param agentId
     * @return the lastAgentArrival
     */
    public int getLastAgentArrival(int agentId) {
        return getLastAgentArrival().get(agentId);
    }

    /**
     * @param lastAgentArrival the lastAgentArrival to set
     * @param agentId
     */
    public void setLastAgentArrival(int agentId, int nodeAge) {
        this.getLastAgentArrival().put(agentId, nodeAge);
    }

    /**
     * @param agentId
     * @return the lastMessageArrival
     */
    public int getLastMessageArrival(int agentId) {
        return getLastMessageArrival().get(agentId);
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageArrival(int agentId, int nodeAge, String newLocation) {
        String key = agentId + "-" + newLocation;
        getLastMessageArrival().put(key, nodeAge);
    }

    public void calculateTimeout() {
        Iterator<Map.Entry<Integer, Integer>> iter = getResponsibleAgents().entrySet().iterator();
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageArrival().entrySet().iterator();

        if (!getResponsibleAgents().isEmpty()) {
            //to complete
        }
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
        System.out.println("Max median timeout" + maxMedianTimeout);
        return maxMedianTimeout;
        /*ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : timeout) {
            dtimeout.add(d.doubleValue());
        }

        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getMedian();*/
        //} 

        //return Collections.max(getTimeout());
    }

    /**
     * @return the lastAgentArrival
     */
    public HashMap<Integer, Integer> getLastAgentArrival() {
        return lastAgentArrival;
    }

    /**
     * @return the lastMessageArrival
     */
    public HashMap<String, Integer> getLastMessageArrival() {
        return lastMessageArrival;
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageArrival(HashMap<String, Integer> lastMessageArrival) {
        this.lastMessageArrival = lastMessageArrival;
    }

    public double getStdDevTimeout() {
        int maxMedianStdTimeout = Integer.MIN_VALUE;
        System.out.println("nodeTimeouts" + getNodeTimeouts());

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
        System.out.println("Max std timeout" + maxMedianStdTimeout);
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

}
