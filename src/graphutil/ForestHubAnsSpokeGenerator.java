package graphutil;

import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import java.util.Random;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.ArrayList;
import org.apache.commons.collections15.Factory;

/**
 * ForestHubAndSpokeGenerator: Generate four hub and spoke networks join by a 
 * spoke
 */
public class ForestHubAnsSpokeGenerator<V, E> extends Lattice2DGenerator {

    private int numNodes = 0;
    private double beta = 0;
    private int degree = 0;
    private Random random = new Random();
    Factory<V> vertex_factory;
    Factory<E> edge_factory;
    Factory<? extends Graph<V, E>> graph_factory;
    int n_clusters = 0;

    /**
     * 
     * @param graph_factory
     * @param vertex_factory
     * @param edge_factory
     * @param numNodes
     * @param n_clusters
     * @param is_toroidal 
     */
    public ForestHubAnsSpokeGenerator(Factory<? extends Graph<V, E>> graph_factory, Factory<V> vertex_factory,
            Factory<E> edge_factory, int numNodes, int n_clusters, boolean is_toroidal) {
        super(graph_factory, vertex_factory, edge_factory, numNodes, is_toroidal);

        this.numNodes = numNodes;
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
        this.n_clusters = n_clusters;
        this.graph_factory = graph_factory;
        //System.out.println("Creating a lattice with n="+nodes+", k="+degree+", and beta="+beta+".");
    }

    /**
     * Generates a beta-network from a 1-lattice according to the parameters
     * given.
     *
     * @return a beta-network model that is potentially a small-world
     */
    public Graph generateGraph() {
        Graph c = new UndirectedSparseGraph();
        ArrayList<Object> a = new ArrayList();

        for (int i = 0; i < n_clusters; i++) {
            Graph temp = new HubAndSpokeGraphGenerator(graph_factory, vertex_factory, edge_factory, numNodes / n_clusters, true).generateGraph();
            for(Object v: temp.getVertices()){
                c.addVertex(v);
            }
            for (Object e : temp.getEdges()) {
                c.addEdge(e, temp.getEndpoints(e));
            }
            
            int k = Math.abs(random.nextInt() % numNodes/n_clusters);
            a.add(temp.getVertices().toArray()[k]);
            
            k = Math.abs(random.nextInt() % numNodes/n_clusters);
            a.add(temp.getVertices().toArray()[k]);
        }
        
        //System.out.println("a" + a);
        for(int i = 1; i < a.size()-2; i+=2){
            //int k = Math.abs(random.nextInt() % numNodes/n_clusters);
            c.addEdge(edge_factory.create(), a.get(i), a.get(i+1));
        }
        c.addEdge(edge_factory.create(), a.get(0), a.get(a.size()-1));

        return c;
    }
}
