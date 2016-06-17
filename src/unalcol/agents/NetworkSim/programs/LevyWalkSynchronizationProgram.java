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
    ArrayList<Integer> direccion;
    boolean resort;
    String lv;
    int par = 0;

    public LevyWalkSynchronizationProgram(float pf) {
        this.pf = pf;
        alpha = (float) Math.random();
        acumulator = 0;
        dirPos = (int) (Math.random() * 4.0);
        T = 1;
        resort = true;
        direccion = new ArrayList<>();
        direccion.add(0);
        direccion.add(90);
        direccion.add(180);
        direccion.add(360);
    }

//Manejar Grados y permutar nodos con base en los vecinos
    // 2 vecinos 180 180
    // 5 vecinos 72 grados 
    // Direecion 
    //Gnupplot
    @Override
    public Action compute(Percept p) {
        ActionParameters act = new ActionParameters("move");
        Collection<GraphElements.MyVertex> vs = (Collection<GraphElements.MyVertex>) p.getAttribute("neighbors");
        ArrayList<Integer> dir = new ArrayList();
        ArrayList<GraphElements.MyVertex> vertices = new ArrayList();

        //System.out.println("vs" + vs.size());
        for (int j = 0; j < vs.size(); j++) {
            //System.out.println("d"+((GraphElements.MyVertex) (vs.toArray()[j])).toString() + " vs lv:" + lv);
            if (vs.size() == 1 || !((GraphElements.MyVertex) (vs.toArray()[j])).toString().equals(lv)) {
                vertices.add((GraphElements.MyVertex) vs.toArray()[j]);
            }
        }
        //System.out.println("vertices" + vertices.size());

        int n_angle = 360 / vertices.size();
        int dmap;

        for (int i = 0; i < vertices.size(); i++) {
            dir.add(i * n_angle);
        }
        //System.out.println("dir" + dir);
        alpha = (float) Math.random();
        acumulator += alpha;
        if (acumulator >= T) {
            dirPos = (int) (Math.random() * direccion.size());
            acumulator = 0;
            resort = true;
        }
        dmap = getClosestDirection(dir, direccion.get(dirPos));

        par++;

        if (par % 2 == 0) {
            lv = ((GraphElements.MyVertex) vertices.toArray()[dmap]).toString();
        }
        //System.out.println("dmap" + dmap);
        //Generar un vector de dirección Lw
        //System.out.println("dirPos" + dirPos + " size " + vs.toArray().length);
        act.setAttribute("location", vertices.toArray()[dmap]);
        /* If termite has a message then react to this message */
        return act;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int getClosestDirection(ArrayList<Integer> dir, Integer angle) {
        int dif = 0;
        int closestDir = 0;
        for (int i = 1; i < dir.size(); i++) {
            if ((Math.abs(dir.get(closestDir) - angle) >= Math.abs(dir.get(i)) - angle)) {
                closestDir = i;
            }
        }
        return closestDir;
    }

}
