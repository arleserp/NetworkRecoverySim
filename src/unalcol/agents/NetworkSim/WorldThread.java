/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import unalcol.agents.NetworkSim.environment.NetworkEnvironment;
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
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromone;
import unalcol.agents.NetworkSim.environment.NetworkMessageBuffer;
import unalcol.agents.NetworkSim.util.graphStatistics;
import unalcol.agents.NetworkSim.util.graphVisualization;

/**
 * Creates a simulation without graphic interface
 *
 * @author arles.rodriguez
 */
public class WorldThread implements Runnable {

    private NetworkEnvironment world;
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

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop
     * @param pf
     * @param width
     * @param height
     * @return
     */
    WorldThread(int pop, float pf) {
        population = pop;
        probFailure = pf;
        positions = new Hashtable<>();
        //vertexNumber = nv;
        //channelNumber = ne;
        System.out.println("Pop: " + population);
        System.out.println("Pf: " + pf);
        System.out.println("Movement: " + SyncronizationMain.motionAlg);
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

        String[] _percepts = {"data", "neighbors"};
        String[] _actions = {"move"};
        SimpleLanguage languaje = new SimpleLanguage(_percepts, _actions);

        //report = new reportHealingProgram(population, probFailure, this);
        //greport = new GraphicReportHealingObserver(probFailure);
        //Create graph
        Graph<GraphElements.MyVertex, String> g = graphSimpleFactory.createGraph(SyncronizationMain.graphMode);

        System.out.println("Average Path Length: " + graphStatistics.computeAveragePathLength(g));
        Map<GraphElements.MyVertex, Double> m = graphStatistics.clusteringCoefficients(g);
        System.out.println("Clustering coeficients:" + m);
        System.out.println("Average Clustering Coefficient: " + graphStatistics.averageCC(g));
        System.out.println("Average degree: " + graphStatistics.averageDegree(g));

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = ProgramWorldSimpleFactory.createProgram(probFailure, SyncronizationMain.motionAlg);
            MobileAgent a = new MobileAgent(program, i);
            a.setLocation(getLocation(g));
            a.setProgram(program);
            a.setAttribute("infi", new ArrayList<String>());
            NetworkMessageBuffer.getInstance().createBuffer(a.getId());
            agents.add(a);
        }

        graphVisualization = new graphVisualization();
        world = new NetworkEnvironmentPheromone(agents, languaje, g);
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
                if (world instanceof NetworkEnvironmentPheromone) {
                    ((NetworkEnvironmentPheromone) world).evaporatePheromone();
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

    private GraphElements.MyVertex getLocation(Graph<GraphElements.MyVertex, String> g) {
        int pos = (int) (Math.random() * g.getVertexCount());
        Collection E = g.getVertices();
        return (GraphElements.MyVertex) E.toArray()[pos];
    }

}
