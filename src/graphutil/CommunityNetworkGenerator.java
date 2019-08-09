package graphutil;

/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import java.util.Random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.util.ArrayList;
import org.apache.commons.collections15.Factory;

/**
 * WattsBetaSmallWorldGenerator is a graph generator that produces a small world
 * network using the beta-model as proposed by Duncan Watts. The basic ideas is
 * to start with a one-dimensional ring lattice in which each vertex has
 * k-neighbors and then randomly rewire the edges, with probability beta, in
 * such a way that a small world networks can be created for certain values of
 * beta and k that exhibit low charachteristic path lengths and high clustering
 * coefficient.
 *
 * @see "Small Worlds:The Dynamics of Networks between Order and Randomness by
 * D.J. Watts"
 * @author Christopher Brooks, Scott White
 *
 */
public class CommunityNetworkGenerator<V, E> extends Lattice2DGenerator {

    private int numNodes = 0;
    private double beta = 0;
    private int degree = 0;
    private Random random = new Random();
    Factory<V> vertex_factory;
    Factory<E> edge_factory;
    Factory<? extends Graph<V, E>> graph_factory;
    int n_clusters = 0;

    /**
     * Constructs the small world graph generator.
     *
     * @param numNodes the number of nodes in the ring lattice
     * @param beta the probability of an edge being rewired randomly; the
     * proportion of randomly rewired edges in a graph.
     * @param degree the number of edges connected to each vertex; the local
     * neighborhood size.
     */
    public CommunityNetworkGenerator(Factory<? extends Graph<V, E>> graph_factory, Factory<V> vertex_factory,
            Factory<E> edge_factory, int numNodes, double beta, int degree, boolean is_toroidal, int n_clusters) {
        super(graph_factory, vertex_factory, edge_factory, numNodes, is_toroidal);

        if (numNodes < 10) {
            throw new IllegalArgumentException("Lattice must contain at least 10 vertices.");
        }
        if (degree % 2 != 0) {
            throw new IllegalArgumentException("All nodes must have an even degree.");
        }
        if (beta > 1.0 || beta < 0.0) {
            throw new IllegalArgumentException("Beta must be between 0 and 1.");
        }
        this.numNodes = numNodes;
        this.beta = beta;
        this.degree = degree;
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
            Graph temp = new WattsBetaSmallWorldGenerator(graph_factory, vertex_factory, edge_factory, numNodes / n_clusters, beta, 2, true).generateGraph();
            for(Object v: temp.getVertices()){
                c.addVertex(v);
            }
            for (Object e : temp.getEdges()) {
                c.addEdge(e, temp.getEndpoints(e));
            }
            int k = Math.abs(random.nextInt() % numNodes/n_clusters);
            a.add(temp.getVertices().toArray()[k]);
        }
        System.out.println("a" + a);
        for(int i = 1; i < n_clusters; i++){
            //int k = Math.abs(random.nextInt() % numNodes/n_clusters);
            c.addEdge(edge_factory.create(), a.get(0), a.get(i));
        }
        //return the result
        return c;
    }
}
