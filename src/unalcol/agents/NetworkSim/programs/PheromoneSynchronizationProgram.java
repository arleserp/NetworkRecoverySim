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
public class PheromoneSynchronizationProgram implements AgentProgram {

    float pf;

    public PheromoneSynchronizationProgram(float pf) {
        this.pf = pf;
        System.out.println("pf: " + pf);
    }

    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");
        
        int pos;

        if (Math.random() < pf) {
            return new ActionParameters("die");
        }

        Collection<GraphElements.MyVertex> vs = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");
        pos = (int) carry(vs);
        act.setAttribute("location", vs.toArray()[pos]);
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

    private int carry(Collection<GraphElements.MyVertex> vs) {
        int dirPos = 0;
        float q0 = 0.9f;

        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {
            for (int k = 0; k < vs.size(); k++) {
                if (((GraphElements.MyVertex) vs.toArray()[k]).getPh() != -1) {
                    dirPos = k;
                    break;
                }
            }

            for (int k = dirPos + 1; k < vs.size(); k++) {
                if (((GraphElements.MyVertex) vs.toArray()[k]).getPh() != -1 && ((GraphElements.MyVertex) vs.toArray()[dirPos]).getPh() > ((GraphElements.MyVertex) vs.toArray()[k]).getPh()) {
                    dirPos = k;
                }
            }

            //store location with the min amount of pheromone
            float min = ((GraphElements.MyVertex) vs.toArray()[dirPos]).getPh();
            temp.add(dirPos);
            for (int k = 0; k < vs.size(); k++) {
                if (((GraphElements.MyVertex) vs.toArray()[k]).getPh() == min) {
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
                phinv[i] = 1 - ((GraphElements.MyVertex) vs.toArray()[i]).getPh();
            }
            dirPos = Roulette(phinv);
            //dirPos = LevyWalk(proximitySensor, termitesNeighbor);
        }

        return dirPos;
    }

}
