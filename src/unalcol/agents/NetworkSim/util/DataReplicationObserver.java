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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.SimulationParameters;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication;

/**
 *
 * @author Arles Rodriguez
 */
public class DataReplicationObserver implements Observer {

    JFrame frame;
    BasicVisualizationServer<GraphElements.MyVertex, String> vv = null;
    boolean added = false;
    public static boolean isUpdating;
    HashMap<Integer, Double> globalInfo = new HashMap();
    HashMap<Integer, Integer> agentsNumber = new HashMap<>();
    HashMap<Integer, Integer> nodesComplete = new HashMap<>();

    XYSeries agentsLive;
    XYSeriesCollection juegoDatos = new XYSeriesCollection();

    public DataReplicationObserver() {
        frame = new JFrame("Simple Graph View");
        //frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(vv);
        //frame.setPreferredSize(new Dimension(600, 600));
        //frame.setLocationRelativeTo(null);
        // frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        isUpdating = false;
        agentsLive = new XYSeries("agentsLive");
        juegoDatos.addSeries(agentsLive);
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        //System.out.println("observer update");
        if (o instanceof NetworkEnvironmentReplication) {
            final NetworkEnvironmentReplication n = (NetworkEnvironmentReplication) o;

            if (Math.random() > 0.9) { //Receives to many notify if replication explodes.
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
                        layout = new CircleLayout<>(g);
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
                        if (n.getLocationAgents().containsValue(i)) {
                            return Color.YELLOW;
                        }
                        if (n.getVisitedNodes().contains(i)) {
                            return Color.BLUE;
                        }
                        //if(i.getData().size() > 0){
                        //    System.out.println("i"+ i.getData().size());
                        //}
                        if (i.getData().size() == n.getTopology().getVertices().size()) {
                            return Color.GREEN;
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
            }

            //System.out.println("World age" + n.getAge() + ", info:" + n.getAmountGlobalInfo());
            if (!globalInfo.containsKey(n.getAge())) {
                //System.out.println("n" + n.getAge() + ", al:" + n.getAgentsLive());
                synchronized (DataReplicationObserver.class) {
                    agentsLive.add(n.getAge(), n.getAgentsLive());
                }

                globalInfo.put(n.getAge(), n.getAmountGlobalInfo());
            }

            if (!agentsNumber.containsKey(n.getAge())) {
                agentsNumber.put(n.getAge(), n.getAgentsLive());
            }

            if (!nodesComplete.containsKey(n.getAge())) {
                nodesComplete.put(n.getAge(), n.getCompletionPercentage());
            }

            boolean areAllAgentsDead = n.areAllAgentsDead();
            if ((SimulationParameters.maxIter == -1 && n.nodesComplete()) || (SimulationParameters.maxIter >= 0 && n.getAge() >= SimulationParameters.maxIter) /*|| n.getAgentsDie() == (n.getTotalAgents())*/ || (!SimulationParameters.activateReplication.equals("replalgon") && areAllAgentsDead)) {
                //StatsTemperaturesMapImpl sti = new StatsTemperaturesMapImpl("experiment-p-" + ((World) obs).getAgents().size() + "- pf-" + pf + ".csv");

                if (areAllAgentsDead) {
                    if (agentsNumber.containsKey(n.getAge())) {
                        agentsNumber.put(n.getAge() + 1, 0);
                    }
                }
                if (nodesComplete.containsKey(n.getAge())) {
                    nodesComplete.put(n.getAge(), n.getCompletionPercentage());
                }

                if (!isUpdating) {
                    System.out.println("stopping simulation");
                    isUpdating = true;
                    n.stop();
                    StatisticsProviderReplication sti;
                    String filename = "exp+ps+" + SimulationParameters.popSize + "+pf+" + SimulationParameters.pf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + n.getTopology().getEdges().size() + "+v+" + n.getTopology().getVertices().size() + "+" + SimulationParameters.graphMode;

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
                    filename += "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay;
                    String fileImage = filename + ".jpg";
                    String fileImageAgentsLive = filename + "+agentsLive.jpg";
                    saveImage(fileImageAgentsLive);
                    String pref = filename;
                    pref = pref.replaceAll(".graph", "");
                    filename += ".csv";

                    //Write global information info
                    String dirName = pref + "+info";
                    createDir(dirName);
                    String infoStats = "./" + dirName + "/" + pref + "+" + getFileName() + "+infostats.csv";

                    PrintWriter escribir = null;
                    try {
                        escribir = new PrintWriter(new BufferedWriter(new FileWriter(infoStats, true)));
                        SortedSet<Integer> keys = new TreeSet<>(globalInfo.keySet());
                        for (int x : keys) {
                            escribir.println(x + "," + globalInfo.get(x));
                        }
                        escribir.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Write agents live number
                    String agNumberdirName = pref + "+agentNumber";
                    createDir(agNumberdirName);
                    String agentNumberStats = "./" + agNumberdirName + "/" + pref + "+" + getFileName() + "+agentnumber.csv";

                    PrintWriter escribirAgentNumber = null;
                    try {
                        escribirAgentNumber = new PrintWriter(new BufferedWriter(new FileWriter(agentNumberStats, true)));
                        SortedSet<Integer> keysAg = new TreeSet<>(agentsNumber.keySet());
                        for (int x : keysAg) {
                            escribirAgentNumber.println(x + "," + agentsNumber.get(x));
                        }
                        escribirAgentNumber.close();

                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Write completion percentage
                    //Write agents live number
                    String ncNumberdirName = pref + "+nodeComplete";
                    createDir(ncNumberdirName);
                    String ncNumberStats = "./" + ncNumberdirName + "/" + pref + "+" + getFileName() + "+nodecomplete.csv";

                    PrintWriter escribirNodeCompleteNumber = null;
                    try {
                        escribirNodeCompleteNumber = new PrintWriter(new BufferedWriter(new FileWriter(ncNumberStats, true)));
                        SortedSet<Integer> nodesAg = new TreeSet<>(nodesComplete.keySet());
                        for (int y : nodesAg) {
                            escribirNodeCompleteNumber.println(y + "," + nodesComplete.get(y));
                        }
                        escribirNodeCompleteNumber.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    sti = new StatisticsProviderReplication(filename);

                    //ToFix: Statist
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
        /*Calendar c = new GregorianCalendar();
        String dia, mes, annio, hora, minutos, segundos;
        dia = Integer.toString(c.get(Calendar.DATE));
        mes = Integer.toString(c.get(Calendar.MONTH) + 1);
        annio = Integer.toString(c.get(Calendar.YEAR));
        hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        minutos = Integer.toString(c.get(Calendar.MINUTE));
        segundos = Integer.toString((c.get(Calendar.SECOND)));
        return annio + mes + dia + hora + minutos + segundos;
         */
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);

        return reportDate;
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

    public void saveImage(String filename) {
        FileOutputStream output;
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Agents Live", "round number", "agents",
                juegoDatos, PlotOrientation.VERTICAL,
                true, true, false);

        try {
            output = new FileOutputStream(filename + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 400, 400, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataReplicationObserver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataReplicationObserver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
