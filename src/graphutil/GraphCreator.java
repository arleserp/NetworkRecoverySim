/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphutil;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.util.ArrayList;
import org.apache.commons.collections15.Factory;
import java.util.Vector;

/**
 *
 * @author Arles Rodriguez
 */
public class GraphCreator {

    public static class GraphFactory implements Factory<Graph<MyVertex, String>> {

        @Override
        public Graph<MyVertex, String> create() {
            return new SparseMultigraph<>();
        }
    }

    public static class VertexFactory implements Factory {
        int a = 0;
        Vector agentes = new Vector();
        ArrayList<Double> allData = new ArrayList();
        
        @Override
        public MyVertex create() {
            //System.out.println("create!");
            MyVertex v = new MyVertex("p"+a++);
            ArrayList Data = new ArrayList();
            Double rnd = Math.random();
            while(allData.contains(rnd)){
                rnd = Math.random();
            }
            Data.add(rnd);
            allData.add(rnd);
            //System.out.println("add"+allData);
            v.setData(Data);
            return v;
        }

        public Vector getAgents() {
            return agentes;
        }
    }

    public static class EdgeFactory implements Factory<String> {
        int chn = 0;
        int input;

        public String create() {
            return (String) "e" + chn++;
        }
    }
   
}
