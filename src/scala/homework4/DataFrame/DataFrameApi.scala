package scala.homework4.DataFrame

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

object DataFrameApi extends SparkSessionWrapper {

  import spark.implicits._

  def main(args: Array[String]): Unit = {
    var path_to_yellow_taxi = "src/resources/data/yellow_taxi_jan_25_2018"
    var path_to_taxi_zones = "src/resources/data/taxi_zones.csv"

    val yellowTaxiDataFrame: DataFrame = spark.read
      .format("parquet")
      .load(path_to_yellow_taxi)

    yellowTaxiDataFrame.show (10)
    yellowTaxiDataFrame.orderBy($"total_amount".desc).show (10)

    val taxiZonesDataFrame: DataFrame = spark.read.format("csv")
      .option("delimiter", ",").option("quote", "")
      .option("header", "true")
      .schema(customSchema)
    .load (path_to_taxi_zones)

    taxiZonesDataFrame.show (10)

    val grupedByPULocationID = yellowTaxiDataFrame.groupBy("PULocationID")
      .count()
      .withColumnRenamed("PULocationID", "LocationID" )

    grupedByPULocationID.show(10)

    val joinedTables = grupedByPULocationID.join(taxiZonesDataFrame, "LocationID")
    joinedTables.show(10)

    var groupedByZone = joinedTables
      .groupBy("Zone")
      .sum("count")
      .withColumnRenamed("sum(count)", "ordersCount")
      .orderBy($"ordersCount".desc)
    groupedByZone.show()

    groupedByZone.write.mode(SaveMode.Overwrite).format("parquet").save("src/resources/result/theMostPopularZones")
  }

  private val customSchema = StructType(
    Array(
      StructField("LocationID", IntegerType, nullable = true),
      StructField("Borough", StringType, nullable = true),
      StructField("Zone", StringType, nullable = true),
      StructField("service_zone", StringType, nullable = true),
    )
  )
}
