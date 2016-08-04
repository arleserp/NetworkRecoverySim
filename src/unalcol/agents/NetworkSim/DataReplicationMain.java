/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

/**
 *
 * @author Arles Rodriguez
 */
public class DataReplicationMain {

    /*public static String graphMode = "lattice";
    public static int popSize = 5;
    public static int channelNumber = 5;
    public static int vertexNumber = 5;
    public static float pf = 0.5f;
    public static float beta = 1f;
    public static int rows = 5;
    public static int columns = 5;
    public static String motionAlg = "random";
    public static String filename = "";
    public static int maxIter = -1;
    public static int clusters = 4;
    public static int startNodesScaleFree = 1;
    public static int edgesToAttachScaleFree = 2;
    public static int numSteps = 5;
    public static int degree = 2;
    public static String filenameLoc = "";*/

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
                if (args.length > 6) {
                    SimulationParameters.filenameLoc = args[7];
                }
                if (args.length > 7) {
                    SimulationParameters.maxIter = Integer.valueOf(args[8]);
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
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
                }
            }

            if (SimulationParameters.graphMode.equals("communitycircle")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.beta = Float.valueOf(args[2]);
                SimulationParameters.degree = Integer.valueOf(args[3]);
                SimulationParameters.clusters = Integer.valueOf(args[4]);
                SimulationParameters.popSize = Integer.valueOf(args[5]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[6]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
                }
            }

            if (SimulationParameters.graphMode.equals("scalefree")) {
                SimulationParameters.startNodesScaleFree = Integer.valueOf(args[1]);
                SimulationParameters.edgesToAttachScaleFree = Integer.valueOf(args[2]);
                SimulationParameters.numSteps = Integer.valueOf(args[3]);
                SimulationParameters.popSize = Integer.valueOf(args[4]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[5]));
                SimulationParameters.motionAlg = args[6];
                if (args.length > 6) {
                    SimulationParameters.filenameLoc = args[7];
                }
                if (args.length > 7) {
                    SimulationParameters.maxIter = Integer.valueOf(args[8]);
                }
            }

            if (SimulationParameters.graphMode.equals("lattice")) {
                SimulationParameters.rows = Integer.valueOf(args[1]);
                SimulationParameters.columns = Integer.valueOf(args[2]);
                SimulationParameters.popSize = Integer.valueOf(args[3]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[4]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
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
            }

            if (SimulationParameters.graphMode.equals("foresthubandspoke")) {
                SimulationParameters.vertexNumber = Integer.valueOf(args[1]);
                SimulationParameters.clusters = Integer.valueOf(args[2]);
                SimulationParameters.popSize = Integer.valueOf(args[3]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[4]));
                SimulationParameters.motionAlg = args[5];
                if (args.length > 5) {
                    SimulationParameters.filenameLoc = args[6];
                }
                if (args.length > 6) {
                    SimulationParameters.maxIter = Integer.valueOf(args[7]);
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
            }

            if (SimulationParameters.graphMode.equals("load")) {
                SimulationParameters.filename = args[1];
                SimulationParameters.popSize = Integer.valueOf(args[2]);
                SimulationParameters.pf = Float.valueOf(String.valueOf(args[3]));
                SimulationParameters.motionAlg = args[4];
                if (args.length > 4) {
                    SimulationParameters.filenameLoc = args[5];
                }
                if (args.length > 5) {
                    SimulationParameters.maxIter = Integer.valueOf(args[6]);
                }
            }
            System.out.println("MaxIter" + SimulationParameters.maxIter);
            
            
            DataReplicationEscenario w = new DataReplicationEscenario(SimulationParameters.popSize, SimulationParameters.pf);
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
