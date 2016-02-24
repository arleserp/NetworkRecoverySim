package unalcol.agents.NetworkSim.util;

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
import java.util.Collection;
import java.util.LinkedList;
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
public class WattsBetaSmallWorldGenerator<V, E> extends Lattice2DGenerator {

    private int numNodes = 0;
    private double beta = 0;
    private int degree = 0;
    private Random random = new Random();
    Factory<V> vertex_factory;
    Factory<E> edge_factory;

    /**
     * Constructs the small world graph generator.
     *
     * @param numNodes the number of nodes in the ring lattice
     * @param beta the probability of an edge being rewired randomly; the
     * proportion of randomly rewired edges in a graph.
     * @param degree the number of edges connected to each vertex; the local
     * neighborhood size.
     */
    public WattsBetaSmallWorldGenerator(Factory<? extends Graph<V, E>> graph_factory, Factory<V> vertex_factory,
            Factory<E> edge_factory, int numNodes, double beta, int degree, boolean is_toroidal) {
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
        //System.out.println("Creating a lattice with n="+nodes+", k="+degree+", and beta="+beta+".");
    }

    /**
     * Generates a beta-network from a 1-lattice according to the parameters
     * given.
     *
     * @return a beta-network model that is potentially a small-world
     */
    public Graph generateGraph() {
        Graph g = new UndirectedSparseGraph();
        int numEdges = 0;
        ArrayList vertices = new ArrayList();

        for (int i = 0; i < numNodes; i++) {
            //Vertex v = vertex_factory.create();
            g.addVertex(vertex_factory.create());
        }

        //rewire edges
        for (int i = 0; i < numNodes; i++) {
            int start = i - degree;
            if (start < 0) {
                start = numNodes + start;
            }

            int count = 0;
            int stop = 2 * degree;
            while (count < stop) {
                if ((i != start)) {
                    g.addEdge(edge_factory.create(), g.getVertices().toArray()[i], g.getVertices().toArray()[start]);
                    numEdges++;
                }
                if (i != start) {
                    count++;
                }
                start = (start + 1) % numNodes;
            }
        }
        //continue...
        LinkedList edgeList = new LinkedList();

        for (int i = 0; i < g.getEdges().size(); i++) {
            edgeList.add(g.getEdges().toArray()[i]);
        }

        //For each edge
        while (edgeList.size() > 0) {
            //Get the next edge
            Object nextEdge = edgeList.removeFirst();
            //System.out.println("next" + nextEdge);

            //System.out.println("g" + g);
            //System.out.println("contains edge" + g.containsEdge(nextEdge));

            //System.out.println("incident" + g.getIncidentVertices(nextEdge));
            Object source = g.getIncidentVertices(nextEdge).toArray()[0];
            Object target = g.getIncidentVertices(nextEdge).toArray()[1];
            //System.out.println("source" + source);
            //System.out.println("dest" + target);

            //Throw a random dart
            double percent = random.nextDouble();

            //If this should be shuffled
            if (percent <= beta) {

                //Choose a new node 
                int k = 0;
                Object rndDest = null;
                boolean candidate = false;

                //Keep looping until we have a suitable candidate
                while (!candidate) {
                    //choose a new edge
                    do {
                        k = Math.abs(random.nextInt() % numNodes);
                        rndDest = g.getVertices().toArray()[k];
                    } while (g.getVertices().toArray()[k].equals(source));
                    
                    //reset variable for this pass
                    candidate = true;

                    //Make sure we are not create a self loop
                    candidate = candidate && (!g.getEdges().toArray()[k].equals(source));
                    candidate = candidate && (!g.getEdges().toArray()[k].equals(target));

                    //Check to see if this edge already exists
                    int edgeCount = 0;
                    Collection edgeIter = g.getNeighbors(source);
                    //System.out.println("ed iter" + edgeIter);
                    for (int j = 0; j < edgeIter.size(); j++) {
                        //If this edge already exists
                        candidate = candidate && (edgeIter.toArray()[j] != rndDest);
                        edgeCount++;
                    }

                    //If edgeCount indicates that this node is connected to all other nodes than we should stop trying
                    //there is no way to switch it (no node that we are not connected to).
                    if ((edgeCount == numNodes)) {
                        candidate = false;
                        break;
                    } else if ((edgeCount == (numNodes - 1))) {
                        candidate = false;
                        break;
                    }
                }

                //If this is still a candidate, then swap the edges
                if (candidate) {
                    //Switch in the new Edge
                    g.removeEdge(nextEdge);
                    g.addEdge(edge_factory.create(), source, rndDest);
                }
            }
        }
        //return the result
        return g;
    }
}
