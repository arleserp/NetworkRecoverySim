/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.collections15.Factory;
//import unalcol.agents.AgentProgram;
//import unalcol.agents.distributed.testing.GenerateIntegerDataSet;
import java.util.Vector;


/**
 *
 * @author Arles Rodriguez
 */
public class GraphCreator {

    public static class GraphFactory implements Factory<Graph<GraphElements.MyVertex, String>> {

        @Override
        public Graph<GraphElements.MyVertex, String> create() {
            return new SparseMultigraph<>();
        }
    }

    static class VertexFactory implements Factory {

        int a = 0;
        Vector agentes = new Vector();
        ArrayList<Double> allData = new ArrayList();
        
        //would not be duplicated
//        Language languaje;
//        AgentProgram program;x
//        GenerateIntegerDataSet dataset;

//        VertexFactory(Language lang, AgentProgram ap) {
//            languaje = lang;
//            program = ap;
//        }
//
//        VertexFactory(Language lang, AgentProgram ap, GenerateIntegerDataSet ds) {
//            languaje = lang;
//            program = ap;
//            dataset = ds;
//        }

        @Override
        public GraphElements.MyVertex create() {
            //System.out.println("create!");
            GraphElements.MyVertex v = new GraphElements.MyVertex("p"+a++);
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
