/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observer;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import environment.NetworkEnvironment;
import graphutil.MyVertex;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import networkrecoverysim.DataReplicationEscenarioNodeFailing;
import networkrecoverysim.SimulationParameters;
import org.apache.commons.collections15.Transformer;
import staticagents.Node;
import util.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez
 */
public class DataReplicationNodeFailingObserver implements Observer {

    JFrame frame;
    BasicVisualizationServer<MyVertex, String> vv = null;
    boolean added = false;
    boolean isDrawing = false;
    public static boolean isUpdating;
    HashMap<Integer, Double> globalInfo = new HashMap();
    HashMap<Integer, Integer> agentsNumber = new HashMap<>();
    HashMap<Integer, Integer> nodesComplete = new HashMap<>();
    HashMap<Integer, StatisticsNormalDist> roundVsInfoAvg = new HashMap<>();

    public static int lastagentsAlive = -1;
    public static int lastnodesAlive = -1;
    private long lastAge = -1;

    DataReplicationEscenarioNodeFailing dataReplEsc;

    public DataReplicationNodeFailingObserver(DataReplicationEscenarioNodeFailing drs) {
        frame = new JFrame("Simple Graph View");
        //frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(vv);
        //frame.setPreferredSize(new Dimension(600, 600));
        //frame.setLocationRelativeTo(null);
        // frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        isUpdating = false;
        dataReplEsc = drs;
    }

    public class FrameGraphUpdater extends Thread {

        Graph<MyVertex, String> g;
        JFrame frame;
        NetworkEnvironment n;

        public FrameGraphUpdater(Graph<MyVertex, String> g, JFrame frame, NetworkEnvironment ne) {
            this.g = g;
            this.frame = frame;
            this.n = ne;
        }

