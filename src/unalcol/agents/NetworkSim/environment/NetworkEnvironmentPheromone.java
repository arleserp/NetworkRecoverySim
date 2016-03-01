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
        currentNode = a.getLocation();
        visitedNodes.add(currentNode);

        getLocationAgents().set(a.getId(), a.getLocation());

        //detect other agents in network
        ArrayList<Integer> agentNeighbors = getAgentNeighbors(a);
        //System.out.println(a.getId() + "agentNeigbors" + agentNeighbors);

        //serialize messages 
        String[] message = new String[2]; //msg: [from|msg]
        message[0] = String.valueOf(a.getId());
        message[1] = ObjectSerializer.serialize(a.getData());

        //for each neighbor send a message
        for (Integer idAgent : agentNeighbors) {
            //System.out.println("a" + a.getId() + "send message to" + idAgent);
            NetworkMessageBuffer.getInstance().putMessage(idAgent, message);
            a.incMsgSend();
        }

        String[] inbox = NetworkMessageBuffer.getInstance().getMessage(a.getId());

        int old_size = a.getData().size();
        int new_size = 0;
        //inbox: id | infi 
        if (inbox != null) {
            a.incMsgRecv();
            //System.out.println("a" + a.getId() + "recv message from: " + inbox[0]);
            //System.out.println("my "+ a.getData().size());
            ArrayList senderInf = (ArrayList) ObjectSerializer.deserialize(inbox[1]);
            //System.out.println("received" + senderInf.size());
            // Join ArrayLists

            a.getData().removeAll(senderInf);
            a.getData().addAll(senderInf);
            new_size = a.getData().size();

            if (old_size < new_size) {
                a.setPheromone(1.0f);
            }
            //System.out.println("joined" + a.getData().size());
        }

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
                    a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                    a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));
                    a.setRound(a.getRound() + 1);

                    boolean complete = false;
                    if (a.getData().size() == topology.getVertexCount()) {
                        complete = true;
                    }

                    if (getRoundComplete() == -1 && complete) {
                        //System.out.println("complete! round" + a.getRound());
                        setRoundComplete(a.getRound());
                        setIdBest(a.getId());
                        updateWorldAge();
                    }
                    break;
                default:
                    msg = "[Unknown action " + act
                            + ". Action not executed]";
                    System.out.println(msg);
                    break;
            }
        }

        updateWorldAge();
        setChanged();
        notifyObservers();
        return flag;
    }

    public void evaporatePheromone() {
        for (GraphElements.MyVertex v : topology.getVertices()) {
            //System.out.println(v.toString() + "before:" + v.getPh());
            v.setPh(v.getPh() - v.getPh() * 0.001f);
            //System.out.println(v.toString() + "after:" + v.getPh());
        }
    }

}
