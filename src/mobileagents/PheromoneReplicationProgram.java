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
import unalcol.random.RandomUtil;

/**
 *
 * @author Arles Rodriguez
 */
public class PheromoneReplicationProgram implements AgentProgram {

    float pf;

    public PheromoneReplicationProgram(float pf) {
        this.pf = pf;
        //System.out.println("pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");

        //This can happen!
        if (p.getAttribute("nodedeath") != null) {
            System.out.println("agent fail because node is not running.");
            return new ActionParameters("die");
        }

        int pos;
        ArrayList<MyVertex> vs = null;
        //if (Math.random() < pf) {
        //    return new ActionParameters("die");
        //}
        //System.out.println("perception " + p.getAttribute("neighbors"));
        try {
            Collection<MyVertex> c = (Collection<MyVertex>) p.getAttribute("neighbors");
            Iterator<MyVertex> it = c.iterator();
            vs = new ArrayList<>();
            while (it.hasNext()) {
                MyVertex v = it.next();
                if (v != null && !v.getStatus().equals("failed")) {
                    vs.add(v);
                }
            }
            pos = (int) carry(vs);
            act.setAttribute("location", vs.toArray()[pos]);
            act.setAttribute("pf", pf);
        } catch (Exception ex) {
            // System.out.println("this cannot happen!!! agent fail because node is not running or was killed determining new movement." + vs);
            //return new ActionParameters("die");
            // System.out.println("Inform node that possibly a node is death: " + ex.getLocalizedMessage());
            return new ActionParameters("die");
        }
        /* If termite has a message then react to this message */
        return act;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    int Roulette(float[] pheromone) {
        //System.out.println("roulette");
        float sum = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
        }
        double rand = (double) (Math.random() * sum);
        sum = 0;
        int mov = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
            if (rand < sum) {
                mov = k;
                break;
            }
        }
        return mov;
    }

    private int carry(Collection<MyVertex> vs) {
        int dirPos = 0;
        float q0 = 0.9f;

        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {

            for (int k = 0; k < vs.size(); k++) {
                if (((MyVertex) vs.toArray()[k]) != null && ((MyVertex) vs.toArray()[k]).getPh() != -1) {
                    dirPos = k;
                    break;
                }
            }

            for (int k = dirPos + 1; k < vs.size(); k++) {
                if (((MyVertex) vs.toArray()[k]) != null && ((MyVertex) vs.toArray()[k]).getPh() != -1 && ((MyVertex) vs.toArray()[dirPos]).getPh() > ((MyVertex) vs.toArray()[k]).getPh()) {
                    dirPos = k;
                }
            }

            //store location with the min amount of pheromone
            float min = ((MyVertex) vs.toArray()[dirPos]).getPh();
            temp.add(dirPos);
            for (int k = 0; k < vs.size(); k++) {
                if (((MyVertex) vs.toArray()[k]) != null && ((MyVertex) vs.toArray()[k]).getPh() == min) {
                    temp.add(k);
                }
            }
            //dirPos = LevyWalk(proximitySensor, termitesNeighbor);
            //if (!temp.contains(dirPos)) {

            dirPos = temp.get(RandomUtil.nextInt(temp.size()));
            //}
        } else {
            //Idea is choose direction with the less amount of pheromone
            float[] phinv = new float[vs.size()];
            for (int i = 0; i < vs.size(); i++) {
                if (((MyVertex) vs.toArray()[i]) == null) {
                    phinv[i] = 0;
                } else {
                    phinv[i] = 1 - ((MyVertex) vs.toArray()[i]).getPh();
                }
            }
            dirPos = Roulette(phinv);
            //dirPos = LevyWalk(proximitySensor, termitesNeighbor);
        }
        return dirPos;
    }
}