        public void run() {
            if (isDrawing) {
                return;
            }

            try {
                isDrawing = true;
                if (g.getVertexCount() == 0) {
                    System.out.println("no nodes alive.");
                } else {
                    Layout<MyVertex, String> layout = null;
                    /*
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
                }*/
                    layout = new ISOMLayout<>(g);
                    //layout = new CircleLayout<>(g);

                    BasicVisualizationServer<MyVertex, String> vv = new BasicVisualizationServer<>(layout);
                    vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size

                    // vv.getRenderContext().setVertexFillPaintTransformer(n.vertexColor);
                    // vv.getRenderContext().setEdgeDrawPaintTransformer(n.edgeColor);
                    Transformer<MyVertex, Paint> vertexColor = new Transformer<MyVertex, Paint>() {
                        @Override
                        public Paint transform(MyVertex i) {
//                            if (((NetworkEnvironmentPheromoneReplicationNodeFailing) n).isOccuped(i)) {
//                                return Color.YELLOW;
//                            }
//                            if (n.getVisitedNodes().contains(i)) {
//                                return Color.BLUE;
//                            }
                            //if(i.getData().size() > 0){
                            //    System.out.println("i"+ i.getData().size());
                            //}
                            /*if (i.getData().size() == n.getTopology().getVertices().size()) {
                                return Color.GREEN;
                            }*/
                            return Color.RED;
                        }
                    };

                    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
                    //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
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
            } catch (NullPointerException ex) {
                System.out.println("exeeeeeeeeeeeeeeeeeeeeeeeeeeeepyion" + ex);
                isDrawing = false;
            }
            isDrawing = false;
        }
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        //System.out.println("observer update");
        if (o instanceof NetworkEnvironment) {
            final NetworkEnvironment n = (NetworkEnvironment) o;

//            if (!agentsNumber.containsKey(n.getAge())) {
//                agentsNumber.put(n.getAge(), n.getAgentsLive());
//            }
//
//            if (SimulationParameters.simMode.equals("nhopsinfo")) {
//                if (!roundVsInfoAvg.containsKey(n.getAge())) {
//                    roundVsInfoAvg.put(n.getAge(), n.getAmountOfNeighbourInfo());
//                }
//            }
//            int agentsAlive = n.getAgentsAlive();

            int nodesAlive = n.getNodesAlive();
            if (lastnodesAlive == -1 || nodesAlive != lastnodesAlive) {
                System.out.println("Nodes alive: " + nodesAlive);
                lastnodesAlive = nodesAlive;
            }

//            if (lastagentsAlive == -1 || agentsAlive != lastagentsAlive) {
//                System.out.println("Agents alive: " + agentsAlive);
//                lastagentsAlive = agentsAlive;
//            }

//            System.out.println("maxIter:" + SimulationParameters.maxIter  + ", " + n.getAge());
            if (SimulationParameters.maxIter >= 0 && n.getAge() >= SimulationParameters.maxIter || nodesAlive == 0) {
                if (!isUpdating) {
                    System.out.println("stopping simulation");
                    isUpdating = true;
                    n.stop();

                    String baseFilename = SimulationParameters.reportsFilenamePrefix;
                    System.out.println("base filename:" + baseFilename);
                    
                    if (SimulationParameters.simMode.equals("nhopsinfo")) {
                        String roundVsInfoFileName = baseFilename + "+nhopsInfo+" + SimulationParameters.nhopsChain + "+roundVsInfo";
                        createDir(roundVsInfoFileName);
                        String roundVsInfoStats = "./" + roundVsInfoFileName + "/" + baseFilename + "+" + getFileName() + "+roundVsInfo.csv";
                        PrintWriter writeRoundVsInfo = null;
                        try {
                            writeRoundVsInfo = new PrintWriter(new BufferedWriter(new FileWriter(roundVsInfoStats, true)));

                            SortedSet<Integer> keysAg = new TreeSet<>(roundVsInfoAvg.keySet());
                            for (int x : keysAg) {
                                writeRoundVsInfo.println(x + "," + roundVsInfoAvg.get(x).getMean() + "," + roundVsInfoAvg.get(x).getStdDev());
                            }
                            writeRoundVsInfo.close();
                        } catch (IOException ex) {
                            Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //Write similarity metrics by round by simulation
                    String graphSimilarity = baseFilename + "+similarity";
                    createDir(graphSimilarity);
                    String graphSimilarityStats = "./" + graphSimilarity + "/" + baseFilename + "+" + getFileName() + "+similarity.csv";

                    PrintWriter writeSimilarity = null;
                    try {
                        writeSimilarity = new PrintWriter(new BufferedWriter(new FileWriter(graphSimilarityStats, true)));
                        SortedSet<Integer> keysSim = new TreeSet<>(dataReplEsc.getSimilarity().keySet());
                        for (int x : keysSim) {
                            writeSimilarity.println(x + "," + dataReplEsc.getSimilarity().get(x));
                        }
                        writeSimilarity.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Write agents live number
                    String agNumberdirName = baseFilename + "+agentNumber";
                    createDir(agNumberdirName);
                    String agentNumberStats = "./" + agNumberdirName + "/" + baseFilename + "+" + getFileName() + "+agentnumber.csv";

                    PrintWriter escribirAgentNumber = null;
                    try {
                        escribirAgentNumber = new PrintWriter(new BufferedWriter(new FileWriter(agentNumberStats, true)));
                        SortedSet<Integer> keysAg = new TreeSet<>(agentsNumber.keySet());
                        for (int x : keysAg) {
                            escribirAgentNumber.println(x + "," + agentsNumber.get(x));
                        }
                        escribirAgentNumber.close();

                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Statistics regarding messages received by node.
                    StatisticsProviderReplicationNodeFailing sti = new StatisticsProviderReplicationNodeFailing(baseFilename);

                    //ToFix: Statistis
                    sti.printStatistics(n);

                    SimulationParameters.stopTime = System.currentTimeMillis();
                    System.out.println("The end" + n.getAge() + " time of simulation:" + (SimulationParameters.stopTime-SimulationParameters.startTime));                   
                    System.exit(0);
                }
            }
        }
    }

    private String getFileName() {
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

}
