/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.programs;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.Percept;

/**
 *
 * @author Arles Rodriguez
 */
public class NodeFailingProgram implements AgentProgram {

    float pf;

    public NodeFailingProgram(float pf) {
        this.pf = pf;
        System.out.println("Node pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        //System.out.println("Node threeeeeeeeeeeeeeeeeead!");
        ActionParameters act = new ActionParameters("communicate");
        //System.out.println("communicate!");
        //Now a node can fail also
        if (Math.random() < pf) {
            //System.out.println("die");
            return new ActionParameters("die");
        }
        return act;
    }

    @Override
    public void init() {
        
    }

}
