for /L %%x in (1, 1, 30) do (
  echo %%x%round
 
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.1+degree+4.graph 10 0 carriers smallworld+v+100+beta+0.1+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 carriers smallworld+v+100+beta+0.3+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 carriers smallworld+v+100+beta+0.5+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 carriers smallworld+v+100+beta+0.9+degree+4.loc

java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.1+degree+4.graph 10 0 random smallworld+v+100+beta+0.1+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 random smallworld+v+100+beta+0.3+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.5+degree+4.graph 10 0 random smallworld+v+100+beta+0.5+degree+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.9+degree+4.graph 10 0 random smallworld+v+100+beta+0.9+degree+4.loc

rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.1+degree+4.graph 10 0 levywalk smallworld+v+100+beta+0.1+degree+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 levywalk smallworld+v+100+beta+0.3+degree+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 levywalk smallworld+v+100+beta+0.5+degree+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load smallworld+v+100+beta+0.3+degree+4.graph 10 0 levywalk smallworld+v+100+beta+0.9+degree+4.loc


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+4+numSt+97.graph 10 0 carriers scalefree+sn+4+eta+4+numSt+97.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+2+numSt+97.graph 10 0 carriers scalefree+sn+4+eta+2+numSt+97.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97.loc


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+4+numSt+97.graph 10 0 random scalefree+sn+4+eta+4+numSt+97.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+2+numSt+97.graph 10 0 random scalefree+sn+4+eta+2+numSt+97.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0 random scalefree+sn+4+eta+1+numSt+97.loc


rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+4+numSt+97.graph 10 0 levywalk scalefree+sn+4+eta+4+numSt+97.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+2+numSt+97.graph 10 0 levywalk scalefree+sn+4+eta+2+numSt+97.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97.loc


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 carriers community+v+100+beta+0.9+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 carriers community+v+100+beta+0.5+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 carriers community+v+100+beta+0.3+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 carriers community+v+100+beta+0.1+degree+4+clusters+4.loc


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 random community+v+100+beta+0.9+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 random community+v+100+beta+0.5+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 random community+v+100+beta+0.3+degree+4+clusters+4.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 random community+v+100+beta+0.1+degree+4+clusters+4.loc


rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.9+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.9+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.5+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.5+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.3+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.3+degree+4+clusters+4.loc
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load community+v+100+beta+0.1+degree+4+clusters+4.graph 10 0 levywalk community+v+100+beta+0.1+degree+4+clusters+4.loc


java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 0 levywalk lattice+r+10+c+10.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 0 random lattice+r+10+c+10.loc
java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load lattice+r+10+c+10.graph 10 0 carriers lattice+r+10+c+10.loc

rem pause
)
