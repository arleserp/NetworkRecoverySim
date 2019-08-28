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
public class diffExportToGephiCSV {
    static Graph<MyVertex, String> A;
    static Graph<MyVertex, String> B;

    public static void main(String[] args) {
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
                String outputDir = "csvdiff";
                String graphCmpDiff = cmpDir;
                System.out.println("experiments dir:" + graphCmpDiff);
                File f = new File(graphCmpDiff);
                File[] files = f.listFiles();
                System.out.println("loading.... " + fileA);
                A = GraphSerialization.loadDeserializeGraph(fileA);
                System.out.println("A" + A);
                createDir("csvdiff");
                System.out.println("Loading cmpgraph... " + files.length);
                for (File fileB : files) {
                    extension = "";
                    i = fileB.getName().lastIndexOf('.');
                    p = Math.max(fileB.getName().lastIndexOf('/'), fileB.getName().lastIndexOf('\\'));
                    if (i > p) {
                        extension = fileB.getName().substring(i + 1);
                    }
                    fileA = fileA.replace(".graph", "");
                    //spoke condition added for spokecommunitycircle+v+100+beta+0.5+degree+4+clusters+4+sp+10+length+3
                    // communitycircle+v+100+beta+0.5+degree+4+clusters+4 is substring
                    if (fileB.isFile() && extension.equals("graph") && fileB.getName().contains(fileA) && fileA.contains("spoke") == fileB.getName().contains("spoke")) {
                        if (A == null) {
                            System.out.println("You must load network A");
                        }
                        try {
                            System.out.println("loading... " + fileB);
                            B = GraphSerialization.loadDeserializeGraph("./cmpgraph/" + fileB.getName());
                            System.out.println("B" + B);

                            //node file                                
                            String nodeCSV = "./" + outputDir + "/" + fileB.getName().replace("graph", "diff.node.csv");
                            String edgeCSV = "./" + outputDir + "/" + fileB.getName().replace("graph", "diff.edge.csv");

                            PrintWriter nodeCSVFile;
                            nodeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeCSV, true)));
                            int nodeId = 0;
                            HashMap<String, Integer> dictIds = new HashMap<>();
                            nodeCSVFile.println("Id,Label,State");

                            // draw the vertices in the graph
                            for (MyVertex v : A.getVertices()) {
                                // Get the position of the vertex                
                                if (containsVertex(B, v.getName())) {
                                    nodeCSVFile.println(nodeId + "," + v.getName() + ",Recovered");
                                } else {
                                    nodeCSVFile.println(nodeId + "," + v.getName() + ",Failed");
                                }
                                dictIds.put(v.getName(), nodeId++);
                            }

                            try (PrintWriter edgeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(edgeCSV, true)))) {
                                edgeCSVFile.println("Source,Target,Type,State");
                                
                                // draw the edges
                                //problem of implementation????? when i repair network structure I rename edges!
                                A.getEdges().stream().forEach((ed) -> {
                                    //System.out.println("edge"+ ed);
                                    // get the end points of the edge
                                    Pair<MyVertex> endpoints = A.getEndpoints(ed);
                                    
                                    //due to I generate edges with a different name! :(
                                    String newname = "e" + endpoints.getFirst().getName() + endpoints.getSecond().getName();
                                    String newnameB = "e" + endpoints.getSecond().getName() + endpoints.getFirst().getName();
                                    String newnameC = "eb" + endpoints.getFirst().getName() + endpoints.getSecond().getName();
                                    String newnameD = "eb" + endpoints.getSecond().getName() + endpoints.getFirst().getName();
                                    if (!B.containsEdge(ed) && !B.containsEdge(newname) && !B.containsEdge(newnameB) && !B.containsEdge(newnameC) && !B.containsEdge(newnameD)) {
                                        edgeCSVFile.println(dictIds.get(endpoints.getFirst().getName()) + "," + dictIds.get(endpoints.getSecond().getName()) + ",Undirected,EdgeFailed");
                                    } else {
                                        edgeCSVFile.println(dictIds.get(endpoints.getFirst().getName()) + "," + dictIds.get(endpoints.getSecond().getName()) + ",Undirected,EdgeRecovered");
                                    }
                                });
                                nodeCSVFile.close();
                                edgeCSVFile.close();
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(diffExportToGephiCSV.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
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
