/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package staticagents;

import environment.NetworkEnvironment;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import serialization.StringSerializer;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 * Simplified version of node
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {
    private MyVertex v;  //vertex in topology that represents a node
    private String visitedStatus;
    private final ConcurrentHashMap<Integer, Integer> agentsInNode; //Maybe delete    
    private ArrayList<Agent> currentAgents;
    private double pfCreate;
    private int roundsWithOutVisit;
    private int roundsWithoutAck;
    private int rounds;
    public boolean isProcessing = false;
    private HashMap<String, ArrayList> networkdata;
    private HashMap<Object, ArrayList> pending;
    private HashMap<String, Integer> respAgentsBkp; //Maybe delete     
    private HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsInNeighbors;
    AtomicInteger c = new AtomicInteger(0); //maybe delete or rename
    private HashMap<Integer, String> idCounter; //added by arles.rodriguez 12/12/2018
    private HashMap<MyVertex, Integer> distancesToNode;
    private int numberMessagesRecvByRound;
    private int numberMessagesSentByRound;
    private double sizeMessagesSent;
    private double sizeMessagesRecv;

    public HashMap<MyVertex, Integer> getDistancesToNode() {
        return distancesToNode;
    }

    public void setDistancesToNode(HashMap<MyVertex, Integer> distancesToNode) {
        this.distancesToNode = distancesToNode;
    }

    protected Hashtable<String, Object> properties = new Hashtable<>(); //hashtable is synchronized 

    public void incrementAgentCount() {
        c.incrementAndGet();
    }

    public void decrementAgentCount() {
        c.decrementAndGet();
    }

    public int getAgentCount() {
        return c.get();
    }

    public HashMap<Object, ArrayList> getPending() {
        return pending;
    }

    public void setPending(HashMap<Object, ArrayList> pending) {
        this.pending = pending;
    }

    //try to stimate pf locally 1/numberofagentcreated
    public Node(AgentProgram _program, MyVertex ve) {
        super(_program);
        this.pending = new HashMap();
        this.networkdata = new HashMap<>();
        this.v = ve;
        currentAgents = new ArrayList<>();
        rounds = 0;
        respAgentsBkp = new HashMap<>();
        agentsInNode = new ConcurrentHashMap<>();
        agentsInNeighbors = new HashMap<>();
        idCounter = new HashMap<>();
        visitedStatus = "";
    }

    public MyVertex getVertex() {
        return v;
    }

    public String getName() {
        return v.getName();
    }

    public void addAgent(Agent a) {
        getCurrentAgents().add(a);
    }

    public void addAgentInNode(int agId, int fId) {
        getAgentsInNode().put(agId, fId);
    }

    public void deleteAgentInNode(int agId) {
        getAgentsInNode().remove(agId);
    }

    public void deleteAgent(Agent a) {
        synchronized (Node.class) {
            for (Agent x : getCurrentAgents()) {
                if (x.equals(a)) {
                    getCurrentAgents().remove(x);
                }
            }
        }
    }

    /**
     * @return the pfCreate
     */
    public double getPfCreate() {
        return pfCreate;
    }

    /**
     * @param pfCreate the pfCreate to set
     */
    public void setPfCreate(double pfCreate) {
        this.pfCreate = pfCreate;
    }

    /**
     * @return the agentsInNode
     */
    public ArrayList<Agent> getCurrentAgents() {
        return currentAgents;
    }

    /**
     * @param currentAgents the currentAgents to set
     */
    public void setCurrentAgents(ArrayList<Agent> currentAgents) {
        this.currentAgents = currentAgents;
    }

    /**
     * @return the roundsWithOutVisit
     */
    public int getRoundsWithOutVisit() {
        return roundsWithOutVisit;
    }

    /**
     * @param roundsWithOutVisit the roundsWithOutVisit to set
     */
    public void setRoundsWithOutVisit(int roundsWithOutVisit) {
        this.roundsWithOutVisit = roundsWithOutVisit;
    }

    /**
     */
    public void addRoundsWithOutVisit() {
        roundsWithOutVisit++;
    }

    /**
     * @return the roundsWithoutAck
     */
    public int getRoundsWithoutAck() {
        return roundsWithoutAck;
    }

    /**
     * @param roundsWithoutAck the roundsWithoutAck to set
     */
    public void setRoundsWithoutAck(int roundsWithoutAck) {
        this.roundsWithoutAck = roundsWithoutAck;
    }

    /**
     * increases rounds without ack
     */
    public void incRoundsWithoutAck() {
        roundsWithoutAck++;
    }

    /**
     * @return the rounds
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * @param rounds the rounds to set
     */
    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void incRounds() {
        synchronized (Node.class) {
            this.rounds++;
        }
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
     * @param v the v to set
     */
    public void setVertex(MyVertex v) {
        this.v = v;
    }

    /**
     * @return the respAgentsBkp
     */
    public HashMap<String, Integer> getRespAgentsBkp() {
        return respAgentsBkp;
    }

    /**
     * @param respAgentsBkp the respAgentsBkp to set
     */
    public void setRespAgentsBkp(HashMap<String, Integer> respAgentsBkp) {
        this.respAgentsBkp = respAgentsBkp;
    }

    /**
     * @return the agentsInNode
     */
    public ConcurrentHashMap<Integer, Integer> getAgentsInNode() {
        return agentsInNode;
    }

    public ArrayList<Integer> getDuplicatedAgents() { //maybe delete?
        ArrayList<Integer> duplicatedAgents = new ArrayList<>();

        Iterator<Integer> it = agentsInNode.keySet().iterator();
        //System.out.print("Agents in node: " + agentsInNode + ", duplicated agents:");
        // 1. Rule to find if a process with original father has repeated agents
        while (it.hasNext()) {
            int agent = it.next();
            int father = agentsInNode.get(agent);
            if (father != -1) {
                if (agentsInNode.containsKey(father)) {
                    duplicatedAgents.add(agent);
                    System.out.print("1. " + agent);
                }
            }
        }

        // 2. Compare by now is O(n^2) detection can be optimized
        Object[] keys = agentsInNode.keySet().toArray();
//        Arrays.sort(keys);        

        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                //System.out.println("2." + agentsInNode + "-" + agentsInNode.get(keys[i]) + " vs " + agentsInNode.get(keys[j]));
                if (agentsInNode.get(keys[i]) != -1 && (Objects.equals(agentsInNode.get(keys[i]), agentsInNode.get(keys[j])))) {
                    if (!duplicatedAgents.contains((int) keys[j])) {
                        duplicatedAgents.add((int) keys[j]);
                        System.out.print("," + keys[j]);
                    }
                }
            }
        }

        if (!duplicatedAgents.isEmpty()) {
            //System.out.print("Agents in node: " + agentsInNode + ", duplicated agents detected. ");
            //System.out.println("yayayayay");
            for (Integer r : duplicatedAgents) {
                agentsInNode.remove(r);
            }
            //System.out.println("After - Agents in node: " + agentsInNode + ".");
        }
        return duplicatedAgents;
    }

    /**
     * @return the agentsInNeighbors
     */
    public HashMap<String, ConcurrentHashMap<Integer, Integer>> getAgentsInNeighbors() {
        return agentsInNeighbors;
    }

    /**
     * @param agentsInNeighbors the agentsInNeighbors to set
     */
    public void setAgentsInNeighbors(HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsInNeighbors) {
        this.agentsInNeighbors = agentsInNeighbors;
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

    public HashMap<Integer, String> getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(HashMap<Integer, String> idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public String toString() {
        return "Node{" + "v=" + v + ", rounds=" + rounds + '}';

    }

    /**
     * BFS algorithm to obtain list of neighbours in a determined hop and its
     * distances
     *
     * @param neighbours list of neighbours of a determined node in hop
     * nhopsChain
     * @param nhopsChain number of hops to obtain networks
     * @param nodeName node that callas the algorithm
     * @param distances distances to a determined node
     */
    private void getListNeighboursHop(ArrayList<String> neighbours, int nhopsChain, String nodeName, HashMap<String, Integer> distances) {
        int lvls = 1;
        Deque<String> q = new LinkedList<>();
        distances.put(nodeName, 0);
        if (nhopsChain == 0) {
            return;
        }
        while (nodeName != null) {
            if (getNetworkdata().containsKey(nodeName) && getNetworkdata().get(nodeName) != null) {
                List<String> list = new ArrayList<>(getNetworkdata().get(nodeName));
                Iterator<String> itr = list.iterator();
                while (itr.hasNext()) {
                    String ne = itr.next();
                    if (!distances.containsKey(ne)) {
                        distances.put(ne, distances.get(nodeName) + 1);
                        q.add(ne);
                    }
                }
            }
            nodeName = q.poll();
        }
    }

    /**
     * Prune data about neighbours in a determined number of hops
     *
     * @param nhops number of hops
     */
    public void pruneInformation(int nhops) {
        System.out.println("begin prune" + this.getVertex().getName());
        ArrayList<String> neighbours = new ArrayList<>();
        neighbours.add(this.getVertex().getName());
        HashMap<String, Integer> distances = new HashMap<>();
        getListNeighboursHop(neighbours, nhops, this.getVertex().getName(), distances);
        for (String neig : distances.keySet()) {
            if (!neighbours.contains(neig) && distances.get(neig) <= nhops) {
                neighbours.add(neig);
            }
        }
        System.out.println("neighbours size:" + neighbours.size() + " networkdata.size=" + networkdata.size());
        if (neighbours.size() != networkdata.size()) {
            HashMap<String, ArrayList> networkDatatmp = new HashMap<>();
            for (String s : neighbours) {
                if (networkdata.containsKey(s)) {
                    networkDatatmp.put(s, networkdata.get(s));
                }
            }
            networkdata = networkDatatmp;
        }
        System.out.println("end prune" + this.getVertex().getName());
    }

    public String getVisitedStatus() {
        return visitedStatus;
    }

    public void setVisitedStatus(String visitedStatus) {
        this.visitedStatus = visitedStatus;
    }    
    
    
    /**
     * This Method defines if create a new node or not
     *
     * @param env network environment.
     */
    public void evaluateNodeCreation(NetworkEnvironment env) {
        ArrayList<String> topologyData = new ArrayList(env.getTopologyNames(getVertex())); // Get topology of the network
        int sizeNetworkData = getNetworkdata().get(getName()).size();
        double totalSizeMsgSent = sizeNetworkData * 56;

        env.increaseTotalSizeMsgSent(totalSizeMsgSent);  //a ping is 56 bytes sent
        increaseMessagesSentByRound(totalSizeMsgSent, sizeNetworkData);

        int topologyDataSize =topologyData.size();
        double totalSizeMsgRecv = topologyDataSize * 56.0;
        env.increaseTotalSizeMsgRecv(totalSizeMsgRecv); //response of nodes
        increaseMessagesRecvByRound(totalSizeMsgRecv, topologyDataSize);

        if (getNetworkdata().containsKey(getName())) {
            List<String> nd = new ArrayList((Collection) getNetworkdata().get(getName()));

            //dif = nd - topologyData
            List<String> dif = new ArrayList<>(nd);
            dif.removeAll(topologyData);

            //dif = topologyData - nd
            List<String> dif2 = new ArrayList<>(topologyData);
            dif2.removeAll(nd);
            dif.removeAll(dif2);
            dif.addAll(dif2);

            if (!dif.isEmpty()) {
                for (String d : dif) {
                    //without neigbor data of d is impossible create d ?
                    if (getNetworkdata().containsKey(d)) {
                        List<String> neigdiff = (ArrayList) getNetworkdata().get(d);
                        String min;
                        min = env.getMinimumId(neigdiff, d, this);
                        //I'm minimum, I will create node and say others that connect with it
                        if (min.equals(getName())) {
                            // Create node and say neighbours that connect with it
                            env.createNewNode(this, d, neigdiff);
                        }
                    }
                }
            }
        } else if (getNetworkdata().isEmpty()) {
            getNetworkdata().put(getName(), topologyData);
        }
    }

    /**
     * @return memoryconsumption by node in bytes
     */
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

}
