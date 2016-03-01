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
public class LevyWalkSynchronizationProgram implements AgentProgram {

    float pf;
    float alpha;
    float acumulator;
    int dirPos;
    float T;

    public LevyWalkSynchronizationProgram(float pf) {
        this.pf = pf;
        alpha = (float) Math.random();
        acumulator = 0;
        dirPos = (int) (Math.random() * 8.0);
        T = 1;
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");

        Collection<GraphElements.MyVertex> vs = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");
        
        acumulator += alpha;
        if (acumulator >= T || dirPos >= vs.toArray().length) {
            alpha = (float) Math.random();
            dirPos = (int) (Math.random() * vs.toArray().length);
            acumulator = 0;
        }
        
        System.out.println("dirPos" + dirPos + " size " + vs.toArray().length);
        act.setAttribute("location", vs.toArray()[dirPos]);
        /* If termite has a message then react to this message */
        return act;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
