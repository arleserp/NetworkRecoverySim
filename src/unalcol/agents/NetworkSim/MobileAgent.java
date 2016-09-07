/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 *
 * @author Arles Rodriguez
 */
public class MobileAgent extends Agent implements Serializable {

    protected Hashtable<String, Object> properties = new Hashtable<String, Object>();
    //LinkedBlockingQueue<Object> mbuffer = new LinkedBlockingQueue<>();
    private int round;
    private GraphElements.MyVertex location;
    private ArrayList data;
    private float pheromone;
    private int id;
    private int nMsgSend;
    private int nMsgRecv;
    private int idFather;
    private GraphElements.MyVertex prevLocation;

    public MobileAgent(AgentProgram _program, int ida) {
        super(_program);
        data = new ArrayList();
        round = -1;
        id = ida;
        pheromone = 1.0f;
        idFather = -1;
    }

    
    public void setAttribute(String key, Object value) {
        properties.put(key, value);
    }

    public Object getAttribute(String key) {
        return properties.get(key);
    }

    public boolean removeAttribute(String key) {
        if (properties.remove(key) != null) {
            return true;
        } else {
            return (false);
        }
    }

    /**
     * @return the pheromone
     */
    public float getPheromone() {
        return pheromone;
    }

    /**
     * @param pheromone the pheromone to set
     */
    public void setPheromone(float pheromone) {
        this.pheromone = pheromone;
    }

//    public void putMessage(Object message) {
//        mbuffer.add(message);
//    }
//
//    public Object getMessage(){
//        return mbuffer.poll();
//    }
    /**
     * @return the round round is defined as the number of iteration by
     * distributed Asynchronous Round-Based Computation Differently from
     * synchronous sys -tems, the rounds are not given for free in an
     * asynchronous system. Each process pi has to handle a local variable ri
     * which denotes its current round number. We first consider that, in each
     * round r it executes, a process sends a message to each of its neighbors,
     * and receives a message from each of them.
     */
    public int getRound() {
        return round;
    }

    /**
     * @param round the round to set
     */
    public void setRound(int round) {
        this.round = round;
    }

//    public boolean hasMessages() {
//        synchronized (mbuffer) {
//            return !mbuffer.isEmpty();
//        }
//    }
    public void log(String msg) {
        System.out.println("Agent: " + this.getAttribute("ID") + " : " + msg);
    }

    /**
     * @return the location
     */
    public GraphElements.MyVertex getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(GraphElements.MyVertex location) {
        this.location = location;
    }

    /**
     * @return the data
     */
    public ArrayList getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList data) {
        this.data = data;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nMsgSend
     */
    public int getnMsgSend() {
        return nMsgSend;
    }

    /**
     * @param nMsgSend the nMsgSend to set
     */
    public void setnMsgSend(int nMsgSend) {
        this.nMsgSend = nMsgSend;
    }

    /**
     * @return the nMsgRecv
     */
    public int getnMsgRecv() {
        return nMsgRecv;
    }

    /**
     * @param nMsgRecv the nMsgRecv to set
     */
    public void setnMsgRecv(int nMsgRecv) {
        this.nMsgRecv = nMsgRecv;
    }

    public void incMsgSend() {
        synchronized (MobileAgent.class) {
            nMsgSend++;
        }
    }

    public void incMsgRecv() {
        synchronized (MobileAgent.class) {
            nMsgRecv++;
        }
    }

    /**
     * @return the idFather
     */
    public int getIdFather() {
        return idFather;
    }

    /**
     * @param idFather the idFather to set
     */
    public void setIdFather(int idFather) {
        this.idFather = idFather;
    }

    /**
     * @return the prevLocation
     */
    public GraphElements.MyVertex getPrevLocation() {
        return prevLocation;
    }

    /**
     * @param prevLocation the prevLocation to set
     */
    public void setPrevLocation(GraphElements.MyVertex prevLocation) {
        this.prevLocation = prevLocation;
    }

}
