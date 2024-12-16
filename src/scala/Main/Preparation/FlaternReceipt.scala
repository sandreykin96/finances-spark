package scala.Main.Preparation

import org.apache.spark.sql.{SaveMode}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.Main.SparkSessionWrapper
import org.apache.spark.sql.{SparkSession, functions => F}

object FlaternReceipt extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {

    var savedReceipts = "src/resources/ingest/receipts"
    var preparedReceipts = "src/resources/preparation/receipts"

    import spark.implicits._

    val receiptsDF = spark
      .read.parquet(savedReceipts)

    receiptsDF.show()

    val itemSchema = new StructType()
      .add("name", StringType)
      .add("price", DoubleType)
      .add("quantity", IntegerType)
      .add("sum", DoubleType)
      .add("nds", IntegerType)
      .add("nds_sum", DoubleType)
      .add("product_type", IntegerType)
      .add("payment_type", IntegerType)

    // Плоский DataFrame
    val flattenedDF = receiptsDF
      .withColumn("items", from_json($"items", ArrayType(itemSchema))) // Преобразуем JSON-строку в массив
      .withColumn("item", explode($"items")) // Разворачиваем массив items
      .select(
        $"user_id",
              $"created_at",
              $"item.name".as("name"),
              $"item.price".as("price"),
              $"item.quantity".as("quantity"),
              $"item.sum".as("sum"),
              $"item.nds".as("nds"),
              $"item.nds_sum".as("nds_sum"),
              $"item.product_type".as("product_type"),
              $"item.payment_type".as("payment_type"),
    ) // Извлекаем нужные поля
      .withColumn("period", F.date_format($"created_at", "yyyyMM"))
      .withColumn("user_id", col("user_id").cast(IntegerType))
      .withColumn("created_at", col("created_at").cast(TimestampType))

    flattenedDF.show(50)

    flattenedDF.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(preparedReceipts)

    spark.stop()
  }
}

