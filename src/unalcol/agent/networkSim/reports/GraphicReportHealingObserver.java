package unalcol.agent.networkSim.reports;
//
//import java.awt.Frame;
//import java.awt.event.WindowEvent;
//import java.awt.image.BufferedImage;
//import java.io.BufferedWriter;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Calendar; 
//import java.util.GregorianCalendar;
//import java.util.Hashtable;
//import java.util.Iterator;
//import java.util.Observable;
//import java.util.Observer;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//
///**
// *
// * @author Arles
// */
//public class GraphicReportHealingObserver extends java.awt.Frame implements Observer {
//
//    int iteration = 0;
//    float fProb = 0;
//    WorldCanvas wc = null;
//    XYSeries seekers;
//    XYSeries carriers;
//    XYSeries levyWalkers;
//    XYSeries globalInfo = new XYSeries("Global Information");
//    XYSeriesCollection juegoDatos2 = new XYSeriesCollection();
//    XYSeriesCollection juegoDatos = new XYSeriesCollection();
//    BufferedImage grafica = null;
//    float pf = 0;
//    boolean isUpdating;
//    Frame frame2;
//    String filename;
//    String failfilename;
//    static boolean firstTime = true;
//
//    public GraphicReportHealingObserver(float probFailure) {
//        initComponents();
//        frame2 = new Frame();
//        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
//
//        if (AppMain.mode.equals("hybrid")) {
//            seekers = new XYSeries("Levy Walkers");
//            carriers = new XYSeries("Carriers");
//        } else if (AppMain.mode.equals("hybrid3")) {
//            seekers = new XYSeries("Seekers");
//            carriers = new XYSeries("Carriers");
//            levyWalkers = new XYSeries("Levy Walkers");
//        } else {
//            seekers = new XYSeries("Seekers");
//            carriers = new XYSeries("Carriers");
//        }
//
//        juegoDatos.addSeries(seekers);
//        juegoDatos.addSeries(carriers);
//
//        if (AppMain.mode.equals("hybrid3")) {
//            juegoDatos.addSeries(levyWalkers);
//        }
//
//        juegoDatos2.addSeries(globalInfo);
//
//        if (AppMain.graph.equals("graphson")) {
//            this.setLocation(10, 500);
//            this.setSize(210, 260);
//            this.show();
//        }
//        if (AppMain.graph.equals("graphson")) {
//            frame2.setLocation(350, 150);
//            frame2.setSize(450, 450);
//            frame2.show();
//        }
//        pf = probFailure;
//        isUpdating = false;
//        filename = "dataCollected+" + getFileName();
//        failfilename = "failInfo+" + getFileName();
//    }
//
//    private void initComponents() {
//        addWindowListener(new java.awt.event.WindowAdapter() {
//            /* public void windowClosing(java.awt.event.WindowEvent evt) {
//             exitForm(evt);
//             }*/
//        });
//        pack();
//    }
//
//    public void update(Observable obs, Object arg) {
//        if (obs instanceof World) {
//            //System.out.println("obs" + obs);
//            int s = ((World) obs).getSeekers();
//            int c = ((World) obs).getCarriers();
//            int l = -1;
//
//            if (firstTime) {
//                if (AppMain.mode.equals("hybrid") || AppMain.mode.equals("hybrid3") || AppMain.mode.equals("hybrid4")) {
//                    filename += "+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height + "+n+" + AppMain.historySize + "+rho+" + AppMain.hybridThreshold + "+evap+" + AppMain.evap + "+" + AppMain.maze +".csv";
//                    failfilename += "+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height + "+n+" + AppMain.historySize + "+rho+" + AppMain.hybridThreshold + "+evap+" + AppMain.evap + "+" + AppMain.maze + ".csv";
//                } else {
//                    filename += "+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height + "+" + AppMain.maze + ".csv";
//                    failfilename += "+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height + "+" + AppMain.maze + ".csv";
//                }
//                firstTime = false;
//            }
//
//            iteration = ((World) obs).getAge();
//
//            if (!((World) obs).isFinished() && (s > 0 || c > 0)) {
//                double infoCollected = ((((double) ((World) obs).getAmountGlobalInfo()) / (double) (((((World) obs).width) * ((World) obs).height) - ((World) obs).wallsnumber)) * 100.0);
//                try {
//                    PrintWriter escribir;
//                    escribir = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
//
//                    if (AppMain.mode.equals("hybrid3")) {
//                        l = ((WorldHybridLWSandCImpl) ((World) obs)).LevyWalkers;
//                        escribir.println(iteration + "," + ((World) obs).getAmountGlobalInfo() + "," + c + "," + s + "," + infoCollected + "," + l);
//                    } else {
//                        escribir.println(iteration + "," + ((World) obs).getAmountGlobalInfo() + "," + c + "," + s + "," + infoCollected);
//                    }
//                    escribir.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(StatsTemperaturesImpl.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                //System.out.println("c:" + c + ", lw:" + s + "info: " + infoCollected + "r:" +infoCollected);
//            }
//
//            if (AppMain.graph.equals("graphson")) {
//                carriers.add(this.iteration, c);
//                seekers.add(this.iteration, s);
//
//                if (AppMain.mode.equals("hybrid3")) {
//                    levyWalkers.add(this.iteration, l);
//                }
//                //System.out.println("(((((World) obs).width)* ((World) obs).height)" + infoCollected);
//                globalInfo.add(this.iteration, (((World) obs).getAmountGlobalInfo() / (double) (((((World) obs).width) * ((World) obs).height) - ((World) obs).wallsnumber) * 100.0));
//                grafica = this.creaImagen();
//                this.getGraphics().drawImage(grafica, 10, 20, null);
//                grafica = this.creaImgGlobalInfo(globalInfo, false);
//                frame2.getGraphics().drawImage(grafica, 10, 20, null);
//                /* Output for global information collected*/
//            }
//            // if(((World)obs).getRoundGetInfo() != -1){
//            if ((AppMain.maxIter == -1 && ((World) obs).getIdBest() != -1) || (AppMain.maxIter >= 0 && iteration >= AppMain.maxIter) || (((World) obs).getAgentsDie() == ((World) obs).getAgents().size()) || ((World) obs).getIdBest() != -1) {
//                //StatsTemperaturesMapImpl sti = new StatsTemperaturesMapImpl("experiment-p-" + ((World) obs).getAgents().size() + "- pf-" + pf + ".csv");
//                if (!isUpdating) {
//                    isUpdating = true;
//                    ((World) obs).stop();
//                    StatsTemperaturesImpl sti;
//                    if (AppMain.mode.equals("hybrid") || AppMain.mode.equals("hybrid3") || AppMain.mode.equals("hybrid3")) {
//                        sti = new StatsTemperaturesImpl("experiment+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height + "+n+" + AppMain.historySize + "+rho+" + AppMain.hybridThreshold + "+evap+" + AppMain.evap + "+" + AppMain.maze + ".csv");
//                    } else {
//                        sti = new StatsTemperaturesImpl("experiment+p+" + ((World) obs).getAgents().size() + "+pf+" + pf + "+mode+" + AppMain.getMode() + "+maxIter+" + AppMain.maxIter + "+w+" + ((World) obs).width + "+h+" + ((World) obs).height+ "+" + AppMain.maze+ ".csv");
//                    }
//                    sti.printStatistics((World) obs);
//                    System.out.println("The end" + ((World) obs).getAge());
//                    if (AppMain.graph.equals("graphson")) {
//                        grafica = this.creaImgGlobalInfo(globalInfo, true);
//                        frame2.getGraphics().drawImage(grafica, 10, 20, null);
//                    }
//                    ((World) obs).setFinished(true);
//
//                    try {
//                        PrintWriter escribir;
//                        escribir = new PrintWriter(new BufferedWriter(new FileWriter(failfilename, true)));
//
//                        Hashtable A = (Hashtable) ((World) obs).getFailAgentsInformation();
//
//                        //System.out.println("A" + A);
//
//                        for (Iterator<Integer> iterator = A.keySet().iterator(); iterator.hasNext();) {
//                            int key = iterator.next();
//                            escribir.println(A.get(key));
//                        }
//
//                        escribir.close();
//                    } catch (IOException ex) {
//                        Logger.getLogger(StatsTemperaturesImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                    System.exit(0);
//                }
//            }
//
//        }
//    }
//
//    public void addObserver(World w) {
//        w.addObserver(this);
//    }
//
//    public BufferedImage creaImagen() {
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                getTechniqueName(AppMain.getMode()), "Round number", "Agents",
//                juegoDatos, PlotOrientation.VERTICAL,
//                true, true, false);
//        /*
//         JFreeChart chart =
//         ChartFactory.createTimeSeriesChart("Sesiones en Adictos al Trabajo"
//         "Meses", "Sesiones", juegoDatos,
//         false,
//         false,
//         true // Show legend
//         );
//         */
//        BufferedImage image = chart.createBufferedImage(200, 200);
//        return image;
//    }
//
//    public BufferedImage creaImgGlobalInfo(XYSeries glbInfo, boolean export) {
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                getTechniqueName(AppMain.getMode()), "Round number", "GlobalInfo",
//                juegoDatos2, PlotOrientation.VERTICAL,
//                true, true, false);
//
//        BufferedImage image = chart.createBufferedImage(400, 400);
//        if (export) {
//            FileOutputStream output;
//            try {
//                output = new FileOutputStream(AppMain.getMode() + pf + "globalInfo" + ".jpg");
//                ChartUtilities.writeChartAsJPEG(output, 1.0f, chart, 400, 400, null);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(ECALAgentsRight.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(ECALAgentsRight.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return image;
//    }
//
//    public void paint(java.awt.Graphics g) {
//        grafica = this.creaImagen();
//        g.drawImage(grafica, 20, 20, null);
//
//    }
//
//    private String getTechniqueName(String mode) {
//        switch (mode) {
//            case "sandclw":
//                return "SandC with Lw";
//            case "lwsandc2":
//                return "Lw and C";
//            case "lwsandc":
//                return "Lw and C-Lw";
//            case "lwphevap2":
//                return "C and Evap";
//            case "lwphevap":
//                return "C-Lw and Evap";
//            default:
//                return mode;
//        }
//    }
//
//    private String getFileName() {
//        Calendar c = new GregorianCalendar();
//        String dia, mes, annio, hora, minutos, segundos;
//        dia = Integer.toString(c.get(Calendar.DATE));
//        mes = Integer.toString(c.get(Calendar.MONTH) + 1);
//        annio = Integer.toString(c.get(Calendar.YEAR));
//        hora = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
//        minutos = Integer.toString(c.get(Calendar.MINUTE));
//        segundos = Integer.toString((c.get(Calendar.SECOND)));
//        return annio + mes + dia + hora + minutos + segundos;
//    }
//}
