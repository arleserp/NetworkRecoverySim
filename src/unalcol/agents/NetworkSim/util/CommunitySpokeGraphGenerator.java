/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import unalcol.agents.NetworkSim.GraphCreator;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.SimulationParameters;

/**
 *
 * @author arlese.rodriguezp
 */
public class CommunitySpokeGraphGenerator {

    public static Graph<GraphElements.MyVertex, String> createGraph(GraphCreator.GraphFactory graphFactory, GraphCreator.VertexFactory vertexFactory, GraphCreator.EdgeFactory edgeFactory, String communitySrc, int spokes, int length) {

        Graph<GraphElements.MyVertex, String> g = GraphSerialization.loadDeserializeGraph(communitySrc);
        SimulationParameters.globalData = new ArrayList();
        for (GraphElements.MyVertex vertex : g.getVertices()) {
            SimulationParameters.globalData.removeAll(vertex.getData());
            SimulationParameters.globalData.addAll(vertex.getData());
        }

        
        for (int i = 1; i <= g.getVertexCount(); i++) {
            vertexFactory.create();
        }
        
        for (int i = 1; i <= g.getEdgeCount()*1000; i++) {
            edgeFactory.create();
        }        
        
        
        ArrayList<GraphElements.MyVertex> lastSpokes = new ArrayList<>();
        for (int i = 0; i < spokes; i++) {
            GraphElements.MyVertex sp = vertexFactory.create();
            g.addVertex(sp);                  
            lastSpokes.add(sp);
            g.addEdge(edgeFactory.create(), sp, (GraphElements.MyVertex)g.getVertices().toArray()[i]);                        
        }
        
        
        addSpokesRecursively(lastSpokes, length, vertexFactory, edgeFactory, g);
        
        return g;
    }

    private static void addSpokesRecursively(ArrayList<GraphElements.MyVertex> lastSpokes, int length, GraphCreator.VertexFactory vertexFactory, GraphCreator.EdgeFactory edgeFactory, Graph<GraphElements.MyVertex, String> g) {
        if(length == 1){
            return;
        }
        ArrayList<GraphElements.MyVertex> temp = new ArrayList<>();
        for (int i = 0; i < lastSpokes.size(); i++) {
            GraphElements.MyVertex sp = vertexFactory.create();
            g.addVertex(sp);
            temp.add(sp);
            g.addEdge(edgeFactory.create(), sp, lastSpokes.get(i));                
        }        
        addSpokesRecursively(temp, (length - 1), vertexFactory, edgeFactory, g);
    }

    public CommunitySpokeGraphGenerator() {

    }

}
