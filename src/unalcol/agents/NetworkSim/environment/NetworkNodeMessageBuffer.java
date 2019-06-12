/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.environment;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.SimulationParameters;

/**
 * Singleton interface for messaging agents
 *
 * @author Arles Rodriguez
 */
public class NetworkNodeMessageBuffer {

    Hashtable<String, LinkedBlockingQueue> mbuffer;
    static final int MAXQUEUE = 5; //Max input buffer size by process

    private static class Holder {

        static final NetworkNodeMessageBuffer INSTANCE = new NetworkNodeMessageBuffer();
    }

    private NetworkNodeMessageBuffer() {
        mbuffer = new Hashtable<>();
    }

    public static NetworkNodeMessageBuffer getInstance() {
        return Holder.INSTANCE;
    }

    public void createBuffer(String pid) {
        if (mbuffer.containsKey(pid)) {
            System.out.println("error already exists!!!!");
        } else {
            mbuffer.put(pid, new LinkedBlockingQueue());
        }
    }

    public void deleteBuffer(String pid) {
        mbuffer.remove(pid);
    }

    public class putMessageInThread implements Runnable {

        private NetworkEnvironmentPheromoneReplicationNodeFailingChain env;
        private Node n;
        private String location;
        String[] msgnode;
        private int MULTIPLIER;

        public putMessageInThread(NetworkEnvironmentPheromoneReplicationNodeFailingChain env, Node n, String location, String[] msgnode, int MULTIPLIER) {
            this.env = env;
            this.n = n;
            this.location = location;
            this.msgnode = msgnode;
            this.MULTIPLIER = MULTIPLIER;
        }

        @Override
        public void run() {
            if (env.getNetworkDelays().containsKey(n.getVertex().getName() + location)) {
                try {
                    Thread.sleep(env.getNetworkDelays().get(n.getVertex().getName() + location) * MULTIPLIER);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NetworkNodeMessageBuffer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println(n.getVertex().getName() + location + " not found in delay data structure");
                n.sleep(SimulationParameters.averageDelay);
            }
            putMessage(location, msgnode);
        }
    }

    public synchronized void putMessageWithNetworkDelay(NetworkEnvironmentPheromoneReplicationNodeFailingChain env, Node n, String location, String[] msg, int MULTIPLIER) {
        Thread t = new Thread(new putMessageInThread(env, n, location, msg, MULTIPLIER));
        t.start();
    }

    public synchronized boolean putMessage(String pid, String[] msg) {
        //if (msg[0].equals("departing") || msg[0].equals("arrived") || msg[0].equals("freeresp")) {
        /* if (msg[0].equals("arrived")) {
            System.out.print("new message to: " + pid + " ");
            for (String x : msg) {
                System.out.print(" " + x);
            }
            System.out.println("");
        }*/
        if (pid == null) {
            //System.out.println("Warning!!! Destination is null");
            return false;
        }
        if (!mbuffer.containsKey(pid)) {
            //System.out.println("Warning!!!! Destination does not exist:" + pid + " msg:" + msg[0] + "from" + msg[1]);
            return false;
        }

        try {
            mbuffer.get(pid).add(msg);
        } catch (NullPointerException ex) {
            System.out.println("error leyendo buffer:" + pid);
        }
        return true;
    }

    // Called by Consumer
    public synchronized String[] getMessage(String pid) {
        try {
            //if (pid.equals("p23")) {
            //System.out.println("Node id" + pid + ", mbuffer size:" + mbuffer.get(pid).size());
            //}
            if (!mbuffer.get(pid).isEmpty()) {
                return (String[]) (mbuffer.get(pid).poll());
            }
        } catch (NullPointerException ex) {
            //System.out.println("Error reading mbuffer for agent:" + pid + "buffer: " + mbuffer);            cs
            System.out.println("error reading mbuffer " + pid + "... creating new buffer.");
            //createBuffer(pid);
            //mbuffer.put(pid, new LinkedBlockingQueue())
            //System.exit(1);
        }
        return null;
    }

}
