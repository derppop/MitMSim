package app

import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import NetGraphAlgebraDefs.NodeObject
import core.GraphWalker.randomWalk
import core.HoneypotIdentifier.identifyHoneypots
import core.NodeIdentifier.identifyNodes
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import core.Attacker.attackNodes
import core.Evaluator.evaluateDecision
import NetGraphAlgebraDefs._
import Utilz.NGSConstants
import models.ResultOutput
import org.apache.spark.graphx.Graph
import org.slf4j.LoggerFactory
import util.Config.Job.{maxIterations, resultDirectory, graphDirectory}
import util.GraphLoader.loadGraphX
import util.ResultWriter.addToYaml
import java.io.File

import scala.annotation.tailrec

object Driver {
  private val logger = LoggerFactory.getLogger(Driver.getClass)

  // entry point for project, starts the initial process
  def startProcess(): Unit = {
    logger.info("Running preprocessing")
    val currentDir = new java.io.File(".").getCanonicalPath
    val graphPath = new File(currentDir, graphDirectory)
    val resultPath = new File(currentDir, resultDirectory)
    checkDirectory(graphPath)
    checkDirectory(resultPath)

    val conf = new SparkConf().setAppName("MitMSim").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val (graphName, perturbedGraphName) = generateGraphs()
    val originalGraph = loadGraphX(sc, graphName)
    val perturbedGraph = loadGraphX(sc, perturbedGraphName)
    perturbedGraph.get.edges.foreach(x => {
      println(s"${x.srcId} - ${x.dstId}")
    })
    val valuableNodes: Set[Long] = originalGraph.get.vertices.collect {
      case (id, node) if (node.valuableData) => id.toLong
    }.collect().toSet

    runJobs(1, sc, originalGraph, perturbedGraph, valuableNodes)
  }

  // creates graph and perturbs it using NetGameSim
  private def generateGraphs(): (String, String) = {
    // Generate graph
    val graph: NetGraph = NetModelAlgebra().get
    val graphName = NGSConstants.OUTPUTFILENAME
    val perturbedGraphName = graphName + ".perturbed"
    graph.persist(outputDirectory, graphName) // Serialize original graph
    // Perturb graph
    val (perturbedGraph, changes): GraphPerturbationAlgebra#GraphPerturbationTuple = GraphPerturbationAlgebra(graph.copy)
    perturbedGraph.persist(outputDirectory, perturbedGraphName) // Serialize perturbed graph
    GraphPerturbationAlgebra.persist(changes, outputDirectory.concat(graphName.concat(".yaml")))
    (graphName, perturbedGraphName)
  }

  // recursive function to run iterations of jobs
  @tailrec
  private def runJobs(iteration: Int, sc: SparkContext, originalGraph: Option[Graph[NodeObject, Action]], perturbedGraph: Option[Graph[NodeObject, Action]], valuableNodes: Set[Long]): Unit = {
    val (path, decisionToAttack, result) = startJob(sc, originalGraph.get, perturbedGraph.get, valuableNodes)
    if (iteration >= maxIterations) {
      logger.info(s"Job reached max iterations: $maxIterations")
      return
    }
    logger.info(s"Starting a new job at iteration $iteration")
    if (result.isDefined) {
      val entry = new ResultOutput(iteration, path.collect().map(_.id).mkString(" -> "), if (decisionToAttack) "Attack" else "Do not attack", if (result.get) "Success" else "Failure")
      if (result.get) {
        addToYaml(entry, resultDirectory + "/result")
        runJobs(iteration + 1, sc, originalGraph, perturbedGraph, valuableNodes)
      } else {
        addToYaml(entry, resultDirectory + "/result")
        logger.info("Attack failed")
      }
    } else {
      runJobs(iteration + 1, sc, originalGraph, perturbedGraph, valuableNodes) // didnt find valuable nodes, didnt attack, do not output
    }
  }

  // returns the path of walk, decision to attack, and results of the attack (success or failure)
  private def startJob(sc: SparkContext, originalGraph: Graph[NodeObject, Action], perturbedGraph: Graph[NodeObject, Action], valuableNodes: Set[Long]): (RDD[NodeObject], Boolean, Option[Boolean]) = {
    val path: RDD[NodeObject] = randomWalk(sc, perturbedGraph)
    val simNodes = identifyNodes(originalGraph, path)
    val honeypots = identifyHoneypots(path, simNodes)
    var attackList = sc.emptyRDD[(Long, Long)]
    if (honeypots.isEmpty) { // no honeypots detected, attack nodes in walk
      attackList = attackNodes(simNodes, valuableNodes)
    }
    val success: Option[Boolean] = evaluateDecision(originalGraph, attackList, path, valuableNodes)
    (path, !attackList.isEmpty(), success)
  }

  // ensures if an output directory exists and is empty
  private def checkDirectory(path: File): Unit = {
    if (path.exists()) {
      logger.info(s"Clearing path at ${path.getAbsolutePath}")
      path.listFiles().foreach { file =>
        if (!file.delete) {
          logger.warn(s"Failed to delete ${file.getAbsolutePath}")
        }
      }
    } else {
      logger.info(s"Path at ${path.getAbsolutePath} does not exist, creating it")
      if (!path.mkdirs()) {
        logger.warn(s"Failed to create ${path.getAbsolutePath}")
      }
    }
  }

}
