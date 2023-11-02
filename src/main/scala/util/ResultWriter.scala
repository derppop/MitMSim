package util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.{BufferedWriter, FileWriter}
import models.ResultOutput


// outputs results of a job to yaml
object ResultWriter {
  def addToYaml(entry: ResultOutput, filePath: String): Unit = {
    val mapper = new ObjectMapper(new YAMLFactory())
    mapper.registerModule(DefaultScalaModule)

    val yamlString = mapper.writeValueAsString(entry)

    val fileWriter = new BufferedWriter(new FileWriter(filePath, true))
    try {
      fileWriter.write(yamlString)
      fileWriter.newLine()
    } finally {
      fileWriter.close()
    }
  }
}

