/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkrecoverysim;

import staticagents.NodeFailingProgram;
import edu.uci.ics.jung.graph.Graph;
import graphutil.GraphComparator;
import graphutil.GraphSerialization;
import graphutil.MyVertex;
import graphutil.GraphFactory;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Observer;
// unalcol.agent.networkSim.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import mobileagents.MobileAgent;
import mobileagents.MotionProgramSimpleFactory;
import mobileagents.NetworkMessageBuffer;
import observer.DataReplicationNodeFailingObserver;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import serialization.StringSerializer;
import staticagents.NetworkNodeMessageBuffer;
import staticagents.Node;
import environment.NetworkEnvironment;
import environment.NetworkEnvironmentNodeFailingAllInfo;
import environment.NetworkEnvironmentNodeFailingMulticast;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Creates a simulation without graphic interface
 *
 * @author arles.rodriguez
 */
public class DataReplicationEscenarioNodeFailing implements Runnable {

    private NetworkEnvironment world;
    public boolean renderAnts = true;
    public boolean renderSeeking = true;
    public boolean renderCarrying = true;
    int modo = 0;
//    GraphicReportHealingObserver greport;
    int population = 100;
    int vertexNumber = 100;
    int channelNumber = 100;
    float probFailure = (float) 0.1;
    Hashtable<String, Object> positions;
    int width;
    int height;
    private Observer graphVisualization;

    public Observer getGraphVisualization() {
        return graphVisualization;
    }

    public void setGraphVisualization(Observer graphVisualization) {
        this.graphVisualization = graphVisualization;
    }
    ArrayList<MyVertex> locations;
    HashMap<String, Long> networkDelays;
    int indexLoc;
    boolean added = false;
    //JFrame frame;
    //JFrame frame2;
    private boolean isDrawing = false;
    XYSeries agentsLive;
    XYSeries nodesLive;
    XYSeries neighborMatchingSim;
    XYSeriesCollection juegoDatos = new XYSeriesCollection();
    SimilarityAndLiveStatsThread fgup = null;
    private final JPanel networkPanel;
    private final JPanel bPanel;
    private final JButton redraw;
    Graph<MyVertex, String> initialNetwork;

    HashMap<Integer, Double> similarity;
    boolean alreadyPainted = false;
    final Semaphore available = new Semaphore(1);

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop
     * @param pf
     * @param width
     * @param height
     * @return
     */
    DataReplicationEscenarioNodeFailing(int pop, float pf) {
        population = pop;
        probFailure = pf;
        positions = new Hashtable<>();
        System.out.println("Pop: " + population);
        System.out.println("Pf: " + pf);
        System.out.println("Movement: " + SimulationParameters.motionAlg);
        indexLoc = 0;
        //frame = new JFrame("Simple Graph View");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame2 = new JFrame("Agent and Node Number");
        agentsLive = new XYSeries("agentsLive");
        nodesLive = new XYSeries("nodesLive");
        neighborMatchingSim = new XYSeries("Neighbour Sim");

        juegoDatos.addSeries(agentsLive);
        juegoDatos.addSeries(nodesLive);
        juegoDatos.addSeries(neighborMatchingSim);

//        frame2.setLocation(650, 150);
//        frame2.setSize(450, 450);
//        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        bPanel = new JPanel();
        redraw = new JButton("Redraw Network");
        bPanel.add(redraw);
        networkPanel = new JPanel();
//        frame.setSize(650, 650);
//        frame.setVisible(true);
//        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        similarity = new HashMap<>();
    }

