package graphutil;

import java.util.ArrayList;
import mobileagents.MobileAgent;

/**
 * Utility class to define vertices This class define vertex factory and node
 * information regarding node id data in a node, pheromone in node and mobile
 * agents in a determined node.
 *
 * @author Arles Rodríguez
 */
public class MyVertex implements Comparable<MyVertex> {

    private String name;
    private ArrayList data;
    private float ph = 0.5f;
    private ArrayList<MobileAgent> agents;
    private String status;
    private int lastTimeVisited;
    private int lastVisitedAgent;
    private ArrayList lastAgentInfo;

    int State;

    public MyVertex(String name) {
        this.name = name;
        agents = new ArrayList<>();
        lastTimeVisited = -1;
        lastVisitedAgent = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setState(int s) {
        State = s;
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
     * @return the ph
     */
    public float getPh() {
        return ph;
    }

    /**
     * @param ph the ph to set
     */
    public void setPh(float ph) {
        this.ph = ph;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public void saveAgentInfo(ArrayList info, int agentId, int roundNumber, int lastAgentTime) {
        setLastAgentInfo(new ArrayList(info));
        setLastVisitedAgent(agentId);
        setLastTimeVisited(roundNumber);
    }

    /**
     * @return the lastTimeVisited
     */
    public int getLastTimeVisited() {
        return lastTimeVisited;
    }

    /**
     * @param lastTimeVisited the lastTimeVisited to set
     */
    public void setLastTimeVisited(int lastTimeVisited) {
        this.lastTimeVisited = lastTimeVisited;
    }

    /**
     * @return the lastVisitedAgent
     */
    public int getLastVisitedAgent() {
        return lastVisitedAgent;
    }

    /**
     * @param lastVisitedAgent the lastVisitedAgent to set
     */
    public void setLastVisitedAgent(int lastVisitedAgent) {
        this.lastVisitedAgent = lastVisitedAgent;
    }

    /**
     * @return the lastAgentInfo
     */
    public ArrayList getLastAgentInfo() {
        return lastAgentInfo;
    }

    /**
     * @param lastAgentInfo the lastAgentInfo to set
     */
    public void setLastAgentInfo(ArrayList lastAgentInfo) {
        this.lastAgentInfo = lastAgentInfo;
    }

    /**
     * Compare two vertex named p#id 
     * @param f1  
     * @param f2
     * @return compareTo of vertex
     */
    @Override
    public int compareTo(MyVertex t) {        
        return Integer.valueOf(this.getName().substring(1)).compareTo(Integer.valueOf(t.getName().substring(1)));                
    }
}
