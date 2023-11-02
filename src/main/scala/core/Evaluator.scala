package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD

// judges whether a decision by MitMSim was a success, failure, or neither
object Evaluator {
  private def pathContainsHoneypots(originalGraph: Graph[NodeObject, Action], path: RDD[NodeObject]): Boolean = {
    val idsInGraph = originalGraph.vertices.map(_._1).collect().toSet
    path.map(node => !idsInGraph.contains(node.id)).reduce(_ || _)
  }

  private def pathContainsValuableDate(valuableData: Set[Long], path: RDD[NodeObject]): Boolean = {
    path.map(node => valuableData.contains(node.id)).reduce(_ || _)
  }

  def evaluateDecision(originalGraph:  Graph[NodeObject, Action], attackList: RDD[(Long, Long)], path: RDD[NodeObject], valuableNodes: Set[Long]): Option[Boolean] = {
    if (pathContainsHoneypots(originalGraph, path)) {
      if (attackList.isEmpty()) Some(true) else Some(false) // if attacked a honeypot, failure
    } else { // path doesnt contain honeypots
      // if valuable data and attacked: true
      // else: false
      if (pathContainsValuableDate(valuableNodes, path)) {
        if (attackList.isEmpty()) Some(false) else Some(true) // attacked nodes with valuable data and no honeypots, success
      } else {
        if (attackList.isEmpty()) None else Some(false)
      }
    }
  }
}
