ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
val sparkVersion = "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "office",
    libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "3.2.0",
    libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion,
    libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,

    libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion,
    libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion,

    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

  )
