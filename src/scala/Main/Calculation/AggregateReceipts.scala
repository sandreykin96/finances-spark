package scala.Main.Calculation

import org.apache.spark.sql.{SaveMode}
import org.apache.spark.sql.functions._
import scala.Main.SparkSessionWrapper
import scala.models.Receipt

object AggregateReceipts extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {
    var preparedReceipts = "src/resources/preparation/receipts"
    var aggregatedReceiptsByProductTypePath = "src/resources/aggregate/receiptsByProductType"
    var sumOfExpencesByUserAndPeriodPath = "src/resources/aggregate/sumOfExpencesByUserAndPeriod"

    import spark.implicits._

    val receiptsDs = spark
      .read.parquet(preparedReceipts)
      .as[Receipt]
      .map(Receipt(_))

    receiptsDs.show()

    var groupedByUserPeriodAndProductType = receiptsDs
      .groupBy("user_id", "period", "product_type")
      .agg(sum("sum").alias("total_wasted_sum"))
      .orderBy(desc("total_wasted_sum"))

    groupedByUserPeriodAndProductType.show()

    var sumOfExpencesByUserAndPeriod = receiptsDs
      .groupBy("user_id", "period")
      .agg(sum("sum").alias("total_wasted_sum"))
      .orderBy(desc("total_wasted_sum"))

    sumOfExpencesByUserAndPeriod.show()

    groupedByUserPeriodAndProductType.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(aggregatedReceiptsByProductTypePath)

    sumOfExpencesByUserAndPeriod.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(sumOfExpencesByUserAndPeriodPath)

    spark.stop()
  }
}

