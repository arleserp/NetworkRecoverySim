/*
 * GraphElements.java
 *
 * Created on March 21, 2007, 9:57 AM
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package unalcol.agents.NetworkSim;

import java.io.Serializable;
import java.util.ArrayList;
import org.apache.commons.collections15.Factory;

/**
 *
 * @author Dr. Greg M. Bernstein
 */
public class GraphElements {

    public enum States {
        BUSY, FREE
    }

    /**
     * Creates a new instance of GraphElements
     */
    public GraphElements() {
    }

    public static class MyVertex implements Serializable {

        private String name;
        private boolean packetSwitchCapable;
        private boolean tdmSwitchCapable;
        private ArrayList data;
        private float ph = 0.5f;
        private ArrayList<MobileAgent> agents;
        private String status;
        private int lastTimeVisited;
        private int lastVisitedAgent;
        private int lastAgentTime;
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

        public boolean isPacketSwitchCapable() {
            return packetSwitchCapable;
        }

        public void setPacketSwitchCapable(boolean packetSwitchCapable) {
            this.packetSwitchCapable = packetSwitchCapable;
        }

        public boolean isTdmSwitchCapable() {
            return tdmSwitchCapable;
        }

        public void setTdmSwitchCapable(boolean tdmSwitchCapable) {
            this.tdmSwitchCapable = tdmSwitchCapable;
        }

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
            setLastAgentTime(lastAgentTime);
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

        private void setLastAgentTime(int lastAgentTime) {
            this.lastAgentTime = lastAgentTime;
        }
    }

    public static class MyEdge {

        private double capacity;
        private double weight;
        private String name;

        public MyEdge(String name) {
            this.name = name;
        }

        public double getCapacity() {
            return capacity;
        }

        public void setCapacity(double capacity) {
            this.capacity = capacity;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
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
    }

    // Single factory for creating Vertices...
    public static class MyVertexFactory implements Factory<MyVertex> {

        private static int nodeCount = 0;
        private static boolean defaultPSC = false;
        private static boolean defaultTDM = true;
        private static MyVertexFactory instance = new MyVertexFactory();

        private MyVertexFactory() {
        }

        public static MyVertexFactory getInstance() {
            return instance;
        }

        public GraphElements.MyVertex create() {
            String name = "Node" + nodeCount++;
            MyVertex v = new MyVertex(name);
            v.setPacketSwitchCapable(defaultPSC);
            v.setTdmSwitchCapable(defaultTDM);
            return v;
        }

        public static boolean isDefaultPSC() {
            return defaultPSC;
        }

        public static void setDefaultPSC(boolean aDefaultPSC) {
            defaultPSC = aDefaultPSC;
        }

        public static boolean isDefaultTDM() {
            return defaultTDM;
        }

        public static void setDefaultTDM(boolean aDefaultTDM) {
            defaultTDM = aDefaultTDM;
        }
    }

    // Singleton factory for creating Edges...
    public static class MyEdgeFactory implements Factory<MyEdge> {

        private static int linkCount = 0;
        private static double defaultWeight;
        private static double defaultCapacity;

        private static MyEdgeFactory instance = new MyEdgeFactory();

        private MyEdgeFactory() {
        }

        public static MyEdgeFactory getInstance() {
            return instance;
        }

        public GraphElements.MyEdge create() {
            String name = "Link" + linkCount++;
            MyEdge link = new MyEdge(name);
            link.setWeight(defaultWeight);
            link.setCapacity(defaultCapacity);
            return link;
        }

        public static double getDefaultWeight() {
            return defaultWeight;
        }

        public static void setDefaultWeight(double aDefaultWeight) {
            defaultWeight = aDefaultWeight;
        }

        public static double getDefaultCapacity() {
            return defaultCapacity;
        }

        public static void setDefaultCapacity(double aDefaultCapacity) {
            defaultCapacity = aDefaultCapacity;
        }

    }

}
