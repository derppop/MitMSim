package util

import NetGraphAlgebraDefs.NetGraph.logger
import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import NetGraphAlgebraDefs.{Action, NetGraphComponent, NodeObject}
import org.apache.spark.SparkContext
import org.slf4j.LoggerFactory
import org.apache.spark.graphx.{Edge, EdgeRDD, Graph, VertexRDD}

import java.io.{FileInputStream, ObjectInputStream}
import scala.util.Try

object GraphLoader {
  private val logger = LoggerFactory.getLogger(GraphLoader.getClass)
  def loadGraphX(sc: SparkContext, fileName: String, dir: String = outputDirectory): Option[Graph[NodeObject, Action]] = {
        logger.info(s"Loading the NetGraph from $dir$fileName")

    Try(new FileInputStream(s"$dir$fileName"))
      .map(fis => (fis, new ObjectInputStream(fis)))
      .map { case (fis, ois) =>
        val ng = ois.readObject().asInstanceOf[List[NetGraphComponent]]
        logger.info(s"Deserialized the object $ng")
        ois.close()
        fis.close()
        ng
      }
      .toOption
      .flatMap { lstOfNetComponents =>
        val nodes = lstOfNetComponents.collect { case node: NodeObject => node }
        val edges = lstOfNetComponents.collect { case edge: Action => edge }

        // build vertex and edge RDDs on spark cluster
        val vertexRDD: VertexRDD[NodeObject] = VertexRDD.apply(sc.parallelize(nodes.map(n => (n.id.toLong, n))))
        val edgeRDD: EdgeRDD[Action] = EdgeRDD.fromEdges(sc.parallelize(edges.map(e => Edge(e.fromNode.id.toLong, e.toNode.id.toLong, e))))

        val graph = Graph(vertexRDD, edgeRDD)

        Some(graph)
      }
  }
}
