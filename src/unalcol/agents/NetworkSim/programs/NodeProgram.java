/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.programs;

import java.util.ArrayList;
import java.util.Collection;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.Percept;
import unalcol.random.RandomUtil;

/**
 *
 * @author Arles Rodriguez
 */
public class NodeProgram implements AgentProgram {

    float pf;

    public NodeProgram(float pf) {
        this.pf = pf;
        System.out.println("pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("communicate");
        //int pos;

        //if (Math.random() < pf) {
        //    return new ActionParameters("die");
        //}

        //Collection<GraphElements.MyVertex> vs = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");
        //act.setAttribute("location", vs.toArray()[pos]);
        /* If termite has a message then react to this message */
        return act;
    }

    @Override
    public void init() {
        
    }

}
