/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Singleton interface for messaging agents
 *
 * @author Arles Rodriguez
 */
public class NetworkMessageMobileAgentBuffer {

    Hashtable<Integer, LinkedBlockingQueue> mbuffer;
    static final int MAXQUEUE = 5; //Max input buffer size by process

    private static class Holder {
        static final NetworkMessageMobileAgentBuffer INSTANCE = new NetworkMessageMobileAgentBuffer();
    }

    private NetworkMessageMobileAgentBuffer() {
        mbuffer = new Hashtable<>();
    }

    public static NetworkMessageMobileAgentBuffer getInstance() {
        return Holder.INSTANCE;
    }

    public void createBuffer(Integer pid) {
        mbuffer.put(pid, new LinkedBlockingQueue());
    }

    public void putMessage(Integer pid, String[] msg) {
        mbuffer.get(pid).add(msg);
    }

    // Called by Consumer
    public String[] getMessage(Integer pid) {
        try {
            //System.out.println("agent id" + pid + ", mbuffer" + mbuffer.get(pid).toString());
            if (!mbuffer.get(pid).isEmpty()) {
                return (String[]) (mbuffer.get(pid).poll());
            }
        } catch (NullPointerException ex) {
            System.out.println("Error reading mbuffer for mobile agent:" + pid + "buffer: " + mbuffer);
        }
        return null;
    }

}
