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
public class SyncronizationMain {

    public static String graphMode = "lattice";
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
    
    // Perform simulation
    public static void main(String[] args) {
        if (args.length >= 1) {
            //Pop Size
            System.out.println("graphmode:" + args[0]);
            
            graphMode = args[0];
            
            if (graphMode.equals("smallworld")) {
                beta = Float.valueOf(args[1]);
                vertexNumber = Integer.valueOf(args[2]);
                popSize = Integer.valueOf(args[3]);
                pf = Float.valueOf(String.valueOf(args[4]));
                motionAlg = args[5];
            }
            
            if (graphMode.equals("scalefree")) {
                vertexNumber = Integer.valueOf(args[1]);
                popSize = Integer.valueOf(args[2]);
                pf = Float.valueOf(String.valueOf(args[3]));
                motionAlg = args[4];
            }
            
            if (graphMode.equals("lattice")) {
                rows = Integer.valueOf(args[1]);
                columns = Integer.valueOf(args[2]);
                popSize = Integer.valueOf(args[3]);
                pf = Float.valueOf(String.valueOf(args[4]));
                motionAlg = args[5];
            }
            
            if (graphMode.equals("load")) {
                filename = args[1];
                popSize = Integer.valueOf(args[2]);
                pf = Float.valueOf(String.valueOf(args[3]));
                motionAlg = args[4];
            }
            
            WorldThread w = new WorldThread(popSize, pf);
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
