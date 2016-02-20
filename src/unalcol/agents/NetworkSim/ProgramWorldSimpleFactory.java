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
import unalcol.agents.NetworkSim.environment.NetworkEnvironment;
import unalcol.agents.NetworkSim.environment.NetworkEnvironmentPheromone;
import unalcol.agents.NetworkSim.programs.RandomSynchronizationProgram;
import unalcol.agents.simulate.Environment;
import unalcol.agents.simulate.util.SimpleLanguage;

/**
 * Factory Class to create a determined kind of World and Program given some
 * arguments
 *
 * @author Arles Rodriguez
 */
public class ProgramWorldSimpleFactory {

    /**
     * Creates and assign a program to each Agent
     *
     * @param popSize
     * @param probFailure
     * @param failuresByTermite
     * @return an AgentProgram
     */
    public static AgentProgram createProgram(float pf, String mode) {
        AgentProgram program = null;
        switch (mode) {
            case "random":
                program = new RandomSynchronizationProgram(pf);
                break;
            case "carriers":
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
                return new NetworkEnvironment(agents, language, gr);
            case "carriers":
                return new NetworkEnvironmentPheromone(agents, language, gr);
            default:
                return new NetworkEnvironment(agents, language, gr);
        }
    }

}
