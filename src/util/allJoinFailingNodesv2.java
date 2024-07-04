/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import graphutil.GraphSerialization;
import graphutil.MyVertex;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arlese.rodriguezp
 */
public class allJoinFailingNodesv2 {

    static Graph<MyVertex, String> originalNetwork;
    static Graph<MyVertex, String> B;

    public static void main(String[] args) throws IOException {
        File srcDir = new File("./");
        File[] filesSrcDir = srcDir.listFiles();

        for (File fileSrc : filesSrcDir) {
            String fileA = fileSrc.getName();
            int i = fileA.lastIndexOf('.');
            String extension = "";
            int p = Math.max(fileA.lastIndexOf('/'), fileA.lastIndexOf('\\'));
            if (i > p) {
                extension = fileA.substring(i + 1);
            }
            if (fileSrc.isFile() && extension.equals("graph")) {
                String cmpDir = "./cmpgraph";
                String outputDir = "csvjoinv2";
                String graphCmpDiff = cmpDir;
                System.out.println("experiments dir:" + graphCmpDiff);
                File f = new File(graphCmpDiff);
                File[] files = f.listFiles();
                System.out.println("loading.... " + fileA);
                originalNetwork = GraphSerialization.loadDeserializeGraph(fileA);
                System.out.println("A" + originalNetwork);
                createDir("csvjoinv2");
                System.out.println("Loading cmpgraph... " + files.length);
                int c = 0;
                int d = 0;
                //node file         
                fileA = fileA.replace(".graph", "");
                String nodeCSV = "./" + outputDir + "/" + fileA + ".join.node.csv";;
                String edgeCSV = "./" + outputDir + "/" + fileA + ".join.edge.csv";;
                PrintWriter nodeCSVFile;
                nodeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeCSV, true)));
                PrintWriter edgeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(edgeCSV, true)));
                for (File fileB : files) {
                    extension = "";
                    i = fileB.getName().lastIndexOf('.');
                    p = Math.max(fileB.getName().lastIndexOf('/'), fileB.getName().lastIndexOf('\\'));
                    if (i > p) {
                        extension = fileB.getName().substring(i + 1);
                    }
                    

                    //spoke condition added for spokecommunitycircle+v+100+beta+0.5+degree+4+clusters+4+sp+10+length+3
                    // communitycircle+v+100+beta+0.5+degree+4+clusters+4 is substring
                    if (fileB.isFile() && extension.equals("graph")) { // && fileB.getName().contains(fileA) && fileA.contains("spoke") == fileB.getName().contains("spoke")) {
                        if (originalNetwork == null) {
                            System.out.println("You must load network A");
                        }

                        System.out.println("loading... " + fileB);
                        B = GraphSerialization.loadDeserializeGraph("./cmpgraph/" + fileB.getName());
                        System.out.println("B" + B);

                        //HashMap<String, Integer> dictIds = new HashMap<>();
                        String sep = ",";
                        // draw the vertices in the graph
                        for (MyVertex v : originalNetwork.getVertices()) {
                            if (v.getName().contains(",")) {
                                sep = ";";
                            }
                            if (c == 0) {
                                nodeCSVFile.println("Id" + sep + "State");
                                c++;
                            }
                            // Get the position of the vertex                
                            if (containsVertex(B, v.getName())) {
                                nodeCSVFile.println(v.getName() + sep + "Recovered");
                            } else {
                                nodeCSVFile.println(v.getName() + sep + "Failed");
                            }
                            //dictIds.put(v.getName());
                        }

                        final String sepa = sep;
                        if (d == 0) {
                            edgeCSVFile.println("Source" + sepa + "Target" + sepa + "Type" + sepa + "State");
                            d++;
                        }

                        // draw the edges
                        //problem of implementation????? when i repair network structure I rename edges!
                        originalNetwork.getEdges().stream().forEach((ed) -> {
                            //System.out.println("edge"+ ed);
                            // get the end points of the edge
                            Pair<MyVertex> endpoints = originalNetwork.getEndpoints(ed);

                            //due to I generate edges with a different name! :(
                            String newname = "e" + endpoints.getFirst().getName() + endpoints.getSecond().getName();
                            String newnameB = "e" + endpoints.getSecond().getName() + endpoints.getFirst().getName();
                            String newnameC = "eb" + endpoints.getFirst().getName() + endpoints.getSecond().getName();
                            String newnameD = "eb" + endpoints.getSecond().getName() + endpoints.getFirst().getName();
                            if (!B.containsEdge(ed) && !B.containsEdge(newname) && !B.containsEdge(newnameB) && !B.containsEdge(newnameC) && !B.containsEdge(newnameD)) {
                                edgeCSVFile.println(endpoints.getFirst().getName() + sepa + endpoints.getSecond().getName() + sepa + "Undirected" + sepa + "EdgeFailed");
                            } else {
                                edgeCSVFile.println(endpoints.getFirst().getName() + sepa + endpoints.getSecond().getName() + sepa + "Undirected" + sepa + "EdgeRecovered");
                            }
                        });
                    }
                }
                nodeCSVFile.close();
                edgeCSVFile.close();
            }
        }
    }

    private static void createDir(String filename) {
        File theDir = new File(filename);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + filename);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                System.out.println("Security Exception!");
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

    }

    public static boolean containsVertex(Graph<MyVertex, String> g, String name) {
        return g.getVertices().stream().anyMatch((v) -> (v.getName().equals(name)));
    }
}
