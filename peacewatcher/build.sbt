import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "peacewatcher",
    // idePackagePrefix := Some("peaceland"),
    libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.5",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.30.0",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
  )
