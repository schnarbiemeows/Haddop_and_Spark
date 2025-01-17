import org.apache.spark.{SparkContext,SparkConf}

object DailyRevenue {
	def main(args: Array[String]) = {
		val conf = new SparkConf().setMaster(args(3)).setAppName("Daily Revenue")
		val sc = new SparkContext(conf)

		val baseDir = args(0)
		val productsPath = args(1)
		val outputPath = args(2)
		val orders = sc.textFile(baseDir + "orders")
		val orderItems = sc.textFile(baseDir + "order_items")
		val ordersFiltered = orders.filter(rec => rec.split(",")(3) == "COMPLETE" || rec.split(",")(3) == "CLOSED")
		val ordersMap = ordersFiltered.map(rec => (rec.split(",")(0).toInt, rec.split(",")(1)))
		val orderItemsMap = orderItems.map(rec => (rec.split(",")(1).toInt, (rec.split(",")(2).toInt, rec.split(",")(4).toFloat)))
		val ordersJoin = ordersMap.join(orderItemsMap)
		val ordersJoinMap = ordersJoin.map(rec => ((rec._2._1, rec._2._2._1),rec._2._2._2))
		val ordersJoinDailyRevenue = ordersJoinMap.reduceByKey((rec, y) => rec + y)
		val productsLocalData = scala.io.Source.fromFile(productsPath).getLines.toList
		val products = sc.parallelize(productsLocalData)
		val filtered_products = products.filter(x => x.split(",")(4) != "")
		val tupled_products = filtered_products.map(rec => (rec.split(",")(0).toInt,rec.split(",")(2)))
		val ordersJoinDailyRevenueMap = ordersJoinDailyRevenue.map(rec => (rec._1._2, (rec._1._1, rec._2)))
		val ordersJoinProducts = ordersJoinDailyRevenueMap.join(tupled_products)
		val ordersJoinProductsMap = ordersJoinProducts.map(rec => ((rec._2._1._1, rec._2._1._2) , rec._2._2))
		val partialSortedRecords = ordersJoinProductsMap.sortByKey(false)
		val partialSortedRecordsMap = partialSortedRecords.map(rec => (rec._1._1, (rec._1._2, rec._2)))
		val finalSortedRecords = partialSortedRecordsMap.sortByKey(true)
		val finalRecords = finalSortedRecords.map(rec => rec.toString).map(rec => rec.replace("(","")).map(rec => rec.replace(")",""))
		finalRecords.saveAsTextFile(outputPath + "local_daily_revenue_txt_scala_results")

	}	
}