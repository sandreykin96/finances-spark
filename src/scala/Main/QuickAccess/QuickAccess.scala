package scala.Main.QuickAccess

import org.apache.spark.sql.SaveMode

import org.apache.spark.sql.functions._
import scala.Main.SparkSessionWrapper
import scala.models.{User, UserMonthResult}

object QuickAccess extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {
    // var aggregatedReceiptsByProductTypePath = "src/resources/aggregate/receiptsByProductType"
    var sumOfExpensesByUserAndPeriodPath = "src/resources/aggregate/sumOfExpencesByUserAndPeriod"
    var savedUsers = "src/resources/ingest/users"

    import spark.implicits._

    val usersDs = spark
      .read.parquet(savedUsers)
      .as[User]
      .map(User(_))

    usersDs.show()

    val sumOfExpensesByUserAndPeriod = spark
      .read.parquet(sumOfExpensesByUserAndPeriodPath)


    sumOfExpensesByUserAndPeriod.show()

    val averageExpenses = sumOfExpensesByUserAndPeriod.groupBy("user_id")
      .agg(
        avg($"total_wasted_sum").as("average_wasted")
      )

   val joinedTables = sumOfExpensesByUserAndPeriod
     .join(usersDs, "user_id")
     .join(averageExpenses, "user_id")
     .withColumn("average_surplus", col("income") - col("average_wasted"))
     .withColumn("mounts_to_goal",
       when(col("average_surplus") > 0, col("saving_goal") / col("average_surplus")).otherwise( -1)
     )
     .as[UserMonthResult]
     .map(UserMonthResult(_))

    joinedTables.show()

    joinedTables.write
      .mode(SaveMode.Overwrite)
      .format("jdbc")
      .option("url", "jdbc:postgresql://localhost:5432/finances")
      .option("driver", "org.postgresql.Driver")
      .option("user", "esf")
      .option("password", "secret")
      .option("dbtable", "personal_finances")
      .save()

    spark.stop()
  }
}

