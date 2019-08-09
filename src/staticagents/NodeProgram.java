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
public class NodeProgram implements AgentProgram {
    float pf; //failure probability

    public NodeProgram(float pf) {
        this.pf = pf;
        System.out.println("pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("communicate");
        return act;
    }

    @Override
    public void init() {
        
    }

}
