package environment;

import java.util.Hashtable;
import unalcol.agents.simulate.util.*;
import unalcol.agents.*;
import unalcol.agents.simulate.*;
import java.util.Vector;
import edu.uci.ics.jung.graph.*;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import mobileagents.MobileAgent;
import staticagents.Node;

public abstract class NetworkEnvironment extends Environment {

    public List<MyVertex> visitedNodes = Collections.synchronizedList(new ArrayList());
    public HashMap<MobileAgent, MyVertex> locationAgents = null;
    private HashMap<MyVertex, ArrayList<Agent>> nodesAgents = null;
    private boolean finished = false;
    private AtomicInteger round = new AtomicInteger(0);
    public static int agentsDie = 0;
    private static int totalMobileAgents = 0;

    private static HashMap<String, Long> networkDelays; //used to administrate delays in messages

    /**
     *
     * @param _agents
     * @param _language
     * @param gr
     */
    public NetworkEnvironment(Vector<Agent> _agents, Graph gr) {
        super(_agents);
        for (Agent a : this.getAgents()) {
            if (a instanceof MobileAgent) {
                totalMobileAgents++;
            }
        }
        TopologySingleton.getInstance().init(gr);
        locationAgents = new HashMap<>();
    }

    /**
     * @return the totalAgents
     */
    public int getTotalAgents() {
        totalMobileAgents = 0;
        Vector<Agent> agents1 = (Vector<Agent>) this.getAgents().clone();
        for (Agent a : agents1) {
            if (a instanceof MobileAgent) {
                totalMobileAgents++;
            }
        }
        return totalMobileAgents;
    }

    /**
     * @param aTotalAgents the totalAgents to set
     */
    public static void setTotalAgents(int aTotalAgents) {
        totalMobileAgents = aTotalAgents;
    }

    public abstract boolean act(Agent agent, Action action);

    @Override
    public Percept sense(Agent agent) {
        Percept p = new Percept();

        if (agent instanceof MobileAgent) {
            MobileAgent a = (MobileAgent) agent;

            if (a.status != Action.DIE && getTopology().containsVertex(a.getLocation())) {
                p.setAttribute("neighbors", getTopology().getNeighbors(a.getLocation()));
                a.getData().removeAll(a.getLocation().getData());
                a.getData().addAll(a.getLocation().getData());
                a.getLocation().saveAgentInfo(a.getData(), a.getId(), a.getRound(), getAge());
            } else {
                System.out.println("Agent is removed from node that failed before:" + a.getId() + " status is dead: " + (a.status == Action.DIE) + ", loc" + a.getLocation());
                p.setAttribute("nodedeath", a.getLocation());
            }
        }
        if (agent instanceof Node) {
            Node n = (Node) agent;
            try {
                ArrayList<Agent> agentNode = new ArrayList<>();
                synchronized (NetworkEnvironment.class) {
                    ArrayList<Agent> agentsCopy = new ArrayList(getAgents());
                    for (Agent a : agentsCopy) {
                        if (a instanceof MobileAgent) {
                            MobileAgent ma = (MobileAgent) a;
                            if (ma.getLocation() != null && ma.getLocation().getName().equals(n.getVertex().getName())) {
                                agentNode.add(ma);
                            }
                        }
                    }
                }
                n.setCurrentAgents(agentNode);
            } catch (Exception e) {
                System.out.println("Exception loading agents in this location" + e.getMessage() + " node:" + n.getVertex().getName());
            }
        }
        return p;
    }

    /**
     * increases the number of agents with failures
     */
    public void increaseAgentsDie() {
        synchronized (NetworkEnvironment.class) {
            NetworkEnvironment.agentsDie++;
        }
    }

    /**
     * @return number of agents with failures
     */
    public int getAgentsDie() {
        synchronized (NetworkEnvironment.class) {
            return agentsDie;
        }
    }

