/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.Graph;
import java.util.HashSet;
import java.util.Set;
import unalcol.agents.NetworkSim.util.WattsBetaSmallWorldGenerator;

/**
 *
 * @author Arles Rodriguez
 */
public class graphSimpleFactory {

    public static Graph<GraphElements.MyVertex, String> createGraph(String topology) {
        //Network creation
        //GraphCreator.VertexFactory v =  new GraphCreator.VertexFactory(languaje, ap, dataset);
        //temp new
        Graph<GraphElements.MyVertex, String> g = null;
        int seed = (int) (Math.random() * 10000);

        GraphCreator.VertexFactory v = new GraphCreator.VertexFactory();

       
        switch (topology) {
            case "scalefree":
                Set<GraphElements.MyVertex> seedSet = new HashSet<>();
                BarabasiAlbertGenerator bag = new BarabasiAlbertGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), 1, 2, seed, seedSet);
                bag.evolveGraph(SyncronizationMain.vertexNumber - 1);
                g = bag.create();
                break;
            case "smallworld":
                g = new WattsBetaSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SyncronizationMain.vertexNumber, SyncronizationMain.beta, 2, true).generateGraph();
                break;
            case "kleinberg":
                g = new KleinbergSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SyncronizationMain.vertexNumber, 0).create();
                break;
            case "lattice":
                g = new Lattice2DGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SyncronizationMain.rows, SyncronizationMain.columns, false).create();
                //layout = new CircleLayout<>(g);
                break;
            default:
                g = new EppsteinPowerLawGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), SyncronizationMain.rows, SyncronizationMain.columns, 5).create();
                break;
        }

        System.out.println("creating g: " + g.toString() + " with method " + topology);
        return g;
    }
}
