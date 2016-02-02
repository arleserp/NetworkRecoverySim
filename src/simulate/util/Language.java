package unalcol.agents.simulate.util;

import unalcol.agents.simulate.gui.*;


public interface Language {

  public int getActionIndex( String action );

  public int getPerceptIndex( String percept );

  public String getAction( int index );

  public String getPercept( int index );

  public int getActionsNumber();
  public int getPerceptsNumber();
}
