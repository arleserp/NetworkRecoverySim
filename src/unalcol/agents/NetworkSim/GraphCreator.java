/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import java.util.ArrayList;
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
        //would not be duplicated
//        Language languaje;
//        AgentProgram program;
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
            GraphElements.MyVertex v = new GraphElements.MyVertex("p"+a++);
            ArrayList Data = new ArrayList();
            Data.add(Math.random());
            v.setData(Data);
            return v;
        }

        public Vector getAgents() {
            return agentes;
        }
    }

    static class EdgeFactory implements Factory<String> {
        int chn = 0;
        int input;

        public String create() {
            return (String) "e" + chn++;
        }
    }
   
}
