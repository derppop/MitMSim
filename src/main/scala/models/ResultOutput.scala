package models

// used to output results in yaml format
class ResultOutput(iteration: Int, path: String, decision: String, outcome: String) {
  def getIteration: Int = iteration
  def getPath: String = path
  def getDecision: String = decision
  def getOutcome: String = outcome
}
