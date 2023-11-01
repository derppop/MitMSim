package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import util.SimRank.getSimilarityScore
import util.Config.Comparison.simThreshold

object NodeIdentifier {

  // returns a list of possible identities for each node in a walk
  def identifyNodes(graph: Graph[NodeObject, Action], path: RDD[NodeObject]): RDD[(Long, (Long, Double))] = {
    // create all pairs between walk nodes and graph nodes
    val pairs = path.cartesian(graph.vertices)

    // get similarity for each pair and filter based on threshold
    val filteredPairs = pairs.flatMap { case (walkNode, (graphNodeId, graphNode)) =>
      val simScore = getSimilarityScore(graphNode, walkNode)
      if (simScore > simThreshold) Some((walkNode.id.toLong, (graphNodeId.toLong, simScore))) else None
    }

    // map nodes with highest similarity
    val grouped = filteredPairs.groupByKey().mapValues(iter => iter.maxBy(_._2))
    grouped
  }

}
