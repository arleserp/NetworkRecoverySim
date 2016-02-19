/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.programs.RandomSynchronizationProgram;

/**
 * Factory Class to create a determined kind of World and Program given some
 * arguments
 *
 * @author Arles Rodriguez
 */
public class ProgramMobileAgentsFactory {

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
        switch(mode){
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
     * @param termites
     * @param size_w
     * @param size_h
     * @return a World given the parameters of size and agents
     */
//    public static World createWorld(Vector<Agent> termites, int size_w, int size_h) {
//        World world;
//        switch (AppMain.mode) {
//            case "sequential":
//                world = new WorldSequentialImpl(termites, size_w, size_h);
//                break;
//            case "random":
//                world = new WorldTemperaturesOneStepRandomImpl(termites, size_w, size_h);
//                break;
//            case "sandc":
//                world = new WorldTemperaturesOneStepPheromoneImpl(termites, size_w, size_h);
//                break;
//            case "oneph":
//                world = new WorldTemperaturesOneStepOnePheromoneImpl(termites, size_w, size_h);
//                break;
//            case "onephevap":
//                world = new WorldTemperaturesOneStepOnePheromoneEvaporationImpl(termites, size_w, size_h);
//                break;
//            case "levywalk":
//                world = new WorldLevyWalkImpl(termites, size_w, size_h);
//                break;
//            case "lwphevapMap":
//                world = new WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl(termites, size_w, size_h);
//                break;
//            case "lwphevap":
//            case "lwphevap2":
//                world = new WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl(termites, size_w, size_h);
//                break;
//            case "lwsandc":
//            case "lwsandc2":
//                world = new WorldTemperaturesLWHOneStepPheromoneImpl(termites, size_w, size_h);
//                break;
//            case "hybrid":
//                world = new WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl(termites, size_w, size_h);
//                break;
//            case "hybrid2":
//                world = new WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2(termites, size_w, size_h);
//                break;
//            case "hybrid3":
//                world = new WorldHybridLWSandCImpl(termites, size_w, size_h);
//                break;
//            case "hybrid4":
//                world = new WorldHybridLwSandLwCImpl(termites, size_w, size_h);
//                break;
//            case "sandclw":
//                world = new WorldTemperaturesOneStepPheromoneImpl(termites, size_w, size_h);
//                break;
//            case "hlwsandc":
//                world = new WorldTemperaturesOneStepHLwSandcPheromoneImpl(termites, size_w, size_h);
//                break;
//            case "lwphclwevap":
//                world = new WorldLwphCLwEvapImpl(termites, size_w, size_h);
//                break;
//            case "turnoncontact":
//                world = new WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl(termites, size_w, size_h);
//                break;
//            default:
//                world = new WorldTemperaturesOneStepRandomImpl(termites, size_w, size_h);
//                break;
//        }
//        return world;
//    }



}
