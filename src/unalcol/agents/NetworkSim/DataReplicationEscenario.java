/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import static cern.clhep.Units.g;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observer;
// unalcol.agent.networkSim.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;
import java.util.Vector;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplication;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication;
import unalcol.agents.NetworkSim.environment.NetworkMessageBuffer;
import unalcol.agents.NetworkSim.environment.NetworkNodeMessageBuffer;
import unalcol.agents.NetworkSim.programs.NodeProgram;
import unalcol.agents.NetworkSim.util.GraphStats;
//import unalcol.agents.NetworkSim.util.GraphStatistics;
import unalcol.agents.NetworkSim.util.DataReplicationObserver;
import unalcol.agents.NetworkSim.util.StringSerializer;

/**
 * Creates a simulation without graphic interface
 *
 * @author arles.rodriguez
 */
public class DataReplicationEscenario implements Runnable {

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

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop
     * @param pf
     * @param width
     * @param height
     * @return
     */
    DataReplicationEscenario(int pop, float pf) {
        population = pop;
        probFailure = pf;
        positions = new Hashtable<>();
        //vertexNumber = nv;
        //channelNumber = ne;
        System.out.println("Pop: " + population);
        System.out.println("Pf: " + pf);
        System.out.println("Movement: " + SimulationParameters.motionAlg);
        indexLoc = 0;
        //System.out.println("Vertex Number: "  + vertexNumber);
        //System.out.println("Channel Number: "  + channelNumber);
    }

    /**
     *
     * Initializes simulation.
     */
    public void init() {
        Vector<Agent> agents = new Vector();
        System.out.println("fp" + probFailure);

        //Language for Agents
        String[] _percepts = {"data", "neighbors"};
        String[] _actions = {"move", "die"};
        SimpleLanguage agentsLanguage = new SimpleLanguage(_percepts, _actions);

        //Language for nodes
        String[] nodePercepts = {"data", "neighbors"};
        String[] nodeActions = {"communicate", "die"};
        SimpleLanguage nodeLanguaje = new SimpleLanguage(nodePercepts, nodeActions);
        NodeProgram np = new NodeProgram(SimulationParameters.pf);
        
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

        
        
        for(GraphElements.MyVertex v: g.getVertices()){
            Node n = new Node(np, v);
            NetworkNodeMessageBuffer.getInstance().createBuffer(v.getName());
            agents.add(n);
        }
        
        if (SimulationParameters.filenameLoc.length() > 1) {
            loadLocations();
        }

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(probFailure, SimulationParameters.motionAlg);
            MobileAgent a = new MobileAgent(program, i);
            GraphElements.MyVertex tmp = getLocation(g);
            System.out.println("tmp" + tmp);
            a.setLocation(tmp);
            a.setProgram(program);
            a.setAttribute("infi", new ArrayList<String>());
            NetworkMessageBuffer.getInstance().createBuffer(a.getId());
            agents.add(a);
        }

        graphVisualization = new DataReplicationObserver();
        world = new NetworkEnvironmentPheromoneReplication(agents, agentsLanguage, g);
        world.addObserver(graphVisualization);
        //greport.addObserver(world);
        world.not();
        world.run();
        executions++;
        /*        world.updateSandC();
         world.calculateGlobalInfo();
         world.nObservers();
         */
    }

    /**
     * Runs a simulation.
     *
     */
    @Override
    public void run() {
        try {
            while (true) { //!world.isFinished()) {
                Thread.sleep(30);

                //System.out.println("halo");
                /* world.updateSandC();
            world.calculateGlobalInfo();

            if (world.getAge() % 2 == 0 || world.getAgentsDie() == world.getAgents().size() || world.getRoundGetInfo() != -1) {
                world.nObservers();
            }
                 */
                if (world instanceof NetworkEnvironmentPheromoneReplication && SimulationParameters.motionAlg.equals("carriers")) {
                    ((NetworkEnvironmentPheromoneReplication) world).evaporatePheromone();
                }
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
            System.out.println("null???");
            return null;
        } else {
            int pos = (int) (Math.random() * g.getVertexCount());
            Collection E = g.getVertices();
            return (GraphElements.MyVertex) E.toArray()[pos];
        }
    }

}
