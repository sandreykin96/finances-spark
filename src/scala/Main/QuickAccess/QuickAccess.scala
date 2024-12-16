package scala.Main.QuickAccess

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

import scala.Main.SparkSessionWrapper
import scala.models.{Receipt, User, UserMonthResult}

object QuickAccess extends SparkSessionWrapper {
  def main(args: Array[String]): Unit = {
    var aggregatedReceiptsByProductTypePath = "src/resources/aggregate/receiptsByProductType"
    var sumOfExpencesByUserAndPeriodPath = "src/resources/aggregate/sumOfExpencesByUserAndPeriod"
    var savedUsers = "src/resources/ingest/users"

    import spark.implicits._

    val usersDs = spark
      .read.parquet(savedUsers)
      .as[User]
      .map(User(_))

    usersDs.show()

    val sumOfExpencesByUserAndPeriod = spark
      .read.parquet(sumOfExpencesByUserAndPeriodPath)

   sumOfExpencesByUserAndPeriod.show()

    import spark.implicits._

   val joinedTables = sumOfExpencesByUserAndPeriod
     .join(usersDs, "user_id")
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

