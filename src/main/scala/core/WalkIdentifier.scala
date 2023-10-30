package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD
import util.SimRank.getSimilarityScore
import util.Config.Comparison.simThreshold

object WalkIdentifier {

  // returns a list of possible identities for each node in a walk
  def identifyNodes(graph: Graph[NodeObject, Action], path: RDD[NodeObject]): Map[Long, List[(Long, Double)]] = {
    var sims: Map[Long, List[(Long, Double)]] = Map()
    path.foreach{case (walkNode) =>
      val similarNodes: List[(Long, Double)] = List()
      graph.vertices.foreach{case (graphNode) =>
        val simScore = getSimilarityScore(graphNode._2, walkNode)
        if (simScore > simThreshold) {
          similarNodes.concat(List(graphNode._2.id.toLong, simScore))
        }
      }
      // insert similarNodes into sims
      sims += walkNode.id.toLong -> similarNodes
    }
    sims
  }

}
