/*
 *
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;


/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * MessagesSent2.java
 * ----------------------
 * (C) Copyright 2003, 2004, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for the Australian Institute of Marine Science);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: MessagesSent2.java,v 1.12 2004/06/02 14:35:42 mungady Exp $
 *
 * Changes
 * -------
 * 21-Aug-2003 : Version 1, contributed by David Browning (for the Australian Institute of 
 *               Marine Science);
 * 27-Aug-2003 : Renamed BoxAndWhiskerCategoryDemo --> MessagesSent2, moved dataset creation
 *               into the demo (DG);
 *
 */
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.SyncronizationMain;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class GenerateGraphInformation extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String[] aMode;

    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(GenerateGraphInformation.class);

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
/*    public GenerateGraphInformation(final String title, ArrayList<Double> pf) {
        super(title);
//        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(pf);
        final CategoryAxis xAxis = new CategoryAxis("");
        //final NumberAxis yAxis = new NumberAxis("Round number");
        final NumberAxis yAxis = new NumberAxis("");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        Font font = new Font("Dialog", Font.PLAIN, 14);
        xAxis.setTickLabelFont(font);
        yAxis.setTickLabelFont(font);
        yAxis.setLabelFont(font);
        xAxis.setMaximumCategoryLabelLines(4);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        final JFreeChart chart = new JFreeChart(
                "Information Collected",
                new Font("SansSerif", Font.BOLD, 18),
                plot,
                true
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1600, 800));
        setContentPane(chartPanel);

        TextTitle legendText = null;
        if (pf.size() == 1) {
            legendText = new TextTitle("Population Size");
        } else {
            legendText = new TextTitle("Population Size - Probability of Failure");
        }

        legendText.setFont(font);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        chart.getLegend().setItemFont(font);

        FileOutputStream output;
        try {
            output = new FileOutputStream("InfoCollected" + pf + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 1200, 800, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }*/

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    /* private BoxAndWhiskerCategoryDataset createSampleDataset() {

        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        ArrayList<Integer> aPops = new ArrayList<>();
        ArrayList<Double> aPf = new ArrayList<>();
        ArrayList<String> aTech = new ArrayList<>();

        final DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("exp") && !file.getName().contains("gstats")) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));

                //System.out.println("file" + filenamep[8]);
                int popsize = Integer.valueOf(filenamep[2]);
                double pf = Double.valueOf(filenamep[4]);
                String mode = filenamep[6];
                String graphtype;

                int maxIter = -1;
                //if (!filenamep[8].isEmpty()) {
                maxIter = Integer.valueOf(filenamep[8]);
                //}
                graphtype = filenamep[13];

                System.out.println("psize:" + popsize);
                System.out.println("pf:" + pf);
                System.out.println("mode:" + mode);
                System.out.println("maxIter:" + maxIter);
                System.out.println("graph type:" + graphtype);

                //String[] aMode = {"random", "levywalk", "sandc", "sandclw"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
                //if (/*Pf == pf && *//*isInMode(aMode, mode)) {
                final List list = new ArrayList();
                try {
                    sc = new Scanner(file);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GenerateGraphInformation.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                int agentsCorrect = 0;
                int worldSize = 0;
                double averageExplored = 0.0;
                int bestRoundNumber = 0;
                double avgSend = 0;
                double avgRecv = 0;
                double avgdataExplInd = 0;
                ArrayList<Double> acSt = new ArrayList<>();
                ArrayList<Double> avgExp = new ArrayList<>();
                ArrayList<Double> bestR = new ArrayList<>();
                ArrayList<Double> avSnd = new ArrayList<>();
                ArrayList<Double> avRecv = new ArrayList<>();
                ArrayList<Double> avIndExpl = new ArrayList<>();

                String[] data = null;
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    //System.out.println("line:" + line);
                    data = line.split(",");
                    agentsCorrect = Integer.valueOf(data[0]);
                    //agentsIncorrect = Integer.valueOf(data[1]); // not used
                    worldSize = Integer.valueOf(data[2]);
                    averageExplored = Double.valueOf(data[4]);
                    // data[3] stdavgExplored - not used
                    bestRoundNumber = Integer.valueOf(data[10]);
                    avgSend = Double.valueOf(data[8]);
                    avgRecv = Double.valueOf(data[6]);

                    //Add Data and generate statistics 
                    acSt.add((double) agentsCorrect);
                    avgExp.add(averageExplored);
                    avSnd.add(avgSend);
                    avRecv.add(avgRecv);
                    avIndExpl.add(avgdataExplInd);
                    //if (bestRoundNumber != 0 && bestRoundNumber != -1) {
                    list.add(averageExplored);
                    //}
                }
                LOGGER.debug("Adding series " + i);
                LOGGER.debug(list.toString());

                String[] filenametmp = file.getName().split(Pattern.quote(graphtype));
                String fn2 = filenametmp[1].replace(".graph.csv", "");
                if (Pf.contains(pf)) {
                    /*pf == 1.0E-4 || pf == 3.0E-4*/
 /*if (Pf.size() == 1) {
                        dataset.add(list, popsize, getTechniqueName(mode) + graphtype + fn2);
                    } else {
                        dataset.add(list, String.valueOf(popsize) + "-" + pf, getTechniqueName(mode) + "+" + graphtype + fn2);
                    }
                }
                //}
            }

        }
        return dataset;
    }*/

    private static void getGraphStats() {
        String sDirectorio = experimentsDir;
        System.out.println("experiments dir" + sDirectorio);
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("graph")) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                System.out.println("mode" + filenamep[0]);
                String mode = filenamep[0];

                Graph g = null;
                System.out.println("file" + file.getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(fileInputStream);
                    ObjectInput input = new ObjectInputStream(buffer);
                    g = (Graph) input.readObject();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                String graphStats = file.getName().replace("graph", "graphstats");
                try {
                    PrintWriter escribir;
                    escribir = new PrintWriter(new BufferedWriter(new FileWriter(graphStats, true)));
                    escribir.println("Average Path Length: " + GraphStats.computeAveragePathLength(g));
                    Map<GraphElements.MyVertex, Double> m = GraphStats.clusteringCoefficients(g);
                    escribir.println("Clustering coeficients:" + m);
                    escribir.println("Average Clustering Coefficient: " + GraphStats.averageCC(g));
                    escribir.println("Average degree: " + GraphStats.averageDegree(g));
                    escribir.println("StdDev Average Path Length: " + GraphStats.computeStdDevAveragePathLength(g));
                    escribir.println("StdDev Degree: " + GraphStats.StdDevDegree(g));
                    escribir.close();
                } catch (IOException ex) {
                    Logger.getLogger(StatisticsProvider.class.getName()).log(Level.SEVERE, null, ex);
                }

                //draw graph
                String fileImage = file.getName().replace("graph", "");
                Layout<GraphElements.MyVertex, String> layout = null;

                switch (mode) {
                    case "scalefree":
                        layout = new ISOMLayout<>(g);
                        break;
                    case "smallworld":
                        layout = new CircleLayout<>(g);
                        break;
                    case "community":
                        layout = new CircleLayout<>(g);
                        break;
                    case "kleinberg":
                        layout = new CircleLayout<>(g);
                        break;
                    case "lattice":
                        layout = new ISOMLayout<>(g);
                        break;
                    default:
                        layout = new ISOMLayout<>(g);
                        break;
                }

                BasicVisualizationServer<GraphElements.MyVertex, String> vv = new BasicVisualizationServer<>(layout);
                vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size
                VisualizationImageServer<GraphElements.MyVertex, String> vis
                        = new VisualizationImageServer<>(vv.getGraphLayout(),
                                vv.getGraphLayout().getSize());

                BufferedImage image = (BufferedImage) vis.getImage(
                        new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                                vv.getGraphLayout().getSize().getHeight() / 2),
                        new Dimension(vv.getGraphLayout().getSize()));

                // Write image to a png file
                File outputfile = new File(fileImage + ".png");

                try {
                    ImageIO.write(image, "png", outputfile);
                } catch (IOException e) {
                    // Exception handling
                }
                //*/

            }
        }

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    /**
     * For testing from the command line.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {

        if (args.length > 0) {
            experimentsDir = args[0];
        }

        //GenerateGraphInformation demo = new GenerateGraphInformation(null);
        GenerateGraphInformation.getGraphStats();
        /*if (args.length > 1) {
            mazeMode = args[1];
        }
        

        /*ArrayList<Double> failureProbs = getFailureProbs();

        for (Double pf : failureProbs) {
            ArrayList<Double> pfi = new ArrayList<>();
            pfi.add(pf);
            final GenerateGraphInformation demo = new GenerateGraphInformation("Information Collected", pfi);
        }
         */
    }

    public GenerateGraphInformation(String title) {
        super(title);
    }
}
