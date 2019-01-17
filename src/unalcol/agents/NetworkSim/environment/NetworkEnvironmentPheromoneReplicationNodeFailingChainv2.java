package unalcol.agents.NetworkSim.environment;

import unalcol.agents.simulate.util.*;
import unalcol.agents.*;

import java.util.Vector;

import edu.uci.ics.jung.graph.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.NetworkSim.MobileAgent;
import unalcol.agents.NetworkSim.Node;
import unalcol.agents.NetworkSim.SimulationParameters;
import unalcol.agents.NetworkSim.util.StringSerializer;

/**
 * *
 * This class pretends to stablish a better way to have replication in mobile
 * agents. Nodes a, b agent id: 100. departing: a -> b nhops: number of nodes
 * tracking the agents. New variable nhops = 2 hops //constant to maintain a
 * reference of a determined agent. counter a{100} - Agent arrived to b
 * -freeresp to node a Agent a{100, counter = 1} b -> c departing b{100,
 * counter=0} Agent 100 arrived to c - freeresp to node b b{100, counter =1} b
 * resends the message to agent a{100, counter = 2} the counter > nhops 100
 * departs from c then send departing message to c, c{100, counter=0} transmit
 * it to b b{100, counter =1} , a{100, counter = 2} then a deletes reference to
 * 100. warranty that at least two nodes have references of the same agent. See
 * if a lose agents: - loops: + Restart the counter count=0; //be careful.
 *
 * @author Arles
 */
public class NetworkEnvironmentPheromoneReplicationNodeFailingChainv2 extends NetworkEnvironmentPheromoneReplicationNodeFailingChain {

    public NetworkEnvironmentPheromoneReplicationNodeFailingChainv2(Vector<Agent> _agents, SimpleLanguage _language, SimpleLanguage _nlanguage, Graph gr) {
        super(_agents, _language, _nlanguage, gr);
    }

