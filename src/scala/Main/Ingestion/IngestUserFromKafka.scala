package scala.Main.Ingestion

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.{SaveMode, functions}
import org.apache.spark.sql.types._
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
      .withColumn("user_id", functions.json_tuple(functions.col("value"), "id"))
      .withColumn("name", functions.json_tuple(functions.col("value"), "name"))
      .withColumn("email", functions.json_tuple(functions.col("value"), "email"))
      .withColumn("saving_goal", functions.json_tuple(functions.col("value"), "saving_goal"))
      .withColumn("amount_of_money", functions.json_tuple(functions.col("value"), "amount_of_money"))
      .withColumn("debt", functions.json_tuple(functions.col("value"), "debt"))
      .withColumn("income", functions.json_tuple(functions.col("value"), "income"))
      .withColumn("user_id", col("user_id").cast(IntegerType))
      .withColumn("saving_goal", col("saving_goal").cast(DoubleType))
      .withColumn("amount_of_money", col("amount_of_money").cast(DoubleType))
      .withColumn("debt", col("debt").cast(DoubleType))
      .withColumn("income", col("income").cast(DoubleType))

      .drop("value")

    users.show()

    users.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .save(savedUsers)

    spark.stop()
  }
}
