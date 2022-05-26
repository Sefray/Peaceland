ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "drone",
    idePackagePrefix := Some("peaceland"),
    libraryDependencies += "org.apache.kafka" %% "kafka" % "2.6.0"
  )
