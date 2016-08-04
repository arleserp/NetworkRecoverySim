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
}
