# CS 441 HW 2 - Man in the middle sim

### Joseph David - jdavid31@uic.edu

## Functionality
GraphComparison utilizes [netgamesim](https://github.com/0x1DOCD00D/NetGameSim) for graph generation, perturbation, 
and serialization. The graphs are then deserialized into GraphX instances.
After deserializing the graph a job is started, a job consists of producing a random
walk on the perturbed graph, attempting to identify each node in the walk via [simrank](src/main/scala/util/SimRank.scala), 
then using those identities to detect honeypots and valuable data within that walk.
Finally, the program decides based on a few factors whether to attack or not. These factors include
honeypot and valuable data presence within the walk. After the program makes its decision it is evaluated,
if the program correctly attacked nodes that contained valuable data it is considered a success, if the program
attacks a node that is a honeypot or nodes that dont contain valuable data it is considered a failure, if there are
no honeypots and no nodes containing valuable data neither success or failure are possible. If the decision made
by the program is a success the results are outputted to a yaml file specified in the config file and the program
will continue to another iteration where another random walk is generated and the next job starts, if the decision 
is a failure then the results will also be outputted to the yaml file but the process will terminate, if the decision
is not a failure or success nothing will be outputted to the yaml file and the next iteration will start.


## Usage
Use the following command to produce the project jar.
 ````bash
 sbt assembly
 ````
Once the jar is built, the job can be run locally with the following command.
````
spark-submit --class app.Main --master local[4] --driver-class-path MitMSim-assembly-1.0.0.jar MitMSim-assembly-1.0.0.jar
````

Spark 3.5.0 and Scala 2.13.10 was used to develop and test this project

## Configuration
The project configuration variables can be found in [application.conf](src/main/resources/application.conf).  
Along with the configurations for graph generation and perturbation that are inherited from [netgamesim](https://github.com/0x1DOCD00D/NetGameSim)
the following is a list of config variables specific to this project.

### Job
* maxIterations - The maximum number of iterations/walks to be computed in a single run of the program
* graphDirectory - Local directory for graphs to be outputted and read from
* resultDirectory - The folder you want the results of the job to be stored

### GraphWalker
* maxWalkLength - The maximum number of nodes that will be visited per walk
* chanceToEndWalk - The percent chance for a walk to end abruptly at each node in the walk

### Comparison
* simThreshold - The minimum similarity score to consider a match between nodes valid

### SimRank
* propertySimWeight - Weight of properties similarity in computing similarity score
* childrenSimWeight - Weight of children similarity in computing similarity score
* depthSimWeight - Weight of depth similarity in computing similarity score
* branchFactorSimWeight - Weight of branchFactor similarity in computing similarity score
* storedValSimWeight - Weight of storedVal similarity in computing similarity score

[Local demo](https://youtu.be/rwVXnIllb3E)