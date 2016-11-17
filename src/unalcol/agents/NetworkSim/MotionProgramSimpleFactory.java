/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import java.util.Vector;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentCollection;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromoneCollection;
import unalcol.agents.NetworkSim.programs.LevyWalkSynchronizationProgram;
import unalcol.agents.NetworkSim.programs.PheromoneReplicationProgram;
import unalcol.agents.NetworkSim.programs.PheromoneSynchronizationProgram;
import unalcol.agents.NetworkSim.programs.RandomSynchronizationProgram;
import unalcol.agents.simulate.Environment;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Factory Class to create a determined kind of World and Program given some
 * arguments
 *
 * @author Arles Rodriguez
 */
public class MotionProgramSimpleFactory {

    /**
     * Creates and assign a program to each Agent
     *
     * @param popSize
     * @param probFailure
     * @param failuresByTermite
     * @return an AgentProgram
     */
    public static AgentProgram createMotionProgram(float pf, String mode) {
        AgentProgram program = null;
        switch (mode) {
            case "random":
                program = new RandomSynchronizationProgram(pf);
                break;
            case "carriersrep":
                program = new PheromoneReplicationProgram(pf);
                break;
            case "carriers":
                program = new PheromoneSynchronizationProgram(pf);
                break;
            case "carriersnoevap":
                program = new PheromoneSynchronizationProgram(pf);
                break;
            case "levywalk":
                program = new LevyWalkSynchronizationProgram(pf);
                break;
            default:
                program = new RandomSynchronizationProgram(pf);
                break;
        }

        return program;
    }

    /**
     * Create a world with a vector of agents and a world size given
     *
     * @param mode
     * @param agents
     * @param language
     * @param _agents
     * @param _language
     * @param gr
     *
     * @return a World given the parameters of size and agents
     */
    public static Environment createWorld(String mode, Vector<Agent> agents, SimpleLanguage language, Graph<GraphElements.MyVertex, String> gr) {
        switch (mode) {
            case "random":
                return new NetworkEnvironmentCollection(agents, language, gr);
            case "carriers":
                return new NetworkEnvironmentPheromoneCollection(agents, language, gr);
            default:
                return new NetworkEnvironmentCollection(agents, language, gr);
        }
    }

}
