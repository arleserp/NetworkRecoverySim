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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arlese.rodriguezp
 */
public class allJoinFailingNodes {

    static Graph<MyVertex, String> originalNetwork;
    static Graph<MyVertex, String> B;
    static HashMap<String, String> nodesState = new HashMap<>();
    static HashMap<String, String> edgesState = new HashMap<>();
    static HashMap<String, Integer> counterNodesState = new HashMap<>();
    static HashMap<String, Integer> counterEdgesState = new HashMap<>();

    static String outputDir = "csvjoin";

    static void writeFile(String filename, String sep) throws IOException {
        //node file                                
        String nodeCSV = "./" + outputDir + "/" + filename + ".join.node.csv";
        String edgeCSV = "./" + outputDir + "/" + filename + ".join.edge.csv";
        PrintWriter nodeCSVFile;
        nodeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeCSV, true)));

        nodeCSVFile.println("Id" + sep + "State"+ sep + "SimFailed");
        for (String key : nodesState.keySet()) {
            int val = 0;
            if (counterNodesState.containsKey(key)) {
                val = counterNodesState.get(key);
            }
            nodeCSVFile.println(key + sep + nodesState.get(key) + sep + val);
        }
        nodeCSVFile.close();
        PrintWriter edgeCSVFile = new PrintWriter(new BufferedWriter(new FileWriter(edgeCSV, true)));
        final String sepa = sep;
        edgeCSVFile.println("Source" + sepa + "Target" + sepa + "Type" + sepa + "State"+ sep + "SimFailed");

        for (String key : edgesState.keySet()) {
            int val = 0;
            if (counterEdgesState.containsKey(key)) {
                val = counterEdgesState.get(key);
            }
            edgeCSVFile.println(key + sepa + "Undirected" + sepa + edgesState.get(key) + sepa + val);
        }
        edgeCSVFile.close();
    }

    public static void main(String[] args) {
        File srcDir = new File("./");
        File[] filesSrcDir = srcDir.listFiles();
        String sep = ",";

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
                String graphCmpDiff = cmpDir;
                System.out.println("experiments dir:" + graphCmpDiff);
                File f = new File(graphCmpDiff);
                File[] files = f.listFiles();
                System.out.println("loading.... " + fileA);
                originalNetwork = GraphSerialization.loadDeserializeGraph(fileA);
                System.out.println("A" + originalNetwork);
                createDir("csvjoin");
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
                    if (fileB.isFile() && extension.equals("graph")) { // && fileB.getName().contains(fileA) && fileA.contains("spoke") == fileB.getName().contains("spoke")) {
                        if (originalNetwork == null) {
                            System.out.println("You must load network A");
                        }

                        System.out.println("loading... " + fileB);
                        B = GraphSerialization.loadDeserializeGraph("./cmpgraph/" + fileB.getName());
                        System.out.println("B" + B);

                        //HashMap<String, Integer> dictIds = new HashMap<>();
                        // draw the vertices in the graph
                        for (MyVertex v : originalNetwork.getVertices()) {
                            if (v.getName().contains(",")) {
                                sep = ";";
                            }
                            // Get the position of the vertex                
                            if (!nodesState.containsKey(v.getName()) && containsVertex(B, v.getName())) {
                                nodesState.put(v.getName(), "Recovered");
                            } else if (!containsVertex(B, v.getName())) {
                                nodesState.put(v.getName(), "Failed");
                                if (!counterNodesState.containsKey(v.getName())) {
                                    counterNodesState.put(v.getName(), 1);
                                } else {
                                    counterNodesState.put(v.getName(), counterNodesState.get(v.getName())+1);
                                }
                            }
                            //dictIds.put(v.getName());
                        }
                        final String sepa = sep;
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

                            String edgeKeyName = endpoints.getFirst().getName() + sepa + endpoints.getSecond().getName();

                            if (!B.containsEdge(ed) && !B.containsEdge(newname) && !B.containsEdge(newnameB) && !B.containsEdge(newnameC) && !B.containsEdge(newnameD)) {
                                edgesState.put(edgeKeyName, "EdgeFailed");
                                if (!counterEdgesState.containsKey(edgeKeyName)) {
                                    counterEdgesState.put(edgeKeyName, 1);
                                } else {
                                    counterEdgesState.put(edgeKeyName, counterEdgesState.get(edgeKeyName)+1);
                                }
                            } else if (!edgesState.containsKey(edgeKeyName)) {
                                edgesState.put(edgeKeyName, "EdgeRecovered");
                            }
                        });
                    }
                }
                try {
                    writeFile(fileA, sep);
                } catch (IOException ex) {
                    Logger.getLogger(allJoinFailingNodes.class.getName()).log(Level.SEVERE, null, ex);
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
