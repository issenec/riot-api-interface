name := "riot-api-interface"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.5"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

val akkaVersion = "2.3.9"

val sparkVersion = "1.5.0"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.7.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "io.spray" %% "spray-client" % "1.3.3",
  "org.elasticsearch" % "elasticsearch" % "1.7.1" % "provided",
  "org.elasticsearch" %% "elasticsearch-spark" % "2.1.1",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-graphx" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.specs2" %% "specs2-core" % "3.6.4" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")