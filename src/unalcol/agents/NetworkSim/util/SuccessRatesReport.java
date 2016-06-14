/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class SuccessRatesReport extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String mazeMode = "mazeon";
    private static String[] aMode;
    private static final long serialVersionUID = 1L;

    static class LabelGenerator extends AbstractCategoryItemLabelGenerator implements CategoryItemLabelGenerator {

        private static final long serialVersionUID = 1L;

        public String generateLabel(CategoryDataset categorydataset, int i, int j) {
            String s = null;
            double d = 0.0D;
            if (category != null) {
                Number number = categorydataset.getValue(i, category.intValue());
                d = number.doubleValue();
            } else {
                d = calculateSeriesTotal(categorydataset, i);
            }
            Number number1 = categorydataset.getValue(i, j);
            if (number1 != null) {
                double d1 = number1.doubleValue();
                //s = /*number1.toString() +*/ " (" + formatter.format(d1 / d) + ")";
            }
            return s;
        }

        private double calculateSeriesTotal(CategoryDataset categorydataset, int i) {
            double d = 0.0D;
            for (int j = 0; j < categorydataset.getColumnCount(); j++) {
                Number number = categorydataset.getValue(i, j);
                if (number != null) {
                    d += number.doubleValue();
                }
            }

            return d;
        }

        private Integer category;
        private NumberFormat formatter;

        public LabelGenerator(int i) {
            this(new Integer(i));
        }

        public LabelGenerator(Integer integer) {
            super("", NumberFormat.getInstance());
            formatter = NumberFormat.getPercentInstance();
            category = integer;
        }
    }

    private static ArrayList<Double> getFailureProbs() {
        ArrayList<Double> pfs = new ArrayList<>();
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
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("experiment") && file.getName().contains(mazeMode)) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));
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

                System.out.println("pf:" + pf);
                if (!pfs.contains(pf)) {
                    pfs.add(pf);
                }
            }
        }
        System.out.println("pfs" + pfs);
        return pfs;
    }

    public SuccessRatesReport(String s) {
        super(s);
        ArrayList<Double> failureProbs = getFailureProbs();

        for (Double pf : failureProbs) {
            ArrayList<Double> pfi = new ArrayList<>();
            pfi.add(pf);
            CategoryDataset categorydataset = createDataset(pfi);
            JFreeChart jfreechart = createChart(categorydataset, pfi);
        }
        /*
         ArrayList<Double> pf0 = new ArrayList<>();
         ArrayList<Double> pfg1 = new ArrayList<>();
         ArrayList<Double> pfg2 = new ArrayList<>();
         ArrayList<Double> pfg3 = new ArrayList<>();
         ArrayList<Double> pfg4 = new ArrayList<>();
         ArrayList<Double> pfg5 = new ArrayList<>();
         ArrayList<Double> pfg6 = new ArrayList<>();

         pf0.add(0.0);
         pfg1.add(1.0E-4);
         pfg2.add(3.0E-4);
         pfg3.add(5.0E-4);
         pfg4.add(7.0E-4);
         pfg5.add(9.0E-4);
         //pfg3.add(1.0E-3);

         CategoryDataset categorydataset = createDataset(pf0);
         JFreeChart jfreechart = createChart(categorydataset, pf0);

         categorydataset = createDataset(pfg1);
         jfreechart = createChart(categorydataset, pfg1);
         categorydataset = createDataset(pfg2);
         jfreechart = createChart(categorydataset, pfg2);
         categorydataset = createDataset(pfg3);
         jfreechart = createChart(categorydataset, pfg3);
         categorydataset = createDataset(pfg4);
         jfreechart = createChart(categorydataset, pfg4);
         categorydataset = createDataset(pfg5);
         jfreechart = createChart(categorydataset, pfg5);

         ChartPanel chartpanel = new ChartPanel(jfreechart);
         chartpanel.setPreferredSize(new Dimension(500, 270));
         setContentPane(chartpanel);*/
    }

    private static CategoryDataset createDataset(ArrayList<Double> Pf) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Hashtable<String, String> Pop = new Hashtable<>();
        PrintWriter escribir;
        Scanner sc = null;
        double sucessfulExp = 0.0;
        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("experiment") && file.getName().contains(mazeMode)) {
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                String[] filenamep = file.getName().split(Pattern.quote("+"));

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

                //String[] aMode = {"random", "levywalk", "sandc", "sandclw"};
                //String[] aMode = {"lwphclwevap", "lwsandc2", "lwsandc", "lwphevap2", "lwphevap"};
                // String[] aMode = {"levywalk", "lwphevap", "hybrid"};
                //String[] aMode = {"levywalk", "lwphevap", "hybrid", "hybrid3", "hybrid4", "sequential"};
                if (isInMode(aMode, mode)) {
                    final List list = new ArrayList();
                    try {
                        sc = new Scanner(file);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SuccessRatesReport.class
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
                        worldSize = Integer.valueOf(data[3]);
                        averageExplored = Double.valueOf(data[4]);
                        // data[3] stdavgExplored - not used
                        bestRoundNumber = Integer.valueOf(data[6]);
                        avgSend = Double.valueOf(data[7]);
                        avgRecv = Double.valueOf(data[8]);
                        avgdataExplInd = Double.valueOf(data[11]);

                        //Add Data and generate statistics 
                        acSt.add((double) agentsCorrect);
                        avgExp.add(averageExplored);

                        avSnd.add(avgSend);
                        avRecv.add(avgRecv);
                        avIndExpl.add(avgdataExplInd);

                        sucessfulExp = 0.0;
                        for (int j = 0; j < acSt.size(); j++) {
                            if (acSt.get(j) > 0) {
                                sucessfulExp++;
                            }
                        }

                    }
                    if (Pf.contains(pf)) {
                        defaultcategorydataset.addValue(((double) sucessfulExp) / acSt.size() * 100.0, "" + popsize, getTechniqueName(mode) + "\nPf:" + pf);
                        /*pf == 1.0E-4 || pf == 3.0E-4*/
                    }
                }
            }

        }
        return defaultcategorydataset;
    }

    private static JFreeChart createChart(CategoryDataset categorydataset, ArrayList<Double> pf) {
        JFreeChart jfreechart = ChartFactory.createBarChart("Success Rates - " + getTitle(pf), "", "", categorydataset, PlotOrientation.VERTICAL, true, true, false);
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
        categoryitemrenderer.setBaseItemLabelGenerator(new LabelGenerator(null));
        numberaxis.setRange(0, 100);
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
            output = new FileOutputStream("sucessrates1" + pf + mazeMode + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, jfreechart, 650, 370, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SuccessRatesReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SuccessRatesReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jfreechart;
    }

    /*public static JPanel createDemoPanel() {
     JFreeChart jfreechart = createChart(createDataset());
     return new ChartPanel(jfreechart);
     }*/
    private static String getTitle(ArrayList<Double> pf) {
        String s = "pf=";

        for (int i = 0; i < pf.size(); i++) {
            s += pf.get(i);
            if (i != pf.size() - 1) {
                s += " and ";
            }
        }
        return s;
    }

    private static boolean isInMode(String[] aMode, String mode) {
        for (String temp : aMode) {
            if (temp.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) {
        if (args.length > 0) {
            experimentsDir = args[0];
        }

        if (args.length > 1) {
            mazeMode = args[1];
        }

        aMode = new String[args.length - 2];

        for (int i = 2; i < args.length; i++) {
            aMode[i - 2] = args[i];
        }

        SuccessRatesReport itemlabeldemo2 = new SuccessRatesReport("Sucess Rates");
        //itemlabeldemo2.pack();
        //RefineryUtilities.centerFrameOnScreen(itemlabeldemo2);
        //itemlabeldemo2.setVisible(true);
    }

    private static String getTechniqueName(String mode) {
        switch (mode) {
            case "sandclw":
                return "SandC with Lw";
            case "lwsandc2":
                return "Lw and C";
            case "lwsandc":
                return "Lw and C-Lw";
            case "lwphevap2":
                return "C and Evap";
            case "lwphevap":
                return "C-Lw and Evap";
            default:
                return mode;
        }
    }

    private static class CustomRenderer extends BarRenderer {

        /**
         * The colors.
         */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         *
         * @param colors the colors.
         */
        public CustomRenderer(final Paint[] colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item. Overrides the default behaviour
         * inherited from AbstractSeriesRenderer.
         *
         * @param row the series.
         * @param column the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(final int row, final int column) {
            return this.colors[column % this.colors.length];
        }
    }

}