/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphutil;

import graphcreation.LineGraphGenerator;
import graphcreation.CommunityCircleNetworkGenerator;
import graphcreation.CommunityNetworkGenerator;
import graphcreation.ForestHubAnsSpokeGenerator;
import graphcreation.HubAndSpokeGraphGenerator;
import graphcreation.WattsBetaSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import networkrecoverysim.SimulationParameters;

/**
 *
 * @author Arles Rodriguez
 */
public class GraphFactory {

    public static Graph<MyVertex, String> createGraph(String topology) {
        //Network creation
        //GraphCreator.VertexFactory v =  new GraphCreator.VertexFactory(languaje, ap, dataset);
        //temp new
        Graph<MyVertex, String> g = null;
        int seed = (int) (Math.random() * 10000);

        GraphCreator.VertexFactory v = new GraphCreator.VertexFactory();

        switch (topology) {
            case "scalefree":
                Set<MyVertex> seedSet = new HashSet<>();
                //chgne
                BarabasiAlbertGenerator bag = new BarabasiAlbertGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.startNodesScaleFree, SimulationParameters.edgesToAttachScaleFree, seed, seedSet);
                bag.evolveGraph(SimulationParameters.numSteps - 1);
                g = bag.create();
                SimulationParameters.globalData = v.allData;
                break;
            case "smallworld":
                g = new WattsBetaSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, SimulationParameters.beta, SimulationParameters.degree, true).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "community":
                g = new CommunityNetworkGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, SimulationParameters.beta, SimulationParameters.degree, true, SimulationParameters.clusters).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "communitycircle":
                g = new CommunityCircleNetworkGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, SimulationParameters.beta, SimulationParameters.degree, true, SimulationParameters.clusters).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "kleinberg":
                g = new KleinbergSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, 0).create();
                SimulationParameters.globalData = v.allData;
                break;
            case "lattice":
                g = new Lattice2DGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.rows, SimulationParameters.columns, false).create();
                //layout = new CircleLayout<>(g);
                SimulationParameters.globalData = v.allData;
                break;
            case "line":
                g = new LineGraphGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, false).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "hubandspoke":
                g = new HubAndSpokeGraphGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, false).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "foresthubandspoke":
                g = new ForestHubAnsSpokeGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, SimulationParameters.clusters, false).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "circle":
                g = new LineGraphGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.vertexNumber, true).generateGraph();
                SimulationParameters.globalData = v.allData;
                break;
            case "load":
                g = GraphSerialization.loadDeserializeGraph(SimulationParameters.filename);
                SimulationParameters.globalData = new ArrayList();
//                for (MyVertex vertex : g.getVertices()) {
//                    SimulationParameters.globalData.removeAll(vertex.getData());
//                    SimulationParameters.globalData.addAll(vertex.getData());
//                }
                break;
            default:
                g = new EppsteinPowerLawGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SimulationParameters.rows, SimulationParameters.columns, 5).create();
                SimulationParameters.globalData = v.allData;
                break;
        }
        SimulationParameters.vertexNumber = g.getVertexCount();
        System.out.println("creating g: " + g.toString() + " with method " + topology);
        return g;
    }
}
