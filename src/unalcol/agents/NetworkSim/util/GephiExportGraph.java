/*
 *
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

/*
* Export graph file to JUNG
*/
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import unalcol.agents.NetworkSim.GraphElements;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class GephiExportGraph {

    private static String experimentsDir = ".";
    private static String[] aMode;
    private static Integer sizeX = 1200;
    private static Integer sizeY = 800;
    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(GephiExportGraph.class);

    private static void exportToCSV() {
        String sDirectorio = experimentsDir;
        System.out.println("experiments dir" + sDirectorio);
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("graph")) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                System.out.println("mode" + filenamep[0]);
                String mode = filenamep[0];

                Graph<GraphElements.MyVertex, String> g = null;
                System.out.println("file" + file.getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(fileInputStream);
                    ObjectInput input = new ObjectInputStream(buffer);
                    g = (Graph) input.readObject();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                String nodeCSV = file.getName().replace("graph", "node");
                String edgeCSV = file.getName().replace("graph", "edge");

                try {
                    //node file
                    PrintWriter nodeCSVFile;
                    nodeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeCSV, true)));
                    int nodeId = 0;
                    HashMap<String, Integer> dictIds = new HashMap<>();
                    nodeCSVFile.println("Id,Label");
                    for (GraphElements.MyVertex v : g.getVertices()) {
                        dictIds.put(v.getName(), nodeId++);
                        nodeCSVFile.println(nodeId + "," + v.getName());
                        //System.out.println();                        
                    }

                    nodeCSVFile.println("Source,Target,Type");
                    PrintWriter edgeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(edgeCSV, true)));
                    for (String ed : g.getEdges()) {
                        Pair<GraphElements.MyVertex> endpoints = g.getEndpoints(ed);
                        edgeCSVFile.println(dictIds.get(endpoints.getFirst().getName()) + "," + dictIds.get(endpoints.getSecond().getName()) + ", Directed");
                    }
                    nodeCSVFile.close();
                    edgeCSVFile.close();
                } catch (IOException ex) {
                    Logger.getLogger(StatisticsProvider.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public static void main(final String[] args) {
        if (args.length > 0) {
            experimentsDir = args[0];
        }
        GephiExportGraph.exportToCSV();
    }

}
