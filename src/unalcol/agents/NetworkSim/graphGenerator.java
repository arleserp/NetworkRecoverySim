/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import unalcol.agents.NetworkSim.util.GraphSerialization;
import unalcol.agents.NetworkSim.util.GraphStats;

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
    public static int degree = 4;
    public static int clusters = 4;
    public static int startNodesScaleFree = 4;
    public static int edgesToAttachScaleFree = 4;
    public static int numSteps = 100;
    public static int length = 1;

    // Perform simulation
    public static void main(String[] args) {
        if (args.length >= 1) {
            //Pop Size
            System.out.println("graphmode:" + args[0]);
            graphMode = args[0];

            filename = graphMode;

            if (graphMode.equals("smallworld")) {
                vertexNumber = Integer.valueOf(args[1]);
                beta = Float.valueOf(args[2]);
                degree = Integer.valueOf(args[3]);
                filename += "+v+" + vertexNumber + "+beta+" + beta + "+degree+" + degree;
            }

            if (graphMode.equals("community")) {
                vertexNumber = Integer.valueOf(args[1]);
                beta = Float.valueOf(args[2]);
                degree = Integer.valueOf(args[3]);
                clusters = Integer.valueOf(args[4]);
                filename += "+v+" + vertexNumber + "+beta+" + beta + "+degree+" + degree + "+clusters+" + clusters;
            }

            if (graphMode.equals("communitycircle")) {
                vertexNumber = Integer.valueOf(args[1]);
                beta = Float.valueOf(args[2]);
                degree = Integer.valueOf(args[3]);
                clusters = Integer.valueOf(args[4]);
                filename += "+v+" + vertexNumber + "+beta+" + beta + "+degree+" + degree + "+clusters+" + clusters;
            }

            if (graphMode.equals("line")) {
                vertexNumber = Integer.valueOf(args[1]);
                filename += "+v+" + vertexNumber;
            }

            if (graphMode.equals("hubandspoke")) {
                vertexNumber = Integer.valueOf(args[1]);
                filename += "+v+" + vertexNumber;
            }

            if (graphMode.equals("foresthubandspoke")) {
                vertexNumber = Integer.valueOf(args[1]);
                clusters = Integer.valueOf(args[2]);
                filename += "+v+" + vertexNumber + "+clusters+" + clusters;
            }

            if (graphMode.equals("circle")) {
                vertexNumber = Integer.valueOf(args[1]);
                filename += "+v+" + vertexNumber;
            }

            if (graphMode.equals("scalefree")) {
                startNodesScaleFree = Integer.valueOf(args[1]);
                edgesToAttachScaleFree = Integer.valueOf(args[2]);
                numSteps = Integer.valueOf(args[3]);
                filename += "+sn+" + startNodesScaleFree + "+eta+" + edgesToAttachScaleFree + "+numSt+" + numSteps;
            }

            if (graphMode.equals("lattice")) {
                rows = Integer.valueOf(args[1]);
                columns = Integer.valueOf(args[2]);
                filename += "+r+" + rows + "+c+" + columns;
            }

            if (graphMode.equals("longhubandspoke")) {
                vertexNumber = Integer.valueOf(args[1]);                
                filename += "+v+" + vertexNumber;               
                length = Integer.valueOf(args[2]);
                filename += "+l+" +length;
            }

            filename += ".graph";

            Graph<GraphElements.MyVertex, String> g;
            do {
                g = graphSimpleFactorySave.createGraph(graphMode);
                System.out.println("end create");
                GraphSerialization.saveSerializedGraph(filename, g);
            } while (GraphStats.computeAveragePathLength(g) == -1);

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
