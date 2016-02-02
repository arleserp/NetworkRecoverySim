/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.collections15.Transformer;
import unalcol.agents.NetworkSim.GraphElements;

/**
 *
 * @author Arles Rodriguez
 */
public class graphStatistics {

    public static double computeAveragePathLength(Graph<GraphElements.MyVertex, String> graph) {
        double sum = 0;
        double n = 0;
        //Transformer<GraphElements.MyVertex, Double> distances = DistanceStatistics.averageDistances(graph, new UnweightedShortestPath<>(graph));
        Transformer<GraphElements.MyVertex, Double> distances = DistanceStatistics.averageDistances(graph, new UnweightedShortestPath(graph));
        System.out.println("distances" + distances.toString());
        
        for (GraphElements.MyVertex v : graph.getVertices()) {
            
            sum += distances.transform(v);
            System.out.println("v" + v + ", distances" + distances.transform(v));
            n++;
        }
        return sum / n;
    }

    public static Map clusteringCoefficients(Graph g){
        return edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients(g);
    }
    
    public static Double averageCC(Graph g){
        Map<GraphElements.MyVertex, Double> m = clusteringCoefficients(g);
        Collection<Double> val = m.values();
        double sum = 0;
        for(Double v: val){
            sum+= v;
        }
        return sum/val.size();
    }
    
    
    public static Double averageDegree(Graph g){
        Collection vertices = g.getVertices();
        double sum = 0;
        for(Object v: vertices){
            sum+= g.degree((GraphElements.MyVertex)v);
        }
        return sum/g.getVertexCount();
    }
    
    
 /*   public void computeClusteringStats(Graph<GraphElements.MyVertex, String> graph) {
        //clusteringStats = new SummaryStatistics();
        for (GraphElements.MyVertex vertex : graph.getVertices()) {
            if (degree(vertex) > 0) {
                double clusteringCoefficient
                        = computeWeightedClusteringCoefficient(vertex);
                clusteringStats.addValue(clusteringCoefficient);
                logger.debug("cc(" + vertex.hashCode() + ") = "
                        + clusteringCoefficient);
            }
        }
    }
*/
}
