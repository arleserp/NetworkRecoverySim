/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.Edge;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.xml.soap.Node;
import org.apache.commons.collections15.Transformer;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.SyncronizationMain;
import unalcol.agents.NetworkSim.environment.NetworkEnvironment;

/**
 *
 * @author Arles Rodriguez
 */
public class GraphVisualizationObserver implements Observer {

    JFrame frame;
    BasicVisualizationServer<GraphElements.MyVertex, String> vv = null;
    boolean added = false;
    boolean isUpdating;

    public GraphVisualizationObserver() {
        frame = new JFrame("Simple Graph View");
        //frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(vv);
        //frame.setPreferredSize(new Dimension(600, 600));
        //frame.setLocationRelativeTo(null);
        // frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        isUpdating = false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof NetworkEnvironment) {
            final NetworkEnvironment n = (NetworkEnvironment) o;
            // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
            Graph<GraphElements.MyVertex, String> g = n.getTopology();
            Layout<GraphElements.MyVertex, String> layout = null;

            switch (SyncronizationMain.graphMode) {
                case "scalefree":
                    layout = new ISOMLayout<>(g);
                    break;
                case "smallworld":
                    layout = new CircleLayout<>(g);
                    break;
                case "community":
                    layout = new CircleLayout<>(g);
                    break;
                case "kleinberg":
                    layout = new CircleLayout<>(g);
                    break;
                case "lattice":
                    layout = new ISOMLayout<>(g);
                    break;
                default:
                    layout = new ISOMLayout<>(g);
                    break;
            }

            BasicVisualizationServer<GraphElements.MyVertex, String> vv = new BasicVisualizationServer<>(layout);
            vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size

            // vv.getRenderContext().setVertexFillPaintTransformer(n.vertexColor);
            // vv.getRenderContext().setEdgeDrawPaintTransformer(n.edgeColor);
            Transformer<GraphElements.MyVertex, Paint> vertexColor = new Transformer<GraphElements.MyVertex, Paint>() {
                @Override
                public Paint transform(GraphElements.MyVertex i) {
                    if (n.getLocationAgents().contains(i)) {
                        return Color.YELLOW;
                    }
                    if (n.getVisitedNodes().contains(i)) {

                        return Color.BLUE;
                    }
                    return Color.RED;
                }
            };

            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
            //n.setVV(vv);
            vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);

            if (!added) {
                frame.getContentPane().add(vv);
                added = true;
                frame.pack();
                frame.setVisible(true);
            } else {
                frame.repaint();
            }

            
            if ((SyncronizationMain.maxIter == -1 && n.getIdBest() != -1) || (SyncronizationMain.maxIter >= 0 && n.getAge() >= SyncronizationMain.maxIter)) {
                //StatsTemperaturesMapImpl sti = new StatsTemperaturesMapImpl("experiment-p-" + ((World) obs).getAgents().size() + "- pf-" + pf + ".csv");
                if (!isUpdating) {
                    isUpdating = true;
                    n.stop();
                    StatisticsProvider sti;
                    String filename = "exp+ps+" + n.getAgents().size() + "+pf+" + SyncronizationMain.pf + "+mode+" + SyncronizationMain.motionAlg + "+maxIter+" + SyncronizationMain.maxIter + "+e+" + n.topology.getEdges().size() + "+v+" + n.topology.getVertices().size() + "+" + SyncronizationMain.graphMode;

                    if (SyncronizationMain.graphMode.equals("smallworld")) {
                        filename += "+beta+" + SyncronizationMain.beta;
                        filename += "+degree+" + SyncronizationMain.degree;
                    }

                    if (SyncronizationMain.graphMode.equals("community")) {
                        filename += "+beta+" + SyncronizationMain.beta;
                        filename += "+degree+" + SyncronizationMain.degree;
                        filename += "+clusters+" + SyncronizationMain.clusters;
                    }

                    if (SyncronizationMain.graphMode.equals("scalefree")) {
                        filename += "+stnds+" + SyncronizationMain.startNodesScaleFree;
                        filename += "+edgetat+" + SyncronizationMain.edgesToAttachScaleFree;
                        filename += "+numsteps+" + SyncronizationMain.numSteps;
                    }

                    if (SyncronizationMain.graphMode.equals("lattice")) {
                        filename += "+rows+" + SyncronizationMain.rows;
                        filename += "+col+" + SyncronizationMain.columns;
                    }
                    String fileImage = filename + ".jpg";
                    String graphStats = filename + ".gstats.csv";
                    filename += ".csv";
                    //draw graph
                    VisualizationImageServer<GraphElements.MyVertex, String> vis
                            = new VisualizationImageServer<>(vv.getGraphLayout(),
                                    vv.getGraphLayout().getSize());

                    BufferedImage image = (BufferedImage) vis.getImage(
                            new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                                    vv.getGraphLayout().getSize().getHeight() / 2),
                            new Dimension(vv.getGraphLayout().getSize()));

// Write image to a png file
                    File outputfile = new File(fileImage + ".png");

                    try {
                        ImageIO.write(image, "png", outputfile);
                    } catch (IOException e) {
                        // Exception handling
                    }
                    //

                    try {
                        PrintWriter escribir;
                        escribir = new PrintWriter(new BufferedWriter(new FileWriter(graphStats, true)));
                        escribir.println("Average Path Length: " + GraphStats.computeAveragePathLength(g));
                        Map<GraphElements.MyVertex, Double> m = GraphStats.clusteringCoefficients(g);
                        escribir.println("Clustering coeficients:" + m);
                        escribir.println("Average Clustering Coefficient: " + GraphStats.averageCC(g));
                        escribir.println("Average degree: " + GraphStats.averageDegree(g));
                        escribir.println("StdDev Average Path Length: " + GraphStats.computeStdDevAveragePathLength(g));
                        escribir.println("StdDev Degree: " + GraphStats.computeStdDevAveragePathLength(g));
                        escribir.close();
                    } catch (IOException ex) {
                        Logger.getLogger(StatisticsProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    sti = new StatisticsProvider(filename);
                    sti.printStatistics(n);
                    System.out.println("The end" + n.getAge());
                    System.exit(0);
                }
            }
        }

        // Transformer maps the vertex number to a vertex property
    }
}
