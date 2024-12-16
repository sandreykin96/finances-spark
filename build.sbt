ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "2.12.15"

val sparkVersion = "3.2.1"
val kafkaClientsVersion = "2.3.0"
val sparkHiveVersion = "3.2.0"

lazy val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % "0.11.2")

lazy val sparkDependencies = Seq(
  "org.apache.spark"         %% "spark-sql"            % sparkVersion,
  "io.netty"          % "netty-all"                   % "4.1.97.Final",
  "org.apache.spark"         %% "spark-streaming"     % sparkVersion,
  "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion,
  "org.postgresql" % "postgresql" % "42.2.18",
  "org.apache.spark"         %% "spark-hive"          % sparkHiveVersion,
  "org.apache.hadoop" % "hadoop-common" % "2.7.4",
  "org.apache.hadoop" % "hadoop-auth" % "3.2.3",
  "org.apache.kafka"         % "kafka-clients"        % kafkaClientsVersion,
  "org.apache.spark"         % "spark-sql-kafka-0-10_2.12" % sparkVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "SparkStreaming",
    libraryDependencies ++= sparkDependencies ++ circeDependencies,
    javacOptions ++= Seq("-source", "16"),
    javaOptions ++= Seq( // Spark-specific JVM options
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
    ),
    compileOrder := CompileOrder.JavaThenScala
  )