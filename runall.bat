for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.5+degree+2.graph 10 0 carriersrep smallworld+v+100+beta+0.5+degree+2.loc -1 replalgon 1000
 rem pause
)
for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 rem pause
)


for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.03 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 replalgon 1000
 rem pause
)
for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.03 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 rem pause
)

for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.05 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 replalgon 1000
 rem pause
)
for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.05 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 rem pause
)

for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.07 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 replalgon 1000
 rem pause
)
for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.07 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 rem pause
)

for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.09 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 replalgon 1000
 rem pause
)
for /L %%x in (1, 1, 30) do (
  rem echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.09 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 rem pause
)


for /L %%x in (1, 1, 30) do (
  echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.1 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 repalgoff 1000
 pause
)

for /L %%x in (1, 1, 30) do (
  echo %%x%round
  java  -Xmx4200m -classpath dist/NetworkSimulator.jar unalcol.agents.NetworkSim.DataReplicationMain load smallworld+v+100+beta+0.1+degree+2.graph 10 0.1 carriersrep smallworld+v+100+beta+0.1+degree+2.loc -1 replalgon 1000
 pause
)
