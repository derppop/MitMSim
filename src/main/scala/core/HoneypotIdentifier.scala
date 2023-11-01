package core

import NetGraphAlgebraDefs.NodeObject
import org.apache.spark.rdd.RDD

object HoneypotIdentifier {

  def identifyHoneypots(path: RDD[NodeObject], simNodes: RDD[(Long, (Long, Double))]): List[NodeObject] = {
    // find node in path that dont have a match
    // return a list of these nodes
    val matchedNodes = simNodes.keys.collect().toSet
    path.filter(node => !matchedNodes.contains(node.id.toLong)).collect().toList
  }

}
