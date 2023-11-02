package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object GraphWalker {
    private val graphWalkerConfig = config.getConfig("GraphWalker")
    val maxWalkLength: Int = graphWalkerConfig.getInt("maxWalkLength")
    val chanceToEndWalk: Double = graphWalkerConfig.getDouble("chanceToEndWalk")
  }

  object SimRank {
    private val simRankConfig = config.getConfig("SimRank")
    val propertySimWeight: Double = simRankConfig.getString("propertySimWeight").toDouble
    val childrenSimWeight: Double = simRankConfig.getString("childrenSimWeight").toDouble
    val depthSimWeight: Double = simRankConfig.getString("depthSimWeight").toDouble
    val branchFactorSimWeight: Double = simRankConfig.getString("branchFactorSimWeight").toDouble
    val storedValSimWeight: Double = simRankConfig.getString("storedValSimWeight").toDouble
  }

  object Comparison {
    private val comparisonConfig = config.getConfig("Comparison")
    val simThreshold: Double = comparisonConfig.getDouble("simThreshold")
  }

  object Job {
    private val jobConfig = config.getConfig("Job")
    val resultDirectory: String = jobConfig.getString("resultDirectory")
    val maxIterations: Int = jobConfig.getInt("maxIterations")
    val graphDirectory: String = jobConfig.getString("graphDirectory")
  }

}
