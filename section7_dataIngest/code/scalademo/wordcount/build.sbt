name := "wordcount"
version := "1.0"
scalaVersion := "2.10.7"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.6.3"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.3"
libraryDependencies += "org.apache.spark" % "spark-streaming-flume-sink_2.10" % "1.6.3"
libraryDependencies += "org.apache.spark" % "spark-streaming-flume_2.10" % "1.6.3"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.3.2"
libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.6"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.3"
