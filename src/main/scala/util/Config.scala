package util
import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object GraphWalker {
    private val graphWalkerConfig = config.getConfig("GraphWalker")
    val maxWalkLength: Int = graphWalkerConfig.getInt("maxWalkLength")
    val chanceToEndWalk: Double = graphWalkerConfig.getDouble("chanceToEndWalk")
  }

}
