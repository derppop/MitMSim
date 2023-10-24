
import NetGraphAlgebraDefs.*
import NetGraphAlgebraDefs.NetModelAlgebra.{outputDirectory, perturbationCoeff, propValueRange}
import Utilz.NGSConstants
import java.io.{DataInput, DataInputStream, DataOutput, DataOutputStream, File, FileInputStream, FileOutputStream}
import scala.collection.mutable
import scala.collection.mutable.Set
import scala.jdk.CollectionConverters.*
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.rdd.RDD
//import org.apache.spark.graphx._
//import org.apache.spark.SparkContext

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

  def loadGraphX(fileName: String, dir: String= outputDirectory): Option[Graph[NodeObject, Action]] = {

  }

  def main(args: Array[String]): Unit = {
    val (graphName, perturbedGraphName) = generateGraphs()
  }
}
