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
    public static int population = 5;
    public static int channelNumber = 5;
    public static int vertexNumber = 5;
    public static float pf = 0.5f;
    public static float beta = 1f;

    // Perform simulation
    public static void main(String[] args) {
        if (args.length >= 1) {
            //Pop Size
            System.out.println("graphmode:" + args[0]);
            graphMode = args[0];
            WorldThread w = new WorldThread(population, pf,vertexNumber, channelNumber); 
            
            w.init(); 
            w.run();
        } else {
            System.out.println("Usage:");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim graphmode [smallworld|scalefree|lattice]");
        }
    }
}
