import org.apache.spark.SparkConf
import org.apache.spark.streaming._

object StreamingDepartmentCount {

    def main(args: Array[String]) {
	  val executionMode = args(0)
      val conf = new SparkConf().setAppName("Streaming word count").setMaster(executionMode)
      val ssc = new StreamingContext(conf, Seconds(30))
      val messages = ssc.socketTextStream(args(1),args(2).toInt)
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