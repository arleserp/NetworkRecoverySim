/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim;

import java.util.ArrayList;
import java.util.Iterator;
import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;

/**
 *
 * @author ARODRIGUEZ
 */
public class Node extends Agent {
    private GraphElements.MyVertex v;
    private ArrayList<Agent> currentAgents;
    private ArrayList<Agent> responsibleAgents;
    
    public Node(AgentProgram _program, GraphElements.MyVertex ve) {
        super(_program);
        this.v = ve;
        currentAgents = new ArrayList<>();
    }
    
    public GraphElements.MyVertex getVertex(){
        return v;
    }
    
    public void addAgent(Agent a){
        currentAgents.add(a);
    }
    
    public void deleteAgent(Agent a){
        synchronized(Node.class){
            for (Agent x : currentAgents) {
                if(x.equals(a)){
                    currentAgents.remove(x);
                }
            }
        }
    }
}
