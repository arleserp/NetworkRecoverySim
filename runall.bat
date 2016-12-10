for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.5 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 
 rem pause
)
