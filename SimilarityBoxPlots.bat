java -classpath dist/NetworkRecoverySim.jar graphmetrics.BoxPlotSimilarityVsRound . 1000 650 5
java -classpath dist/NetworkRecoverySim.jar graphmetrics.CompiledBoxPlotSimilarityVsRound . 400 400
java -classpath dist/NetworkRecoverySim.jar graphmetrics.MemoryConsumption 
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalNSent
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalNRecv
REM java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumption
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalSizeSent
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalSizeRecv
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NumberOfMobileAgents

java -classpath dist/NetworkRecoverySim.jar graphmetrics.MemoryConsumptionMa 
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalNSentMa
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalNRecvMa
REM java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumption
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalSizeSentMa
java -classpath dist/NetworkRecoverySim.jar graphmetrics.NetworkConsumptionLocalSizeRecvMa


