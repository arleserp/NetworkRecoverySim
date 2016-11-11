/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.SimulationParameters;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentCollection;

/**
 *
 * @author Arles Rodriguez
 */
public class DataCollectionObserver implements Observer {

    JFrame frame;
    BasicVisualizationServer<GraphElements.MyVertex, String> vv = null;
    boolean added = false;
    boolean isUpdating;
    HashMap<Integer, Double> globalInfo = new HashMap();

    public DataCollectionObserver() {
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
        if (o instanceof NetworkEnvironmentCollection) {
            final NetworkEnvironmentCollection n = (NetworkEnvironmentCollection) o;
            // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
            Graph<GraphElements.MyVertex, String> g = n.getTopology();
            Layout<GraphElements.MyVertex, String> layout = null;

            switch (SimulationParameters.graphMode) {
                case "scalefree":
                    layout = new ISOMLayout<>(g);
                    break;
                case "smallworld":
                    layout = new CircleLayout<>(g);
                    break;
                case "community":
                    layout = new ISOMLayout(g);
                    break;
                case "kleinberg":
                    layout = new CircleLayout<>(g);
                    break;
                case "circle":
                    layout = new ISOMLayout<>(g);
                    break;
                case "line":
                    layout = new ISOMLayout<>(g);
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

            //System.out.println("World age" + n.getAge() + ", info:" + n.getAmountGlobalInfo());
            if (!globalInfo.containsKey(n.getAge())) {
                globalInfo.put(n.getAge(), n.getAmountGlobalInfo());
            }
            if ((SimulationParameters.maxIter == -1 && n.getIdBest() != -1) || (SimulationParameters.maxIter >= 0 && n.getAge() >= SimulationParameters.maxIter) || n.getAgentsDie() == (n.getAgents().size())) {
                //StatsTemperaturesMapImpl sti = new StatsTemperaturesMapImpl("experiment-p-" + ((World) obs).getAgents().size() + "- pf-" + pf + ".csv");

                if (!isUpdating) {
                    isUpdating = true;
                    n.stop();
                    StatisticsProvider sti;
                    String filename = "exp+ps+" + n.getAgents().size() + "+pf+" + SimulationParameters.pf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + n.topology.getEdges().size() + "+v+" + n.topology.getVertices().size() + "+" + SimulationParameters.graphMode;

                    if (SimulationParameters.graphMode.equals("smallworld")) {
                        filename += "+beta+" + SimulationParameters.beta;
                        filename += "+degree+" + SimulationParameters.degree;
                    }

                    if (SimulationParameters.graphMode.equals("community")) {
                        filename += "+beta+" + SimulationParameters.beta;
                        filename += "+degree+" + SimulationParameters.degree;
                        filename += "+clusters+" + SimulationParameters.clusters;
                    }

                    if (SimulationParameters.graphMode.equals("scalefree")) {
                        filename += "+stnds+" + SimulationParameters.startNodesScaleFree;
                        filename += "+edgetat+" + SimulationParameters.edgesToAttachScaleFree;
                        filename += "+numsteps+" + SimulationParameters.numSteps;
                    }

                    if (SimulationParameters.graphMode.equals("lattice")) {
                        filename += "+rows+" + SimulationParameters.rows;
                        filename += "+col+" + SimulationParameters.columns;
                    }
                    String fileImage = filename + ".jpg";

                    String pref = filename;
                    pref = pref.replaceAll(".graph", "");
                    filename += ".csv";

                    String dirName = pref + "+info";
                    createDir(dirName);
                    String infoStats = "./" + dirName + "/" + pref + "+" + getFileName() + "+infostats.csv";

                    PrintWriter escribir = null;
                    try {
                        escribir = new PrintWriter(new BufferedWriter(new FileWriter(infoStats, true)));
                    } catch (IOException ex) {
                        Logger.getLogger(DataCollectionObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    for (int x : globalInfo.keySet()) {
                        escribir.println(x + "," + globalInfo.get(x));
                    }
                    escribir.close();
                    
                    sti = new StatisticsProvider(filename);
                    sti.printStatistics(n);
                    System.out.println("keys" + globalInfo);
                    System.out.println("The end" + n.getAge());
                    System.exit(0);
                }
            }
        }

        // Transformer maps the vertex number to a vertex property
    }

    private String getFileName() {
        Calendar c = new GregorianCalendar();
        String dia, mes, annio, hora, minutos, segundos;
        dia = Integer.toString(c.get(Calendar.DATE));
        mes = Integer.toString(c.get(Calendar.MONTH) + 1);
        annio = Integer.toString(c.get(Calendar.YEAR));
        hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        minutos = Integer.toString(c.get(Calendar.MINUTE));
        segundos = Integer.toString((c.get(Calendar.SECOND)));
        return annio + mes + dia + hora + minutos + segundos;
    }

    private void createDir(String filename) {
        File theDir = new File(filename);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + filename);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                System.out.println("Security Exception!");
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

    }
}
