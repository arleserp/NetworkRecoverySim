for /L %%x in (1, 1, 30) do (
  echo %%x%round
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 0 carriers lattice+r+10+c+10.loc -1
 pause
 rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+100+c+2.graph 10 0 random lattice+r+100+c+2.graph -1
)
