package core

import NetGraphAlgebraDefs.{Action, NodeObject}
import org.apache.spark.SparkContext
import util.Config.GraphWalker.{chanceToEndWalk, maxWalkLength}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object GraphWalker {

  def randomWalk(sc: SparkContext, graph: Graph[NodeObject, Action]): RDD[NodeObject] = {
    val vertices = graph.vertices.collect()
    val initialNode = vertices(Random.nextInt(vertices.length))._2

    var currentNode = initialNode
    var path = ArrayBuffer[NodeObject]()
    for (_ <- 1 to maxWalkLength) {
      path += currentNode
      // decide whether to end walk abruptly or not
      if (Random.nextDouble() < chanceToEndWalk) {
        return sc.parallelize(path.toSeq)
      }
      // list of edges from current node
      val edges = graph.edges.filter(e => e.srcId == currentNode.id).collect()

      if (edges.nonEmpty) {
        // decide which edge to traverse
        val nextNode = edges(Random.nextInt(edges.length)).dstId
        currentNode = graph.vertices
          .filter{case (id, _) => id == nextNode}
          .collect()
          .headOption
          .get
          ._2
      } else {
        // terminating node, end walk here
        return sc.parallelize(path.toSeq)
      }
    }
    sc.parallelize(path.toSeq)
  }
}
