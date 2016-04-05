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
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
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
                    
                    filename += ".csv";
                    

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
