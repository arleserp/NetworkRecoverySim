#!/bin/sh 
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 rem pause
done

i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.03 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.03 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.05 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.05 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.07 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.07 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.09 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 rem pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.09 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 rem pause
done
i=1

while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.1 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.1 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 REM pause
done
i=1
`while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.3 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.3 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.5 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.5 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.7 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  rem echo %%x%round
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.7 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 REM pause
done
i=1

while [ $i -le 30 ]; do
  rem echo %%x%round  
   echo $i
	i=$(($i+1))
	java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.9 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
 REM pause
done
i=1
while [ $i -le 30 ]; do
  REM echo %%x%round
   echo $i
	i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.9 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
 REM pause
done
