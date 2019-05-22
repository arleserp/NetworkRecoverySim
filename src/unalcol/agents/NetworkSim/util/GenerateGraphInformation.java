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
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import unalcol.agents.NetworkSim.GraphElements;
import static unalcol.agents.NetworkSim.util.GraphStats.clusteringCoefficients;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class GenerateGraphInformation extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String[] aMode;
    private static Integer sizeX = 1200;
    private static Integer sizeY = 800;
    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(GenerateGraphInformation.class);

    private static void drawHistogramDegree(HashMap<Object, Integer> map, String name) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        Collection<Integer> c = map.values();
        List<Integer> list = new ArrayList<Integer>(c);

        Collections.sort(list);
        int j = 0;
        for (Integer i : list) {
            defaultcategorydataset.addValue(i, "", String.valueOf(j));
            j++;
        }
        JFreeChart jfreechart = ChartFactory.createBarChart("Degree " + name, "", "", defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.getTitle().setFont(new Font("Sans-Serif", Font.PLAIN, 18));
        jfreechart.setBackgroundPaint(new Color(221, 223, 238));
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.white);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        BarRenderer renderer = (BarRenderer) categoryplot.getRenderer();
        //categoryplot.setBackgroundPaint(new Color(221, 223, 238));

        renderer.setSeriesPaint(0, new Color(130, 165, 70));
        renderer.setSeriesPaint(1, new Color(220, 165, 70));
        renderer.setSeriesPaint(4, new Color(255, 165, 70));
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        // renderer.setMaximumBarWidth(1);
        renderer.setGradientPaintTransformer(null);
        renderer.setDefaultBarPainter(new StandardBarPainter());

        categoryplot.setRenderer(renderer);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setUpperMargin(0.25D);
        CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
        categoryitemrenderer.setBaseItemLabelsVisible(true);
        //categoryitemrenderer.setBaseItemLabelGenerator(new LabelGenerator(null));
        numberaxis.setRange(0, 50);
        //numberaxis.setNumberFormatOverride(NumberFormat.getPercentInstance());

        Font font = new Font("SansSerif", Font.ROMAN_BASELINE, 14);
        numberaxis.setTickLabelFont(font);
        CategoryAxis axisd = categoryplot.getDomainAxis();
        ValueAxis axisr = categoryplot.getRangeAxis();
        axisd.setTickLabelFont(font);
        axisr.setTickLabelFont(font);

        final ChartPanel chartPanel = new ChartPanel(jfreechart);
        chartPanel.setPreferredSize(new java.awt.Dimension(650, 370));

        FileOutputStream output;
        try {
            output = new FileOutputStream(name + "degree.jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, jfreechart, 550, 270, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void drawHistogramRank(HashMap<Object, Double> map, String name) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();

        Collection<Double> c = map.values();
        List<Double> list = new ArrayList<Double>(c);

        Collections.sort(list);
        int j = 0;
        for (Double i : list) {
            defaultcategorydataset.addValue(i, "", String.valueOf(j));
            j++;
        }

        /*for (Object o : map.keySet()) {
            defaultcategorydataset.addValue(map.get(o), "", ((GraphElements.MyVertex) o).toString());
        }*/
        JFreeChart jfreechart = ChartFactory.createBarChart("BetweenessCentrality " + name, "", "", defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.getTitle().setFont(new Font("Sans-Serif", Font.PLAIN, 18));
        jfreechart.setBackgroundPaint(new Color(221, 223, 238));
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.white);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        BarRenderer renderer = (BarRenderer) categoryplot.getRenderer();
        //categoryplot.setBackgroundPaint(new Color(221, 223, 238));

        renderer.setSeriesPaint(0, new Color(130, 165, 70));
        renderer.setSeriesPaint(1, new Color(220, 165, 70));
        renderer.setSeriesPaint(4, new Color(255, 165, 70));
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        // renderer.setMaximumBarWidth(1);
        renderer.setGradientPaintTransformer(null);
        renderer.setDefaultBarPainter(new StandardBarPainter());

        categoryplot.setRenderer(renderer);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setUpperMargin(0.25D);
        CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
        categoryitemrenderer.setBaseItemLabelsVisible(true);
        //categoryitemrenderer.setBaseItemLabelGenerator(new LabelGenerator(null));
        //numberaxis.setRange(0, 1);
        //numberaxis.setNumberFormatOverride(NumberFormat.getPercentInstance());

        Font font = new Font("SansSerif", Font.ROMAN_BASELINE, 12);
        numberaxis.setTickLabelFont(font);
        CategoryAxis axisd = categoryplot.getDomainAxis();
        ValueAxis axisr = categoryplot.getRangeAxis();
        axisd.setTickLabelFont(font);
        axisr.setTickLabelFont(font);

        final ChartPanel chartPanel = new ChartPanel(jfreechart);
        chartPanel.setPreferredSize(new java.awt.Dimension(650, 370));

        FileOutputStream output;
        try {
            output = new FileOutputStream(name + "rank.jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, jfreechart, 650, 370, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public void GenerateAvgDistanceGraphInformation() {
        //super(title);
        final BoxAndWhiskerCategoryDataset dataset = getAvgPathBoxPlotStats();
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
                "Average Distance",
                new Font("SansSerif", Font.BOLD, 18),
                plot,
                true
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1600, 800));
        setContentPane(chartPanel);

        TextTitle legendText = null;
        legendText = new TextTitle("");
        legendText.setFont(font);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        chart.getLegend().setItemFont(font);

        FileOutputStream output;
        try {
            output = new FileOutputStream("Average Distance" + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 1200, 800, null);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public void GenerateAvgDegreeGraphInformation() {
        //super(title);
        final BoxAndWhiskerCategoryDataset dataset = getAvgDegreeBoxPlotStats();
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
                "Average Degree",
                new Font("SansSerif", Font.BOLD, 18),
                plot,
                true
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1600, 800));
        setContentPane(chartPanel);

        TextTitle legendText = null;
        legendText = new TextTitle("");
        legendText.setFont(font);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        chart.getLegend().setItemFont(font);

        FileOutputStream output;
        try {
            output = new FileOutputStream("Average Degree" + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 1200, 800, null);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public void GenerateAvgClusterGraphInformation() {
        //super(title);
        final BoxAndWhiskerCategoryDataset dataset = getAvgClusteringBoxPlotStats();
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
                "Average Cluster Coefficient",
                new Font("SansSerif", Font.BOLD, 18),
                plot,
                true
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1600, 800));
        setContentPane(chartPanel);

        TextTitle legendText = null;
        legendText = new TextTitle("");
        legendText.setFont(font);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        chart.getLegend().setItemFont(font);

        FileOutputStream output;
        try {
            output = new FileOutputStream("Cluster Coefficient" + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 1200, 800, null);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(GenerateGraphInformation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BoxAndWhiskerCategoryDataset getAvgPathBoxPlotStats() {
        final DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();
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
                final List list = new ArrayList();

                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                System.out.println("mode" + filenamep[0]);
                String mode = filenamep[0];

                Graph<GraphElements.MyVertex, String> g = null;
                System.out.println("file" + file.getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(fileInputStream);
                    ObjectInput input = new ObjectInputStream(buffer);
                    g = (Graph) input.readObject();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                UnweightedShortestPath u = new UnweightedShortestPath(g);
                double n = g.getVertexCount();
                double sum;
                for (GraphElements.MyVertex v : g.getVertices()) {
                    sum = 0;
                    for (GraphElements.MyVertex w : g.getVertices()) {
                        if (!w.equals(v)) {
                            if (u.getDistance(v, w) != null) {
                                double distance = u.getDistance(v, w).doubleValue();
                                sum += distance;
                            } else {
                                System.out.println("Graph is not connected now!");
                            }
                        }
                    }
                    list.add(sum / n - 1);
                }
                String fileImage = file.getName().replace("graph", "");
                dataset.add(list, 10, fileImage);
                //dataset.add(list, popsize, getTechniqueName(mode) + graphtype + fn2);
            }
        }
        return dataset;
    }

    private BoxAndWhiskerCategoryDataset getAvgClusteringBoxPlotStats() {
        final DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();
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
                final List list = new ArrayList();

                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                System.out.println("mode" + filenamep[0]);
                String mode = filenamep[0];

                Graph<GraphElements.MyVertex, String> g = null;
                System.out.println("file" + file.getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(fileInputStream);
                    ObjectInput input = new ObjectInputStream(buffer);
                    g = (Graph) input.readObject();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                Map<GraphElements.MyVertex, Double> m = clusteringCoefficients(g);
                Collection<Double> val = m.values();
                for (Double v : val) {
                    list.add(v);
                }
                String fileImage = file.getName().replace("graph", "");
                dataset.add(list, 10, fileImage);
            }
        }
        return dataset;
    }

    private BoxAndWhiskerCategoryDataset getAvgDegreeBoxPlotStats() {
        final DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();
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
                final List list = new ArrayList();

                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
                System.out.println("mode" + filenamep[0]);
                String mode = filenamep[0];

                Graph<GraphElements.MyVertex, String> g = null;
                System.out.println("file" + file.getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(fileInputStream);
                    ObjectInput input = new ObjectInputStream(buffer);
                    g = (Graph) input.readObject();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                ArrayList<Double> dataDegree = new ArrayList<>();
                Collection vertices = g.getVertices();
                //double sum = 0;

                HashMap<Integer, Integer> hd = new HashMap<>();

                for (Object v : vertices) {
                    if (!hd.containsKey(g.degree((GraphElements.MyVertex) v))) {
                        hd.put(g.degree((GraphElements.MyVertex) v), 1);
                    } else {
                        hd.put(g.degree((GraphElements.MyVertex) v), hd.get(g.degree((GraphElements.MyVertex) v)) + 1);
                    }
                }
                System.out.println("key seeeeeeeeeeeeeeeeeeeeeeeeeeeeeet" + hd);
                for (int key : hd.keySet()) {
                    System.out.println(list.add(hd.get(key)));
                    list.add(hd.get(key));
                }
                //sum += g.degree((GraphElements.MyVertex) v);
                //list.add((double) g.degree((GraphElements.MyVertex) v));
                String fileImage = file.getName().replace("graph", "");
                dataset.add(list, 10, fileImage);
                //dataset.add(list, popsize, getTechniqueName(mode) + graphtype + fn2);
            }
        }
        return dataset;
    }

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
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GraphSerialization.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("graph" + g.toString());
                String graphStats = file.getName().replace("graph", "graphstats");
                String rankingsFile = file.getName().replace("graph", "ranking");

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
                    escribir.println("Diameter: " + GraphStats.getDiameter(g));
                    //escribir.println("Maximum average Distance:");

                    PrintWriter escribir2 = new PrintWriter(new BufferedWriter(new FileWriter(rankingsFile, true)));
                    PrintWriter escribir3;

                    BetweennessCentrality ranker = new BetweennessCentrality(g);
                    ranker.step();
                    ranker.setRemoveRankScoresOnFinalize(false);
                    ranker.evaluate();
                    //System.out.println("Rank" + ranker.toString());
                    //ranker.printRankings(true, true);
                    HashMap<Object, Double> map = new HashMap();
                    //escribir.println("********************Ranker******************************");

                    for (Object v : g.getVertices()) {
                        Double rank = ranker.getVertexRankScore(v);
                        Double normalized = (Double) rank / ((g.getEdgeCount() - 1) * (g.getEdgeCount() - 2) / 2);
                        map.put(v, normalized);
                        escribir2.println(normalized);
                        //escribir.println(v + "- rank: " + rank + ", norm: " + normalized);
                        // System.out.println("Score for " + v + " = " + ranker.getVertexRankScore(v)); 
                    }
                    Collection<Double> c = map.values();
                    List<Double> list = new ArrayList<Double>(c);
                    Collections.sort(list);
                    StatisticsNormalDist st = new StatisticsNormalDist((ArrayList<Double>) list, list.size());
                    escribir.println("skew Betweeness centrality: " + st.getSkewness());
                    drawHistogramRank(map, file.getName());

                    HashMap<Object, Integer> mapdegree = new HashMap();
                    Collection vertices = g.getVertices();

                    for (Object v : vertices) {
                        if (!mapdegree.containsKey(g.degree((GraphElements.MyVertex) v))) {
                            mapdegree.put(g.degree((GraphElements.MyVertex) v), 1);
                        } else {
                            mapdegree.put(g.degree((GraphElements.MyVertex) v), mapdegree.get(g.degree((GraphElements.MyVertex) v)) + 1);
                        }
                    }
/*
                    for (Object v : vertices) {
                        mapdegree.put(v, g.degree((GraphElements.MyVertex) v));
                    }*/
                    drawHistogramDegree(mapdegree, file.getName());
                    escribir.close();
                    escribir2.close();

                } catch (IOException ex) {
                    Logger.getLogger(StatisticsProvider.class
                            .getName()).log(Level.SEVERE, null, ex);
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
                        layout = new ISOMLayout<>(g);
                        break;
                    case "kleinberg":
                        layout = new CircleLayout<>(g);
                        break;
                    case "circle":
                        layout = new ISOMLayout<>(g);
                        break;
                    case "line":
                        layout = new ISOMLayout<>(g);
                        break;
                    case "lattice":
                        layout = new ISOMLayout<>(g);
                        break;
                    default:
                        layout = new ISOMLayout<>(g);
                        break;
                }

                if (fileImage.contains("smallworld")) {
                    layout = new CircleLayout<>(g);
                }
                BasicVisualizationServer<GraphElements.MyVertex, String> vv = new BasicVisualizationServer<>(layout);
                vv.setPreferredSize(new Dimension(600, 600)); //Sets the viewing area size
                vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
                vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

                VisualizationImageServer<GraphElements.MyVertex, String> vis
                        = new VisualizationImageServer<>(vv.getGraphLayout(),
                                vv.getGraphLayout().getSize());

                vis.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphElements.MyVertex>());
                //vis.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

                BufferedImage image = (BufferedImage) vis.getImage(
                        new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                                vv.getGraphLayout().getSize().getHeight() / 2),
                        new Dimension(vv.getGraphLayout().getSize()));
                // Write image to a png file
                File outputfile = new File(fileImage + "png");

                try {
                    ImageIO.write(image, "png", outputfile);
                } catch (IOException e) {
                    // Exception handling
                }
            }
        }

    }

    public void GenerateBetweenessCentrality() {
        /*            BetweennessCentrality ranker = new BetweennessCentrality(someGraph);
        ranker.evaluate();
        ranker.printRankings();
         */

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

        GenerateGraphInformation.getGraphStats();
        GenerateGraphInformation g = new GenerateGraphInformation(experimentsDir);

        g.GenerateAvgDegreeGraphInformation();
        g.GenerateAvgDistanceGraphInformation();
        g.GenerateAvgClusterGraphInformation();
        

    }

    public GenerateGraphInformation(String title) {
        super(title);
    }
}
