/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import edu.uci.ics.jung.algorithms.matrix.GraphMatrixOperations;
import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author ARODRIGUEZ
 */
public class LaplacianUtility {

    public double getEigenValueLapplacianMatrix(Graph g) {
        SparseDoubleMatrix2D D = GraphMatrixOperations.createVertexDegreeDiagonalMatrix(g);
        SparseDoubleMatrix2D A = GraphMatrixOperations.graphToSparseMatrix(g);

        SparseDoubleMatrix2D L = new SparseDoubleMatrix2D(g.getVertexCount(), g.getVertexCount());
        
        for (int i = 0; i < g.getVertexCount(); i++) {
            for (int j = 0; i < g.getVertexCount(); j++) {
                L.set(i, j, D.get(i, j)-A.get(i, j));
            }
        }
        
        return 9;
        
    }

}
