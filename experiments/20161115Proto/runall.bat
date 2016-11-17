for /L %%x in (1, 1, 30) do (
  echo %%x%round
  java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.8 carriers smallworld+v+100+beta+0.1+degree+2.loc -1 
rem pause
)
