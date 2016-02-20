package unalcol.agents.NetworkSim.environment;

import java.util.Hashtable;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;
import unalcol.agents.simulate.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.apache.commons.collections15.Transformer;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;

public class NetworkEnvironment extends Environment {

    public static String msg = null;
    public int[][] structure = null;
    public SimpleLanguage language = null;
    Date date;
    Hashtable<String, ArrayList> mbuffer = new Hashtable();
    public Graph<GraphElements.MyVertex, String> topology;
    GraphElements.MyVertex currentNode = null;
    String currentEdge = null;
    String lastactionlog;
    public ArrayList<GraphElements.MyVertex> visitedNodes = new ArrayList();
    public ArrayList<GraphElements.MyVertex> locationAgents = null;
    
    
    BasicVisualizationServer<GraphElements.MyVertex, String> vv;

    void setVV(BasicVisualizationServer<GraphElements.MyVertex, String> v) {
        vv = v;
    }
    // Transformer maps the vertex number to a vertex property
    Transformer<GraphElements.MyVertex, Paint> vertexColor = new Transformer<GraphElements.MyVertex, Paint>() {
        @Override
        public Paint transform(GraphElements.MyVertex i) {
            System.out.println("callllll" + currentNode);
           
            if (getVisitedNodes().contains(i)) {
                return Color.BLUE;
            }
            return Color.RED;
        }
    };

    Transformer<String, Paint> edgeColor = new Transformer<String, Paint>() {
        @Override
        public Paint transform(String i) {
            //System.out.println("callllll" + currentNode);
            if (i.equals(currentEdge)) {
                return Color.YELLOW;
            }
            return Color.BLACK;
        }
    };

    public int getRowsNumber() {
        return structure.length;
    }

    public int getColumnsNumber() {
        return structure[0].length;
    }

    public boolean act(Agent agent, Action action) {
        boolean flag = (action != null);
        MobileAgent a = (MobileAgent) agent;
        ActionParameters ac = (ActionParameters) action;
        currentNode = a.getLocation();
        visitedNodes.add(currentNode);
        getLocationAgents().set(a.getId(), a.getLocation());
        //(GraphElements.MyVertex) a.getAttribute("ID");
        //System.out.println("cn" + currentNode);
//        vv.repaint();
        String log;

        if (flag) {
            //Agents can be put to Sleep for some ms
            //sleep is good is graph interface is on
            agent.sleep(10);

            String act = action.getCode();
            String msg = null;

            /**
             * 0- "move"
             */
            /* @TODO: Detect Stop Conditions for the algorithm */
            switch (language.getActionIndex(act)) {
                case 0: // move
                    GraphElements.MyVertex v = (GraphElements.MyVertex) ac.getAttribute("location");
                    a.setLocation(v);
    
                    ArrayList<Object> copy = new ArrayList<>(a.getData());
                    Iterator<Object> it = copy.iterator();
                    /*while(it.hasNext()){
                        Object x = it.next();
                        if(x == null){
                            System.out.println("error!");
                        }
                        if (!v.getData().contains(x)) {
                            v.getData().add(x);
                        }
                    }*/
                    
                    a.setRound(a.getRound() + 1);
                    
                    if(a.getData().size() == getTopology().getVertexCount()){
                        System.out.println("complete" + a.getRound());
                        a.die();
                    }
                    break;
                default:
                    msg = "[Unknown action " + act
                            + ". Action not executed]";
                    System.out.println(msg);
                    break;
            }
        }
        setChanged();
        notifyObservers();
        return flag;
    }

    @Override
    public Percept sense(Agent agent) {
        MobileAgent anAgent = (MobileAgent) agent;
        Percept p = new Percept();
        //System.out.println("sense - topology " + topology);
        //Load neighbors 
        p.setAttribute("neighbors", getTopology().getNeighbors(anAgent.getLocation()));
        
        //Load data in Agent
        //clone ArrayList
        ArrayList<Object> copy = new ArrayList<>(anAgent.getLocation().getData());
        System.out.println("copy" + copy);
        Iterator<Object> it = copy.iterator();
        while(it.hasNext()){
            Object x = it.next();
            if(x == null){
                System.out.println("error 2!");
            }
            if (!anAgent.getData().contains(x)) {
                anAgent.getData().add(x);
            }
        }
        
        System.out.println("agent info size:" + anAgent.getData().size());
        return p;
    }

    public NetworkEnvironment(Vector<Agent> _agents, SimpleLanguage _language, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents);
        int n = _agents.size();
        locationAgents = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            MobileAgent ag = (MobileAgent) _agents.get(i);
            locationAgents.add(new GraphElements.MyVertex("null"));
            //System.out.println("creating buffer id" + ag.getAttribute("ID"));

        }
        language = _language;
        date = new Date();
        topology = gr;
        //r = new reportPajeFormat();
        //r.addObserver(this);
    }


    public Vector<Action> actions() {
        Vector<Action> acts = new Vector<Action>();
        int n = language.getActionsNumber();
        for (int i = 0; i < n; i++) {
            acts.add(new Action(language.getAction(i)));
        }
        return acts;
    }

    @Override
    public void init(Agent agent) {
        MobileAgent sim_agent = (MobileAgent) agent;
        //@TODO: Any special initialization processs of the environment
    }

    public String getLog() {
        return lastactionlog;
    }

    public void updateLog(String event, String log) {
        Date datenow = new Date();
        long diff = datenow.getTime() - date.getTime();
        //long diffSeconds = diff / 1000 % 60;
        lastactionlog = event + (String.valueOf(diff / 1000)) + " " + log;
        setChanged();
        notifyObservers();
    }

    private void returnOutput(String pid, Hashtable out) {
        controlBoard.getInstance().addOutput(pid, out);
    }

    /**
     * @return the topology
     */
    public Graph<GraphElements.MyVertex, String> getTopology() {
        return topology;
    }

    /**
     * @param topology the topology to set
     */
    public void setTopology(Graph<GraphElements.MyVertex, String> topology) {
        this.topology = topology;
    }

    /**
     * @return the visitedNodes
     */
    public ArrayList<GraphElements.MyVertex> getVisitedNodes() {
        return visitedNodes;
    }

    /**
     * @param visitedNodes the visitedNodes to set
     */
    public void setVisitedNodes(ArrayList<GraphElements.MyVertex> visitedNodes) {
        this.visitedNodes = visitedNodes;
    }

    public void not() {
        setChanged();
        notifyObservers();
    }

    /**
     * @return the locationAgents
     */
    public ArrayList<GraphElements.MyVertex> getLocationAgents() {
        return locationAgents;
    }

    /**
     * @param locationAgents the locationAgents to set
     */
    public void setLocationAgents(ArrayList<GraphElements.MyVertex> locationAgents) {
        this.locationAgents = locationAgents;
    }

}