    /**
     *
     * Initializes simulation.
     */
    public void init() {
        SimulationParameters.startTime = System.currentTimeMillis();
        Vector<Agent> agents = new Vector();
        List<Node> nodes = new ArrayList<>();
        System.out.println("fp" + probFailure);

        //Language for Agents
        String[] _percepts = {"data", "neighbors"};
        String[] _actions = {"move", "die", "informfailure"};
        SimpleLanguage agentsLanguage = new SimpleLanguage(_percepts, _actions);

        //Language for nodes
        String[] nodePercepts = {"data", "neighbors"};
        String[] nodeActions = {"communicate", "die"};
        SimpleLanguage nodeLanguaje = new SimpleLanguage(nodePercepts, nodeActions);

        //Create graph and initial network
        Graph<MyVertex, String> g = GraphFactory.createGraph(SimulationParameters.graphMode);
        StringSerializer s = new StringSerializer();
        String aCopy = s.serialize(g); //save a copy via serialization
        initialNetwork = (Graph<MyVertex, String>) s.deserialize(aCopy); //create a clone of original graph by deserializing

        //Define prefix filename
        String fileNamePrefix;
        String graphType = SimulationParameters.graphMode;
        graphType = graphType.replaceAll(".graph", "");

        if (SimulationParameters.nofailRounds == 0) {
            if (SimulationParameters.simMode.contains("chain")) {
                fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsChain + "+wsize+" + SimulationParameters.wsize + ".timeout";
            } else {
                fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+wsize+" + SimulationParameters.wsize;
            }
        } else if (SimulationParameters.simMode.contains("chain")) {
            fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsChain + "+wsize+" + SimulationParameters.wsize + "+nofailr+" + SimulationParameters.nofailRounds;
        } else {
            fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+wsize+" + SimulationParameters.wsize + "+nofailr+" + SimulationParameters.nofailRounds + "";
        }

        SimulationParameters.reportsFilenamePrefix = fileNamePrefix;

        //Create nodes
        for (MyVertex v : g.getVertices()) {
            v.setStatus("alive");  //to evaluate if use node marking
            Node n = null;
            NodeFailingProgram np = new NodeFailingProgram(SimulationParameters.npf);
            n = new Node(np, v);
            n.setPfCreate(0);
            NetworkNodeMessageBuffer.getInstance().createBuffer(v.getName());
            agents.add(n);
            nodes.add(n);
        }

        if (!SimulationParameters.simMode.equals("broadcast") && !SimulationParameters.simMode.equals("allinfo")) {
            if (SimulationParameters.filenameLoc.length() > 1) {
                //loadLocations();
                //loadNetworkDelays();
            }
            //Creates "Agents"
            for (int i = 0; i < population; i++) {
                AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
                MobileAgent a = new MobileAgent(program, i);
                MyVertex tmp = getLocation(g);
                System.out.println("tmp" + tmp);
                a.setRound(-1);
                a.setLocation(tmp);
                a.setPrevLocation(tmp);

                a.setPrevPrevLocation(tmp);
                a.setProgram(program);
                a.setAttribute("infi", new ArrayList<>());
                NetworkMessageBuffer.getInstance().createBuffer(a.getId());
                agents.add(a);
//                Initialize implies arrival message from nodes TO review
//                String[] msgnode = new String[3];
//                msgnode[0] = "arrived";
//                msgnode[1] = String.valueOf(a.getId());
//                msgnode[2] = String.valueOf(a.getIdFather());
//                NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode); //no delay
            }
        }

        graphVisualization = new DataReplicationNodeFailingObserver(this);

        switch (SimulationParameters.simMode) {
            case "broadcast":
                world = new NetworkEnvironmentNodeFailingMulticast(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                break;
//            case "chain":
//            case "chainnoloop":
//                world = new NetworkEnvironmentPheromoneReplicationNodeFailingChain(agents, agentsLanguage, nodeLanguaje, g);
//                ((NetworkEnvironmentPheromoneReplicationNodeFailingChain) world).addNodes(nodes);
//                break;
            case "allinfo":
                world = new NetworkEnvironmentNodeFailingAllInfo(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(((NetworkEnvironmentNodeFailingAllInfo) world).loadAllTopology());
                }
                break;
            case "nhopsinfo":
                world = new NetworkEnvironmentNodeFailingAllInfo(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(((NetworkEnvironmentNodeFailingAllInfo) world).loadPartialNetwork(SimulationParameters.nhopsChain, n));
                }
                break;
//            default:
//                world = new NetworkEnvironmentPheromoneReplicationNodeFailing(agents, agentsLanguage, nodeLanguaje, g);
//                ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).addNodes(nodes);
//                break;
        }

        //world.setNetworkDelays(networkDelays);
        world.addObserver(graphVisualization);
        world.sChAndNot();
        world.run();
    }

    public Graph<MyVertex, String> getInitialNetwork() {
        return initialNetwork;
    }

//    @Override
//    public void actionPerformed(ActionEvent ae
//    ) {
//        final JButton source = (JButton) ae.getSource();
//        if (source.equals(redraw)) {
//            redrawNetwork();
//        }
//    }

//    private void redrawNetwork() {
//        DrawCurrentGraphThread fgup2 = new DrawCurrentGraphThread(world.getTopology(), frame, world);
//        fgup2.start();
//    }

    public class SimilarityAndLiveStatsThread implements Runnable {
        NetworkEnvironment environment;

        public SimilarityAndLiveStatsThread(NetworkEnvironment ne) {
            this.environment = ne;
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Nodes and Agent Number vs Round", "Round number", "Agents-Nodes",
                    juegoDatos, PlotOrientation.VERTICAL,
                    true, true, false);
            ChartPanel chpanel = new ChartPanel(chart);

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(chpanel, BorderLayout.NORTH);
        }

        @Override
        public void run() {
            try {
                available.acquire();
                long starTime = System.currentTimeMillis();

                world.updateWorldAge();
                isDrawing = true;

                if (world.getNodes().isEmpty()) {
                    System.out.println("no nodes alive.");
                } else {
//                  int agentsAlive = n.getAgentsAlive();
                    int nodesAlive = environment.getNodesAlive();
                    //System.out.println("n" + n.getAge() + "," + agentsAlive);
                    //System.out.println("n" + n.getAge() + "," + nodesAlive);
                    if (nodesAlive == 0) {
                        System.out.println("no nodes alive.");
                        //break;
                    } else if (environment != null) {
                        if (environment.getAge() % 50 == 0 || (environment.getAge() >= 1000 && environment.getAge() <= 1050) ) { //SimulationParameters.maxIter > 500 && n.getAge() % 50 == 0) || (SimulationParameters.maxIter <= 500 && n.getAge() % 50 == 0)) { //backwards compatibility
//                                agentsLive.add(n.getAge(), agentsAlive);
                            nodesLive.add(environment.getAge(), nodesAlive);
                            //call comparator here!
                            GraphComparator gnm = new GraphComparator();
                            double sim = 0;
                            sim = gnm.calculateSimilarity(environment);
                            neighborMatchingSim.add(environment.getAge(), sim);
                            similarity.put(environment.getAge(), sim);
                            
                            //frame2.repaint();

                            if (environment.getAge() >= 1400 && !alreadyPainted) {
                                String baseFilename = SimulationParameters.reportsFilenamePrefix;
                                String dir = "cmpgraph";
                                createDir(dir);
                                GraphSerialization.saveSerializedGraph("./" + dir + "/" + getFileName() + "+" + baseFilename + "+round+" + environment.getAge() + ".graph", world.getTopology());
                                alreadyPainted = true;
                            }
                        }
                    } // System.out.println("entra:" + n.getAge());
                    //frame2.getGraphics().drawImage(creaImagen(), 0, 0, null);
                }
                long actStopTime = System.currentTimeMillis();
                long timeTaken = actStopTime - starTime;
                //System.out.println("time taken calculating metrics: " + timeTaken);
                //System.out.println("Doing a task during : - Time - " + new Date());
                available.release();
            } catch (InterruptedException ex) {
                System.out.println("Error obtaining live statistics" + ex.toString());
            }
        }
    }

//    public class DrawCurrentGraphThread extends Thread {
//
//        Graph<MyVertex, String> g;
//        JFrame frame;
//        NetworkEnvironment n;
//
//        public DrawCurrentGraphThread(Graph<MyVertex, String> g, JFrame frame, NetworkEnvironment ne) {
//            this.g = g;
//            this.frame = frame;
//            this.n = ne;
//        }
//
//        @Override
//        public void run() {
//            isDrawing = true;
//            if (g.getVertexCount() == 0) {
//                System.out.println("no nodes alive.");
//            } else {
//                //GraphComparator gcmp = new GraphComparator();
//                //nodesLive.add(n.getAge(), gcmp.calculateSimilarity(initialNetwork, g));
//                try {
//                    Layout<MyVertex, String> layout = null;
//
//                    layout = new ISOMLayout<>(world.getTopology());
//
//                    BasicVisualizationServer<MyVertex, String> vv = new BasicVisualizationServer<>(layout);
//                    vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size
//                    //vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//                    //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
//                    //n.setVV(vv);
//                    Transformer<MyVertex, Paint> vertexColor = new Transformer<MyVertex, Paint>() {
//                        @Override
//                        public Paint transform(MyVertex i) {
//                            if (n.isOccuped(i)) {
//                                return Color.YELLOW;
//                            }
//
//                            if (i.getStatus() != null && i.getStatus().equals("failed")) {
//                                return Color.BLACK;
//                            }
//
//                            if (i.getStatus() != null && i.getStatus().equals("visited")) {
//                                return Color.BLUE;
//                            }
//
//                            //if(i.getData().size() > 0){
//                            //    System.out.println("i"+ i.getData().size());
//                            //}
//                            /*if (i.getData().size() == n.getTopology().getVertices().size()) {
//                                return Color.GREEN;
//                            }*/
//                            return Color.RED;
//                        }
//                    };
//                    vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
//                    if (!added) {
//                        networkPanel.add(vv);
//                        added = true;
//                        frame.pack();
//                        frame.setVisible(true);
//                    } else {
//                        frame.repaint();
//                    }
//                } catch (NullPointerException ex) {
//                    System.out.println("exception drawing graph: " + ex.getLocalizedMessage());
//                    isDrawing = false;
//                }
//            }
//        }
//    }

