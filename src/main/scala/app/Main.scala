package app

import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import NetGraphAlgebraDefs._
import Utilz.NGSConstants
import org.apache.spark.{SparkConf, SparkContext}
import util.GraphLoader.loadGraphX


object Main {
  def generateGraphs(): (String, String) = {
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



  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MitMSim").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val (graphName, perturbedGraphName) = generateGraphs()
    val originalGraph = loadGraphX(sc, graphName)
    val perturbedGraph = loadGraphX(sc, perturbedGraphName)

  }
}