REM for /L %%x in (1, 1, 10) do (

Java -d64 -Xms3G -Xmx3G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkRecoverySim.jar networkrecoverysim.DataReplicationNodeFailingMain load smallworld+v+100+beta+0.5+degree+2.graph 0 0 carriersrep smallworld+v+100+beta+0.5+degree+2.loc 10000 replalgon NODELAY  0.25 4 nhopsinfo 4 5 0 backtolowpf5000

REM Java -d64 -Xms3G -Xmx3G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationNodeFailingMain load scalefree+sn+4+eta+1+numSt+97.graph 0 0 random scalefree+sn+4+eta+1+numSt+97.loc 6000 replalgon NODELAY  0.25 8 nhopsinfo 8 5 0 backtolowpf5000

	
REM Java -d64 -Xms3G -Xmx3G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationNodeFailingMain load circlelonghubandspoke+v+100+l+2.graph 0 0 random circlelonghubandspoke+v+100+l+2.loc 10000 replalgon NODELAY  0.25 4 nhopsinfo 4 5 0 backtolowpf5000


REM Java -d64 -Xms3G -Xmx3G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationNodeFailingMain load longhubandspoke+v+100+l+2.graph 0 0 random longhubandspoke+v+100+l+2.loc 10000 replalgon NODELAY  0.25 4 nhopsinfo 4 5 0 backtolowpf5000

REM Java -d64 -Xms3G -Xmx3G  -XX:+UseG1GC  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70  -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationNodeFailingMain load longhubandspoke+v+100+l+3.graph 0 0 random longhubandspoke+v+100+l+3.loc 10000 replalgon NODELAY  0.25 4 nhopsinfo 4 5 0 backtolowpf5000

REM )
