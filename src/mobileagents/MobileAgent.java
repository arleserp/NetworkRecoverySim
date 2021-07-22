/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

import graphutil.MyVertex;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import networkrecoverysim.SimulationParameters;
import serialization.StringSerializer;
import staticagents.Node;
import unalcol.agents.Action;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 *
 * @author Arles Rodriguez
 */
public class MobileAgent extends Agent implements Serializable {

    protected Hashtable<String, Object> properties = new Hashtable<>(); //use hashtable because is synchronized
    //LinkedBlockingQueue<Object> mbuffer = new LinkedBlockingQueue<>();
    private int round;
    private MyVertex location;
    private ArrayList data;
    private float pheromone;
    private int id;
    private int nMsgSend;
    private int nMsgRecv;
    private int idFather;
    private MyVertex prevLocation;
    private MyVertex prevPrevLocation;
    private ArrayList<String> lastLocations;
    private HashMap<String, ArrayList> networkdata;

    //metrics related to local consumption
    private int numberMessagesRecvByRound;
    private int numberMessagesSentByRound;
    private double sizeMessagesSent;
    private double sizeMessagesRecv;

    private int redundancyFactorK;
    private int counter = 0;

    public void incr() {
        counter++;
    }

    public void restartCounter() {
        counter = 0;
    }

    public boolean check() {
        return counter < redundancyFactorK;
    }

    public int getCounter() {
        return counter;
    }

    public MyVertex getPrevPrevLocation() {
        return prevPrevLocation;
    }

    public void setPrevPrevLocation(MyVertex prevPrevLocation) {
        this.prevPrevLocation = prevPrevLocation;
    }
    private List<HashMap> localNetwork;
    private List<HashMap<String, Integer>> localAgentsInNetwork;
    private List<HashMap<String, ConcurrentHashMap<Integer, Integer>>> localAgentsInNetworkHmap;
    //private List<HashMap> agents;

    private HashMap<String, Integer> respAgentsBkp;

    public MobileAgent(AgentProgram _program, int ida) {
        super(_program);
        localNetwork = new ArrayList<>();
        data = new ArrayList();
        round = -1;
        id = ida;
        pheromone = 1.0f;
        idFather = -1;
        respAgentsBkp = new HashMap<>();
        localAgentsInNetwork = new ArrayList<>();
        localAgentsInNetworkHmap = new ArrayList<>();
        lastLocations = new ArrayList<String>();
        networkdata = new HashMap<>();
        redundancyFactorK = SimulationParameters.redundancyFactor;
    }

    public ArrayList<String> getLastLocations() {
        return lastLocations;
    }

    public void setLastLocations(ArrayList<String> lastLocations) {
        this.lastLocations = lastLocations;
    }

