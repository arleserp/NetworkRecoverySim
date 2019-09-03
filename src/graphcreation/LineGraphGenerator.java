package graphcreation;

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
public class LineGraphGenerator<V, E> extends Lattice2DGenerator {

    private int numNodes = 0;
    private double beta = 0;
    private int degree = 0;
    private Random random = new Random();
    Factory<V> vertex_factory;
    Factory<E> edge_factory;

    /**
     * Constructs the small world graph generator.
     *
     * @param graph_factory
     * @param vertex_factory
     * @param numNodes the number of nodes in the ring lattice
     * proportion of randomly rewired edges in a graph.
     * @param is_toroidal
     * @param edge_factory
     */
    public LineGraphGenerator(Factory<? extends Graph<V, E>> graph_factory, Factory<V> vertex_factory,
            Factory<E> edge_factory, int numNodes, boolean is_toroidal) {
        super(graph_factory, vertex_factory, edge_factory, numNodes, is_toroidal);

        this.numNodes = numNodes;
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
    }

    /**
     * Generates a beta-network from a 1-lattice according to the parameters
     * given.
     *
     * @return a beta-network model that is potentially a small-world
     */
    public Graph generateGraph() {
        Graph g = new UndirectedSparseGraph();

        for (int i = 0; i < numNodes; i++) {
            g.addVertex(vertex_factory.create());
        }
        
        //rewire edges
        for (int i = 0; i < numNodes-1; i++) {
           g.addEdge(edge_factory.create(), g.getVertices().toArray()[i], g.getVertices().toArray()[i+1]);
        }
        
        if(is_toroidal){
           g.addEdge(edge_factory.create(), g.getVertices().toArray()[0], g.getVertices().toArray()[numNodes-1]);
        }
        return g;
    }
}
