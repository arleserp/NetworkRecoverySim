package graphutil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class GraphComparator {

    public class CustomComparator implements Comparator<MyVertex> {

        @Override
        public int compare(MyVertex f1, MyVertex f2) {
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

    public double calculateSimilarity(Graph<MyVertex, String> a, Graph<MyVertex, String> b) {
        String aCopy = null;
        String bCopy = null;
        Graph<MyVertex, String> copyA;
        Graph<MyVertex, String> copyB = new UndirectedSparseGraph<>();
        copyA = a;
        /*synchronized (a) {
            aCopy = StringSerializer.serialize(a);
            copyA = (Graph<MyVertex, String>) StringSerializer.deserialize(aCopy);
        }*/
        synchronized (b) {
            //bCopy = StringSerializer.serialize(b);
            // copyB = (Graph<MyVertex, String>) StringSerializer.deserialize(bCopy);
            for (MyVertex v : b.getVertices()) {
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

        ArrayList<MyVertex> av = new ArrayList<>(copyA.getVertices());
        Collections.sort(av, new CustomComparator());

        Iterator<MyVertex> it = av.iterator();
        HashMap<String, Integer> names = new HashMap<>();
        HashMap<String, MyVertex> namesB = new HashMap<>();

        int i = 0;
        while (it.hasNext()) {
            MyVertex va = it.next();
            names.put(va.getName(), i++);
        }

        it = copyA.getVertices().iterator();
        while (it.hasNext()) {
            MyVertex va = it.next();
            ArrayList<MyVertex> na = new ArrayList<>(copyA.getNeighbors(va));
            Iterator<MyVertex> itn = na.iterator();
            while (itn.hasNext()) {
                adyacA[names.get(va.getName())][names.get(itn.next().getName())] = 1;
            }
        }

        Iterator<MyVertex> itB = copyB.getVertices().iterator();
        while (itB.hasNext()) {
            MyVertex vb = itB.next();
            //namesB.put(vb.getName(), vb);
            ArrayList<MyVertex> nb = new ArrayList<>(copyB.getNeighbors(vb));
            Iterator<MyVertex> itnb = nb.iterator();
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

        /*if (similarity * 100.0 < 90) {
            String baseFilename = SimulationParameters.genericFilenameTimeouts;
            baseFilename = baseFilename.replace(".timeout", "");
            baseFilename = baseFilename.replace("timeout+", "");
            //System.out.println("base filename:" + baseFilename);
            String dir = "cmpgraph";
            createDir(dir);
            GraphSerialization.saveSerializedGraph("./" + dir + "/" + getFileName() + "+" + baseFilename + "+Similarity+" + similarity + ".graph", copyB);
        }*/
        return similarity * 100.0;

        //}
    }

    private String getFileName() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        return reportDate;
    }

    private void createDir(String filename) {
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

}
