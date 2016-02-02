/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
// unalcol.agent.networkSim.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.random.RandomUtil;
import java.util.Vector;
import unalcol.agents.NetworkSim.util.graphStatistics;

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

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop
     * @param pf
     * @param width
     * @param height
     * @return
     */
    WorldThread(int pop, float pf, int nv, int ne) {
        population = pop;
        probFailure = pf;
        positions = new Hashtable<>();
        vertexNumber = nv;
        channelNumber = ne;
        System.out.println("Pop: "  + population);
        System.out.println("Pf: "  + pf);
        System.out.println("Vertex Number: "  + vertexNumber);
        System.out.println("Channel Number: "  + channelNumber);
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
//        greport = new GraphicReportHealingObserver(probFailure);

        //Create graph
        Graph<GraphElements.MyVertex, String> g = graphSimpleFactory.createGraph(SyncronizationMain.graphMode, vertexNumber, channelNumber);

        System.out.println("Distances: " + graphStatistics.computeAveragePathLength(g));
        Map<GraphElements.MyVertex, Double> m = graphStatistics.clusteringCoefficients(g);
        System.out.println("Clustering coeficients:" + m);
        System.out.println("Average Clustering Coefficient: " + graphStatistics.averageCC(g));
        System.out.println("Average degree: " + graphStatistics.averageDegree(g));

        //Creates "Agents"
        for (int i = 0; i < population; i++) {
            AgentProgram program = ProgramMobileAgentsFactory.createProgram(probFailure);
            MobileAgent a = new MobileAgent(program);
            a.setAttribute("ID", String.valueOf(i));
            a.setLocation(getLocation(g));
            a.setProgram(program);
            a.setAttribute("infi", new ArrayList<String>());
//          NetworkMessageBuffer.getInstance().createBuffer((String) t.getAttribute("ID"));
            agents.add(a);
        }

        world = new NetworkEnvironment(agents, languaje, g);
        //greport.addObserver(world);
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
        //try {
//        while (!world.isFinished()) {
//            //Thread.sleep(30);
//
//            world.updateSandC();
//            world.calculateGlobalInfo();
//
//            if (world.getAge() % 2 == 0 || world.getAgentsDie() == world.getAgents().size() || world.getRoundGetInfo() != -1) {
//                world.nObservers();
//            }
//
//            if (world instanceof WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl) {
//                ((WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl) world).evaporatePheromone();
//            }
//
//            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) {
//                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl) world).evaporatePheromone();
//            }
//
//            if (world instanceof WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) {
//                ((WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2) world).evaporatePheromone();
//            }
//
//            if (world instanceof WorldLwphCLwEvapImpl) {
//                ((WorldLwphCLwEvapImpl) world).evaporatePheromone();
//            }
//        }
        /*} catch (InterruptedException e) {
         System.out.println("interrupted!");
         }
         System.out.println("End WorldThread");*/
    }

    private GraphElements.MyVertex getLocation(Graph<GraphElements.MyVertex, String> g) {
        int pos = (int) (Math.random() * g.getVertexCount());
        Collection E = g.getVertices();
        return  (GraphElements.MyVertex)E.toArray()[pos];
    }


}
