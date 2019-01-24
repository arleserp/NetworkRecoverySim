/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.NetworkSim.programs;

/**
 *
 * @author arlese.rodriguezp
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.NetworkSim.ActionParameters;
import unalcol.agents.NetworkSim.GraphElements;
import unalcol.agents.Percept;

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

        Collection<GraphElements.MyVertex> empty = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");
        Iterator<GraphElements.MyVertex> it = empty.iterator();
        ArrayList<GraphElements.MyVertex> vs = new ArrayList<>();
        ArrayList<GraphElements.MyVertex> tt = new ArrayList<>();

        while (it.hasNext()) {
            GraphElements.MyVertex v = it.next();
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

