REM java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator smallworld 100 0.5 2
REM java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator scalefree 4 1 97
java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator communitycircle 100 0.5 4 4
REM java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.graphGenerator hubandspoke 100 
pause