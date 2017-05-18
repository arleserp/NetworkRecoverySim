/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.environment;

import edu.uci.ics.jung.graph.Graph;
import unalcol.agents.NetworkSim.GraphElements;

/**
 *
 * @author arlese.rodriguezp
 */
public class TopologySingleton {
    private Graph<GraphElements.MyVertex, String> topology;

    public Graph<GraphElements.MyVertex, String> getTopology() {
        return topology;
    }
    
    private static class Holder {
        static final TopologySingleton INSTANCE = new TopologySingleton();
    }

     public static TopologySingleton getInstance() {
        return Holder.INSTANCE;
    }
     
    public void init(Graph<GraphElements.MyVertex, String> t) {
        topology = t;
    }
}
