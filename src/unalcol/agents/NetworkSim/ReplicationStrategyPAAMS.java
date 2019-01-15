/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import unalcol.agents.NetworkSim.util.StatisticsNormalDist;
import unalcol.agents.NetworkSim.util.StringNodeChainHelper;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class ReplicationStrategyPAAMS extends ReplicationStrategyInterface {

    public ReplicationStrategyPAAMS() {
        super();
    }

    @Override
    public void calculateTimeout() {
        Iterator<Map.Entry<Integer, Integer>> iter = getFollowedAgents().entrySet().iterator();
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageFreeResp().entrySet().iterator();
        // System.out.println("node timeouts" + getNodeTimeouts());

        while (iter.hasNext()) {
            //Key: agentId|roundNumber
            Map.Entry<Integer, Integer> Key = iter.next();
            int k = Key.getKey();

            while (iterM.hasNext()) {
                //KeyM: agentId
                Map.Entry<String, Integer> KeyM = iterM.next();
                String[] dataKey = KeyM.getKey().split("-");
                int agentId = Integer.valueOf(dataKey[0]);
                String nodeId = dataKey[1];

                nodeId = StringNodeChainHelper.trimNodeNeighbor(nodeId);
               // System.out.println("NodeId xxxxxxxxxxxxxxxxxxx" + nodeId);
                
                if (agentId == k && getLastAgentDeparting().containsKey(k) && getLastMessageFreeResp().containsKey(KeyM.getKey())) {
                    int diff = Math.abs(getLastMessageFreeResp().get(KeyM.getKey()) - getLastAgentDeparting().get(k));
                    //System.out.println("diff" +  diff);
                    // if (diff != 0) {
                    if (!nodeTimeouts.containsKey(nodeId)) {
                        getNodeTimeouts().put(nodeId, new ArrayList());
                        getNodeTimeouts().get(nodeId).add(INITIAL_TIMEOUT);
                    }

                    if (getNodeTimeouts().get(nodeId).size() >= WINDOW_SIZE) {
                        nodeTimeouts.put(nodeId, new ArrayList<>(nodeTimeouts.get(nodeId).subList(nodeTimeouts.get(nodeId).size() - WINDOW_SIZE, nodeTimeouts.get(nodeId).size())));
                    }
                    
                    if (diff > 20) {
                        getNodeTimeouts().get(nodeId).add(diff);
                    }
                    //System.out.println("calculatetimeout size getNodeTimeOuts():" + nodeId + "+" + getNodeTimeouts().get(nodeId).size());
                    //}
                    //System.out.println("node:" + getVertex().getName() + ", antes:" + getLastMessageArrival());
                    getLastAgentDeparting().remove(k);
                    iterM.remove();
                }
            }
        }
    }

    @Override
    public void addTimeout(int timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int estimateTimeout() {
        // return INITIAL_TIMEOUT;
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
    }

    @Override
    public double getStdDevTimeout() {
        int maxMedianStdTimeout = Integer.MIN_VALUE;
        //System.out.println("getStdTimeout nodeTimeouts" + getNodeTimeouts());

        if (getNodeTimeouts().isEmpty()) {
            return 0;
        }

        for (String key : getNodeTimeouts().keySet()) {
            ArrayList<Double> dtimeout = new ArrayList();
            for (Integer d : getNodeTimeouts().get(key)) {
                dtimeout.add(d.doubleValue());
            }

            if (dtimeout.size() >= WINDOW_SIZE) {
                dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size()));
            }

            //if (dtimeout.size() > 1) {
            StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
            if (st.getStdDev() > maxMedianStdTimeout) {
                maxMedianStdTimeout = (int) st.getStdDevMedian();
            }
        }

        if (maxMedianStdTimeout == Integer.MIN_VALUE) {
            return 0;
        }
        //System.out.println("Max std timeout" + maxMedianStdTimeout);
        return maxMedianStdTimeout;
    }

    @Override
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
            dtimeout = new ArrayList<>(dtimeout.subList(dtimeout.size() - WINDOW_SIZE, dtimeout.size()));
        }
        //if (dtimeout.size() > 1) {
        StatisticsNormalDist st = new StatisticsNormalDist(dtimeout, dtimeout.size());
        return (int) st.getMedian();

    }

    @Override
    public boolean containsAgent(int agentId) {
        return getFollowedAgents().containsKey(agentId);
    }

    @Override
    public void removeReferences(int agentId) {
        getFollowedAgents().remove(agentId);
        getLastAgentDeparting().remove(agentId);
        getFollowedAgentsLocation().remove(agentId);
        getResponsibleAgentsPrevLocations().remove(agentId);
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageFreeResp().entrySet().iterator();
        while (iterM.hasNext()) {
            Map.Entry<String, Integer> KeyM = iterM.next();
            String[] dataKey = KeyM.getKey().split("-");
            int agId = Integer.valueOf(dataKey[0]);
            if (agId == agentId) {
                iterM.remove();
            }
        }
        //System.out.println("removeeeeeeeeeeeeeeeeee!!!!!");
    }
    
    @Override
    public void removeReferencesForCreation(int agentId) {
        getLastAgentDeparting().remove(agentId);
        getFollowedAgentsLocation().remove(agentId);
        getResponsibleAgentsPrevLocations().remove(agentId);
        Iterator<Map.Entry<String, Integer>> iterM = getLastMessageFreeResp().entrySet().iterator();
        while (iterM.hasNext()) {
            Map.Entry<String, Integer> KeyM = iterM.next();
            String[] dataKey = KeyM.getKey().split("-");
            int agId = Integer.valueOf(dataKey[0]);
            if (agId == agentId) {
                iterM.remove();
            }
        }
        //System.out.println("removeeeeeeeeeeeeeeeeee!!!!!");
    }
}
