for /L %%x in (1, 1, 30) do (
java  -d64   -Xms4G -Xmx4G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationNodeFailingMain load scalefree+sn+4+eta+1+numSt+97.graph 20 0 carriersrep scalefree+sn+4+eta+1+numSt+97.loc 5000 replalgon NODELAY 0.001 6 broadcast
)