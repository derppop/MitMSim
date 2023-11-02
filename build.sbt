
ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.10"

val scalaTestVersion = "3.2.15"
val guavaVersion = "31.1-jre"
val typeSafeConfigVersion = "1.4.2"
val logbackVersion = "1.2.10"
val sfl4sVersion = "2.0.0-alpha5"
val graphVizVersion = "0.18.1"
val netBuddyVersion = "1.14.4"
val catsVersion = "2.9.0"
val apacheCommonsVersion = "2.13.0"
val scalaParCollVersion = "1.0.4"
val sparkVersion = "3.5.0"

lazy val commonDependencies = Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % scalaParCollVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalatestplus" %% "mockito-4-2" % "3.2.12.0-RC2" % Test,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion excludeAll(
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.slf4j.impl")
  ),
  "net.bytebuddy" % "byte-buddy" % netBuddyVersion,
  "org.apache.hadoop" % "hadoop-common" % "3.3.6",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.6",
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.6",
  "org.graphstream" % "gs-core" % "2.0",
  "org.yaml" % "snakeyaml" % "2.0",
  "org.mockito" % "mockito-core" % "5.2.0" % Test,
  "org.apache.mrunit" % "mrunit" % "1.1.0" % Test classifier "hadoop2",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-graphx" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.3",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.12.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.3",
  "com.google.guava" % "guava" % guavaVersion
).map(_.exclude("org.slf4j", "*"))

lazy val root = (project in file("."))
  .settings(
    name := "MitMSim"
  )

Compile / run / mainClass := Some("app.Main")

unmanagedBase := baseDirectory.value / "src" / "main" / "resources" / "lib"

scalacOptions ++= Seq("-Ytasty-reader")


libraryDependencies ++= commonDependencies

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}