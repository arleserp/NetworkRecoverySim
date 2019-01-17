/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

/**
 *
 * @author Arles Rodriguez+ Test of new replication strategy version 2.0 Main
 * for the new replication proposal.
 *
 */
public class DataReplicationNodeFailingMainV2 {

    // Perform simulation
    public static void main(String[] args) {
        if (args.length >= 1) {
            //Pop Size
            System.out.println("graphmode:" + args[0]);
            SimulationParameters.graphMode = args[0];

            if (SimulationParameters.graphMode.equals("smallworld")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.beta = Float.valueOf(args[2]);
                SimulationParameters.degree = Integer.valueOf(args[3]);
                SimulationParameters.popSize = Integer.valueOf(args[4]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[5]));
                SimulationParameters.motionAlg = args[6];
                if (args.length > 7) {
                    SimulationParameters.filenameLoc = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.maxIter = Integer.valueOf(args[8]);
                }
                if (args.length > 9) {
                    SimulationParameters.activateReplication = args[9];
                }
                if (args.length > 10) {
                    SimulationParameters.nodeDelay = args[10];
                }
                if (args.length > 11) {
                    SimulationParameters.npf = Float.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }

            }

            if (SimulationParameters.graphMode.equals("community")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.beta = Float.valueOf(args[2]);
                SimulationParameters.degree = Integer.valueOf(args[3]);
                SimulationParameters.clusters = Integer.valueOf(args[4]);
                SimulationParameters.popSize = Integer.valueOf(args[5]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[6]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 6) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 7) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
                }
                if (args.length > 8) {
                    SimulationParameters.activateReplication = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.nodeDelay = args[9];
                }
                if (args.length > 10) {
                    SimulationParameters.npf = Float.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.nhops = Integer.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }
            }

            if (SimulationParameters.graphMode.equals("communitycircle")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.beta = Float.valueOf(args[2]);
                SimulationParameters.degree = Integer.valueOf(args[3]);
                SimulationParameters.clusters = Integer.valueOf(args[4]);
                SimulationParameters.popSize = Integer.valueOf(args[5]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[6]));
                SimulationParameters.motionAlg = args[7];
                if (args.length > 8) {
                    SimulationParameters.filenameLoc = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.maxIter = Integer.valueOf(args[9]);
                }
                if (args.length > 10) {
                    SimulationParameters.npf = Float.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.nhops = Integer.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }

            }

            if (SimulationParameters.graphMode.equals("scalefree")) {
                SimulationParameters.startNodesScaleFree = Integer.valueOf(args[1]);
                SimulationParameters.edgesToAttachScaleFree = Integer.valueOf(args[2]);
                SimulationParameters.numSteps = Integer.valueOf(args[3]);
                SimulationParameters.popSize = Integer.valueOf(args[4]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[5]));
                SimulationParameters.motionAlg = args[6];
                if (args.length > 7) {
                    SimulationParameters.filenameLoc = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.maxIter = Integer.valueOf(args[8]);
                }
                if (args.length > 9) {
                    SimulationParameters.activateReplication = args[9];
                }
                if (args.length > 10) {
                    SimulationParameters.nodeDelay = args[10];
                }
                if (args.length > 11) {
                    SimulationParameters.npf = Float.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.nhops = Integer.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.simMode = String.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.wsize = Integer.valueOf(args[15]);
                }
                if (args.length > 16) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[16]);
                }
            }

            if (SimulationParameters.graphMode.equals("lattice")) {
                SimulationParameters.rows = Integer.valueOf(args[1]);
                SimulationParameters.columns = Integer.valueOf(args[2]);
                SimulationParameters.popSize = Integer.valueOf(args[3]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[4]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 6) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 7) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
                }
                if (args.length > 8) {
                    SimulationParameters.activateReplication = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.nodeDelay = args[9];
                }
                if (args.length > 10) {
                    SimulationParameters.npf = Float.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.nhops = Integer.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }
            }

            if (SimulationParameters.graphMode.equals("line")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.popSize = Integer.valueOf(args[2]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[3]));
                SimulationParameters.motionAlg = args[4];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[5];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[6]);
                }
                if (args.length > 7) {
                    SimulationParameters.activateReplication = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.nodeDelay = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.npf = Float.valueOf(args[9]);
                }
                if (args.length > 10) {
                    SimulationParameters.nhops = Integer.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.simMode = String.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.wsize = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[14]);
                }
            }

            if (SimulationParameters.graphMode.equals("hubandspoke")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.popSize = Integer.valueOf(args[2]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[3]));
                SimulationParameters.motionAlg = args[4];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[5];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[6]);
                }
                if (args.length > 7) {
                    SimulationParameters.activateReplication = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.nodeDelay = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.npf = Float.valueOf(args[9]);
                }
                if (args.length > 10) {
                    SimulationParameters.nhops = Integer.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.simMode = String.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.wsize = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[14]);
                }
            }

            if (SimulationParameters.graphMode.equals("foresthubandspoke")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.clusters = Integer.valueOf(args[2]);
                SimulationParameters.popSize = Integer.valueOf(args[3]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[4]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 6) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 7) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
                }
                if (args.length > 8) {
                    SimulationParameters.activateReplication = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.nodeDelay = args[9];
                }
                if (args.length > 10) {
                    SimulationParameters.npf = Float.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.nhops = Integer.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }
            }

            if (SimulationParameters.graphMode.equals("circle")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.popSize = Integer.valueOf(args[2]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[3]));
                SimulationParameters.motionAlg = args[4];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[5];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[6]);
                }
                if (args.length > 7) {
                    SimulationParameters.activateReplication = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.nodeDelay = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.npf = Float.valueOf(args[9]);
                }
                if (args.length > 10) {
                    SimulationParameters.nhops = Integer.valueOf(args[10]);
                }
                if (args.length > 12) {
                    SimulationParameters.simMode = String.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.wsize = Integer.valueOf(args[14]);
                }
                if (args.length > 15) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[15]);
                }
            }

            if (SimulationParameters.graphMode.equals("load")) {
                SimulationParameters.filename = args[1];
                SimulationParameters.popSize = Integer.valueOf(args[2]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[3]));
                SimulationParameters.motionAlg = args[4];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[5];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[6]);
                }
                if (args.length > 7) {
                    SimulationParameters.activateReplication = args[7];
                }
                if (args.length > 8) {
                    SimulationParameters.nodeDelay = args[8];
                }
                if (args.length > 9) {
                    SimulationParameters.npf = Float.valueOf(args[9]);
                }
                if (args.length > 10) {
                    SimulationParameters.nhops = Integer.valueOf(args[10]);
                }
                if (args.length > 11) {
                    SimulationParameters.simMode = String.valueOf(args[11]);
                }
                if (args.length > 12) {
                    SimulationParameters.nhopsChain = Integer.valueOf(args[12]);
                }
                if (args.length > 13) {
                    SimulationParameters.wsize = Integer.valueOf(args[13]);
                }
                if (args.length > 14) {
                    SimulationParameters.nofailRounds = Integer.valueOf(args[14]);
                }
            }

            //System.out.println("MaxIter" + SimulationParameters.maxIter);
            System.out.println("**************************");
            System.out.println("Parameters of simulation");

            System.out.println(SimulationParameters.printParameters());
            System.out.println("**************************");

            DataReplicationEscenarioNodeFailingv2 w = new DataReplicationEscenarioNodeFailingv2(SimulationParameters.popSize, SimulationParameters.pf);
            w.init();
            w.run();
        } else {
            System.out.println("Usage:");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim graphmode [smallworld|scalefree|lattice]");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim graphmode smallworld beta nodenumber agentsnumber pf motionAlg");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim graphmode scalefree nodenumber agentsnumber pf motionAlg");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim graphmode lattice rows columns agentsnumber pf motionAlg");
        }
    }
}
