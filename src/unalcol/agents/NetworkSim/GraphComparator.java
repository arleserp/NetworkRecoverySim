/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import java.util.Iterator;
import unalcol.agents.NetworkSim.util.StringSerializer;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GraphComparator {

    double calculateSimilarity(Graph<GraphElements.MyVertex, String> a, Graph<GraphElements.MyVertex, String> b) {
        String aCopy = StringSerializer.serialize(a);
        String bCopy = StringSerializer.serialize(b);
        Graph<GraphElements.MyVertex, String> copyA = (Graph<GraphElements.MyVertex, String>)StringSerializer.deserialize(aCopy);
        Graph<GraphElements.MyVertex, String> copyB = (Graph<GraphElements.MyVertex, String>)StringSerializer.deserialize(bCopy);
        
        Iterator<GraphElements.MyVertex> it = copyA.getVertices().iterator();
        
        while(it.hasNext()){
            GraphElements.MyVertex va =it.next();
             
       }
        
        
        return 0.0;
    }
}
