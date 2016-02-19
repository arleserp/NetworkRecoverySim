/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unalcol.agents.NetworkSim.programs;

import java.util.Collection;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.Percept;

/**
 *
 * @author Arles Rodriguez
 */
public class RandomSynchronizationProgram implements AgentProgram {
    float pf;
    
    public RandomSynchronizationProgram(float pf) {
        this.pf = pf;
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");
        int pos;
        
        Collection <GraphElements.MyVertex> vs = (Collection <GraphElements.MyVertex>)p.getAttribute("neighbors");
        pos = (int) (Math.random() * vs.size());
        act.setAttribute("location", vs.toArray()[pos]);
        return act;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