    public MobileAgent() {
        //Default algorithm
        super(null, null);
        this.localNetwork = new ArrayList<>();
        localAgentsInNetwork = new ArrayList<>();
        localAgentsInNetworkHmap = new ArrayList<>();
        lastLocations = new ArrayList<>();

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
    public MyVertex getLocation() throws NullPointerException {
        if (location == null) {
            throw new NullPointerException("Agent has not a valid location: " + this.getId() + " location is null is alive: " + (this.status == Action.DIE));
        }
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(MyVertex location) {
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
    public MyVertex getPrevLocation() {
        return prevLocation;
    }

    /**
     * @param prevLocation the prevLocation to set
     */
    public void setPrevLocation(MyVertex prevLocation) {
        this.prevLocation = prevLocation;
    }

    /**
     * @return the localNetwork
     */
    public List<HashMap> getLocalNetwork() {
        return localNetwork;
    }

    /**
     * @param localNetwork the localNetwork to set
     */
    public void setLocalNetwork(List localNetwork) {
        this.localNetwork = localNetwork;
    }

    /**
     * @return the respAgentsBkp
     */
    public HashMap getRespAgentsBkp() {
        return respAgentsBkp;
    }

    /**
     * @return the networkdata
     */
    public HashMap<String, ArrayList> getNetworkdata() {
        return networkdata;
    }

    /**
     * @param networkdata the networkdata to set
     */
    public void setNetworkdata(HashMap<String, ArrayList> networkdata) {
        this.networkdata = networkdata;
    }

    /**
     * @param respAgentsBkp the respAgentsBkp to set
     */
    public void setRespAgentsBkp(HashMap respAgentsBkp) {
        this.respAgentsBkp = respAgentsBkp;
    }

    /**
     * @return the localAgentsInNetwork
     */
    public List<HashMap<String, Integer>> getLocalAgentsInNetwork() {
        return localAgentsInNetwork;
    }

    /**
     * @param localAgentsInNetwork the localAgentsInNetwork to set
     */
    public void setLocalAgentsInNetwork(List<HashMap<String, Integer>> localAgentsInNetwork) {
        this.localAgentsInNetwork = localAgentsInNetwork;
    }

    /**
     * @return the localAgentsInNetworkHmap
     */
    public List<HashMap<String, ConcurrentHashMap<Integer, Integer>>> getLocalAgentsInNetworkHmap() {
        return localAgentsInNetworkHmap;
    }

    /**
     * @param localAgentsInNetworkHmap the localAgentsInNetworkHmap to set
     */
    public void setLocalAgentsInNetworkHmap(List<HashMap<String, ConcurrentHashMap<Integer, Integer>>> localAgentsInNetworkHmap) {
        this.localAgentsInNetworkHmap = localAgentsInNetworkHmap;
    }

    @Override
    public String toString() {
        return "MobileAgent{" + " id=" + id + ", networkdata size=" + networkdata.size() + ", round=" + round + ", status " + status + '}';
    }

    // Metrics related with consumption of mobile agents
    public int getMemoryConsumption() {
        StringSerializer serializer = new StringSerializer();
        String totalData = serializer.serialize(this.networkdata);
        return totalData.length();
    }

    /**
     * Increase number of messages received by round
     *
     * @param sizeMsgRecv size of messages received
     * @param numberMessages number of messages
     */
    public void increaseMessagesRecvByRound(double sizeMsgRecv, int numberMessages) {
        setSizeMessagesRecv(sizeMsgRecv + getSizeMessagesRecv());
        setNumberMessagesRecvByRound(getNumberMessagesRecvByRound() + numberMessages);
    }

    /**
     * Increase number of messages sent by round
     *
     * @param sizeMsgSent size of messages sent
     * @param numberMessages number of messages sent
     */
    public void increaseMessagesSentByRound(double sizeMsgSent, int numberMessages) {
        setSizeMessagesSent(sizeMsgSent + getSizeMessagesSent());
        setNumberMessagesSentByRound(getNumberMessagesSentByRound() + numberMessages);
    }

    /**
     * Node stats in terms of network
     *
     * @return
     */
    public int getNumberMessagesRecvByRound() {
        return numberMessagesRecvByRound;
    }

    public void setNumberMessagesRecvByRound(int numberMessagesRecvByRound) {
        this.numberMessagesRecvByRound = numberMessagesRecvByRound;
    }

    public int getNumberMessagesSentByRound() {
        return numberMessagesSentByRound;
    }

    public void setNumberMessagesSentByRound(int numberMessagesSentByRound) {
        this.numberMessagesSentByRound = numberMessagesSentByRound;
    }

    public double getSizeMessagesSent() {
        return sizeMessagesSent;
    }

    public void setSizeMessagesSent(double sizeMessagesSent) {
        this.sizeMessagesSent = sizeMessagesSent;
    }

    public double getSizeMessagesRecv() {
        return sizeMessagesRecv;
    }

    public void setSizeMessagesRecv(double sizeMessagesRecv) {
        this.sizeMessagesRecv = sizeMessagesRecv;
    }

    public void initCounterMessagesByRound() {
        sizeMessagesRecv = 0;
        sizeMessagesSent = 0;
        numberMessagesRecvByRound = 0;
        numberMessagesSentByRound = 0;
    }

    /**
     * Prune data in the memory of a mobile agent
     * @param c
     * @param nhops 
     */
    public void pruneData(Node c, int nhops) {
        ArrayList<String> neighbours = new ArrayList<>();
        neighbours.add(c.getVertex().getName());
        HashMap<String, Integer> distances = new HashMap<>();
        c.getListNeighboursHop(nhops, c.getName(), distances);
        
        for (String neig : distances.keySet()) {
            if (!neighbours.contains(neig) && distances.get(neig) <= nhops) {
                neighbours.add(neig);
            }
        }
        
        HashMap<String, ArrayList> networkDatatmp = new HashMap<>();
        for (String s : neighbours) {
            if (networkdata.containsKey(s)) {
                networkDatatmp.put(s, networkdata.get(s));
            }
        }
        networkdata = networkDatatmp;
    }
}
