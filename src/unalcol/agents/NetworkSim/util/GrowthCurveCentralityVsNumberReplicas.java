/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import org.jfree.util.ShapeUtilities;
import unalcol.agents.NetworkSim.GraphElements;

/**
 * Report of Information Collected
 *
 * @author Arles Rodriguez
 */
public class GrowthCurveCentralityVsNumberReplicas extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String minRoundForall = "off";
    private static double maxXvalue = 0.0;
    private static Integer sizeX = 1200;
    private static Integer sizeY = 800;
    private static String graphName = "";
    public HashMap<String, Double> graphMetric = new HashMap<>();

    private HashMap<String, Double> loadGraphMetric() {
        Graph<GraphElements.MyVertex, String> g = GraphSerialization.loadDeserializeGraph(graphName);
        BetweennessCentrality ranker = new BetweennessCentrality(g);
        ranker.step();
        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();
        HashMap<String, Double> map = new HashMap();
        int i=1;
        for (GraphElements.MyVertex v : g.getVertices()) {
            Double rank = ranker.getVertexRankScore(v);
            Double normalized = rank / ((g.getEdgeCount() - 1) * (g.getEdgeCount() - 2) / 2);
            //map.put(v.toString(), (double)i++);//(double)g.getNeighborCount(v));
            map.put(v.toString(), (double)g.getNeighborCount(v));
        }
        return map;
    }

    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(GrowthCurveCentralityVsNumberReplicas.class);

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        java.util.Collections.sort(list);
        return list;
    }

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public GrowthCurveCentralityVsNumberReplicas(final String title) {
        super(title);
        graphMetric = loadGraphMetric();
        String extension;
        Scanner sc = null;
        File f = new File(experimentsDir);
        File[] files = f.listFiles();

        for (File file : files) {
            if (file.isDirectory() && file.getName().endsWith("newagbynode")) {
                //System.out.println("new seriiiiieeeeeeeeeee" + file);
                XYSeriesCollection juegoDatos = new XYSeriesCollection();
                XYSeries minimum = new XYSeries("Minimum");
                XYSeries maximum = new XYSeries("Maximum");
                XYSeries median = new XYSeries("Median");
                juegoDatos.addSeries(minimum);
                juegoDatos.addSeries(maximum);
                juegoDatos.addSeries(median);
                int min_round = Integer.MAX_VALUE;
                int last_round = 0;
                int simulation = 1;
                HashMap<String, ArrayList<Double>> InfoBySimulation = new HashMap<>();
                File subdir = new File(file.getName());
                File[] filesInfo = subdir.listFiles();

                Arrays.sort(filesInfo, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        String f1name = f1.getName();
                        String f2name = f2.getName();

                        String[] splittt = f1name.split(Pattern.quote("+"));
                        String[] splittt2 = f2name.split(Pattern.quote("+"));
                        Date date1 = null, date2 = null;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String dateInString = f1name.split(Pattern.quote("+"))[splittt.length - 2];
                        String dateInString2 = f2name.split(Pattern.quote("+"))[splittt2.length - 2];
                        try {
                            date1 = sdf.parse(dateInString);
                            date2 = sdf.parse(dateInString2);
                        } catch (ParseException ex) {
                            Logger.getLogger(GrowthCurveCentralityVsNumberReplicas.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("date1:" + date1 + ", date2:" + date2);
                        return date1.compareTo(date2);
                        //return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });

                for (File fileInfo : filesInfo) {
                    extension = "";
                    int i = fileInfo.getName().lastIndexOf('.');
                    int p = Math.max(fileInfo.getName().lastIndexOf('/'), fileInfo.getName().lastIndexOf('\\'));
                    if (i > p) {
                        extension = fileInfo.getName().substring(i + 1);
                    }

                    // System.out.println(file.getName() + "extension" + extension);
                    if (fileInfo.isFile() && extension.equals("csv") && fileInfo.getName().startsWith("exp") && fileInfo.getName().contains("newagbynode")) {
                        System.out.println("entraaaaaaaaaaaaaaa");
                        ArrayList Data;
                        System.out.println(fileInfo.getName());
                        System.out.println("get: " + fileInfo.getName());
                        String[] filenamep = fileInfo.getName().split(Pattern.quote("+"));
                        System.out.println("file" + filenamep[8]);
                        int popsize = Integer.valueOf(filenamep[2]);
                        double pf = Double.valueOf(filenamep[4]);
                        String mode = filenamep[6];
                        int maxIter = -1;
                        //if (!filenamep[8].isEmpty()) {
                        maxIter = Integer.valueOf(filenamep[8]);
                        //}
                        System.out.println("psize:" + popsize);
                        System.out.println("pf:" + pf);
                        System.out.println("mode:" + mode);
                        System.out.println("maxIter:" + maxIter);

                        //Read each fileInfo file
                        try {
                            sc = new Scanner(fileInfo);

                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(BoxPlotRoundNumberSort.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }

                        String[] data = null;

                        while (sc.hasNext()) {
                            String line = sc.nextLine();
                            System.out.println("line:" + line);
                            data = line.split(",");
                            String nodeId = data[0];
                            int replicasNumber = data[1].split("-").length;

                            if (!InfoBySimulation.containsKey(nodeId)) {
                                InfoBySimulation.put(nodeId, new ArrayList<Double>());
                            }
                            InfoBySimulation.get(nodeId).add((double) replicasNumber);
                        }
                        sc.close();

                        if (last_round < min_round) {
                            min_round = last_round;
                        }
                        //LOGGER.debug("Adding series " + i);
                        //LOGGER.debug(list.toString());
                        simulation++;
                    }
                }
                //Collection<Integer> unsorted = InfoBySimulation.keySet();
                //List<Integer> sorted = asSortedList(unsorted);

                //for (int k : sorted) {                
                for (String k : InfoBySimulation.keySet()) {
                    StatisticsNormalDist st = new StatisticsNormalDist(InfoBySimulation.get(k), InfoBySimulation.get(k).size());
                    //minimum.add((double)graphMetric.get(k), (double)(st.getMedian() - st.getStdDevMedian()));
                    //maximum.add((double)graphMetric.get(k), (double)(st.getMedian() + st.getStdDevMedian()));                    
                    //maximum.add((double)graphMetric.get(k), st.getMax());
                    median.add((double)graphMetric.get(k), st.getMedian());
                }

                JFreeChart chart = ChartFactory.createXYLineChart(
                        "Agents Number vs Time" + file.getName(), "#Simulation (Sorted by Date)", "#Agents",
                        juegoDatos, PlotOrientation.VERTICAL,
                        true, true, false);

                //  chart.setBackgroundPaint(Color.white);
                final XYPlot plot = chart.getXYPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setDomainCrosshairPaint(Color.lightGray);
                plot.setDomainGridlinesVisible(true);
                //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
                plot.setDomainGridlinePaint(Color.lightGray);
                plot.setRangeGridlinePaint(Color.lightGray);

                final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
                //renderer.setSeriesShapesVisible(1, true);
                renderer.setBaseShapesVisible(true);
                Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
                renderer.setSeriesShape(0, cross);
                renderer.setSeriesPaint(0, Color.MAGENTA);

                plot.setRenderer(renderer);

                NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
                Font font3 = new Font("Dialog", Font.PLAIN, 12);
                domainAxis.setLabelFont(font3);

                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setLabelFont(font3);

                //chart.getLegend().setItemFont(new Font("Palatino", Font.PLAIN, 14));
                if (maxXvalue != 0) {
                    domainAxis.setRange(0, maxXvalue);
                }
                BufferedImage image = chart.createBufferedImage(sizeX, sizeY);

                FileOutputStream output;
                try {
                    output = new FileOutputStream(file.getName() + "centrality vs replicas" + ".jpg");
                    ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, sizeX, sizeY, null);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GrowthCurveCentralityVsNumberReplicas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GrowthCurveCentralityVsNumberReplicas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(final String[] args) {
        if (args.length > 0) {
            graphName = args[0];
        }
        if (args.length > 1) {
            maxXvalue = Double.valueOf(args[1]);
        }
        if (args.length > 2) {
            sizeX = Integer.valueOf(args[2]);
        }
        if (args.length > 3) {
            sizeY = Integer.valueOf(args[3]);
        }

        final GrowthCurveCentralityVsNumberReplicas demo = new GrowthCurveCentralityVsNumberReplicas("Agents Number vs Simulations sorted by date");
    }
}
