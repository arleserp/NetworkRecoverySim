/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphutil;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez
 */
public class NodeStatistics {

    public final static HashMap<String, Double> distances = new HashMap<>();

    public static double computeAveragePathLength(Graph<MyVertex, ?> graph) {
        double sum = 0;
        double n = graph.getVertexCount();
        //Transformer<MyVertex, Double> distances = DistanceStatistics.averageDistances(graph, new UnweightedShortestPath<>(graph));
        UnweightedShortestPath u = new UnweightedShortestPath(graph);

        for (MyVertex v : graph.getVertices()) {
            for (MyVertex w : graph.getVertices()) {
                if (!w.equals(v)) {
                    //System.out.println("<" + w + "," + v + ">" + u.getDistance(v, w).doubleValue());
                    //if(distances.containsKey(v+"-"+w)){
                    //   sum += distances.get(v+"-"+w);
                    /*}else{*/
                    if (u.getDistance(v, w) != null) {
                        double distance = u.getDistance(v, w).doubleValue();
                        sum += distance;
                    } else {
                        System.out.println("Graph is not connected now!");
                        return -1;
                    }
                    /* distances.put(v+"-"+w, distance);
                    }*/
                }
            }
        }
        return sum / (n * (n - 1));
    }

    public static double computeStdDevAveragePathLength(Graph<MyVertex, ?> graph) {
        double sum = 0;
        double n = graph.getVertexCount();
        //Transformer<MyVertex, Double> distances = DistanceStatistics.averageDistances(graph, new UnweightedShortestPath<>(graph));
        UnweightedShortestPath u = new UnweightedShortestPath(graph);

        ArrayList<Double> data = new ArrayList();

        for (MyVertex v : graph.getVertices()) {
            sum = 0;
            for (MyVertex w : graph.getVertices()) {
                if (!w.equals(v)) {
                    //System.out.println("<" + w + "," + v + ">" + u.getDistance(v, w).doubleValue());
                    //if(distances.containsKey(v+"-"+w)){
                    //   sum += distances.get(v+"-"+w);
                    /*}else{*/
                    if (u.getDistance(v, w) != null) {
                        double distance = u.getDistance(v, w).doubleValue();
                        sum += distance;
                    } else {
                        System.out.println("Graph is not connected now!");
                        return -1;
                    }
                    /* distances.put(v+"-"+w, distance);
                    }*/
                }
            }
            data.add(sum / n - 1);
        }
        StatisticsNormalDist st = new StatisticsNormalDist(data, data.size());
        System.out.println("mean" + st.getMean() + ", stdDev" + st.getStdDev());
        return st.getStdDev();
    }

    /**
     * Eccentricity.
     *
     * @param g - to be calculated.
     * @param v - central vertex.
     * @return eccentricity of the specific vertex.
     */
    public static double eccentricity(Graph<MyVertex, ?> g, MyVertex v) {
        UnweightedShortestPath<MyVertex, ?> dist = new UnweightedShortestPath<>(g);
        double eccentricity = 0;
        for (MyVertex end : g.getVertices()) {
            //  Skip v itself
            if (v.equals(end)) {
                continue;
            }
            //  Calculate distance and record the farthest distance
            double distance = dist.getDistance(v, end).doubleValue();
            if (distance > eccentricity) {
                eccentricity = distance;
            }
        }
        return eccentricity;
    }

    public static Map clusteringCoefficients(Graph g) {
        return edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients(g);
    }

    public static Double averageCC(Graph g) {
        Map<MyVertex, Double> m = clusteringCoefficients(g);
        Collection<Double> val = m.values();
        double sum = 0;
        for (Double v : val) {
            sum += v;
        }
        return sum / val.size();
    }

    public static Double averageDegree(Graph g) {
        Collection vertices = g.getVertices();
        double sum = 0;
        for (Object v : vertices) {
            sum += g.degree((MyVertex) v);
        }
        return sum / g.getVertexCount();
    }

    public static Double StdDevDegree(Graph g) {
        ArrayList<Double> dataDegree = new ArrayList<>();
        Collection vertices = g.getVertices();
        //double sum = 0;
        for (Object v : vertices) {
            //sum += g.degree((MyVertex) v);
            dataDegree.add((double) g.degree((MyVertex) v));
        }
        StatisticsNormalDist st = new StatisticsNormalDist(dataDegree, dataDegree.size());
        return st.getStdDev();
    }

    static double getDiameter(Graph g) {
        return DistanceStatistics.diameter(g);
    }

    static void createDir(String filename) {
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

    public static void main(String[] args) throws IOException {
        String sDirectorio = "./";
        System.out.println("experiments dir" + sDirectorio);
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

        String folderName = "network_specs";
        createDir(folderName);

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("graph")) {
                //System.out.println(file.getName());
                //System.out.println("get: " + file.getName());
                //String[] filenamep = file.getName().split(Pattern.quote("+"));
                //System.out.println("mode" + filenamep[0]);
                //String mode = filenamep[0];

                Graph<MyVertex, ?> g = null;
                System.out.println("file:" + file.getName());

                //if (!file.getName().equals("pioro.graph")) {
                //   continue;
                // }
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
                //System.out.println("graph" + g.toString());

                String fileOut = folderName + "/" + file.getName().split("\\.")[0] + ".specs.csv";
                System.out.println("file name output:" + fileOut);
                File fileO = new File(fileOut);
                FileWriter fw = new FileWriter(fileO);
                BufferedWriter bw = new BufferedWriter(fw);
                int n = g.getVertexCount();// number of vertex 
                double d = (double) (n); // this is to normalize the node value

                double norm = (n - 1) * (n - 2) / 2;

                //System.out.println("Network" + g);
                ClosenessCentrality<MyVertex, ?> closeness = new ClosenessCentrality<>(g);
                EigenvectorCentrality<MyVertex, ?> eigenvector = new EigenvectorCentrality<>(g);
                PageRank<MyVertex, ?> pagerank05 = new PageRank<>(g, 0.5);
                pagerank05.evaluate();
                /*PageRank<MyVertex, ?> pagerank085 = new PageRank<>(g, 0.85);
                pagerank085.evaluate();*/

                eigenvector.acceptDisconnectedGraph(true);
                eigenvector.evaluate();

                BetweennessCentrality bwranker = new BetweennessCentrality(g);
                bwranker.setRemoveRankScoresOnFinalize(false);
                bwranker.evaluate();

//                bw.write("Id;closnesscentrality;normclosnesscentrality;eigenvectorcentrality;degree;betweenesscentrality;normbetweenesscentrality;eccentricity\n");
                bw.write("Id;closnesscentrality;eigenvectorcentrality;degree;betweenesscentrality;eccentricity;pagerank\n");
                for (MyVertex v : g.getVertices()) {
                    //bw.write(v.getName() + ";" + closeness.getVertexScore(v)+ ";" + (closeness.getVertexScore(v) * d) + ";" + eigenvector.getVertexScore(v) + ";" + g.degree(v) + ";" + (bwranker.getVertexRankScore(v)) + ";" + (bwranker.getVertexRankScore(v) / norm) + ";" + eccentricity(g, v) + "\n"); // This adjustment allows comparisons between nodes of graphs of different sizes.
                    bw.write(v.getName() + ";" + closeness.getVertexScore(v) + ";" + eigenvector.getVertexScore(v) + ";" + g.degree(v) + ";" + (bwranker.getVertexRankScore(v)) + ";" + eccentricity(g, v) + ";" + pagerank05.getVertexScore(v) +"\n"); // This adjustment allows comparisons between nodes of graphs of different sizes.
                }
                bw.close();
            }
        }
    }
}
