for /L %%x in (1, 1, 30) do (
  echo %%x%round
 


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 carriers communitycircle+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 carriers communitycircle+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 carriers communitycircle+v+100+beta+0.3+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 carriers communitycircle+v+100+beta+0.1+degree+4+clusters+4.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 random communitycircle+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 random communitycircle+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 random communitycircle+v+100+beta+0.3+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 random communitycircle+v+100+beta+0.1+degree+4+clusters+4.loc -1


rem pause
)
