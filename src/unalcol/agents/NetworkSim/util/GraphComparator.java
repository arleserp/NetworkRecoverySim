package unalcol.agents.NetworkSim.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import unalcol.agents.NetworkSim.GraphElements;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GraphComparator {
    public class CustomComparator implements Comparator<GraphElements.MyVertex> {

        @Override
        public int compare(GraphElements.MyVertex f1, GraphElements.MyVertex f2) {
            int v1 = Integer.valueOf(f1.getName().substring(1));
            int v2 = Integer.valueOf(f2.getName().substring(1));
            //System.out.println("v1" + v1 + ", v2" + v2);
            if (v1 == v2) {
                return 0;
            } else if (v1 > v2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public double calculateSimilarity(Graph<GraphElements.MyVertex, String> a, Graph<GraphElements.MyVertex, String> b) {
        String aCopy = null;
        String bCopy = null;
        Graph<GraphElements.MyVertex, String> copyA;
        Graph<GraphElements.MyVertex, String> copyB = new UndirectedSparseGraph<>();
        copyA = a;
        /*synchronized (a) {
            aCopy = StringSerializer.serialize(a);
            copyA = (Graph<GraphElements.MyVertex, String>) StringSerializer.deserialize(aCopy);
        }*/
        synchronized (b) {
            //bCopy = StringSerializer.serialize(b);
            // copyB = (Graph<GraphElements.MyVertex, String>) StringSerializer.deserialize(bCopy);
            for (GraphElements.MyVertex v : b.getVertices()) {
                copyB.addVertex(v);
            }
            for (String e : b.getEdges()) {
                copyB.addEdge(e, b.getIncidentVertices(e));
            }
        }
        //synchronized (b) {
        //copyB = b;

        int size = copyA.getVertexCount();
        int[][] adyacA = new int[size][size];
        int[][] adyacB = new int[size][size];

        for (int i = 0; i < adyacA.length; i++) {
            for (int j = 0; j < adyacA.length; j++) {
                adyacA[i][j] = 0;
                adyacB[i][j] = 0;
            }
        }

        ArrayList<GraphElements.MyVertex> av = new ArrayList<>(copyA.getVertices());
        Collections.sort(av, new CustomComparator());

        Iterator<GraphElements.MyVertex> it = av.iterator();
        HashMap<String, Integer> names = new HashMap<>();
        HashMap<String, GraphElements.MyVertex> namesB = new HashMap<>();

        int i = 0;
        while (it.hasNext()) {
            GraphElements.MyVertex va = it.next();
            names.put(va.getName(), i++);
        }

        it = copyA.getVertices().iterator();
        while (it.hasNext()) {
            GraphElements.MyVertex va = it.next();
            ArrayList<GraphElements.MyVertex> na = new ArrayList<>(copyA.getNeighbors(va));
            Iterator<GraphElements.MyVertex> itn = na.iterator();
            while (itn.hasNext()) {
                adyacA[names.get(va.getName())][names.get(itn.next().getName())] = 1;
            }
        }

        Iterator<GraphElements.MyVertex> itB = copyB.getVertices().iterator();
        while (itB.hasNext()) {
            GraphElements.MyVertex vb = itB.next();
            //namesB.put(vb.getName(), vb);
            ArrayList<GraphElements.MyVertex> nb = new ArrayList<>(copyB.getNeighbors(vb));
            Iterator<GraphElements.MyVertex> itnb = nb.iterator();
            while (itnb.hasNext()) {
                String name = itnb.next().getName();
                //System.out.println(vb.getName() + ": " + name);
                if (names.containsKey(vb.getName()) && names.containsKey(name)) {
                    adyacB[names.get(vb.getName())][names.get(name)] = 1;
                }
            }
        }

        double similarity = 0.0;
        double sumA = 0.0;
        double sumB = 0.0;
        for (int j = 0; j < adyacA.length; j++) {
            for (int k = 0; k < adyacA.length; k++) {
                similarity += adyacA[j][k] * adyacB[j][k];
                sumA += (adyacA[j][k]) * adyacA[j][k];
                sumB += (adyacB[j][k]) * adyacB[j][k];
            }
        }

        similarity /= (Math.sqrt(sumA) * Math.sqrt(sumB));
        return similarity * 100.0;
        //}
    }
}
