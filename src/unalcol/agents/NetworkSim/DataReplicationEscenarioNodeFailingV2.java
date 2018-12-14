/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// unalcol.agent.networkSim.reports.GraphicReportHealingObserver;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.simulate.util.SimpleLanguage;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailing;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailingAllInfo;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailingChain;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneReplicationNodeFailingChainV2;
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
public class DataReplicationEscenarioNodeFailingV2 extends DataReplicationEscenarioNodeFailing implements Runnable, ActionListener {

    /**
     * Creates a simulation without graphic interface
     *
     * @param pop population size
     * @param pf failure probability
     */
    DataReplicationEscenarioNodeFailingV2(int pop, float pf) {
        super(pop, pf);
    }

    /**
     *
     * Initializes simulation.
     */
    @Override
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
        System.out.println("npf" + SimulationParameters.npf);
        NodeFailingProgram np = new NodeFailingProgram(SimulationParameters.npf);
        // NodeFailingProgram np = new NodeFailingProgram((float)Math.random()*SimulationParameters.npf);

        //report = new reportHealingProgram(population, probFailure, this);
        //greport = new GraphicReportHealingObserver(probFailure);
        //Create graph
        Graph<GraphElements.MyVertex, String> g = graphSimpleFactory.createGraph(SimulationParameters.graphMode);
        StringSerializer s = new StringSerializer();
        String aCopy = s.serialize(g);
        initialNetwork = (Graph<GraphElements.MyVertex, String>) s.deserialize(aCopy);

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

        String fileTimeout;

        if (SimulationParameters.nofailRounds == 0) {
            if (SimulationParameters.simMode.contains("chain")) {
                fileTimeout = "timeout+exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsChain + "+wsize+" + SimulationParameters.wsize + ".timeout";
            } else {
                fileTimeout = "timeout+exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+wsize+" + SimulationParameters.wsize + ".timeout";
            }
        } else if (SimulationParameters.simMode.contains("chain")) {
            fileTimeout = "timeout+exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+" + SimulationParameters.nhopsChain + "+wsize+" + SimulationParameters.wsize + "+nofailr+" + SimulationParameters.nofailRounds + ".timeout";
        } else {
            fileTimeout = "timeout+exp+ps+" + population + "+pf+" + SimulationParameters.npf + "+mode+" + SimulationParameters.motionAlg + "+maxIter+" + SimulationParameters.maxIter + "+e+" + g.getEdges().size() + "+v+" + g.getVertices().size() + "+" + graphType + "+" + SimulationParameters.activateReplication + "+" + SimulationParameters.nodeDelay + "+" + SimulationParameters.simMode + "+wsize+" + SimulationParameters.wsize + "+nofailr+" + SimulationParameters.nofailRounds + ".timeout";
        }

        SimulationParameters.genericFilenameTimeouts = fileTimeout;

        ConcurrentHashMap<String, ConcurrentHashMap<Integer, ReplicationStrategyInterface>> nodeTimeout = null;
        //Here we use node pf instead agent pf.
        if (SimulationParameters.simMode.contains("chain")) {
            nodeTimeout = (ConcurrentHashMap) ObjectSerializer.loadDeserializedObject(fileTimeout);
        }

        for (GraphElements.MyVertex v : g.getVertices()) {
            v.setStatus("alive");
            Node n = null;
            if (SimulationParameters.simMode.contains("chain") && nodeTimeout != null && nodeTimeout.containsKey(v.getName())) {
                //System.out.println("load" + nodeTimeout.get(v.getName()));
                n = new Node(np, v, nodeTimeout.get(v.getName()));
            } else {
                n = new Node(np, v);
            }
            n.setPfCreate(0);
            NetworkNodeMessageBuffer.getInstance().createBuffer(v.getName());
            agents.add(n);
            nodes.add(n);
        }

        if (!SimulationParameters.simMode.equals("broadcast") && !SimulationParameters.simMode.equals("allinfo")) {
            if (SimulationParameters.filenameLoc.length() > 1) {
                loadLocations();
                loadNetworkDelays();
            }
            //Creates "Agents"
            for (int i = 0; i < population; i++) {
                AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);
                MobileAgent a = new MobileAgent(program, i);
                GraphElements.MyVertex tmp = getLocation(g);
                System.out.println("tmp" + tmp);
                a.setRound(-1);
                a.setLocation(tmp);
                a.setPrevLocation(tmp);

                a.setPrevPrevLocation(tmp);
                a.setProgram(program);
                a.setAttribute("infi", new ArrayList<>());
                NetworkMessageBuffer.getInstance().createBuffer(a.getId());
                agents.add(a);

                String[] msgnode = new String[3];
                msgnode[0] = "arrived";
                msgnode[1] = String.valueOf(a.getId());
                msgnode[2] = String.valueOf(a.getIdFather());
                NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                //Initialize implies arrival message from nodes!
            }
        }

        this.setGraphVisualization(new DataReplicationNodeFailingObserver(this));

        NetworkEnvironmentReplication world;
        switch (SimulationParameters.simMode) {
            case "broadcast":
                world = new NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast(agents, agentsLanguage, nodeLanguaje, g);
                ((NetworkEnvironmentPheromoneReplicationNodeFailingBroadcast) world).addNodes(nodes);
                break;
            case "chain":
            case "chainnoloop":
                world = new NetworkEnvironmentPheromoneReplicationNodeFailingChain(agents, agentsLanguage, nodeLanguaje, g);
                ((NetworkEnvironmentPheromoneReplicationNodeFailingChain) world).addNodes(nodes);
                break;
            case "allinfo":
                world = new NetworkEnvironmentPheromoneReplicationNodeFailingAllInfo(agents, nodeLanguaje, nodeLanguaje, g);
                ((NetworkEnvironmentPheromoneReplicationNodeFailingAllInfo) world).addNodes(nodes);
                for (Node n : world.getNodes()) {
                    //System.out.println("enerooooooooooooooooooo");
                    n.setNetworkdata(((NetworkEnvironmentPheromoneReplicationNodeFailingAllInfo) world).loadAllTopology());
                }
                break;
            case "chainv2":
                world = new NetworkEnvironmentPheromoneReplicationNodeFailingChainV2(agents, agentsLanguage, nodeLanguaje, g);
                ((NetworkEnvironmentPheromoneReplicationNodeFailingChainV2) world).addNodes(nodes);
                break;
            default:
                world = new NetworkEnvironmentPheromoneReplicationNodeFailing(agents, agentsLanguage, nodeLanguaje, g);
                ((NetworkEnvironmentPheromoneReplicationNodeFailing) world).addNodes(nodes);
                break;
        }
        world.setNetworkDelays(networkDelays);
        world.addObserver(getGraphVisualization());
        setWorld(world);
        world.not();
        world.run();
        executions++;
    }
}
