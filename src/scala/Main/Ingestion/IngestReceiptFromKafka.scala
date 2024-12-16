package scala.Main.Ingestion

import org.apache.spark.sql.{SaveMode, functions}

import scala.Main.SparkSessionWrapper

object IngestReceiptFromKafka extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {
    var host = "localhost:9092"
    var topic = "receipt"
    var savedReceipts = "src/resources/ingest/receipts"

    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", host)
      .option("subscribe", topic)
      .load()

    val receiptValues = df.selectExpr("CAST(value AS STRING)")
      .withColumn("user", functions.json_tuple(functions.col("value"), "user"))
      .withColumn("created_at", functions.json_tuple(functions.col("value"), "created_at"))
      .withColumn("items", functions.json_tuple(functions.col("value"), "items"))
      .drop("value")

    receiptValues.show()

    receiptValues.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(savedReceipts)

    spark.stop()
  }
}
