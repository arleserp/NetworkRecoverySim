for /L %%x in (1, 1, 30) do (
  echo %%x%round
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97a.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97a.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97a.graph 10 0 random scalefree+sn+4+eta+1+numSt+97a.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97a.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97a.loc 2000

 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97b.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97b.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97b.graph 10 0 random scalefree+sn+4+eta+1+numSt+97b.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97b.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97b.loc 2000
 
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97c.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97c.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97c.graph 10 0 random scalefree+sn+4+eta+1+numSt+97c.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97c.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97c.loc 2000
 
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97d.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97d.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97d.graph 10 0 random scalefree+sn+4+eta+1+numSt+97d.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97d.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97d.loc 2000

 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97e.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97e.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97e.graph 10 0 random scalefree+sn+4+eta+1+numSt+97e.loc 2000
 java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97e.graph 10 0 levywalk scalefree+sn+4+eta+1+numSt+97e.loc 2000
 )
