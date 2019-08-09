/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

import agents.ActionParameters;
import graphutil.MyVertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
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

        //This can happen!
        if (p.getAttribute("nodedeath") != null) {
            System.out.println("agent fail because node is not running.");
            return new ActionParameters("die");
        }

        ArrayList<MyVertex> vs = null;
        Collection<MyVertex> c = (Collection<MyVertex>) p.getAttribute("neighbors");
        Iterator<MyVertex> it = c.iterator();
        try {
            vs = new ArrayList<>();
            while (it.hasNext()) {
                MyVertex v = it.next();
                if (v != null && !v.getStatus().equals("failed")) {
                    vs.add(v);
                }
            }
        } catch (Exception ex) {
            // System.out.println("this cannot happen!!! agent fail because node is not running or was killed determining new movement." + vs);
            //return new ActionParameters("die");
            System.out.println("a node is death while reading perception: " + ex.getLocalizedMessage());
            return new ActionParameters("die");
        }

        try {
            boolean isSet = false;
            do {
                pos = (int) (Math.random() * vs.size());
                if (((MyVertex) vs.toArray()[pos]) != null && !((MyVertex) vs.toArray()[pos]).getStatus().equals("failed")) {
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
