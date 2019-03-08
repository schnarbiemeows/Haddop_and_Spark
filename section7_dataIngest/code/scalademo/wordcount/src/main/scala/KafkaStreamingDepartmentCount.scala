import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder

object KafkaStreamingDepartmentCount {

    def main(args: Array[String]) {
	  val executionMode = args(0)
      val conf = new SparkConf().setAppName("Kafka Streaming word count").setMaster(executionMode)
      val ssc = new StreamingContext(conf, Seconds(30))
	  val kafkaParams = Map[String,String]("metadata.broker.list" -> "nn01.itversity.com:6667,nn02.itversity.com:6667,rm01.itversity.com:6667") 
	  val topicSet = Set("kafkademoschnarbs")
	  val directKafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
	  kafkaParams, topicSet)
      val messages = directKafkaStream.map(s => s._2)
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