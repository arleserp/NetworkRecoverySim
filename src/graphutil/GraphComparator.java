package graphutil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.uci.ics.jung.graph.Graph;
import environment.NetworkEnvironment;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GraphComparator {
   
    public double calculateSimilarity(Graph<MyVertex, String> a, NetworkEnvironment world) {
        Graph<MyVertex, String> copyA;
        copyA = a;

        int[][] adyacB = world.getAdyacenceMatrix().clone();
        int size = copyA.getVertexCount();
        int[][] adyacA = new int[size][size];

        for (int i = 0; i < adyacA.length; i++) {
            for (int j = 0; j < adyacA.length; j++) {
                adyacA[i][j] = 0;               
            }
        }
        
        for (MyVertex va : a.getVertices()) {            
            ArrayList<MyVertex> na = new ArrayList<>(copyA.getNeighbors(va));
            Iterator<MyVertex> itn = na.iterator();
            while (itn.hasNext()) {
                adyacA[world.getNametoAdyLocation().get(va.getName())][world.getNametoAdyLocation().get(itn.next().getName())] = 1;
            }
        }

        double similarity = 0.0;
        double sumA = 0.0;
        double sumB = 0.0;
        for (int j = 0; j < adyacA.length; j++) {
            for (int k = 0; k < adyacA.length; k++) {
                similarity += adyacA[j][k] * adyacB[j][k];
                sumA += (adyacA[j][k]) * adyacA[j][k];
                sumB += (adyacB[j][k]) * adyacB[j][k];
            }
        }

        similarity /= (Math.sqrt(sumA) * Math.sqrt(sumB));
        System.out.println("siiim" + similarity);
        /*if (similarity * 100.0 < 90) {
            String baseFilename = SimulationParameters.genericFilenameTimeouts;
            baseFilename = baseFilename.replace(".timeout", "");
            baseFilename = baseFilename.replace("timeout+", "");
            //System.out.println("base filename:" + baseFilename);
            String dir = "cmpgraph";
            createDir(dir);
            GraphSerialization.saveSerializedGraph("./" + dir + "/" + getFileName() + "+" + baseFilename + "+Similarity+" + similarity + ".graph", copyB);
        }*/
        return similarity * 100.0;

        //}
    }
}
