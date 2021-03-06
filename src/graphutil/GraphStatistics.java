/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphutil;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import util.StatisticsNormalDist;



/**
 *
 * @author Arles Rodriguez
 */
public class GraphStatistics {
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
                    if(u.getDistance(v, w) != null){
                        double distance = u.getDistance(v, w).doubleValue(); 
                        sum += distance;   
                    }else{
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
                    if(u.getDistance(v, w) != null){
                        double distance = u.getDistance(v, w).doubleValue(); 
                        sum += distance;   
                    }else{
                        System.out.println("Graph is not connected now!");
                        return -1;
                    }
                       /* distances.put(v+"-"+w, distance);
                    }*/
                }
            }
            data.add(sum/n-1);
        }
        StatisticsNormalDist st = new StatisticsNormalDist(data, data.size());
        System.out.println("mean"+ st.getMean() + ", stdDev" + st.getStdDev());
        return st.getStdDev();
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
            dataDegree.add((double)g.degree((MyVertex) v));
        }
        StatisticsNormalDist st = new StatisticsNormalDist(dataDegree, dataDegree.size());
        return st.getStdDev();
    }

    static double getDiameter(Graph g) {        
        return DistanceStatistics.diameter(g);
    }
    
    
}