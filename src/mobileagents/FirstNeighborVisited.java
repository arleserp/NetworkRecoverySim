/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileagents;

/**
 *
 * @author arlese.rodriguezp
 */
import agents.ActionParameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import graphutil.MyVertex;

/**
 *
 * @author My-Macintosh
 */
public class FirstNeighborVisited implements AgentProgram {

    float pf;

    public FirstNeighborVisited(float pf) {
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

        Collection<MyVertex> empty = (Collection<MyVertex>) p.getAttribute("neighbors");
        Iterator<MyVertex> it = empty.iterator();
        ArrayList<MyVertex> vs = new ArrayList<>();
        ArrayList<MyVertex> tt = new ArrayList<>();

        while (it.hasNext()) {
            MyVertex v = it.next();
            tt.add(v);
            if (v != null) {
                if (v.getStatus() != "visited") {
                    vs.add(v);
                }
            }
        }

        if (vs.size() == 0) {
            vs = tt;
        }

        try {
            boolean isSet = false;
            do {
                pos = (int) (Math.random() * vs.size());
                if (((MyVertex) vs.toArray()[pos]) != null) {
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

