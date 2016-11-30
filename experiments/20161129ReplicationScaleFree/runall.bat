for /L %%x in (1, 1, 30) do (
  echo %%x%round
  java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.05  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.1  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.3  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.5  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.7  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
  rem java   -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0.9  carriersrep scalefree+sn+4+eta+1+numSt+97.loc -1 
rem pause
)
