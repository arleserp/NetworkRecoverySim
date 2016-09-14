package unalcol.agents.NetworkSim.environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;
import unalcol.agents.NetworkSim.MotionProgramSimpleFactory;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.SimulationParameters;
import static unalcol.agents.NetworkSim.environment.NetworkEnvironmentReplication.setTotalAgents;

public class NetworkEnvironmentPheromoneReplication extends NetworkEnvironmentReplication {

    public NetworkEnvironmentPheromoneReplication(Vector<Agent> _agents, SimpleLanguage _language, Graph<GraphElements.MyVertex, String> gr) {
        super(_agents, _language, gr);
    }

    @Override
    public boolean act(Agent agent, Action action) {
        agent.sleep(3000);
        if (agent instanceof MobileAgent) {
            boolean flag = (action != null);
            MobileAgent a = (MobileAgent) agent;
            ActionParameters ac = (ActionParameters) action;
            //System.out.println("cn" + currentNode);

            currentNode = a.getLocation();
            a.setPrevLocation(a.getLocation());
            visitedNodes.add(currentNode);

            if (a.getLocation() != null) {
                getLocationAgents().put(a.getId(), a.getLocation());
            }
            synchronized (a.getLocation().getData()) {
                //Get data from agent and put information in node
                for (Object data : a.getData()) {
                    if (!a.getLocation().getData().contains(data)) {
                        a.getLocation().getData().add(data);
                    }
                }
                //a.getLocation().getData().removeAll(a.getData());
            }

            //System.out.println("vertex" + currentNode.getData().size());
            // Communication among agents 
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

            //System.out.println("a id:" + a.getId());
            String[] inbox = NetworkMessageBuffer.getInstance().getMessage(a.getId());

            int old_size = a.getData().size();
            int new_size = 0;
            //inbox: id | infi 
            if (inbox != null) {
                a.incMsgRecv();
                //System.out.println("recibe");
                /*if (a.getIdFather() == Integer.valueOf(inbox[0])) {
                    System.out.println("My father is alive. Freeing memory");
                    a.die();
                    increaseAgentsDie();
                    getLocationAgents().put(a.getId(), null);
                    a.setLocation(null);
                    setChanged();
                    notifyObservers();
                    return flag;

                }*/
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

                String act = action.getCode();
                String msg = null;

                /**
                 * 0- "move"
                 */
                /* @TODO: Detect Stop Conditions for the algorithm */
                switch (language.getActionIndex(act)) {
                    case 0: // move
                        //a.setPrevLocation(a.getLocation()); //Set previous location
                        System.out.println("a antes" + a.getLocation());
                        GraphElements.MyVertex v = (GraphElements.MyVertex) ac.getAttribute("location");
                        a.setLocation(v);
                        System.out.println("a despues" + a.getLocation());
                        a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                        a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));
                        a.setRound(a.getRound() + 1);

                        boolean complete = false;
                        if (a.getData().size() == topology.getVertexCount()) {
                            complete = true;

                        }

                        if (getRoundComplete() == -1 && complete) {
                            System.out.println("complete! round" + a.getRound());
                            setRoundComplete(a.getRound());
                            setIdBest(a.getId());
                            updateWorldAge();
                        }
                        break;
                    case 1: //die
                        System.out.println("Agent " + a.getId() + "has failed");
                        a.die();
                        increaseAgentsDie();
                        getLocationAgents().put(a.getId(), null);
                        a.setLocation(null);
                        setChanged();
                        notifyObservers();
                        return false;
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
            //System.out.println("wat" + a.getId());
            return flag;
        }
        if (agent instanceof Node) {
            Node n = (Node) agent;
            n.incRounds();
            evaluateAgentCreation(n);

//            n.incRoundsWithoutAck();
            for (Agent ag : n.getCurrentAgents()) {
                MobileAgent a = (MobileAgent) ag;
                if (a.getLocation() != null && a.getPrevLocation() != null) {
                    if (a.getPrevLocation().equals(a.getLocation())) {
                        //n.getResponsibleAgents().put(a.getId(), n.getRounds());
                    } else {
                        String[] message = new String[2]; //msg: [from|msg]
                        //if(a.status != Action.DIE){

                        message[0] = n.getVertex().getName();
                        message[1] = String.valueOf(a.getId());
                        NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), message);
                        System.out.println("I am node" + n.getVertex().getName() +" and send a message to node" + a.getPrevLocation().getName());
                        n.getResponsibleAgents().put(a.getId(), n.getRounds());
                        System.out.println("I am responsible for agent" + a.getId());
                        //}
                    }
                } else {

                }
            }

            String[] inbox;

