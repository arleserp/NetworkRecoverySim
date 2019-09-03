REM java  -classpath dist/NetworkRecoverySimulator.NetworkRecoverySim.jar graphutil.GraphGenerator smallworld 300 0.5 2
REM java  -Xmx4200m -classpath dist/NetworkRecoverySimulator.NetworkRecoverySim.jar graphutil.GraphGenerator scalefree 4 1 97
REM java  -Xmx4200m -classpath dist/NetworkRecoverySimulator.NetworkRecoverySim.jar graphutil.GraphGenerator communitycircle 100 0.5 4 4
REM java  -Xmx4200m -classpath dist/NetworkRecoverySimulator.NetworkRecoverySim.jar graphutil.GraphGenerator hubandspoke 100 
java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator circlelonghubandspoke 102 2 
java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator longhubandspoke 100 2
java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator longhubandspoke 100 3
REM java -classpath dist/NetworkRecoverySimulator.NetworkRecoverySim.jar graphutil.GraphGenerator foresthubandspoke 100 4 
pause