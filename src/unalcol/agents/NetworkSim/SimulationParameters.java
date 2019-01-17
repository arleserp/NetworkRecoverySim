/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;

/**
 *
 * @author arlese.rodriguezp
 */
public class SimulationParameters {

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
    public static int clusters = 4;
    public static int startNodesScaleFree = 1;
    public static int edgesToAttachScaleFree = 2;
    public static int numSteps = 5;
    public static int degree = 2;
    public static String filenameLoc = "";
    public static ArrayList globalData = null;
    public static String activateReplication = "replalgon";
    public static String nodeDelay = "NODELAY";
    public static float npf = 0;
    public static int nhops = 2;
    public static String simMode = "default";
    public static int nhopsChain = 2;
    public static long averageDelay = 30;
    public static String filenameDelays = "";
    public static String genericFilenameTimeouts = "";
    public static int wsize = 5;
    public static int nofailRounds = 0;

    public static String printParameters() {
        return "SimulationParameters{" + "graphMode=" + graphMode + "\n, popSize=" + popSize + "\n, channelNumber=" + channelNumber + "\n, vertexNumber=" + vertexNumber + "\n, pf=" + pf + "\n, beta=" + beta + "\n, rows=" + rows + "\n, columns=" + columns + "\n, motionAlg=" + motionAlg + "\n, filename=" + filename + "\n, maxIter=" + maxIter + "\n, clusters=" + clusters + "\n, startNodesScaleFree=" + startNodesScaleFree + "\n, edgesToAttachScaleFree=" + edgesToAttachScaleFree + "\n, numSteps=" + numSteps + "\n, degree=" + degree + "\n, filenameLoc=" + filenameLoc + "\n, globalData=" + globalData + "\n, activateReplication=" + activateReplication + "\n, nodeDelay=" + nodeDelay + "\n, npf=" + npf + "\n, nhops=" + nhops + "\n, simMode=" + simMode + "\n, nhopsChain=" + nhopsChain + "\n, averageDelay=" + averageDelay + "\n, filenameDelays=" + filenameDelays + "\n, genericFilenameTimeouts=" + genericFilenameTimeouts + "\n, wsize=" + wsize + "\n, nofailRounds=" + nofailRounds + '}';
    }

}
