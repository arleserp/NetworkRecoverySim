/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observer;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import environment.NetworkEnvironment;
import graphutil.GraphComparator;
import graphutil.MyVertex;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.JFrame;
import networkrecoverysim.DataReplicationEscenarioNodeFailing;
import networkrecoverysim.SimulationParameters;
import staticagents.Node;
import util.StatisticsNormalDist;

/**
 *
 * @author Arles Rodriguez
 */
public class DataReplicationNodeFailingObserver implements Observer {

    //JFrame frame;
    BasicVisualizationServer<MyVertex, String> vv = null;
    boolean added = false;
    boolean isDrawing = false;
    public static boolean isUpdating;
    HashMap<Integer, Double> globalInfo = new HashMap();
    HashMap<Integer, Integer> nodesComplete = new HashMap<>();
    HashMap<Integer, StatisticsNormalDist> roundVsInfoAvg = new HashMap<>();
    private String nodeStatsFileName = null;

    public static int lastagentsAlive = -1;
    public static int lastnodesAlive = -1;
    private long lastAge = -1;

    DataReplicationEscenarioNodeFailing dataReplEsc;

    public DataReplicationNodeFailingObserver(DataReplicationEscenarioNodeFailing drs) {
        isUpdating = false;
        dataReplEsc = drs;
        String baseFilename = SimulationParameters.reportsFilenamePrefix;
        System.out.println("base filename:" + baseFilename);
        String avgNodeLife = baseFilename + "+node";
        createDir(avgNodeLife);
        nodeStatsFileName = "./" + avgNodeLife + "/" + baseFilename + "+" + getFileName() + "+nodestats.csv";
    }

