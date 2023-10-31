package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import util.SimRank.getSimilarityScore
import util.Config.Comparison.simThreshold

object NodeIdentifier {

  // returns a list of possible identities for each node in a walk
  def identifyNodes(graph: Graph[NodeObject, Action], path: RDD[NodeObject]): RDD[(Long, List[(Long, Double)])] = {
    // create all pairs between walk nodes and graph nodes
    val pairs = path.cartesian(graph.vertices)

    // get similarity for each pair and filter based on threshold
    val filteredPairs = pairs.flatMap { case (walkNode, (graphNodeId, graphNode)) =>
      val simScore = getSimilarityScore(graphNode, walkNode)
      if (simScore > simThreshold) Some((walkNode.id.toLong, (graphNodeId.toLong, simScore))) else None
    }

    // group by walk node ID and collect similar nodes
    val grouped = filteredPairs.groupByKey().mapValues(_.toList)

    grouped
  }

}
