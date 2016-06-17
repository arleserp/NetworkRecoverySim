for /L %%x in (1, 1, 30) do (
  echo %%x%round
 
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 1e-3 carriers smallworld+v+100+beta+0.1+degree+2.loc -1 
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+2.graph 10 1e-3 carriers smallworld+v+100+beta+0.3+degree+2.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.5+degree+2.graph 10 1e-3 carriers smallworld+v+100+beta+0.5+degree+2.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.9+degree+2.graph 10 1e-3 carriers smallworld+v+100+beta+0.9+degree+2.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 1e-3 random smallworld+v+100+beta+0.1+degree+2.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+2.graph 10 1e-3 random smallworld+v+100+beta+0.3+degree+2.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.5+degree+2.graph 10 1e-3 random smallworld+v+100+beta+0.5+degree+2.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.9+degree+2.graph 10 1e-3 random smallworld+v+100+beta+0.9+degree+2.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+4+numSt+97.graph 10 1e-3 carriers scalefree+sn+4+eta+4+numSt+97.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+2+numSt+97.graph 10 1e-3 carriers scalefree+sn+4+eta+2+numSt+97.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 1e-3 carriers scalefree+sn+4+eta+1+numSt+97.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+4+numSt+97.graph 10 1e-3 scalefree+sn+4+eta+4+numSt+97.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+2+numSt+97.graph 10 1e-3 scalefree+sn+4+eta+2+numSt+97.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 1e-3 scalefree+sn+4+eta+1+numSt+97.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load  community+v+100+beta+0.9+degree+4+clusters+4.graph 10 1e-3 carriers community+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 1e-3 carriers community+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 1e-3 carriers community+v+100+beta+0.3+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 1e-3 carriers community+v+100+beta+0.1+degree+4+clusters+4.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.9+degree+4+clusters+4.graph 10 1e-3 random community+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 1e-3 random community+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 1e-3 random community+v+100+beta+0.3+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 1e-3 random community+v+100+beta+0.1+degree+4+clusters+4.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.9+degree+4+clusters+4.graph 10 1e-3 carriers communitycircle+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.5+degree+4+clusters+4.graph 10 1e-3 carriers communitycircle+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.3+degree+4+clusters+4.graph 10 1e-3 carriers communitycircle+v+100+beta+0.3+degree+4+clusters+4.loc -1 
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.1+degree+4+clusters+4.graph 10 1e-3 carriers communitycircle+v+100+beta+0.1+degree+4+clusters+4.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.9+degree+4+clusters+4.graph 10 1e-3 random communitycircle+v+100+beta+0.9+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.5+degree+4+clusters+4.graph 10 1e-3 random communitycircle+v+100+beta+0.5+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.3+degree+4+clusters+4.graph 10 1e-3 random communitycircle+v+100+beta+0.3+degree+4+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load communitycircle+v+100+beta+0.1+degree+4+clusters+4.graph 10 1e-3 random communitycircle+v+100+beta+0.1+degree+4+clusters+4.loc -1
 
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.9+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.5+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.3+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.1+degree+4+clusters+4.loc

rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 9e-4 levywalk lattice+r+10+c+10.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 1e-3 random lattice+r+10+c+10.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 1e-3 carriers lattice+r+10+c+10.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load foresthubandspoke+v+100+clusters+4.graph 10 1e-3 carriers foresthubandspoke+v+100+clusters+4.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load foresthubandspoke+v+100+clusters+4.graph 10 1e-3 random foresthubandspoke+v+100+clusters+4.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load circle+v+100.graph 10 1e-3 carriers circle+v+100.loc -1 
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load line+v+100.graph 10 1e-3 carriers line+v+100.loc -1

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load circle+v+100.graph 10 1e-3 random circle+v+100.loc -1
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load line+v+100.graph 10 1e-3 random line+v+100.loc -1

rem pause
)