    @Override
    public synchronized void update(Observable o, Object arg) {

        if (arg instanceof Node) {
            final NetworkEnvironment world = (NetworkEnvironment) o;
            //write node averageLife
            Node node = (Node) arg;
            PrintWriter nodeStatsFile;
            try {
                nodeStatsFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeStatsFileName, true)));
                //log: getAge|nodeId|version|rounds|nsentmsg|sizesentmsg|nrecvmsg|sizerecvmsg|neighbours|neighbours.size|memory
                nodeStatsFile.println(world.getAge() + "," + node.getName() + "," + world.getNodeVersion(node) + "," + node.getRounds() + "," + world.getTopology().degree(node.getVertex()));
                nodeStatsFile.close();
            } catch (IOException ex) {
                Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //System.out.println("observer update");
        if (o instanceof NetworkEnvironment) {
            final NetworkEnvironment world = (NetworkEnvironment) o;

//            if (!agentsNumber.containsKey(n.getAge())) {
//                agentsNumber.put(n.getAge(), n.getAgentsLive());
//            }
//
//            if (SimulationParameters.simMode.equals("nhopsinfo")) {
//                if (!roundVsInfoAvg.containsKey(n.getAge())) {
//                    roundVsInfoAvg.put(n.getAge(), n.getAmountOfNeighbourInfo());
//                }
//            }
//            int agentsAlive = n.getAgentsAlive();
            int nodesAlive = world.getNodesAlive();
            if (lastnodesAlive == -1 || nodesAlive != lastnodesAlive) {
                System.out.println("Nodes alive: " + nodesAlive);
                lastnodesAlive = nodesAlive;
            }

//            if (lastagentsAlive == -1 || agentsAlive != lastagentsAlive) {
//                System.out.println("Agents alive: " + agentsAlive);
//                lastagentsAlive = agentsAlive;
//            }
//            System.out.println("maxIter:" + SimulationParameters.maxIter  + ", " + n.getAge());
            if (SimulationParameters.maxIter >= 0 && world.getAge() >= SimulationParameters.maxIter || nodesAlive == 0) {
                if (!isUpdating) {
                    System.out.println("stopping simulation");
                    isUpdating = true;
                    world.stop();

                    System.out.println("nodes" + world.getNodes().size());
                    PrintWriter nodeStatsFile;
                    try {
                        System.out.println("file" + nodeStatsFileName);
                        nodeStatsFile = new PrintWriter(new BufferedWriter(new FileWriter(nodeStatsFileName, true)));
                        //log: getAge|nodeId|version|rounds|nsentmsg|sizesentmsg|nrecvmsg|sizerecvmsg|neighbours|neighbours.size|memory
                        for (Node node : world.getNodes()) {
                            nodeStatsFile.println(world.getAge() + "," + node.getName() + "," + world.getNodeVersion(node) + "," + node.getRounds() + "," + world.getTopology().degree(node.getVertex()));
                        }
                        nodeStatsFile.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //
                    String baseFilename = SimulationParameters.reportsFilenamePrefix;
                    System.out.println("base filename:" + baseFilename);

                    if (SimulationParameters.simMode.equals("nhopsinfo")) {
                        String roundVsInfoFileName = baseFilename + "+nhopsInfo+" + SimulationParameters.nhopsPrune + "+roundVsInfo";
                        createDir(roundVsInfoFileName);
                        String roundVsInfoStats = "./" + roundVsInfoFileName + "/" + baseFilename + "+" + getFileName() + "+roundVsInfo.csv";
                        PrintWriter writeRoundVsInfo = null;
                        try {
                            writeRoundVsInfo = new PrintWriter(new BufferedWriter(new FileWriter(roundVsInfoStats, true)));
                            SortedSet<Integer> keysAg = new TreeSet<>(roundVsInfoAvg.keySet());
                            for (int x : keysAg) {
                                writeRoundVsInfo.println(x + "," + roundVsInfoAvg.get(x).getMean() + "," + roundVsInfoAvg.get(x).getStdDev());
                            }
                            writeRoundVsInfo.close();
                        } catch (IOException ex) {
                            Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //Write similarity metrics by round by simulation
                    //write last similarity reported
                    GraphComparator gnm = new GraphComparator();
                    double finalSimilarity = 0;
                    finalSimilarity = gnm.calculateSimilarity(world);
                    System.out.println("Final Similarity:" + finalSimilarity);
                    dataReplEsc.getSimilarity().put(world.getAge(), finalSimilarity);

                    //Write similarity metrics
                    String graphSimilarity = baseFilename + "+similarity";
                    createDir(graphSimilarity);
                    String graphSimilarityStats = "./" + graphSimilarity + "/" + baseFilename + "+" + getFileName() + "+similarity.csv";

                    PrintWriter writeSimilarity = null;
                    try {
                        writeSimilarity = new PrintWriter(new BufferedWriter(new FileWriter(graphSimilarityStats, true)));
                        SortedSet<Integer> keysSim = new TreeSet<>(dataReplEsc.getSimilarity().keySet());
                        for (Integer x : keysSim) {
                            writeSimilarity.println(x + "," + dataReplEsc.getSimilarity().get(x));
                        }
                        writeSimilarity.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //write node averageLife
                    String avgNodeLife = baseFilename + "+nodeLife";
                    createDir(avgNodeLife);
                    String nodeLifeStats = "./" + avgNodeLife + "/" + baseFilename + "+" + getFileName() + "+nodeLife.csv";
                    PrintWriter writeNodeLife;
                    try {
                        writeNodeLife = new PrintWriter(new BufferedWriter(new FileWriter(nodeLifeStats, true)));
                        for (double x : world.getNodeAverageLife()) {
                            writeNodeLife.println(x);
                        }
                        writeNodeLife.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Write agents live number
                    String agNumberdirName = baseFilename + "+agentNumber";
                    createDir(agNumberdirName);
                    String agentNumberStats = "./" + agNumberdirName + "/" + baseFilename + "+" + getFileName() + "+agentnumber.csv";

                    PrintWriter escribirAgentNumber = null;
                    try {
                        escribirAgentNumber = new PrintWriter(new BufferedWriter(new FileWriter(agentNumberStats, true)));
                        HashMap agentsNumber = dataReplEsc.getMobileAgentsAlive();
                        SortedSet<Integer> keysAg = new TreeSet<>(agentsNumber.keySet());
                        for (int x : keysAg) {
                            escribirAgentNumber.println(x + "," + agentsNumber.get(x));
                        }
                        escribirAgentNumber.close();

                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    world.getLocalStats();

                    //Write stats about total network consumption and total memory consumption by round
                    String resourcesdirName = baseFilename + "+resources";
                    createDir(resourcesdirName);
                    String resourcesStats = "./" + resourcesdirName + "/" + baseFilename + "+" + getFileName() + "+resources.csv";

                    PrintWriter escribirResourcesStats = null;
                    try {
                        escribirResourcesStats = new PrintWriter(new BufferedWriter(new FileWriter(resourcesStats, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());

                        SortedSet<Integer> keyAges = new TreeSet<>(world.getMemoryConsumption().keySet());
                        for (int wRound : keyAges) {
                            //System.out.println("writting stat" + x);
                            escribirResourcesStats.println(wRound + "," + world.getMemoryConsumption().get(wRound));
                        }
                        escribirResourcesStats.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    
                    //Write stats about total network consumption and total memory consumption by round
                    String resourcesdirNameMa = baseFilename + "+resourcesMa";
                    createDir(resourcesdirNameMa);
                    String resourcesStatsMa = "./" + resourcesdirNameMa + "/" + baseFilename + "+" + getFileName() + "+resourcesMa.csv";

                    PrintWriter escribirResourcesStatsMa;
                    try {
                        escribirResourcesStatsMa = new PrintWriter(new BufferedWriter(new FileWriter(resourcesStatsMa, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());

                        SortedSet<Integer> keyAgesMa = new TreeSet<>(world.getMemoryConsumptionMa().keySet());
                        for (int wRoundMa : keyAgesMa) {
                            //System.out.println("writting stat" + x);
                            escribirResourcesStatsMa.println(wRoundMa + "," + world.getMemoryConsumptionMa().get(wRoundMa));
                        }
                        escribirResourcesStatsMa.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    //write stats about amount of messages recv a send in each round, it is shown the overhead imposed for the algorithm in each round.
                    String localNetworkStatsSent;

                    //Write stats about total network consumption and total memory consumption by round
                    //Sent:
                    String localStatsSentdirName = baseFilename + "+localstatssent";
                    createDir(localStatsSentdirName);
                    String localStatsSentFileName = "./" + localStatsSentdirName + "/" + baseFilename + "+" + getFileName() + "+sent.csv";

                    PrintWriter escribirLocalStatsSent = null;
                    try {
                        escribirLocalStatsSent = new PrintWriter(new BufferedWriter(new FileWriter(localStatsSentFileName, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());
                        SortedSet<Integer> worldRoundSet = new TreeSet<>(world.getNumberMessagesSent().keySet());
                        //totalNMsgSentRound|totalSMsgSentRound                        
                        for (Integer r : worldRoundSet) {
                            localNetworkStatsSent = world.getNumberMessagesSent().get(r) + "," + world.getSizeMessagesSent().get(r);
                            escribirLocalStatsSent.println(r + "," + localNetworkStatsSent);
                        }
                        escribirLocalStatsSent.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    
                    
                    //write stats about amount of messages recv a send in each round, it is shown the overhead imposed for the algorithm in each round.
                    String localNetworkStatsSentMa;
                    //Write stats about total network consumption and total memory consumption by round
                    //Sent:
                    String localStatsSentdirNameMa = baseFilename + "+localstatssentMa";
                    createDir(localStatsSentdirNameMa);
                    String localStatsSentFileNameMa = "./" + localStatsSentdirNameMa + "/" + baseFilename + "+" + getFileName() + "+sentMa.csv";

                    PrintWriter escribirLocalStatsSentMa = null;
                    try {
                        escribirLocalStatsSentMa = new PrintWriter(new BufferedWriter(new FileWriter(localStatsSentFileNameMa, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());
                        SortedSet<Integer> worldRoundSetMa = new TreeSet<>(world.getNumberMessagesSentMa().keySet());
                        //totalNMsgSentRound|totalSMsgSentRound                        
                        for (Integer r : worldRoundSetMa) {
                            localNetworkStatsSentMa = world.getNumberMessagesSentMa().get(r) + "," + world.getSizeMessagesSentMa().get(r);
                            escribirLocalStatsSentMa.println(r + "," + localNetworkStatsSentMa);
                        }
                        escribirLocalStatsSentMa.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    //Recv: 
                    String localNetworkStatsRecv;
                    String localStatsRecvdirName = baseFilename + "+localstatsrecv";
                    createDir(localStatsRecvdirName);
                    String localStatsRecvFileName = "./" + localStatsRecvdirName + "/" + baseFilename + "+" + getFileName() + "+recv.csv";

                    PrintWriter escribirLocalStatsRecv = null;
                    try {
                        escribirLocalStatsRecv = new PrintWriter(new BufferedWriter(new FileWriter(localStatsRecvFileName, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());
                        SortedSet<Integer> worldRoundSet = new TreeSet<>(world.getNumberMessagesRecv().keySet());
                        //totalNMsgRecvRound|totalSMsgRecvRound
                        for (int r : worldRoundSet) {
                            localNetworkStatsRecv = world.getNumberMessagesRecv().get(r) + "," + world.getSizeMessagesRecv().get(r);
                            escribirLocalStatsRecv.println(r + "," + localNetworkStatsRecv);
                        }
                        escribirLocalStatsRecv.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    
                     //Recv: 
                    String localNetworkStatsRecvMa;
                    String localStatsRecvdirNameMa = baseFilename + "+localstatsrecvMa";
                    createDir(localStatsRecvdirNameMa);
                    String localStatsRecvFileNameMa = "./" + localStatsRecvdirNameMa + "/" + baseFilename + "+" + getFileName()+ "+recvMa.csv";

                    PrintWriter escribirLocalStatsRecvMa = null;
                    try {
                        escribirLocalStatsRecvMa = new PrintWriter(new BufferedWriter(new FileWriter(localStatsRecvFileNameMa, true)));
                        //System.out.println("writing " + dataReplEsc.getNetworkAndMemoryStats());
                        SortedSet<Integer> worldRoundSetMa = new TreeSet<>(world.getNumberMessagesRecvMa().keySet());
                        //totalNMsgRecvRound|totalSMsgRecvRound
                        for (int r : worldRoundSetMa) {
                            localNetworkStatsRecvMa = world.getNumberMessagesRecvMa().get(r) + "," + world.getSizeMessagesRecvMa().get(r);
                            escribirLocalStatsRecvMa.println(r + "," + localNetworkStatsRecvMa);
                        }
                        escribirLocalStatsRecvMa.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataReplicationNodeFailingObserver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    //System.out.println("pasoooo!");
                    //Statistics regarding messages received by node.
                    StatisticsProviderReplicationNodeFailing sti = new StatisticsProviderReplicationNodeFailing(baseFilename);

                    //ToFix: Statistis
                    sti.printStatistics(world, finalSimilarity);

                    SimulationParameters.stopTime = System.currentTimeMillis();
                    System.out.println("The end" + world.getAge() + " time of simulation:" + (SimulationParameters.stopTime - SimulationParameters.startTime) + " number of node failures: " + world.getNumberFailures());
                    System.exit(0);
                }
            }
        }
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
