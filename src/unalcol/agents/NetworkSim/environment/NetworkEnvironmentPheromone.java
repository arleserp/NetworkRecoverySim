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

public class NetworkEnvironmentPheromone extends NetworkEnvironment {

    public NetworkEnvironmentPheromone(Vector<Agent> _agents, SimpleLanguage _language, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents, _language, gr);
    }



    @Override
    public boolean act(Agent agent, Action action) {
        boolean flag = (action != null);
        MobileAgent a = (MobileAgent) agent;
        ActionParameters ac = (ActionParameters) action;
        //System.out.println("cn" + currentNode);
        visitedNodes.add(currentNode);
        getLocationAgents().set(a.getId(), a.getLocation());

        if (flag) {
            //Agents can be put to Sleep for some ms
            //sleep is good is graph interface is on
            agent.sleep(3);

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
                    while(it.hasNext()){
                        Object x = it.next();
                        if (!v.getData().contains(x)) {
                            v.getData().add(x);
                        }
                    }
                    
                    a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                    a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone()- a.getLocation().getPh()));
                   
                    a.setRound(a.getRound() + 1);

                    if(a.getData().size() == topology.getVertexCount()){
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
        return flag;
    }










}
