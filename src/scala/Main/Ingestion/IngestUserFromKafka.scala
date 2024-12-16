package scala.Main.Ingestion

import org.apache.spark.sql.{SaveMode, functions}

import scala.Main.SparkSessionWrapper

object IngestUserFromKafka extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {
    var host = "localhost:9092"
    var topic = "user"
    var savedUsers = "src/resources/ingest/users"

    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", host)
      .option("subscribe", topic)
      .load()

    val users = df.selectExpr("CAST(value AS STRING)")
      .withColumn("id", functions.json_tuple(functions.col("value"), "id"))
      .withColumn("name", functions.json_tuple(functions.col("value"), "name"))
      .withColumn("email", functions.json_tuple(functions.col("value"), "email"))
      .withColumn("saving_goal", functions.json_tuple(functions.col("value"), "saving_goal"))
      .withColumn("amount_of_money", functions.json_tuple(functions.col("value"), "amount_of_money"))
      .withColumn("debt", functions.json_tuple(functions.col("value"), "debt"))
      .withColumn("income", functions.json_tuple(functions.col("value"), "income"))
      .drop("value")

    users.show()

    users.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(savedUsers)

    spark.stop()
  }
}
