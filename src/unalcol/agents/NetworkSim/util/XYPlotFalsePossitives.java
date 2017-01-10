/*
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
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import org.jfree.util.ShapeUtilities;

/**
 * Demonstration of a box-and-whisker chart using a {@link CategoryPlot}.
 *
 * @author David Browning
 */
public class XYPlotFalsePossitives extends ApplicationFrame {

    private static String experimentsDir = ".";
    private static String[] aMode;
    private static String sortCriteria; //alg|topology
    private static Integer sizeX = 1200;
    private static Integer sizeY = 800;


    /**
     * Access to logging facilities.
     */
    private static final LogContext LOGGER = Log.createContext(XYPlotFalsePossitives.class);

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     */
    public XYPlotFalsePossitives(final String title) {
        super(title);
        File f = new File(experimentsDir);
        File[] files = f.listFiles();

        //System.out.println("new seriiiiieeeeeeeeeee" + file);
        XYSeriesCollection MemoryConsumption = createSampleDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "False Possitives vs Simulation", "Simulation", "#False Possitives",
                MemoryConsumption, PlotOrientation.VERTICAL,
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

        BufferedImage image = chart.createBufferedImage(sizeX, sizeY);

        FileOutputStream output;
        try {
            output = new FileOutputStream("False Possitives vs Simulation" + ".jpg");
            ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, sizeX, sizeY, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AgentsVsTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AgentsVsTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYSeriesCollection createSampleDataset() {

        String sDirectorio = experimentsDir;
        File f = new File(sDirectorio);
        String extension;
        File[] files = f.listFiles();
        Scanner sc = null;
        ArrayList<String> filenamesSorted = new ArrayList<>();
        Hashtable<String, File> fileHashtable = new Hashtable<>();

        final XYSeriesCollection dataset
                = new XYSeriesCollection();

        for (File file : files) {
            extension = "";
            int i = file.getName().lastIndexOf('.');
            int p = Math.max(file.getName().lastIndexOf('/'), file.getName().lastIndexOf('\\'));
            if (i > p) {
                extension = file.getName().substring(i + 1);
            }

            // System.out.println(file.getName() + "extension" + extension);
            if (file.isFile() && extension.equals("csv") && file.getName().startsWith("exp") && !file.getName().contains("gstats")) {
                filenamesSorted.add(file.getName());
                System.out.println(file.getName());
                System.out.println("get: " + file.getName());
                fileHashtable.put(file.getName(), file);
            }
        }

        System.out.println("before:" + filenamesSorted);
        System.out.println("after:" + filenamesSorted);
        for (String filename : filenamesSorted) {

            File file = fileHashtable.get(filename);
            String[] filenamep = file.getName().split(Pattern.quote("+"));
            XYSeries falsePossitives = new XYSeries(filename);
            dataset.addSeries(falsePossitives);
            int i = file.getName().lastIndexOf('.');

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

            final List list = new ArrayList();
            try {
                sc = new Scanner(file);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(XYPlotFalsePossitives.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            String[] data = null;
            int fp;
            int count = 1;
            while (sc.hasNext()) {
                String line = sc.nextLine();
                data = line.split(",");
                fp = Integer.valueOf(data[11]);
                falsePossitives.add(count, fp);
                count++;
            }
            LOGGER.debug("Adding series " + i);
            LOGGER.debug(list.toString());
        }

        return dataset;
    }

    public static void main(final String[] args) {
        if (args.length > 0) {
            experimentsDir = args[0];
        }

        if (args.length > 1) {
            sortCriteria = args[1];
        }

        if (args.length > 2) {
            sizeX = Integer.valueOf(args[2]);
        }

        if (args.length > 2) {
            sizeY = Integer.valueOf(args[2]);
        }

        final XYPlotFalsePossitives demo = new XYPlotFalsePossitives("False Possitives vs Simulation");
    }
    

    private boolean isInMode(String[] aMode, String mode) {
        for (String temp : aMode) {
            if (temp.equals(mode)) {
                return true;
            }
        }
        return false;
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

    private String getTitle(ArrayList<Double> pf) {
        String s = " with a pf=";

        for (int i = 0; i < pf.size(); i++) {
            s += pf.get(i);
            if (i != pf.size() - 1) {
                s += " and pf=";
            }
        }
        return s;
    }

}
