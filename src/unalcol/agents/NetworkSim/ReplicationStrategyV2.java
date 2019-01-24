/**
 * Version2 of Replicatio S
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import unalcol.agents.NetworkSim.util.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class ReplicationStrategyV2 extends ReplicationStrategyInterface {

    public ReplicationStrategyV2() {
        super();
    }

    @Override
    public void calculateTimeout() {
        Iterator<Map.Entry<Integer, Integer>> iter = getFollowedAgents().entrySet().iterator();

        while (iter.hasNext()) {
            // Key<agentId, FatherId>
            Map.Entry<Integer, Integer> Key = iter.next();
            int agId = Key.getKey();

            if (getFirstDepartingMsgTime().containsKey(agId) && getLimitDepartingMsgTime().containsKey(agId)) {
                int diff = Math.abs(getLimitDepartingMsgTime().get(agId) - getFirstDepartingMsgTime().get(agId));

                if (timeouts.isEmpty()) {
                    timeouts.add(INITIAL_TIMEOUT);
                }

                if (timeouts.size() >= WINDOW_SIZE) {
                    timeouts = new ArrayList<>(timeouts.subList(timeouts.size() - WINDOW_SIZE, timeouts.size()));
                }

                if (diff > 20) {
                    timeouts.add(diff);
                }
                getFirstDepartingMsgTime().remove(agId);
                getLimitDepartingMsgTime().remove(agId);
            }
        }
    }

    @Override
    public void addTimeout(int timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int estimateTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getStdDevTimeout() {
        int maxMedianStdTimeout = Integer.MIN_VALUE;
        //System.out.println("getStdTimeout nodeTimeouts" + getNodeTimeouts());

        if (timeouts.isEmpty()) {
            return 0;
        }

        ArrayList<Double> dtimeout = new ArrayList();
        for (Integer d : timeouts) {
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
        if (maxMedianStdTimeout == Integer.MIN_VALUE) {
            return 0;
        }
        //System.out.println("Max std timeout" + maxMedianStdTimeout);
        return maxMedianStdTimeout;
    }

    @Override
    public int estimateExpectedTime(String notUsed) {
        //System.out.println("nodeTimeouts" + getNodeTimeouts());
        if (timeouts.isEmpty()) {
            return INITIAL_TIMEOUT;
        }
        //for (String key : getNodeTimeouts().keySet()) {
        ArrayList<Double> dtimeout = new ArrayList<>();
        for (Integer d : timeouts) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReferences(int agentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeReferencesForCreation(int agentId) {
        getFollowedAgents().remove(agentId);        
        getFirstDepartingMsgTime().remove(agentId);
        getFollowedAgentsLocation().remove(agentId);
        getResponsibleAgentsPrevLocations().remove(agentId);
        getLimitDepartingMsgTime().remove(agentId);        
    }



}
