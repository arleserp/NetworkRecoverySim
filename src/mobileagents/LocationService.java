/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

import edu.uci.ics.jung.graph.Graph;
import graphutil.GraphSerialization;
import graphutil.MyVertex;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import serialization.StringSerializer;

/**
 *
 *
 * @author Arles Rodriguez
 */
public class LocationService {

    private static final Set<Integer> POS_USED = new HashSet<>();

    /**
     * Generate a different location for a determined mobile agent
     * @param g
     * @return
     */
    public static MyVertex getLocation(Graph<MyVertex, ?> g) {
        int pos;
        do {
            pos = (int) (Math.random() * g.getVertexCount());
        } while (POS_USED.contains(pos));
        
        POS_USED.add(pos);
        Collection E = g.getVertices();
        return (MyVertex) E.toArray()[pos];
    }

    // Perform simulation
    public static void main(String[] args) {
        int mobileAgentsNumber = 10;
        String dir = args[0]; //current dir 
        //mobileAgentsNumber = Integer.valueOf(args[1]);  //number of locations to generate

        File f = new File(dir);
        String extension;
        File[] files = f.listFiles();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            if (file.isFile() && extension.equals("graph")) {
                ArrayList<MyVertex> locations = new ArrayList<>();
                System.out.println("File:" + file.getName());
                Graph<MyVertex, String> g = GraphSerialization.loadDeserializeGraph(file.getName());

                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String output = file.getName().replace(extension, "");
                output += "loc";
                mobileAgentsNumber = g.getVertexCount();
                for (int k = 0; k < mobileAgentsNumber; k++) {
                    locations.add(getLocation(g));
                }
                StringSerializer s = new StringSerializer();
                s.saveSerializedObject(output, locations);
                POS_USED.clear(); //clear between graph and graph
            }
        }

    }
}
