package unalcol.agents;

import java.io.Serializable;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;

/**
 * <p>
 * Title: Kernel </p>
 * <p>
 * Description: A multi agent problem solution kernel</p>
 * <p>
 * Copyright: Copyright (c) 2007</p>
 * <p>
 * Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez
 * @version 1.0
 */
public class Kernel extends Observable implements Runnable, Serializable {

    /**
     * Stop/Go checking flag
     */
    public boolean flag = true;
    /**
     * Collection of agents in the problem
     */
    protected Vector<Agent> agents;

    public Kernel(Agent agent) {
        agents = new Vector<Agent>();
        agents.add(agent);
    }

    public Kernel(Vector<Agent> _agents) {
        agents = _agents;

    }

    public Vector<Agent> getAgents() {
        return agents;
    }

    public void setAgents(Vector<Agent> agents) {
        this.agents = agents;
    }

    public Agent getAgent(int index) {
        return agents.get(index);
    }

    public Agent getAgent() {
        return agents.get(0);
    }

    public boolean addAgent(Agent agent) {
        boolean cflag = !agents.contains(agent);
        if (cflag) {
            agents.add(agent);
        }
        return cflag;
    }

    public boolean delAgent(Agent agent) {
        return agents.remove(agent);
    }

    public void stop() {
        flag = false;
        int n = agents.size();
        Agent a;
        for (int i = 0; i < n; i++) {
            agents.get(i).die();
        }
    }

    public void run() {
        flag = true;
        Agent a;
        int n = agents.size();
        for (int i = 0; i < n && flag; i++) {
            a = agents.get(i);
            a.live();
            Thread t = new Thread(a);
            a.setThread(t);
            t.start();
        }
    }
}
