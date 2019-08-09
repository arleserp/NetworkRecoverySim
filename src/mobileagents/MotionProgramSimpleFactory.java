/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

import edu.uci.ics.jung.graph.Graph;
import java.util.Vector;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
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
     * @param pf
     * @param mode
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
            case "FirstNeighbor":
                program = new FirstNeighborVisited(pf);
                break;
            default:
                program = new RandomSynchronizationProgram(pf);
                break;
        }

        return program;
    }
}
