import scala.io.Source

object OrderRevenue {

	def main(args: Array[String]) = {
		val orderNumber = args(0).toInt
		val orderItems = Source.fromFile("C:\\Hadoop\\certification\\InstructorsGITrepo\\data\\retail_db\\order_items\\part-00000").getLines
		val orderRevenue = orderItems.filter(orderItem => orderItem.split(",")(1).toInt == orderNumber).map(orderItem => orderItem.split(",")(4).toFloat).reduce((x,y) => x+y)
		println(orderRevenue)
	}
}