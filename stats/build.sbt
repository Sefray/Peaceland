name := "stats"

version := "0.1"

scalaVersion := "2.12.10"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.2"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.5"
libraryDependencies += "com.google.code.gson" % "gson" % "2.7"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.2.0"