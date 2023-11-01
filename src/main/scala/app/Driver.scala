package app

import NetGraphAlgebraDefs.NodeObject
import core.GraphWalker.randomWalk
import core.HoneypotIdentifier.identifyHoneypots
import core.NodeIdentifier.identifyNodes
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import core.Attacker.attackNodes
import core.Evaluator.evaluateDecision

import NetGraphAlgebraDefs._

import org.apache.spark.graphx.Graph

object Driver {

  // starts a job
  // returns the path of walk, decision to attack, and results of the attack (success or failure)
  def startJob(sc: SparkContext, originalGraph: Graph[NodeObject, Action], perturbedGraph: Graph[NodeObject, Action], valuableNodes: Set[Long]): (RDD[NodeObject], Boolean, Boolean) = {
    val path: RDD[NodeObject] = randomWalk(sc, perturbedGraph)
    val simNodes = identifyNodes(originalGraph, path)
    val honeypots = identifyHoneypots(path, simNodes)
    var attackList = sc.emptyRDD[(Long, Long)]
    if (honeypots.isEmpty) { // no honeypots detected, attack nodes in walk
      attackList = attackNodes(simNodes, valuableNodes)
    }
    val success: Boolean = evaluateDecision(originalGraph, attackList, path, valuableNodes)
    (path, attackList.isEmpty(), success)
  }

}
