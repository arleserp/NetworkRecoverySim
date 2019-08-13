package graphutil;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author arlese.rodriguezp
 */
public class GephiToGraph {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File srcDir = new File("./csvgephi");
        File[] filesSrcDir = srcDir.listFiles();

        for (File fileSrc : filesSrcDir) {
            String fileA = fileSrc.getName();
            int i = fileA.lastIndexOf('.');
            String extension = "";
            int p = Math.max(fileA.lastIndexOf('/'), fileA.lastIndexOf('\\'));
            if (i > p) {
                extension = fileA.substring(i + 1);
            }
            System.out.println("loading" + fileA + " ext " + extension);
            if (fileSrc.isFile() && extension.equals("csv") && fileA.contains("node")) {
                System.out.println("entra" + fileA);
                Graph<MyVertex, String> g = new UndirectedSparseGraph<>();
                GraphCreator.VertexFactory vf = new GraphCreator.VertexFactory();

                System.out.println("loading.... " + fileA);

                BufferedReader br = new BufferedReader(new FileReader(fileSrc));
                HashMap<String, MyVertex> vertices = new HashMap<>();
                HashMap<Integer, String> idNode = new HashMap<>();

                String st;
                while ((st = br.readLine()) != null) {
                    String[] l = st.split(",");
                    if (!(l[0].equals("Id"))) {
                        idNode.put(Integer.parseInt(l[0]), l[1]);
                        vertices.put(l[1], new MyVertex(l[1]));
                        g.addVertex(vertices.get(l[1]));
                    }
                }

                br.close();

                String edges = fileA.replace("node", "edge");
                BufferedReader edbr = new BufferedReader(new FileReader(new File("./csvgephi/" + edges)));

                GraphCreator.EdgeFactory edge_factory = new GraphCreator.EdgeFactory();
                while ((st = edbr.readLine()) != null) {
                    String[] l = st.split(",");
                    if (!l[0].equals("Source")) {
                        Integer source = Integer.parseInt(l[0]);
                        Integer dest = Integer.parseInt(l[1]);
                        g.addEdge(edge_factory.create(), vertices.get(idNode.get(source)), vertices.get(idNode.get(dest)));
                    }
                }
                edbr.close();

                String graphFile = fileA.replace("node", "");
                graphFile = graphFile.replace(".csv", "graph");
                GraphSerialization.saveSerializedGraph(graphFile, g);
            }
        }
    }
}
