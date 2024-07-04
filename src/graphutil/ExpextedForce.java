/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphutil;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * implementation of algorithm defined in Lawyer, G. (2015). Understanding the
 * influence of all nodes in a network. Scientific Reports, 5, 1–9.
 * https://doi.org/10.1038/srep08665
 */
public class ExpextedForce {

    static HashMap<MyVertex, Integer> distances;
    static ArrayList<MyVertex> distanceOne;
    static ArrayList<MyVertex> distanceTwo;

    private static boolean getNeighbours(Graph<MyVertex, ?> g, MyVertex v) {
        int lvls = 1;
        Deque<MyVertex> q = new LinkedList<>();
        distances.put(v, 0);
        q.add(v);
        while (!q.isEmpty()) {
            MyVertex current = q.poll();
            List<MyVertex> neigh = new ArrayList<>(g.getNeighbors(current));
            for (MyVertex ne : neigh) {
                if (!distances.containsKey(ne)) {
                    distances.put(ne, distances.get(v) + 1);
                    q.add(ne);
                } else {
                    switch (distances.get(ne)) {
                        case 1:
                            distanceOne.add(ne);
                            break;
                        case 2:
                            distanceTwo.add(ne);
                            break;
                        case 3:
                            return true; //exits                                                    
                    }
                }
            }
        }
        return false; //
    }
    
   /* 
    static double expectedForce(MyVertex v, Graph<MyVertex, ?> g){
        
    }*/

}
