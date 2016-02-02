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
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import unalcol.agents.NetworkSim.util.WattsBetaSmallWorldGenerator;

/**
 *
 * @author Arles Rodriguez
 */
public class graphSimpleFactory {

    public static Graph<GraphElements.MyVertex, String> createGraph(String topology, int agentsNumber, int channelsNumber) {
        //Network creation
        //GraphCreator.VertexFactory v =  new GraphCreator.VertexFactory(languaje, ap, dataset);
        //temp new
        Graph<GraphElements.MyVertex, String> g = null;
        int seed = (int) (Math.random() * 10000);

        GraphCreator.VertexFactory v = new GraphCreator.VertexFactory();
        
        Layout<GraphElements.MyVertex, String> layout = null;

        switch (topology) {
            case "scalefree":
                Set<GraphElements.MyVertex> seedSet = new HashSet<>();
                BarabasiAlbertGenerator bag = new BarabasiAlbertGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), 1, 2, seed, seedSet);
                bag.evolveGraph(agentsNumber - 1);
                g = bag.create();
                layout = new ISOMLayout<>(g);
                break;
            case "smallworld":
                g = new WattsBetaSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), agentsNumber, SyncronizationMain.beta , 2, true).generateGraph();
                layout = new CircleLayout<>(g);
                break;    
            case "kleinberg":
                g = new KleinbergSmallWorldGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), agentsNumber, 0).create();
                layout = new CircleLayout<>(g);
                break;
            case "lattice":
                g = new Lattice2DGenerator(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), agentsNumber, channelsNumber, false).create();
                layout = new ISOMLayout<>(g);
                //layout = new CircleLayout<>(g);
                break;
            default:
                g = new EppsteinPowerLawGenerator<>(new GraphCreator.GraphFactory(), v, new GraphCreator.EdgeFactory(), agentsNumber, channelsNumber, 5).create();
            break;
        }
         
        //Visualize topology
        layout.setSize(new Dimension(600, 600)); // sets the initial size of the layout space
        
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<GraphElements.MyVertex, String> vv = new BasicVisualizationServer<GraphElements.MyVertex, String>(layout);
        vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size

       // vv.getRenderContext().setVertexFillPaintTransformer(n.vertexColor);
       // vv.getRenderContext().setEdgeDrawPaintTransformer(n.edgeColor);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        //n.setVV(vv);

        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

        System.out.println("creating g: " + g.toString() + " with method " + topology);
        return g;
    }
}

