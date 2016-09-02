/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.environment;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

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
        mbuffer.put(pid, new LinkedBlockingQueue());
    }

    public void putMessage(String pid, String[] msg) {
        mbuffer.get(pid).add(msg);
    }

    // Called by Consumer
    public String[] getMessage(String pid) {
        try {
            //System.out.println("agent id" + pid + ", mbuffer" + mbuffer.get(pid).toString());
            return (String[]) (mbuffer.get(pid).poll());
        } catch (NullPointerException ex) {
            System.out.println("Error reading mbuffer for agent:" + pid + "buffer: " + mbuffer);
            System.exit(1);
        }
        return null;
    }

}
