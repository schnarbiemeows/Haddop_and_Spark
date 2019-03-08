import org.apache.spark.SparkConf
import org.apache.spark.streaming._

object wordCount {

    def main(args: Array[String]) {
	  val executionMode = args(0)
      val conf = new SparkConf().setAppName("Streaming word count").setMaster(executionMode)
      val ssc = new StreamingContext(conf, Seconds(10))
      val lines = ssc.socketTextStream(args(1),args(2).toInt)
      val words = lines.flatMap(line => line.split(" "))
      val tuples = words.map(rec => (rec,1))
      val wordCounts = tuples.reduceByKey((x,y) => x+y)
      wordCounts.print()
      ssc.start()
	  ssc.awaitTermination()
    }
}
