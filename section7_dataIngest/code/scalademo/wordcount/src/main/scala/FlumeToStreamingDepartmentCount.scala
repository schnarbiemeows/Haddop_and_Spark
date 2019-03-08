import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.flume._

object FlumeToStreamingDepartmentCount {

    def main(args: Array[String]) {
	  val executionMode = args(0)
      val conf = new SparkConf().setAppName("Flume Streaming word count").setMaster(executionMode)
      val ssc = new StreamingContext(conf, Seconds(30))
      val stream = FlumeUtils.createPollingStream(ssc, args(1),args(2).toInt)
	  val messages = stream.map(msg => new String(msg.event.getBody.array()))
	  val filteredLines = messages.filter(rec => {
		val endpoint = rec.split(" ")(6)
		endpoint.split("/")(1) == "department"
	  })
      val departments = filteredLines.map(rec => ({
		val endpoint = rec.split(" ")(6)
		endpoint.split("/")(2)
	  },1))
      val departmentTraffic = departments.reduceByKey((x,y) => x+y)
	  departmentTraffic.saveAsTextFiles("/user/schnarbies/deptwisetraffic/cnt")
      ssc.start()
	  ssc.awaitTermination()
    }
}