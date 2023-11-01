package core

import NetGraphAlgebraDefs.NodeObject
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object Attacker {

  def attackNodes(nodeIdentities: RDD[(Long, (Long, Double))], valuableNodes: Set[Long]): RDD[(Long, Long)] = {
    // compare matched nodes in walk with nodes that have valuable data and return intersection
    nodeIdentities.flatMap {case (nodeId, (matchedNodeId, _)) =>
      if (valuableNodes.contains(matchedNodeId)) Some((nodeId, matchedNodeId)) else None
    }
  }
}
