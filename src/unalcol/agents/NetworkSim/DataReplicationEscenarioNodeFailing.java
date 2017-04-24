/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observer;
// unalcol.agent.networkSim.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;
import java.util.Vector;
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
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailing;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication;
import unalcol.agents.NetworkSim.environment.NetworkMessageBuffer;
import unalcol.agents.NetworkSim.environment.NetworkNodeMessageBuffer;
import unalcol.agents.NetworkSim.environment.ObjectSerializer;
import unalcol.agents.NetworkSim.programs.NodeFailingProgram;
import unalcol.agents.NetworkSim.util.DataReplicationNodeFailingObserver;
import unalcol.agents.NetworkSim.util.GraphStats;
//import unalcol.agents.NetworkSim.util.GraphStatistics;
import unalcol.agents.NetworkSim.util.StringSerializer;

/**
 * Creates a simulation without graphic interface
 *
 * @author arles.rodriguez
 */
public class DataReplicationEscenarioNodeFailing implements Runnable {

    private NetworkEnvironmentReplication world;
    public boolean renderAnts = true;
    public boolean renderSeeking = true;
    public boolean renderCarrying = true;
    int modo = 0;
//    GraphicReportHealingObserver greport;
    int executions = 0;
    int population = 100;
    int vertexNumber = 100;
    int channelNumber = 100;
    float probFailure = (float) 0.1;
    Hashtable<String, Object> positions;
    int width;
    int height;
    private Observer graphVisualization;
    ArrayList<GraphElements.MyVertex> locations;
    int indexLoc;
    boolean added = false;
    JFrame frame;
    JFrame frame2;
    private boolean isDrawing = false;
    XYSeries agentsLive;
    XYSeries nodesLive;
    XYSeriesCollection juegoDatos = new XYSeriesCollection();

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
        frame = new JFrame("Simple Graph View");
        //frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2 = new JFrame("Agent and Node Number");
        agentsLive = new XYSeries("agentsLive");
        nodesLive = new XYSeries("nodesLive");
        juegoDatos.addSeries(agentsLive);
        juegoDatos.addSeries(nodesLive);
        frame2.setLocation(350, 150);
        frame2.setSize(450, 450);
        frame2.show();
    }

    /**
     *
     * Initializes simulation.
     */
    public void init() {
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
        NodeFailingProgram np = new NodeFailingProgram(SimulationParameters.npf);
        // NodeFailingProgram np = new NodeFailingProgram((float)Math.random()*SimulationParameters.npf);

        //report = new reportHealingProgram(population, probFailure, this);
        //greport = new GraphicReportHealingObserver(probFailure);
        //Create graph
        Graph<GraphElements.MyVertex, String> g = graphSimpleFactory.createGraph(SimulationParameters.graphMode);

        //maybe to fix: alldata must have getter
        System.out.println("All data" + SimulationParameters.globalData);
        System.out.println("All data size" + SimulationParameters.globalData.size());

        // System.out.println("Average Path Length: " + GraphStatistics.computeAveragePathLength(g));
        Map<GraphElements.MyVertex, Double> m = GraphStats.clusteringCoefficients(g);
        System.out.println("Clustering coeficients:" + m);
        System.out.println("Average Clustering Coefficient: " + GraphStats.averageCC(g));
        System.out.println("Average degree: " + GraphStats.averageDegree(g));

        String graphType = SimulationParameters.graphMode;
        graphType = graphType.replaceAll(".graph", "");
        String fileTimeout = "timeout+exp+ps+" + population + "+pf+" + SimulationParameters.pf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + ".timeout";
        HashMap<String, HashMap> nodeTimeout = (HashMap) ObjectSerializer.loadDeserializedObject(fileTimeout);

        for (GraphElements.MyVertex v : g.getVertices()) {
            Node n = null;
            if (nodeTimeout != null && nodeTimeout.containsKey(v.getName())) {
                n = new Node(np, v, nodeTimeout.get(v.getName()));
            } else {
                n = new Node(np, v);
            }
            n.setPfCreate(0);
            NetworkNodeMessageBuffer.getInstance().createBuffer(v.getName());
            agents.add(n);
            nodes.add(n);
        }

        if (SimulationParameters.filenameLoc.length() > 1) {
            loadLocations();
        }

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(probFailure, SimulationParameters.motionAlg);
            MobileAgent a = new MobileAgent(program, i);
            GraphElements.MyVertex tmp = getLocation(g);
            //System.out.println("tmp" + tmp);
            a.setRound(-1);
            a.setLocation(tmp);
            a.setPrevLocation(tmp);
            a.setProgram(program);
            a.setAttribute("infi", new ArrayList<String>());
            NetworkMessageBuffer.getInstance().createBuffer(a.getId());
            agents.add(a);

            /*String[] msgnode = new String[4];
            msgnode[0] = "arrived";
            msgnode[1] = String.valueOf(a.getId());
            msgnode[2] = String.valueOf(a.getIdFather());
            NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);*/
            //Initialize implies arrival message from nodes!
        }

        graphVisualization = new DataReplicationNodeFailingObserver();
        world = new NetworkEnvironmentPheromoneReplicationNodeFailing(agents, agentsLanguage, nodeLanguaje, g);
        ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).setNodes(nodes);
        world.addObserver(graphVisualization);
        world.not();
        world.run();
        executions++;
    }

    public class FrameGraphUpdater extends Thread {

        Graph<GraphElements.MyVertex, String> g;
        JFrame frame;
        NetworkEnvironmentReplication n;

        public FrameGraphUpdater(Graph<GraphElements.MyVertex, String> g, JFrame frame, NetworkEnvironmentReplication ne) {
            this.g = g;
            this.frame = frame;
            this.n = ne;
        }

        public void run() {
            if (isDrawing) {
                return;
            }
            //System.out.println("n visited nodes size" + n.visitedNodes.size());
            try {
                //    isDrawing = true;
                //if (g.getVertexCount() == 0) {
                //    System.out.println("no nodes alive.");
                //    return;
                //} else {
                Layout<GraphElements.MyVertex, String> layout = null;
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

                BasicVisualizationServer<GraphElements.MyVertex, String> vv = new BasicVisualizationServer<>(layout);
                vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size

                // vv.getRenderContext().setVertexFillPaintTransformer(n.vertexColor);
                // vv.getRenderContext().setEdgeDrawPaintTransformer(n.edgeColor);
                Transformer<GraphElements.MyVertex, Paint> vertexColor = new Transformer<GraphElements.MyVertex, Paint>() {
                    @Override
                    public Paint transform(GraphElements.MyVertex i) {
                        if (((NetworkEnvironmentPheromoneReplicationNodeFailing) n).isOccuped(i)) {
                            return Color.YELLOW;
                        }

                        if (i.getStatus() != null && i.getStatus().equals("visited")) {
                            return Color.BLUE;
                        }
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
                //}
                int agentsAlive = ((NetworkEnvironmentPheromoneReplicationNodeFailing) n).getAgentsAlive();
                int nodesAlive = ((NetworkEnvironmentPheromoneReplicationNodeFailing) n).getNodesAlive();
                agentsLive.add(n.getAge(), agentsAlive);
                nodesLive.add(n.getAge(), nodesAlive);
                frame2.getGraphics().drawImage(creaImagen(), 0, 0, null);
            } catch (NullPointerException ex) {
                System.out.println("exception drawing graph" + ex.getLocalizedMessage());
                isDrawing = false;
            }
            isDrawing = false;
        }
    }

    /**
     * Runs a simulation.
     *
     */
    @Override
    public void run() {
        try {
            while (true) { //!world.isFinished()) {
                Thread.sleep(50);

                if (!isDrawing) {
                    (new FrameGraphUpdater(world.getTopology(), frame, world)).start();
                }
                //System.out.println("go");
                //System.out.println("halo");
                /* world.updateSandC();
            world.calculateGlobalInfo();

            if (world.getAge() % 2 == 0 || world.getAgentsDie() == world.getAgents().size() || world.getRoundGetInfo() != -1) {
                world.nObservers();
            }
                 */
                if (world instanceof NetworkEnvironmentPheromoneReplicationNodeFailing && SimulationParameters.motionAlg.equals("carriers")) {
                    ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).evaporatePheromone();
                }
                if (world instanceof NetworkEnvironmentPheromoneReplicationNodeFailing && SimulationParameters.motionAlg.equals("carriersrep")) {
                    ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).evaporatePheromone();
                }
                ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).updateWorldAge();
                /*
            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) {
                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) world).evaporatePheromone();
            }

            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) {
                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) world).evaporatePheromone();
            }

            if (world instanceof WorldLwphCLwEvapImpl) {
                ((WorldLwphCLwEvapImpl) world).evaporatePheromone();
            }*/
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted!");
        } catch (NullPointerException e) {
            System.out.println("interrupted!");
        }
        /* System.out.println("End WorldThread");*/

    }

    public void loadLocations() {
        locations = (ArrayList<GraphElements.MyVertex>) StringSerializer.loadDeserializeObject(SimulationParameters.filenameLoc);
    }

    private GraphElements.MyVertex getLocation(Graph<GraphElements.MyVertex, String> g) {
        if (SimulationParameters.filenameLoc.length() > 1) {
            GraphElements.MyVertex tmp = locations.get(indexLoc++);
            for (GraphElements.MyVertex v : g.getVertices()) {
                if (v.toString().equals(tmp.toString())) {
                    return v;
                }
            }
            //System.out.println("null???");
            return null;
        } else {
            int pos = (int) (Math.random() * g.getVertexCount());
            Collection E = g.getVertices();
            return (GraphElements.MyVertex) E.toArray()[pos];
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
            Logger.getLogger(DataReplicationNodeFailingObserver.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(DataReplicationNodeFailingObserver.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedImage creaImagen() {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "ssss", "Round number", "Agents",
                juegoDatos, PlotOrientation.VERTICAL,
                true, true, false);
        /*
         JFreeChart chart =
         ChartFactory.createTimeSeriesChart("Sesiones en Adictos al Trabajo"
         "Meses", "Sesiones", juegoDatos,
         false,
         false,
         true // Show legend
         );
         */
        BufferedImage image = chart.createBufferedImage(450, 450);
        return image;
    }

}
