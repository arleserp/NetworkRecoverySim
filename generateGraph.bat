REM java  -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator smallworld 100 0.5 2
REM java  -Xmx4200m -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator scalefree 4 1 97
REM java  -Xmx4200m -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator communitycircle 100 0.5 4 4
REM java  -Xmx4200m -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator hubandspoke 100 
REM java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator circlelonghubandspoke 97 2 
REM java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator longhubandspoke 97 2
REM java   -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator longhubandspoke 97 3

java -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator foresthubandspoke 100 4 
REM java -classpath dist/NetworkRecoverySim.jar graphutil.GraphGenerator comunityspokegraph communitycircle+v+100+beta+0.5+degree+4+clusters+4.graph 5 2

pause