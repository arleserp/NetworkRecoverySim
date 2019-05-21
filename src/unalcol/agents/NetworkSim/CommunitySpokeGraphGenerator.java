/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import unalcol.agents.NetworkSim.util.GraphSerialization;

/**
 *
 * @author arlese.rodriguezp
 */
class CommunitySpokeGraphGenerator {

    static Graph<GraphElements.MyVertex, String> createGraph(GraphCreator.GraphFactory graphFactory, GraphCreator.VertexFactory v, GraphCreator.EdgeFactory edgeFactory, String communitySrc, int spokes) {

        Graph<GraphElements.MyVertex, String> g = GraphSerialization.loadDeserializeGraph(communitySrc);
        SimulationParameters.globalData = new ArrayList();
        for (GraphElements.MyVertex vertex : g.getVertices()) {
            SimulationParameters.globalData.removeAll(vertex.getData());
            SimulationParameters.globalData.addAll(vertex.getData());
        }

        
        for (int i = 1; i <= g.getVertexCount(); i++) {
            v.create();
        }
        
        for (int i = 1; i <= g.getEdgeCount()*1000; i++) {
            edgeFactory.create();
        }        
        
        for (int i = 0; i < spokes; i++) {
            GraphElements.MyVertex sp = v.create();
            g.addVertex(sp);                        
            g.addEdge(edgeFactory.create(), sp, (GraphElements.MyVertex)g.getVertices().toArray()[i]);
        }
        
        return g;
    }

    public CommunitySpokeGraphGenerator() {

    }

}
