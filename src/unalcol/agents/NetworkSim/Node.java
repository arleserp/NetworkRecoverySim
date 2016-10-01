/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {

    private GraphElements.MyVertex v;
    private ArrayList<Agent> currentAgents;
    private HashMap<Integer, Integer> responsibleAgents;
    private double pfCreate;
    private int roundsWithOutVisit;
    private int roundsWithoutAck;
    private int nMsgSend;
    private int nMsgRecv;
    private int rounds;
    private HashMap<Integer, Integer> lastAgentArrival;
    private HashMap<Integer, Integer> lastMessageArrival;
    private ArrayList<Integer> timeout;
    private int amountRounds;

    public Node(AgentProgram _program, GraphElements.MyVertex ve) {
        super(_program);
        this.v = ve;
        currentAgents = new ArrayList<>();
        responsibleAgents = new HashMap<>();
        lastAgentArrival = new HashMap<>();
        lastMessageArrival = new HashMap<>();
        timeout = new ArrayList();
        //timeout.add(20);
        amountRounds = 5;
    }

    public GraphElements.MyVertex getVertex() {
        return v;
    }

    public void addAgent(Agent a) {
        getCurrentAgents().add(a);
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
     * @return the currentAgents
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
     * @param roundsWithOutVisit the roundsWithOutVisit to set
     */
    public void addRoundsWithOutVisit() {
        roundsWithOutVisit++;
    }

    /**
     * @return the responsibleAgents
     */
    public HashMap<Integer, Integer> getResponsibleAgents() {
        return responsibleAgents;
    }

    /**
     * @param responsibleAgents the responsibleAgents to set
     */
    public void setResponsibleAgents(HashMap<Integer, Integer> responsibleAgents) {
        this.responsibleAgents = responsibleAgents;
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
        synchronized (Node.class) {
            nMsgSend++;
        }
    }

    public void incMsgRecv() {
        synchronized (Node.class) {
            nMsgRecv++;
        }
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
     * @param agentId
     * @return the lastAgentArrival
     */
    public int getLastAgentArrival(int agentId) {
        return getLastAgentArrival().get(agentId);
    }

    /**
     * @param lastAgentArrival the lastAgentArrival to set
     * @param agentId
     */
    public void setLastAgentArrival(int agentId, int nodeAge) {
        this.getLastAgentArrival().put(agentId, nodeAge);
    }

    /**
     * @param agentId
     * @return the lastMessageArrival
     */
    public int getLastMessageArrival(int agentId) {
        return getLastMessageArrival().get(agentId);
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageArrival(int agentId, int nodeAge) {
        if(this.getLastAgentArrival().containsKey(agentId)){
            this.getLastMessageArrival().put(agentId, nodeAge);
        }else{
            System.out.println("rarooooooooooooooooooo");
        }
    }

    public int estimateTimeout() {
        amountRounds++;
        System.out.println("last agent arrival:" + getLastAgentArrival() + ", last message arrival:" + getLastMessageArrival());

        Iterator it = getLastAgentArrival().keySet().iterator();
        while (it.hasNext()) {
            Integer agentId = (Integer) it.next();
            System.out.println("node" + this.getVertex().getName() + "agent id:" + agentId + "  lastAgentArrival:"
                    + getLastAgentArrival().get(agentId) + ", lastMessageArrival:" + getLastMessageArrival().get(agentId));

            if (getLastMessageArrival().containsKey(agentId) && !Objects.equals(lastAgentArrival.get(agentId), lastMessageArrival.get(agentId))
                    && getLastMessageArrival().get(agentId) != 0) {
                System.out.println("add");
                //negative numbers?
                getTimeout().add(Math.abs(getLastMessageArrival().get(agentId) - getLastAgentArrival().get(agentId)));
                System.out.println("antes:"+getLastMessageArrival());
                getLastMessageArrival().remove(agentId);
                System.out.println("despues:"+getLastMessageArrival());
                it.remove();

            }

        }
        System.out.println("las agent arrival after" + getLastAgentArrival() + ", las message arrival:" + getLastMessageArrival());

        if (getTimeout().isEmpty()) {
            return Integer.MAX_VALUE;
        }

        int sum = 0;
        for (int time : getTimeout()) {
            sum += time;
        }

        System.out.println("Node:" + getVertex().getName() + "timeout" + getTimeout() + " avg: " + sum / getTimeout().size());
        return sum / getTimeout().size();
    }

    /**
     * @return the timeout
     */
    public ArrayList<Integer> getTimeout() {
        return timeout;
    }

    /**
     * @return the lastAgentArrival
     */
    public HashMap<Integer, Integer> getLastAgentArrival() {
        return lastAgentArrival;
    }

    /**
     * @return the lastMessageArrival
     */
    public HashMap<Integer, Integer> getLastMessageArrival() {
        return lastMessageArrival;
    }

    /**
     * @param lastMessageArrival the lastMessageArrival to set
     */
    public void setLastMessageArrival(HashMap<Integer, Integer> lastMessageArrival) {
        this.lastMessageArrival = lastMessageArrival;
    }

}
