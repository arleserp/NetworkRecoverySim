/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package environment;

import edu.uci.ics.jung.graph.Graph;
import graphutil.MyVertex;

/**
 *
 * @author arlese.rodriguezp
 */
public class TopologySingleton {
    private Graph<MyVertex, String> topology;
    
    public Graph<MyVertex, String> getTopology() {
        return topology;
    }
    
    private static class Holder {
        static final TopologySingleton INSTANCE = new TopologySingleton();
    }

     public static TopologySingleton getInstance() {
        return Holder.INSTANCE;
    }
     
    public void init(Graph<MyVertex, String> t) {
        topology = t;
    }
}
