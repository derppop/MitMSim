package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD

object Evaluator {
  private def pathContainsHoneypots(originalGraph: Graph[NodeObject, Action], path: RDD[NodeObject]): Boolean = {
    val idsInGraph = originalGraph.vertices.map(_._1).collect().toSet
    path.map(node => !idsInGraph.contains(node.id)).reduce(_ || _)
  }

  private def pathContainsValuableDate(valuableData: Set[Long], path: RDD[NodeObject]): Boolean = {
    path.map(node => valuableData.contains(node.id)).reduce(_ || _)
  }

  def evaluateDecision(originalGraph:  Graph[NodeObject, Action], attackList: RDD[(Long, Long)], path: RDD[NodeObject], valuableNodes: Set[Long]): Boolean = {
    // if attacked
      // if honeypot is in path: false
      // if attackList doesnt contain valuable data: false
      // else: true
    // if didnt attack
      // if honeypot is in path: true
      // if honeypot is not in path and path contains valuable data: false
      // if honeypot is not in path and path doesnt contain valuable data: true

    if (pathContainsHoneypots(originalGraph, path)) {
      if (attackList.isEmpty()) true else false // if attacked a honeypot, failure
    } else { // path doesnt contain honeypots
      // if valuable data and attacked: true
      // else: false
      if (pathContainsValuableDate(valuableNodes, path)) {
        if (attackList.isEmpty()) false else true // attacked nodes with valuable data and no honeypots, success
      } else {
        if (attackList.isEmpty()) true else false
      }
    }
  }
}