            while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {

                //inbox: node | agent
                if (inbox != null) {
                    n.incMsgRecv();
                    //n.setRoundsWithoutAck(0);
                    System.out.println("Node " + n.getVertex().getName() + " recv message from: " + inbox[0]);
                    //System.out.println("my "+ a.getData().size());
                    int agentId = Integer.valueOf(inbox[1]);
                    n.getResponsibleAgents().remove(agentId);
                    System.out.println("node " + n.getVertex().getName() + " is responsible for " + n.getResponsibleAgents());
                }
            }

        }
        return false;
    }

    public void evaporatePheromone() {
        for (GraphElements.MyVertex v : topology.getVertices()) {
            //System.out.println(v.toString() + "before:" + v.getPh());
            v.setPh(v.getPh() - v.getPh() * 0.001f);
            //System.out.println(v.toString() + "after:" + v.getPh());
        }
    }

    //Example: It is better handshake protocol. J. Gomez
    public void evaluateAgentCreation(Node n) {
        //System.out.println("n get respo" + n.getVertex().getName() + ", " + n.getResponsibleAgents());
        if (n.getResponsibleAgents().isEmpty()) {
            //System.out.println("empty");
            n.setRoundsWithOutVisit(0);
            n.setPfCreate(0);
        } else {
            synchronized (NetworkEnvironmentPheromoneReplication.class) {
                Iterator<Map.Entry<Integer, Integer>> iter = n.getResponsibleAgents().entrySet().iterator();
                System.out.println("hashmap" + n.getResponsibleAgents());
                while (iter.hasNext()) {
                    Map.Entry<Integer, Integer> Key = iter.next();
                    int k = Key.getKey();
                    if (n.getRounds() - n.getResponsibleAgents().get(k) > 2) { //this is not the expresion
                        System.out.println("entra n rounds:" + n.getRounds() + ", n" + n.getResponsibleAgents().get(k));

                        //if (Math.random() < n.getPfCreate()) {
                        System.out.println("create new agent instance..." + n.getVertex().getName() + " n pf create: " + n.getPfCreate());
                        AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);

                        int newAgentID = agents.size();
                        MobileAgent a = new MobileAgent(program, newAgentID);

                        //System.out.println("creating buffer id" + newAgentID);
                        NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

                        //getLocationAgents().add(new GraphElements.MyVertex("null"));
                        a.setId(newAgentID);
                        a.setData(new ArrayList(n.getVertex().getData()));

                        a.setIdFather(n.getResponsibleAgents().get(k));
                        a.setRound(super.getAge());
                        this.agents.add(a);

                        a.live();
                        Thread t = new Thread(a);
                        a.setThread(t);
                        a.setLocation(n.getVertex());
                        a.setPrevLocation(n.getVertex());
                        a.setArchitecture(this);
                        //System.out.println("this" + this);
                        setTotalAgents(getTotalAgents() + 1);
                        t.start();
                        //n.getResponsibleAgents().remove(k);
                        iter.remove();
                        System.out.println("end creation of agent" + newAgentID);
                    }

                }
            }
        }

    }

    /* if (!n.getCurrentAgents().isEmpty()) {
            n.setRoundsWithOutVisit(0);
            n.setPfCreate(0);
        } else {
            n.addRoundsWithOutVisit();
            if (n.getRoundsWithOutVisit() > 190) {
                n.setPfCreate(1);
                System.out.println("round without visit node:" + n.getVertex().getName() + " rounds: " + n.getRoundsWithOutVisit());
            }
        }

        synchronized (NetworkEnvironmentReplication.class) {
            if (Math.random() < n.getPfCreate()) {
                System.out.println("create new agent instance..." + n.getVertex().getName());
                AgentProgram program = MotionProgramSimpleFactory.createMotionProgram(SimulationParameters.pf, SimulationParameters.motionAlg);

                int newAgentID = agents.size();
                MobileAgent a = new MobileAgent(program, newAgentID);

                //System.out.println("creating buffer id" + newAgentID);
                NetworkMessageBuffer.getInstance().createBuffer(newAgentID);

                //getLocationAgents().add(new GraphElements.MyVertex("null"));
                a.setId(newAgentID);
                a.setData(new ArrayList(n.getVertex().getData()));

                a.setIdFather(n.getVertex().getLastVisitedAgent());
                a.setRound(super.getAge());
                this.agents.add(a);

                a.live();
                Thread t = new Thread(a);
                a.setThread(t);
                a.setLocation(n.getVertex());
                a.setArchitecture(this);
                //System.out.println("this" + this);
                setTotalAgents(getTotalAgents() + 1);
                t.start();

                System.out.println("end creation of agent" + newAgentID);
                n.setRoundsWithOutVisit(0);
            }

        }*/
    //}
}
