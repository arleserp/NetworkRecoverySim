/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package staticagents;

import agents.ActionParameters;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;

import unalcol.agents.Percept;

/**
 *
 * @author Arles Rodriguez
 */
public class NodeFailingProgram implements AgentProgram {
    float pf;

    public NodeFailingProgram(float pf) {
        this.pf = pf;
        //System.out.println("Node pf: " + pf);
    }

    public void setPf(float pf) {
        this.pf = pf;
    }   
    
    @Override
    public Action compute(Percept p) {
        if (Math.random() < pf) {
            return new ActionParameters("die");
        } else{
            return new ActionParameters("communicate");
        }
    }

    @Override
    public void init() {
        
    }

}
