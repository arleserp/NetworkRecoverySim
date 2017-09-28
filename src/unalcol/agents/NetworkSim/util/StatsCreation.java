/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author arlese.rodriguezp
 */
public class StatsCreation {
    public ConcurrentHashMap<String, ArrayList<Integer>> creationLog;
   // public ConcurrentHashMap<Integer, Integer> creationLog;
    
    public StatsCreation() {
        creationLog = new ConcurrentHashMap<>();                
    }
    
    /**
     * 
     * @param node The node to add Statistic
     * @param predecesor Id of predecesor of agent
     */
    public void addCreation(String node, int predecesor){
        if(!creationLog.containsKey(node)){
            creationLog.put(node, new ArrayList());
        }
        creationLog.get(node).add(predecesor);
    }

    public ConcurrentHashMap<String, ArrayList<Integer>> getCreationLog() {
        return creationLog;
    }    
    
    public ArrayList<Integer> getIds(String key){
        return creationLog.get(key);
    }
}