    public HashMap<Integer, Double> getSimilarity() {
        return similarity;
    }

    public void setSimilarity(HashMap<Integer, Double> similarity) {
        this.similarity = similarity;
    }

    /**
     * Runs a simulation.
     *
     */
    @Override
    public void run() {
        if (fgup == null) {
            fgup = new SimilarityAndLiveStatsThread(world);
            //fgup.start();
        }        
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(fgup, 0, 20, TimeUnit.MILLISECONDS);
    }

    public void loadLocations() {
        StringSerializer s = new StringSerializer();
        locations = (ArrayList<MyVertex>) s.loadDeserializeObject(SimulationParameters.filenameLoc);
    }

    public void loadNetworkDelays() {
        String output = SimulationParameters.filenameLoc.replace("loc", "");
        output += "ndelay";
        StringSerializer s = new StringSerializer();
        networkDelays = (HashMap<String, Long>) s.loadDeserializeObject(output);
        System.out.println("net Delays" + networkDelays);
    }

    public MyVertex getLocation(Graph<MyVertex, String> g) {
        if (SimulationParameters.filenameLoc.length() > 1) {
            MyVertex tmp = locations.get(indexLoc++);
            for (MyVertex v : g.getVertices()) {
                if (v.toString().equals(tmp.toString())) {
                    return v;
                }
            }
            //System.out.println("null???");
            return null;
        } else {
            int pos = (int) (Math.random() * g.getVertexCount());
            Collection E = g.getVertices();
            return (MyVertex) E.toArray()[pos];
        }
    }

//    public void saveImage(String filename) {
//        FileOutputStream output;
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "Agents Live", "round number", "agents",
//                juegoDatos, PlotOrientation.VERTICAL,
//                true, true, false);
//
//        try {
//            output = new FileOutputStream(filename + ".jpg");
//            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 400, 400, null);
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(DataReplicationEscenarioNodeFailing.class
//                    .getName()).log(Level.SEVERE, null, ex);
//
//        } catch (IOException ex) {
//            Logger.getLogger(DataReplicationEscenarioNodeFailing.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public BufferedImage creaImagen() {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "ssss", "Round number", "Agents",
                juegoDatos, PlotOrientation.VERTICAL,
                true, true, false);

        BufferedImage image = chart.createBufferedImage(450, 450);
        return image;
    }

    public NetworkEnvironment getWorld() {
        return world;
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

    private String getFileName() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        return reportDate;
    }
}
