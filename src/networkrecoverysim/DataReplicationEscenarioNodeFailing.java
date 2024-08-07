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
import mobileagents.NetworkMessageMobileAgentBuffer;
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
import environment.NetworkEnvironmentNodeFailingMobileAgents;
import environment.NetworkEnvironmentNodeFailingMulticast;
import environment.NetworkEnvironmentNodeFailingTrickle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import trickle.Trickle;

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
    private Observer graphVisualizationObserver;

    public Observer getGraphVisualizationObserver() {
        return graphVisualizationObserver;
    }

    public void setGraphVisualizationObserver(Observer graphVisualizationObserver) {
        this.graphVisualizationObserver = graphVisualizationObserver;
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
    //HashMap<Integer, String> networkAndMemoryStats;
    // HashMap<Integer, HashMap> localStatsByRound;
    HashMap<Integer, Integer> mobileAgentsAlive;
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
//        networkAndMemoryStats = new HashMap<>();
//        localStatsByRound = new HashMap<>();
        mobileAgentsAlive = new HashMap<>();

    }

    /**
     *
     * Initializes simulation.
     */
    public void init() {
        SimulationParameters.startTime = System.currentTimeMillis();
        Vector<Agent> agents = new Vector();
        List<Node> nodes = new ArrayList<>();
        List<MobileAgent> mobileAgents = new ArrayList<>();
        System.out.println("fp" + probFailure);

        //Language for Agents
        String[] _percepts = {"data", "neighbors"};
        String[] _actions = {"move", "die", "informfailure"};
        SimpleLanguage mobileAgentsLanguage = new SimpleLanguage(_percepts, _actions);

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
                fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsPrune + "+redFactor+" + SimulationParameters.redundancyFactor + ".timeout";
            } else {
                fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+redFactor+" + SimulationParameters.redundancyFactor;
            }
        } else if (SimulationParameters.simMode.contains("chain")) {
            fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsPrune + "+redFactor+" + SimulationParameters.redundancyFactor + "+nofailr+" + SimulationParameters.nofailRounds;
        } else {
            fileNamePrefix = "exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+redFactor+" + SimulationParameters.redundancyFactor + "+nofailr+" + SimulationParameters.nofailRounds + "";
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

        if (SimulationParameters.simMode.contains("mobileAgents")) {
            if (SimulationParameters.filenameLoc.length() > 1) {
                loadLocations();
                //loadNetworkDelays();
            }
            //Creates "Mobile Agents"
            for (int i = 0; i < population; i++) {
                AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
                MobileAgent a = new MobileAgent(program, i);
                MyVertex tmp = getLocation(g);
                System.out.println("Agent" + i + " starts at node: " + tmp);
                a.setRound(-1);
                a.setLocation(tmp);
                a.setPrevLocation(tmp);
                a.setPrevPrevLocation(tmp);
                a.setProgram(program);
                a.setAttribute("infi", new ArrayList<>());
                NetworkMessageMobileAgentBuffer.getInstance().createBuffer(a.getId());
                agents.add(a);
                mobileAgents.add(a);
            }
        }

        graphVisualizationObserver = new DataReplicationNodeFailingObserver(this);

        switch (SimulationParameters.simMode) {
            case "broadcast":
                world = new NetworkEnvironmentNodeFailingMulticast(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                break;
            case "mobileAgents":
                world = new NetworkEnvironmentNodeFailingMobileAgents(agents, nodeLanguaje, mobileAgentsLanguage, g);
                world.addNodes(nodes);
                world.addMobileAgents(mobileAgents);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(world.loadPartialNetwork(0, n)); //load current neighbourhood in hop 0
                    //System.out.println("n" +n);
                }

//            case "chain":
//            case "chainnoloop":
//                world = new NetworkEnvironmentPheromoneReplicationNodeFailingChain(agents, agentsLanguage, nodeLanguaje, g);
//                ((NetworkEnvironmentPheromoneReplicationNodeFailingChain) world).addNodes(nodes);
                break;
            case "mobileAgentsP":
                world = new NetworkEnvironmentNodeFailingMobileAgents(agents, nodeLanguaje, mobileAgentsLanguage, g);
                world.addNodes(nodes);
                world.addMobileAgents(mobileAgents);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(world.loadPartialNetwork(0, n)); //load current neighbourhood in hop 0
                    //System.out.println("n" +n);
                }

//            case "chain":
//            case "chainnoloop":
//                world = new NetworkEnvironmentPheromoneReplicationNodeFailingChain(agents, agentsLanguage, nodeLanguaje, g);
//                ((NetworkEnvironmentPheromoneReplicationNodeFailingChain) world).addNodes(nodes);
                break;
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
                    n.setNetworkdata(((NetworkEnvironmentNodeFailingAllInfo) world).loadPartialNetwork(SimulationParameters.nhopsPrune, n));
                }
                break;
            case "trickle":
                world = new NetworkEnvironmentNodeFailingTrickle(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(((NetworkEnvironmentNodeFailingTrickle) world).loadPartialNetwork(0, n));
                    n.setTrickleAlg(new Trickle()); //Initializes trickle
                    n.trickleInterval = n.getTrickleAlg().next();         //create interval {random, I]      
                }
                break;
            case "trickleP":
                world = new NetworkEnvironmentNodeFailingTrickle(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                for (Node n : world.getNodes()) {
                    n.setNetworkdata(((NetworkEnvironmentNodeFailingTrickle) world).loadPartialNetwork(0, n));
                    n.setTrickleAlg(new Trickle()); //Initializes trickle
                    n.trickleInterval = n.getTrickleAlg().next();         //create interval {random, I]                      
                }
                break;
            case "nhopsinfogetdatasize":
                String baseFilename = SimulationParameters.reportsFilenamePrefix;
                System.out.println("base filename:" + baseFilename);
                String reportFilename = baseFilename + "-nhopsinfogetdatasize-" + "-hops-" + SimulationParameters.nhopsPrune + "-dataSize.csv";
                world = new NetworkEnvironmentNodeFailingAllInfo(agents, nodeLanguaje, g);
                world.addNodes(nodes);

                BufferedWriter br;
                try {
                    br = new BufferedWriter(new FileWriter(reportFilename));

                    for (Node n : world.getNodes()) {
                        n.setNetworkdata(((NetworkEnvironmentNodeFailingAllInfo) world).loadPartialNetwork(SimulationParameters.nhopsPrune, n));
                        StringSerializer serializer = new StringSerializer();
                        String totalData = serializer.serialize(n.getNetworkdata());
                        System.out.println(n.getName() + "," + totalData.length() + "," + n.getNetworkdata().size());
                        br.write(n.getName() + "," + totalData.length() + "," + n.getNetworkdata().size() + "\n");
                    }
                    br.close();
                } catch (IOException ex) {
                    System.out.println("Error!");
                    //Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            case "loadalldatasize":
                String baseFilenameall = SimulationParameters.reportsFilenamePrefix;
                System.out.println("base filename:" + baseFilenameall);
                String reportFilenameall = baseFilenameall + "-allnetwork-dataSize.csv";
                world = new NetworkEnvironmentNodeFailingAllInfo(agents, nodeLanguaje, g);
                world.addNodes(nodes);
                BufferedWriter brall;
                try {
                    brall = new BufferedWriter(new FileWriter(reportFilenameall));
                    for (Node n : world.getNodes()) {
                        n.setNetworkdata(((NetworkEnvironmentNodeFailingAllInfo) world).loadAllTopology());
                        StringSerializer serializer = new StringSerializer();
                        String totalData = serializer.serialize(n.getNetworkdata());
                        System.out.println(n.getName() + "," + totalData.length() + "," + n.getNetworkdata().size());
                        brall.write(n.getName() + "," + totalData.length() + "," + n.getNetworkdata().size() + "\n");
                    }
                    brall.close();
                } catch (IOException ex) {
                    System.out.println("Error!");
                    //Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
//            default:
//                world = new NetworkEnvironmentPheromoneReplicationNodeFailing(agents, agentsLanguage, nodeLanguaje, g);
//                ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).addNodes(nodes);
//                break;
        }

        //world.setNetworkDelays(networkDelays);
        if (!SimulationParameters.simMode.equals("nhopsinfogetdatasize") && !SimulationParameters.simMode.equals("loadalldatasize")) {
            world.initNodesVersion();
            world.addObserver(graphVisualizationObserver);
            world.sChAndNot();
            world.run();
        } else {
            System.out.println("The end!");
            System.exit(0);
        }
    }

    public Graph<MyVertex, String> getInitialNetwork() {
        return initialNetwork;
    }

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
                //System.out.println("entra_");
                available.acquire();
                long currentTime = world.updateAndGetSimulationTime();
                //System.out.println("wa: " + world.getAge() + " simulation time: "
                //        + currentTime + " nodes " + world.getNodesAlive() + " mobile: " + world.getMobileAgents().size());
                isDrawing = true;

                //                System.out.println("xxxxxx");
                if (world.getNodes().isEmpty()) {
                    System.out.println("no nodes alive.");
                    world.notifyObs();
                } else {
//                  int agentsAlive = n.getAgentsAlive();
                    int nodesAlive = environment.getNodesAlive();

                    //System.out.println("n" + n.getAge() + "," + agentsAlive);
                    //System.out.println("n" + n.getAge() + "," + nodesAlive);
                    if (nodesAlive == 0) {
                        System.out.println("no nodes alive.");
                        world.notifyObs();
                    } else if (environment != null) {
                        //System.out.println("set size: " + world.getSynsetNodesReported().size() + " vs " + nodesAlive + ", ma: " + world.getMobileAgents().size() + " vs " + world.getSynsetAgentsReported().size() + " wa:" + world.getAge());
                        if (SimulationParameters.simMode.contains("mobileAgents")) {
                            //System.out.println("Khaaaaaaaaaaaaaaaaaa");
                            if (world.getSynsetNodesReported().size() >= nodesAlive && world.getSynsetAgentsReported().size() >= world.getMobileAgents().size()) {
                                // System.out.println("entraaa");
                                System.out.println("wa: " + world.getAge() + " simulation time: "
                                        + currentTime + " nodes " + world.getNodesAlive() + " mobile: " + world.getMobileAgents().size());

                                //here we can obtain any info we want
                                if (SimulationParameters.activateReplication.equals("replalgon")) {
                                    world.fillDataReport();
                                    System.out.println("JSON: " + world.getDataReport());
                                }

                                world.updateWorldAge();
                                world.getSynsetNodesReported().clear();
                                world.getSynsetAgentsReported().clear();
                                synchronized (world.objBlock) {
                                    world.objBlock.notifyAll();
                                }
                                nodesLive.add(environment.getSimulationTime(), nodesAlive);

                                GraphComparator gnm = new GraphComparator();
                                double sim = 0;

                                int worldRound = environment.getAge();

                                sim = gnm.calculateSimilarity(environment);
                                neighborMatchingSim.add(worldRound, sim);
                                similarity.put(worldRound, sim);

                                mobileAgentsAlive.put(worldRound, environment.getMobileAgents().size());

                                if (environment.getAge() >= (SimulationParameters.maxIter - 2) && !alreadyPainted) {
                                    String baseFilename = SimulationParameters.reportsFilenamePrefix;
                                    String dir = "cmpgraph";
                                    createDir(dir);
                                    GraphSerialization.saveSerializedGraph("./" + dir + "/" + getFileName() + "+" + baseFilename + "+round+" + worldRound + ".graph", world.getTopology());
                                    alreadyPainted = true;
                                }
                            }
                        } else {
                            if (world.getSynsetNodesReported().size() >= nodesAlive) {
                                //world.printMatrix();
                                System.out.println("wa: " + world.getAge() + " simulation time: "
                                        + currentTime + " nodes " + world.getNodesAlive() + " mobile: " + world.getMobileAgents().size());
                                if (SimulationParameters.activateReplication.equals("replalgon")) {
                                    world.fillDataReport();
                                }
                                //System.out.println("JSON: " + world.getDataReport());
                                world.updateWorldAge();
                                world.getSynsetNodesReported().clear();
                                //here we can obtain any info we want
                                synchronized (world.objBlock) {
                                    world.objBlock.notifyAll();
                                }

                                nodesLive.add(environment.getSimulationTime(), nodesAlive);

                                GraphComparator gnm = new GraphComparator();
                                double sim = 0;
                                int worldRound = environment.getAge();

                                sim = gnm.calculateSimilarity(environment);
                                neighborMatchingSim.add(worldRound, sim);
                                similarity.put(worldRound, sim);

                                mobileAgentsAlive.put(worldRound, environment.getMobileAgents().size());

                                if (environment.getAge() >= (SimulationParameters.maxIter - 2) && !alreadyPainted) {
                                    String baseFilename = SimulationParameters.reportsFilenamePrefix;
                                    String dir = "cmpgraph";
                                    createDir(dir);
                                    GraphSerialization.saveSerializedGraph("./" + dir + "/" + getFileName() + "+" + baseFilename + "+round+" + worldRound + ".graph", world.getTopology());
                                    alreadyPainted = true;
                                }
                            }
                        }

//                        String netAndMemStats;
//                        HashMap localNodeStatsByRound = environment.getLocalStats();
//                        //totalMemory|totalMsgSent|sizeMsgSent|totalMsgReceived|sizeMsgRecv
//                        netAndMemStats = localNodeStatsByRound.get("totalMemory") + "," + environment.getTotalMsgSent() + ","
//                                + environment.getTotalSizeMsgSent() + "," + environment.getTotalMsgRecv() + "," + environment.getTotalSizeMsgRecv();
//
//                        networkAndMemoryStats.put(worldRound, netAndMemStats);
//                        localStatsByRound.put(worldRound, localNodeStatsByRound);
                    }
                }
                available.release();
            } catch (Exception ex) {
                System.out.println("Error obtaining live statistics" + ex.toString());
                ex.printStackTrace();
            }
        }
    }

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
        //System.out.println("v:"+ );
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

    /**
     * Obtain initial location of agents
     *
     * @param g
     * @return next location
     */
    public MyVertex getLocation(Graph<MyVertex, String> g) {
        if (SimulationParameters.filenameLoc.length() > 1) {
            //System.out.println("loading initial location from file...");
            MyVertex tmp = locations.get(indexLoc++);
            for (MyVertex v : g.getVertices()) {
                if (v.toString().equals(tmp.toString())) {
                    return v;
                }
            }
            return null;
        } else {
            //System.out.println("generating random location");
            int pos = (int) (Math.random() * g.getVertexCount());
            Collection E = g.getVertices();
            return (MyVertex) E.toArray()[pos];
        }
    }

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
        return ((DataReplicationNodeFailingObserver) graphVisualizationObserver).getFileName();
    }

//    public HashMap<Integer, String> getNetworkAndMemoryStats() {
//        return networkAndMemoryStats;
//    }
//
//    public HashMap<Integer, HashMap> getLocalStatsByRound() {
//        return localStatsByRound;
//    }
    public HashMap<Integer, Integer> getMobileAgentsAlive() {
        return mobileAgentsAlive;
    }

}
