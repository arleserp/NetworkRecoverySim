for /L %%x in (1, 1, 30) do (
  echo %%x%round
 rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97A.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97A.loc -1
  rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97B.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97B.loc -1
   rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97C.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97C.loc -1
    rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97D.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97D.loc -1
rem java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97.graph 10 0 random scalefree+sn+4+eta+1+numSt+97.loc -1
	  java  -Xmx4200m -classpath NetworkSimulator.jar unalcol.agents.NetworkSim.SyncronizationMain load scalefree+sn+4+eta+1+numSt+97E.graph 10 0 carriers scalefree+sn+4+eta+1+numSt+97E.loc -1
)
