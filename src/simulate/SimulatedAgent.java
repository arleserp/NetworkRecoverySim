package unalcol.agents.simulate;

import java.util.Hashtable;

import unalcol.agents.Agent;
import unalcol.agents.AgentArchitecture;
import unalcol.agents.AgentProgram;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gómez
 * @version 1.0
 */
public class SimulatedAgent extends Agent{
  protected Hashtable<String,Object> properties = new Hashtable<String,Object>();

  public SimulatedAgent( AgentArchitecture _architecture, AgentProgram _program ){
    super( _architecture, _program );
  }

  public SimulatedAgent(AgentProgram sugarscapeAgentProgram) {
	// TODO Auto-generated constructor stub
	  super(sugarscapeAgentProgram);
}

public void setAttribute( String key, Object value ){
    properties.put(key,value);
  }

  public Object getAttribute( String key ){
    return properties.get(key);
  }
}