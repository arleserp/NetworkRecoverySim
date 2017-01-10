#!/bin/sh
export DISPLAY=:4 
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.001 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.001 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.003 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.003 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.005 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.005 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.007 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.007 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.009 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.009 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.01 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.01 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.1 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.1 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.3 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.3 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.5 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.5 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.7 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
  echo $i
  i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.7 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
i=1
while [ $i -le 30 ]; do  
   echo $i
	i=$(($i+1))
	java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.9 carriersrep hubandspoke+v+100.loc -1 repalgoff 1000
done
i=1
while [ $i -le 30 ]; do
   echo $i
	i=$(($i+1))
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load hubandspoke+v+100.graph 10 0.9 carriersrep hubandspoke+v+100.loc -1 replalgon 1000
done
