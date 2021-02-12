/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package staticagents;

import agents.ActionParameters;
import networkrecoverysim.SimulationParameters;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;

import unalcol.agents.Percept;

/**
 *
 * @author Arles Rodriguez
 */
public class NodeFailingProgram implements AgentProgram {

    float pf;

    public NodeFailingProgram(float pf) {
        this.pf = pf;
        //System.out.println("Node pf: " + pf);
    }

    public void setPf(float pf) {
        this.pf = pf;
    }

    @Override
    public Action compute(Percept p) {

        if (SimulationParameters.failureProfile.contains("FailIntv")) { // Defines failure interval
            String[] FailureInterval = SimulationParameters.failureProfile.split("-");
            int initialRound = Integer.valueOf(FailureInterval[1]);
            int endRound = Integer.valueOf(FailureInterval[2]);
            int round = (Integer)(p.getAttribute("round"));
            
            if (Math.random() < pf && (round >= initialRound && round <= endRound) ) {
                return new ActionParameters("die");
            } else {
                return new ActionParameters("communicate");
            }
        } else {
            if (Math.random() < pf) {
                return new ActionParameters("die");
            } else {
                return new ActionParameters("communicate");
            }
        }
    }

    @Override
    public void init() {

    }

}
