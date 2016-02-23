/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import unalcol.agents.NetworkSim.util.GraphSerialization;

/**
 *
 * @author Arles Rodriguez
 */
public class graphGenerator {
    
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
    
    // Perform simulation
    public static void main(String[] args) {
        if (args.length >= 1) {
            //Pop Size
            System.out.println("graphmode:" + args[0]);
            graphMode = args[0];
            
            if (graphMode.equals("smallworld")) {
                beta = Float.valueOf(args[1]);
                vertexNumber = Integer.valueOf(args[2]);
                filename = args[3];
                System.out.println("Saving graph g mode" + graphMode + ", beta:" + beta + " vertex number: " + vertexNumber + ", in:" + filename);                
            }
            
            if (graphMode.equals("scalefree")) {
                vertexNumber = Integer.valueOf(args[1]);
                filename = args[2];
            }
            
            if (graphMode.equals("lattice")) {
                rows = Integer.valueOf(args[1]);
                columns = Integer.valueOf(args[2]);
                filename = args[3];
            }
            
            Graph<GraphElements.MyVertex, String> g = graphSimpleFactorySave.createGraph(graphMode);
            GraphSerialization.saveSerializedGraph(filename, g);
            //WorldThread w = new WorldThread(popSize, pf);
            //w.init();
            //w.run();
        } else {
            System.out.println("graphGenerator: Generates a new graph and save it");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator graphmode [smallworld|scalefree|lattice]");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator graphmode smallworld beta vertex_number namefile");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator graphmode scalefree vertex_number");
            System.out.println("java -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator graphmode lattice rows columns");
        }
    }
}
