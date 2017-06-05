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
        //System.out.println("random motion program");
        // System.out.println("pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");
        int pos;

        if (Math.random() < pf) {
            return new ActionParameters("die");
        }

        Collection<GraphElements.MyVertex> vs = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");

        try {
            boolean isSet = false;
            do {
                pos = (int) (Math.random() * vs.size());
                if (((GraphElements.MyVertex) vs.toArray()[pos]) != null) {
                    act.setAttribute("location", vs.toArray()[pos]);
                    act.setAttribute("pf", pf);
                    isSet = true;
                }
            } while (!isSet);
            //System.out.println("location" + vs.toArray()[pos]);
        } catch (Exception ex) {
            // System.out.println("this cannot happen!!! agent fail because node is not running or was killed determining new movement." + vs);
            //return new ActionParameters("die");
            // System.out.println("Inform node that possibly a node is death: " + ex.getLocalizedMessage());
            return new ActionParameters("informfailure");
        }
        //System.out.println("act:" + act.getCode());
        return act;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