    public int getAgentsLive() {
        int agentsLive = 0;
        synchronized (NetworkEnvironment.class) {
            Vector<Agent> agentsClone = (Vector) this.getAgents().clone();
            for (Agent a : agentsClone) {
                if (a instanceof MobileAgent) {
                    if (((MobileAgent) a).status != Action.DIE) {
                        agentsLive++;
                    }
                }
            }
        }
        return agentsLive;
    }

    @Override
    public void init(Agent agent) {
        //@TODO: Any special initialization processs of the environment
    }

    /**
     * @return the topology
     */
    public Graph<MyVertex, String> getTopology() {
        return TopologySingleton.getInstance().getTopology();
    }

    /**
     * @param topology the topology to set
     */
    /*public void setTopology(Graph<MyVertex, String> topology) {
        this.topology = topology;
    }*/
    /**
     * @return the visitedNodes
     */
    public List<MyVertex> getVisitedNodes() {
        return visitedNodes;
    }

    /**
     * @param visitedNodes the visitedNodes to set
     */
    public void setVisitedNodes(ArrayList<MyVertex> visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    public void not() {
        setChanged();
        notifyObservers();
    }

    /**
     * @return the locationAgents
     */
    public HashMap<MobileAgent, MyVertex> getLocationAgents() {
        synchronized (NetworkEnvironment.class) {
            return locationAgents;
        }
    }

    /**
     * @param locationAgents the locationAgents to set
     */
    public void setLocationAgents(HashMap<MobileAgent, MyVertex> locationAgents) {
        this.locationAgents = locationAgents;
    }

    public ArrayList<Integer> getAgentNeighbors(MobileAgent x) {
        ArrayList n = new ArrayList();
        System.out.println("getLoc sizeeeeeeeeeee:" + getLocationAgents().size());
        for (int i = 0; i < getLocationAgents().size(); i++) {
            if (i != x.getId() && x.getLocation() != null && x.getLocation().equals(getLocationAgents().get(i))) {
                n.add(i);
            }
        }
        return n;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public synchronized void updateWorldAge() {
        round.incrementAndGet();
        setChanged();
        notifyObservers();
    }

    /**
     * @return the age
     */
    public int getAge() {
        return round.get();
    }

    /**
     * @return the nodesAgents
     */
    public HashMap<MyVertex, ArrayList<Agent>> getNodesAgents() {
        return nodesAgents;
    }

    /**
     * @param nodesAgents the nodesAgents to set
     */
    public void setNodesAgents(HashMap<MyVertex, ArrayList<Agent>> nodesAgents) {
        this.nodesAgents = nodesAgents;
    }

    public boolean areAllAgentsDead() {
        synchronized (NetworkEnvironment.class) {
            Vector cloneAgents = (Vector) this.getAgents().clone();
            Iterator itr = cloneAgents.iterator();
            while (itr.hasNext()) {
                Agent a = (Agent) itr.next();
                if (a instanceof MobileAgent) {
                    if (((MobileAgent) a).status != Action.DIE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public abstract int getNodesAlive();

    public int getAgentsAlive() {
        int agentsAlive = 0;
        synchronized (NetworkEnvironment.class) {
            Vector cloneAgents = (Vector) this.getAgents().clone();
            Iterator itr = cloneAgents.iterator();

            while (itr.hasNext()) {
                Agent a = (Agent) itr.next();
                if (a instanceof MobileAgent) {
                    if (((MobileAgent) a).status != Action.DIE) {
                        agentsAlive++;
                    }
                }
            }
        }
        return agentsAlive;
    }

    public HashMap<String, Long> getNetworkDelays() {
        return networkDelays;
    }

    public void setNetworkDelays(HashMap<String, Long> in) {
        this.networkDelays = in;
    }

    public abstract boolean isOccuped(MyVertex v);

    public abstract void validateNodesAlive();

    public abstract List<Node> getNodes();
}