    @Override
    public boolean act(Agent agent, Action action) {
        if (agent instanceof MobileAgent) {
            agent.sleep(50);
            boolean flag = (action != null);
            MobileAgent a = (MobileAgent) agent;

            currentNode = a.getLocation();
            currentNode.setStatus("v");

            try {
                if (a.status == Action.DIE) {
                    System.out.println("death: " + a.getId());
                    return false;
                }
                //System.out.println("1.");
                String act = action.getCode();

                if (language.getActionIndex(act) == 2) {
                    System.out.println("informfailure" + a.getId());
                    setChanged();
                    notifyObservers();
                    return false;
                } else {
                    if (a.status == Action.DIE) {
                        System.out.println("death before send freeresp:" + a.getId());
                        return false;
                    }

                    currentNode = a.getLocation();
                    //System.out.println("2.");
                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                        /*if (a.status == Action.DIE) {
                            System.out.println("death before getNode" + a.getId());
                            return false;
                        }*/

                        // Here is when agents die and failure is not detected!
                        /*if (a.getLocation().getStatus().equals("failed")) {
                            System.out.println("Kill a id: " + a.getId() + " location failed " + a.getLocation());
                            killAgent(a, true);
                            return false;
                        }*/
                        Node c = getNode(a.getLocation().getName());

                        if (c == null) {
                            System.out.println("Agent" + a.getId() + " location has failed " + a.getLocation().getName());
                            killAgent(a, true);
                            return false;
                        }

                        HashMap<String, Object> nodeNet = new HashMap<>();
                        nodeNet.put(a.getLocation().getName(), getTopologyNames(a.getLocation()));
                        HashMap<String, Integer> agentsinNodeNet = new HashMap<>();
                        agentsinNodeNet.put(c.getVertex().getName(), c.getAgentCount());

                        HashMap<String, ConcurrentHashMap<Integer, Integer>> agentsinNodeNetMap = new HashMap<>();
                        agentsinNodeNetMap.put(c.getVertex().getName(), c.getAgentsInNode());
                        a.getLocalAgentsInNetworkHmap().add(agentsinNodeNetMap);
                        a.getLocalAgentsInNetwork().add(agentsinNodeNet);
                        a.getLocalNetwork().add(nodeNet);

                        //}
                        //let only nhops as local information by agent
                        if (a.getLocalNetwork().size() > SimulationParameters.nhops) {
                            List nl = new ArrayList(a.getLocalNetwork().subList(a.getLocalNetwork().size() - SimulationParameters.nhops, a.getLocalNetwork().size()));
                            a.setLocalNetwork(nl);
                        }

                        /*if (a.getLocalAgentsInNetwork().size() > SimulationParameters.nhops) {
                            List aln = new ArrayList(a.getLocalAgentsInNetwork().subList(a.getLocalAgentsInNetwork().size() - SimulationParameters.nhops, a.getLocalAgentsInNetwork().size()));
                            a.setLocalAgentsInNetwork(aln);
                        }

                        if (a.getLocalAgentsInNetwork().size() > SimulationParameters.nhops) {
                            List aln2 = new ArrayList(a.getLocalAgentsInNetwork().subList(a.getLocalAgentsInNetworkHmap().size() - SimulationParameters.nhops, a.getLocalAgentsInNetworkHmap().size()));
                            a.setLocalAgentsInNetworkHmap(aln2);
                        }*/
                        //Sh Send neighbour data to node ex in HashMap format: {p51={p21, p22, p23}, p22={p1, p2}}
                        if (a.status == Action.DIE) {
                            System.out.println("death before network data" + a.getId());
                            return false;
                        }

                        String[] msgnet = new String[5];
                        msgnet[0] = "networkdata";
                        msgnet[1] = String.valueOf(a.getId());
                        StringSerializer s = new StringSerializer();
                        // msgnet[2] = s.serialize(a.getLocalAgentsInNetwork());
                        msgnet[3] = s.serialize(a.getLocalNetwork());
                        // msgnet[4] = s.serialize(a.getLocalAgentsInNetworkHmap());
                        if (a.status == Action.DIE) {
                            System.out.println("death before network data" + a.getId());
                            return false;
                        }
                        c.putMessage(msgnet);
                    }

                    if (a.status == Action.DIE) {
                        System.out.println("death before put data in node" + a.getId());
                        return false;
                    }
                    //getLocationAgents().put(a, a.getLocation());
                    ActionParameters ac = (ActionParameters) action;

                    if (a.status == Action.DIE) {
                        System.out.println("death before getData" + a.getId());
                        return false;
                    }
                    /* Agents will no get data by now
                    synchronized (a.getLocation().getData()) {
                        //Get data from agent and put information in node
                        for (Object data : a.getData()) {
                            if (a.status == Action.DIE) {
                                System.out.println("death before getData" + a.getId());
                                return false;
                            }
                            if (!a.getLocation().getData().contains(data)) {
                                a.getLocation().getData().add(data);
                                a.setPheromone(1.0f); //??? maybe this is not here
                            }
                        }
                    }
                     */
                    if (flag) {
                        //String act = action.getCode();
                        String msg = null;

                        if (a.status == Action.DIE) {
                            System.out.println("death before switch" + a.getId());
                            return false;
                        }
                        /**
                         * 0- "move"
                         */
                        /* @TODO: Detect Stop Conditions for the algorithm */
                        switch (language.getActionIndex(act)) {
                            case 0: // move
                                //System.out.println("a despues" + a.getLocation());
                                boolean complete = false;
                                if (a.getData().size() == getTopology().getVertexCount()) {
                                    complete = true;
                                }

                                if (getRoundComplete() == -1 && complete) {
                                    System.out.println("complete! round" + a.getRound());
                                    setRoundComplete(a.getRound());
                                    setIdBest(a.getId());
                                    //updateWorldAge();
                                }

                                //get new location
                                GraphElements.MyVertex v = (GraphElements.MyVertex) ac.getAttribute("location");
                                float pf = (float) ac.getAttribute("pf");

                                if (v.getStatus().equals("failed")) {
                                    System.out.println("agent " + a.getId() + ", ojo! aca no me puedo mover ni enviar departing!" + v.getName());
                                    return false;
                                } else //Send a message to current node before moving to new destination v
                                //msgnode: "departing"|agentId|FatherId|newDest
                                {
                                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        StringSerializer s = new StringSerializer();
                                        if (!a.getPrevLocation().equals(a.getLocation())) {
                                            String[] msgnoder = new String[5];
                                            msgnoder[0] = "freeresp";
                                            msgnoder[1] = String.valueOf(a.getId());
                                            msgnoder[2] = a.getLocation().getName();
                                            msgnoder[3] = String.valueOf(1); //first hop
                                            msgnoder[4] = s.serialize(new ArrayList<>(a.getLastLocations())); //a.getPrevPrevLocation().getName();
                                            NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), msgnoder);
                                        }
                                    }

                                    //update list with new locations
                                    a.getLastLocations().add(a.getPrevLocation().getName());
                                    if (a.getLastLocations().size() > SimulationParameters.nhops) {
                                        ArrayList<String> nl = new ArrayList(a.getLastLocations().subList(a.getLastLocations().size() - SimulationParameters.nhops, a.getLastLocations().size()));
                                        a.setLastLocations(nl);
                                    }

                                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        StringSerializer s = new StringSerializer();
                                        String[] msgnode = new String[6];
                                        msgnode[0] = "departing";
                                        msgnode[1] = String.valueOf(a.getId());
                                        msgnode[2] = String.valueOf(a.getIdFather());
                                        msgnode[3] = v.getName();
                                        msgnode[4] = String.valueOf(1); // hop inicial
                                        ArrayList<String> x = new ArrayList<>(a.getLastLocations());
                                        msgnode[5] = s.serialize(x);

                                        NetworkNodeMessageBuffer.getInstance().putMessage(a.getLocation().getName(), msgnode);
                                    }
                                }
                                //Agent Fail when moving
                                if (Math.random() < pf) {
                                    //System.out.println("Agent " + a.getId() + "has failed");
                                    killAgent(a, true);
                                    return false;
                                }
                                if (!SimulationParameters.nodeDelay.equals("NODELAY")) {
                                    int nodeDelay = Integer.valueOf(SimulationParameters.nodeDelay);
                                    a.sleep(nodeDelay);
                                }

                                if (a.status == Action.DIE) {
                                    System.out.println("death before send arrive and move" + a.getId());
                                    return false;
                                }
                                //Move agent to a location
                                //Send message arrived to node arrived|id|getPrevLocation
                                if (v.getStatus().equals("failed")) {
                                    System.out.println("ojo! aca no me puedo mover!");
                                    return false;
                                } else {
                                    a.setPrevPrevLocation(a.getPrevLocation());
                                    a.setPrevLocation(a.getLocation());
                                    //here was: a.getLastLocations().add(a.getPrevPrevLocation().getName());
/*
                                    if (a.getLastLocations().size() > SimulationParameters.nhops) {
                                        ArrayList<String> nl = new ArrayList(a.getLastLocations().subList(a.getLastLocations().size() - SimulationParameters.nhops, a.getLastLocations().size()));
                                        a.setLastLocations(nl);
                                    }
                                     */
                                    a.setLocation(v);
                                    a.getLocation().setStatus("visited");
                                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        String[] msgarrived = new String[4];
                                        msgarrived[0] = "arrived";
                                        msgarrived[1] = String.valueOf(a.getId());
                                        msgarrived[2] = String.valueOf(a.getIdFather());
                                        msgarrived[3] = String.valueOf(1);

                                        if (NetworkNodeMessageBuffer.getInstance().putMessage(v.getName(), msgarrived) == false) {
                                            System.out.println("Agent" + a.getId() + " cannot move to node " + v.getName());
                                            killAgent(a, true);
                                            return false;
                                        }
                                    }

                                    //agent.sleep(50);
                                    // getLocationAgents().put(a, a.getLocation());
                                    a.setPheromone((float) (a.getPheromone() + 0.01f * (0.5f - a.getPheromone())));
                                    // System.out.println("a" + a.getId() + " pheromone " + a.getPheromone());
                                    a.getLocation().setPh(a.getLocation().getPh() + 0.01f * (a.getPheromone() - a.getLocation().getPh()));

                                    a.setRound(a.getRound() + 1);
                                    incrementAgentMovements();

                                    if (a.status == Action.DIE) {
                                        System.out.println("death before send freeresp:" + a.getId());
                                        return false;
                                    }

                                    /*if (SimulationParameters.activateReplication.equals("replalgon")) {
                                        if (a.getPrevLocation() != null) {
                                            String[] msgnoder = new String[3];
                                            msgnoder[0] = "freeresp";
                                            msgnoder[1] = String.valueOf(a.getId());
                                            msgnoder[2] = a.getLocation().getName();
                                            NetworkNodeMessageBuffer.getInstance().putMessage(a.getPrevLocation().getName(), msgnoder);
                                        }
                                    }*/
                                    currentNode = v;
                                }
                                //visitedNodes.add(currentNode);
                                break;
                            case 1: //die
                                System.out.println("action: kill ");
                                killAgent(a, true);
                                return false;
                            default:
                                msg = "[Unknown action " + act
                                        + ". Action not executed]";
                                System.out.println(msg);
                                break;
                        }
                    }
                    //updateWorldAge();
                    setChanged();
                    notifyObservers();
                }
                //System.out.println("wat" + a.getId());
                return flag;
            } catch (NullPointerException ex) {
                System.out.println("Killing agent by null exception: " + a.getId() + " message: ");
                //ex.printStackTrace();
                killAgent(a, true);
                return false;
            }
        }
        if (agent instanceof Node) {
            agent.sleep(50);
            Node n = (Node) agent;
            //System.out.println("thread of node " + n.getVertex().getName() + "ph: " + n.getVertex().getPh());
            n.incRounds();
            //System.out.println("node thread:" + n.getVertex().getName());
            String act = action.getCode();

            //Analyse Topology
            if (!n.isProcessing) {
                Thread analyse = new Thread(new ProcessNetworkDataMessages(n, this));
                analyse.start();
                n.isProcessing = true;
            }

            SimpleLanguage nodeLanguage = getNodeLanguage();
            //Actions given by node program 0:communicate 1:fail
            switch (nodeLanguage.getActionIndex(act)) {
                case 0: //Communicate
                    /* A node process messages */
                    String[] inbox;
                    while ((inbox = NetworkNodeMessageBuffer.getInstance().getMessage(n.getVertex().getName())) != null) {

                        //System.out.println("node name:" + n.getVertex().getName());
                        if (SimulationParameters.activateReplication.equals("replalgon")) {
                            //Send a message to current node before moving to new destination v
                            //Send message arrived to node arrived|id|getPrevLocation
                            if (inbox[0].equals("arrived")) {
                                int agentId = Integer.valueOf(inbox[1]);
                                int father = Integer.valueOf(inbox[2]);
                                n.addAgentInNode(agentId, father);
                                /* ArrayList<Integer> repeatedAgents = n.getDuplicatedAgents();
                                for (int id : repeatedAgents) {
                                    removeAgent(id);
                                    //  System.out.println("deeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeell:" + id);
                                    n.deleteAllFollowedReferences(id);
                                }*/
                                //n.printReplicationHops();
                            }
                        }
                        //msgnode: "departing"|agentId|FatherId|newDest
                        if (inbox[0].equals("departing")) {
                            int agentId = Integer.valueOf(inbox[1]);
                            int father = Integer.valueOf(inbox[2]);
                            String dest = inbox[3];
                            int hop = Integer.valueOf(inbox[4]);
                            StringSerializer s = new StringSerializer();
                            ArrayList<String> PrevLocations = new ArrayList((ArrayList<String>) s.deserialize(inbox[5]));

//                            if (SimulationParameters.simMode.contains("noloop") && n.hasFollowedInNodeBefore(agentId)) {
//                                //n.printReplicationHops();
//                                //System.out.println("ya lo tengo" + n.getVertex().getName() + " agent id:" + agentId);
//                                //deberia aqui borrar todos los nodos en la cadena
//                                n.deleteAllFollowedReferences(agentId);
//                                if (PrevLocations.size() - hop > 0) {
//                                    String prevPrevLoc = PrevLocations.get(PrevLocations.size() - hop);
//                                    int tmphop = hop;
//                                    if (tmphop < SimulationParameters.nhopsChain && !prevPrevLoc.equals(n.getVertex().getName())) {
//                                        //System.out.println(n.getVertex().getName() + "resending freeresp to prevprev" + prevPrevLoc);
//                                        tmphop++;
//                                        String[] msgnodera = new String[5];
//                                        msgnodera[0] = "freeresp";
//                                        msgnodera[1] = String.valueOf(agentId);
//                                        msgnodera[2] = n.getVertex().getName();
//                                        msgnodera[3] = String.valueOf(tmphop); //first hop
//                                        msgnodera[4] = inbox[5]; //Todo: review hops number -> probably this is different
//                                        NetworkNodeMessageBuffer.getInstance().putMessageWithNetworkDelay(this, n, prevPrevLoc, msgnodera, tmphop);
//                                    }
//                                }
//                            }
                            //hops greater than one means departing messages from other nodes
                            if (hop > 1) {
                                n.incMsgRecv();
                            }

                            // increase counter 
                            if (!n.getIdCounter().containsKey(agentId)) {
                                n.getIdCounter().put(agentId, 0);
                                n.setFirstDepartingMsgTime(agentId, n.getRounds()); //set last agent depart message in a given hop.
                                n.getFollowedAgents(1).put(agentId, father); //add agent to followed agents vector
                            } else {
                                n.getIdCounter().put(agentId, n.getIdCounter().get(agentId) + 1);
                            }

                            //if counter is equal to nhopsChain means that 
                            // there are two references to agent agentId in other nodes
                            // reference can be deleted.
                            if (n.getIdCounter().get(agentId) == SimulationParameters.nhopsChain - 1) {
                                System.out.println("delete agent" + agentId + "from " + n.getVertex());
                                n.getIdCounter().remove(agentId);
                                n.calculateTimeout(1);
                                if (n.getFollowedAgents(1).containsKey(agentId)) {
                                    n.getFollowedAgents(1).remove(agentId);
                                }
                                n.setLimitDepartingMsgTime(agentId, n.getRounds());

                            }

                            n.getFollowedAgentsLocation(1).put(agentId, inbox[3]); //set as location the current location of agent                                
                            n.addFollowedAgentsPrevLocations(agentId, PrevLocations, 1); // Add previous locations of agents to resend messages
                            n.calculateTimeout(1);
                            n.deleteAgentInNode(agentId);

                            if (PrevLocations.size() - hop > 0) {
                                String prevLoc = PrevLocations.get(PrevLocations.size() - hop);
                                if (hop < SimulationParameters.nhopsChain && !prevLoc.equals(n.getVertex().getName())) {
                                    hop++;
                                    String[] msgnode = new String[6];
                                    msgnode[0] = "departing";
                                    msgnode[1] = String.valueOf(agentId);
                                    msgnode[2] = String.valueOf(father);
                                    msgnode[3] = n.getVertex().getName() + dest;
                                    msgnode[4] = String.valueOf(hop); // hop inicial
                                    msgnode[5] = inbox[5];
                                    NetworkNodeMessageBuffer.getInstance().putMessageWithNetworkDelay(this, n, prevLoc, msgnode, 1);
                                }
                            }
                        }
                        if (inbox[0].equals("freeresp")) {
//                            int agentId = Integer.valueOf(inbox[1]);
//                            String newLocation = inbox[2];
//                            int hop = Integer.valueOf(inbox[3]);
//                            StringSerializer s = new StringSerializer();
//                            ArrayList<String> PrevLocations = (ArrayList<String>) s.deserialize(inbox[4]);
//                            incrementACKAmount();
//                            n.incMsgRecv();
//                            n.setLastMessageFreeResp(agentId, n.getRounds(), newLocation, hop);
//
//                            //for (int i = 1; i <= SimulationParameters.nhops; i++) {
//                            n.calculateTimeout(hop);
//                            //}
//                            if (n.getFollowedAgents(hop).containsKey(agentId)) {
//                                //  n.deleteAllFollowedReferences(agentId);
//                                n.getFollowedAgents(hop).remove(agentId);
//                                //     n.deleteAgentFromRep(hop, agentId);
////                                    n.removeResponsibleAgentsPrevLocations(agentId, hop);
//                            }
//                            //} else {
//                            //    incrementFalsePossitives();
//                            //}
//                            if (PrevLocations.size() - hop > 0) {
//                                String prevPrevLoc = PrevLocations.get(PrevLocations.size() - hop);
//
//                                if (hop < SimulationParameters.nhopsChain && !prevPrevLoc.equals(n.getVertex().getName())) {
//                                    //System.out.println(n.getVertex().getName() + "resending freeresp to prevprev" + prevPrevLoc);
//                                    hop++;
//                                    String[] msgnoder = new String[5];
//                                    msgnoder[0] = "freeresp";
//                                    msgnoder[1] = String.valueOf(agentId);
//                                    msgnoder[2] = n.getVertex().getName() + newLocation;
//                                    msgnoder[3] = String.valueOf(hop); //first hop
//                                    msgnoder[4] = inbox[4]; //Todo: review hops number -> probably this is different
//                                    NetworkNodeMessageBuffer.getInstance().putMessageWithNetworkDelay(this, n, prevPrevLoc, msgnoder, hop);
//                                }
//                            }

                        }
                        if (inbox[0].equals("connect")) {
                            //message msgnodediff: connect|level|nodeid|nodetoconnect
                            int level = Integer.valueOf(inbox[1]);
                            //String ndet = String.valueOf(inbox[2]);
                            String nodetoConnect = inbox[3];
                            connect(n.getVertex(), nodetoConnect);
                            //createNewAgents(5, n);
                        }
                    }
                    //System.out.println("sale node name:" + n.getVertex().getName());

                    if (SimulationParameters.activateReplication.equals("replalgon")) {
                        n.calculateTimeout(1);
                        evaluateAgentCreation(n, 1);

                    }
                    // Do some stuff
                    //System.out.println("continuaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa?");
                    break;

                case 1: //what happens if a node dies?
                    // System.out.println("node " + n.getVertex().getName() + " n followed agents:" + n.getResponsibleAgents());
                    //KillNode(n);
                    if (n.getRounds() > SimulationParameters.nofailRounds) {
                        KillNode(n);
                    } else {
                        System.out.println("Killed called not die for first " + SimulationParameters.nofailRounds + " rounds!");
                    }
                    break;
                default:
                    System.out.println("acrtion not specified");
            }

            if (n.status != Action.DIE) {
                //System.out.println("node " + n.getVertex().getName());
                //2. Compare topology data with cache given by agents.
                ArrayList<String> topologyData = new ArrayList(getTopologyNames(n.getVertex())); // Get topology of the network
                // System.out.println("node " + n.getVertex().getName() + "antes de if ------:");
                if (n.getNetworkdata().containsKey(n.getVertex().getName())) {
                    //System.out.println("node " + n.getVertex().getName() + "antes de cargar nd  ------:");
                    List<String> nd = new ArrayList((Collection) n.getNetworkdata().get(n.getVertex().getName())); //Store data given by agents

                    //System.out.println("node " + n.getVertex().getName() + " nd:" + nd + "vs  topologyData:" + topologyData);
                    //dif = nd - topologyData
                    List<String> dif = new ArrayList<>(nd);
                    dif.removeAll(topologyData);

                    //dif = topologyData - nd
                    List<String> dif2 = new ArrayList<>(topologyData);
                    dif2.removeAll(nd);

                    dif.removeAll(dif2);
                    dif.addAll(dif2);

                    //System.out.println("node 2 nd" + nd);
                    if (!dif.isEmpty()) {
                        int level = 0;
                        level++;
                        for (String d : dif) {
                            //without neigbor data of d is impossible create d ?
                            if (n.getNetworkdata().containsKey(d)) {
                                List<String> neigdiff = (ArrayList) n.getNetworkdata().get(d);
                                //System.out.println("noooide");
                                String min;
                                // System.out.println(n.getVertex().getName() + "-d:" + d + ", data:" + n.getNetworkdata());
                                //System.out.println("ne:" + neigdiff + ", " + n.getVertex().getName());

                                min = getMinimumId(neigdiff);
                                //min = getMoreInfoId(neigdiff, d);
                                //I'm minimum, I create node
                                //System.out.println("min" + min + " vs " + n.getVertex().getName());
                                if (min.equals(n.getVertex().getName())) {
                                    //System.out.println("create node because node does not detect");
                                    createNewNode(n, d);
                                    //Send message to node neigbours.
                                    //can be no nd but all agentData
                                    if (neigdiff != null && !neigdiff.isEmpty()) {
                                        for (String neig : neigdiff) {
                                            //message msgnodediff: connect|level|nodeid|nodetoconnect
                                            //System.out.println(n.getVertex().getName() + "is sending diff " + dif + "to" + neig);
                                            String[] msgnodediff = new String[5];
                                            msgnodediff[0] = "connect";
                                            msgnodediff[1] = String.valueOf(level);
                                            msgnodediff[2] = n.getVertex().getName();
                                            msgnodediff[3] = d;
                                            NetworkNodeMessageBuffer.getInstance().putMessage(neig, msgnodediff);
                                            //n.getPending().get(dif.toString()).add(neig);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (n.getNetworkdata().isEmpty()) {
                    // System.out.println("node " + n.getVertex().getName() + " no contiene datoooooos! :O");
                    n.getNetworkdata().put(n.getVertex().getName(), topologyData);
                }
                //evaporate pheromone
                n.getVertex().setPh((n.getVertex().getPh() - n.getVertex().getPh() * 0.001f));
            }
        }
        //System.out.println("jojojojojojo");
        return false;
    }

    //Example: It is better handshake protocol. J. Gomez
    @Override
    public void evaluateAgentCreation(Node n, int hop) {
        synchronized (this) {
            Iterator<Map.Entry<Integer, Integer>> iter = n.getFollowedAgents(hop).entrySet().iterator();
            int estimatedTimeout;
            int stdDevTimeout;

            //Replication hops
            if (!n.getFollowedAgents(hop).isEmpty()) {
                n.printReplicationHops();
            }
            //for each followed agent
            while (iter.hasNext()) {
                //Key: agentId
                Map.Entry<Integer, Integer> Key = iter.next();
                int agentId = Key.getKey();
                //System.out.println(n.getVertex().getName() + " hashmap " + n.getResponsibleAgents(hop) + "hop:" + hop);

                //if (n.getFollowedAgentsLocation(hop).containsKey(agentId)) {
                estimatedTimeout = n.estimateExpectedTime(null, hop);
                stdDevTimeout = (int) n.getStdDevTimeout(null, hop);
                //System.out.println("NodeId" + nodeId  + ", hop:" + hop);
                if (n.containsFirstDepartingMsgTime(agentId) && (Math.abs((n.getRounds() - n.getFirstDepartingMsgTime(agentId))) > (estimatedTimeout + 3 * stdDevTimeout))) { //this is not the expresion
                    /*if (n.getResponsibleAgentsLocation().containsKey(k) && n.getNodeTimeouts().containsKey(n.getResponsibleAgentsLocation().get(k))) {
                        n.getNodeTimeouts().get(n.getResponsibleAgentsLocation().get(k)).add(estimatedTimeout);
                        //n.addTimeout(estimatedTimeout);
                    }*/
                    System.out.println("nodep: " + n.getVertex().getName() + ", estimatedTimeout: " + estimatedTimeout + ", 3*stdvTimeout" + 3 * stdDevTimeout + ", lastAgentDep: " + n.getFirstDepartingMsgTime(agentId) + ", node rounds: " + n.getRounds() + ", hops:" + hop);
                    //int father;
                    if (n.getFollowedAgents(hop).containsKey(agentId)) {
                        if (n.getFollowedAgents(hop).get(agentId) == -1) {
                            System.out.println("create new agent instance..." + n.getVertex().getName() + " father: " + agentId);
                            //n.printReplicationHop(hop);
                            createNewAgents(1, n, agentId);
                            // father = agentId;
                        } else {
                            int idOrig = n.getFollowedAgents(hop).get(agentId);
                            createNewAgents(1, n, idOrig);
                            System.out.println("create new agent instance..." + n.getVertex().getName() + " father: " + idOrig);
                            //n.printReplicationHop(hop);
                            //father = idOrig;
                        }
                        //send message to previous locations
                        /*ArrayList<String> PrevLocations = n.getResponsibleAgentsPrevLocations(agentId, hop);
                            int tmphop = hop;
                           /* if (PrevLocations != null && PrevLocations.size() - hop > 0) {
                                String prevPrevLoc = PrevLocations.get(PrevLocations.size() - hop);
                                StringSerializer s = new StringSerializer();

                                String sPrevLocations = (String) s.serialize(PrevLocations);
                                if (tmphop < SimulationParameters.nhopsChain && !prevPrevLoc.equals(n.getVertex().getName())) {
                                    //System.out.println(n.getVertex().getName() + "resending freeresp to prevprev" + prevPrevLoc);
                                    tmphop++;
                                    String[] msgnoder = new String[5];
                                    msgnoder[0] = "freeresp";
                                    msgnoder[1] = String.valueOf(agentId);
                                    StringSerializer sd = new StringSerializer();
                                    msgnoder[2] = sd.serialize(PrevLocations);
                                    msgnoder[3] = String.valueOf(tmphop); //first hop
                                    msgnoder[4] = sPrevLocations; //Todo: review hops number -> probably this is different                                
                                    //System.out.println("xxx:" + msgnoder[2]);
                                    NetworkNodeMessageBuffer.getInstance().putMessage(prevPrevLoc, msgnoder);
                                    //System.out.println("Resending free resp agent:" + agentId + "father" + father + ", hop: " + tmphop + " to:" + prevPrevLoc + " key: " + msgnoder[2]);
                                }
                            }*/
                        iter.remove();
                        n.deleteAgentFromRep(hop, agentId);
                        //System.out.println("after removal");
                        //n.printReplicationHop(hop);
                    }
                    //for (int i = 1; i <= SimulationParameters.nhopsChain; i++) {
//                    n.removeResponsibleAgentsPrevLocations(k, hop);
                    //}
                    //System.out.println("node after: " + n.getVertex().getName() + " - " + n.getResponsibleAgents());
                    //System.out.println("end creation of agent" + newAgentID);
                }
                //}
                /*else {
                    System.out.println("not contains locations!");
                }*/
            }
        }
    }
}
