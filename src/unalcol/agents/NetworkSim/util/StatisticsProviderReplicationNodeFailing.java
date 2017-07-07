/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.agents.NetworkSim.MobileAgent;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication;

/**
 *
 * @author INVESTIGADOR
 */
class StatisticsProviderReplicationNodeFailing {

    private String reportFile;

    StatisticsProviderReplicationNodeFailing(String filename) {
        reportFile = filename;
    }

    Hashtable getStatisticsInteger(NetworkEnvironmentReplication w) {
        Hashtable Statistics = new Hashtable();
        int n = w.getAgents().size();
        ArrayList<Double> msgin = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (w.getAgent(i) instanceof Node) {
                Node node = (Node) w.getAgent(i);
                msgin.add((double) node.getnMsgRecv());
            }
        }
        StatisticsNormalDist strecv = new StatisticsNormalDist(msgin, msgin.size());
        Statistics.put("avgRecv", strecv.getMean());
        Statistics.put("stdDevRecv", strecv.getStdDev());
        Statistics.put("round", w.getAge());
        System.out.println("stats: " + Statistics);
        return Statistics;
    }

    void printStatistics(NetworkEnvironmentReplication w) {
        Hashtable st = getStatisticsInteger(w);
        try {
            PrintWriter escribir;
            escribir = new PrintWriter(new BufferedWriter(new FileWriter(reportFile+".csv", true)));
            escribir.println(st.get("avgRecv") + "," + st.get("stdDevRecv") + "," + st.get("round"));
            escribir.close();
        } catch (IOException ex) {
            Logger.getLogger(StatisticsProviderReplicationNodeFailing.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
