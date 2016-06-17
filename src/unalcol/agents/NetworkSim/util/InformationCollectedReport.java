/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import static sun.management.ConnectorAddressLink.export;
import static unalcol.agents.NetworkSim.SyncronizationMain.pf;

/**
 * Report of Information Collected
 *
 * @author Arles Rodriguez
 */
public class InformationCollectedReport extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String minRoundForall = "off";

    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(InformationCollectedReport.class);

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
    public InformationCollectedReport(final String title) {
        super(title);

        String extension;
        Scanner sc = null;
        File f = new File(experimentsDir);
        File[] files = f.listFiles();

        for (File file : files) {
            if (file.isDirectory() && file.getName().endsWith("info")) {
                System.out.println("new seriiiiieeeeeeeeeee" + file);
                XYSeriesCollection juegoDatos = new XYSeriesCollection();
                XYSeries minimum = new XYSeries("Minimum");
                XYSeries maximum = new XYSeries("Maximum");
                XYSeries median = new XYSeries("Median");
                juegoDatos.addSeries(minimum);
                juegoDatos.addSeries(maximum);
                juegoDatos.addSeries(median);
                int min_round = Integer.MAX_VALUE;
                int last_round = 0;
                HashMap<Integer, ArrayList<Double>> InfoByRound = new HashMap<>();
                File subdir = new File(file.getName());
                File[] filesInfo = subdir.listFiles();
                for (File fileInfo : filesInfo) {
                    extension = "";
                    int i = fileInfo.getName().lastIndexOf('.');
                    int p = Math.max(fileInfo.getName().lastIndexOf('/'), fileInfo.getName().lastIndexOf('\\'));
                    if (i > p) {
                        extension = fileInfo.getName().substring(i + 1);
                    }

                    // System.out.println(file.getName() + "extension" + extension);
                    if (fileInfo.isFile() && extension.equals("csv") && fileInfo.getName().startsWith("exp") && fileInfo.getName().contains("infostats")) {
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
                            //System.out.println("line:" + line);
                            data = line.split(",");
                            int round = Integer.valueOf(data[0]);
                            Double info = Double.valueOf(data[1]);
                            if (!InfoByRound.containsKey(round)) {
                                InfoByRound.put(round, new ArrayList());
                                InfoByRound.get(round).add(info);
                            } else {
                                InfoByRound.get(round).add(info);
                            }
                            last_round = round;
                        }
                        sc.close();

                        if (last_round < min_round) {
                            min_round = last_round;
                        }
                        //LOGGER.debug("Adding series " + i);
                        //LOGGER.debug(list.toString());
                    }
                }
                Collection<Integer> unsorted = InfoByRound.keySet();
                List<Integer> sorted = asSortedList(unsorted);
                System.out.println("min round" + min_round);

                for (int k : sorted) {
                    StatisticsNormalDist st = null;
                    if (minRoundForall.equals("on")) {
                        ArrayList tmp = new ArrayList();
                        for (int i = 0; i < min_round; i++){
                            tmp.add(InfoByRound.get(k).get(i));
                        }
                        st = new StatisticsNormalDist(tmp, tmp.size());
                    } else {
                        st = new StatisticsNormalDist(InfoByRound.get(k), InfoByRound.get(k).size());
                    }
                    minimum.add(k, st.getMin());
                    maximum.add(k, st.getMax());
                    median.add(k, st.getMedian());
                }

                JFreeChart chart = ChartFactory.createXYLineChart(
                        "Information Collected" + file.getName(), "Round number", "GlobalInfo",
                        juegoDatos, PlotOrientation.VERTICAL,
                        true, true, false);

                BufferedImage image = chart.createBufferedImage(600, 600);
                FileOutputStream output;
                try {
                    output = new FileOutputStream(file.getName() + "globalInfo" + ".jpg");
                    ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 600, 600, null);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InformationCollectedReport.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(InformationCollectedReport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(final String[] args) {

        if (args.length > 0) {
            minRoundForall = args[0];
        }
        final InformationCollectedReport demo = new InformationCollectedReport("Information Collected");
    }
}
