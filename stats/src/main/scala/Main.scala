import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{FloatType, LongType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession, Column}
// import scala.collection.immutable._
import scala.collection.JavaConversions._

object Main {
  val sparkMaster: String = scala.util.Properties.envOrElse("PL_SPARK_MASTER", "local")
  val spark: SparkSession = SparkSession.builder.appName("Statistics").config("spark.master", sparkMaster).getOrCreate()
  // for production disable the logs of spark
  // In a real application we would export the stats elsewhere but since we just print them in the console
  // we'll disable the logs for readability
  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    val hdfsHost = scala.util.Properties.envOrElse("PL_HDFS_HOST", "")
    val reportsGlob = scala.util.Properties.envOrElse("PL_REPORTS", "reports/*.txt")

    lazy val data = spark.read.option("inferSchema", "true").option("multiline", "true").format("json").load(s"${hdfsHost}${reportsGlob}")
    
    // Uncomment the next lines to see the data and the schema loaded
    // data.show(false)
    // data.printSchema()

    val flatten_data = data.select($"*", explode($"citizens"))
      .drop($"citizens")
      .withColumn("citizenId", $"col.id")
      .withColumn("score", $"col.score")
      .withColumn("words", $"col.words")
      .drop($"col")
      .select($"*", $"pos.*")
      .drop($"pos")

    // Uncomment the next line if you want to see the data processed
	flatten_data.show(false)
	
    avgNumberOfAlertPerDay(flatten_data)
    top5mostDangerousHours(flatten_data)
    avgScoreOfRebelliousCitizen(flatten_data)
    riskyZone(flatten_data)

    spark.stop()
  }


  def avgScoreOfRebelliousCitizen(data: DataFrame): Unit = {
	val suspicious_words = List("riot", "rebellion", "war", "dictator", "guns")
    val df = data.select($"score", $"citizenId", explode($"words").as("word")) // Array spliting
    .filter($"word".isin(suspicious_words:_*))  // filter on the suspicious words
    .groupBy($"citizenId").avg("score") // removing the double suspicious words for one citizen
    .select(avg($"avg(score)").as("Average score")) // get the average score
	
    println("The average score of rebellious citizen is:")
    df.show()
  }

  def top5mostDangerousHours(data: DataFrame, threshold: Integer = 0): Unit = {
    val df = data.filter($"score" < threshold) // Get the alerts
      .select(substring(col("timestamp"), 0, 2).as("Dangerous hours")) // Select the hour
      .groupBy("Dangerous hours")
      .count()
      .orderBy(desc("count")) // Order to get the most dangerous hours first
    val count = df.count()
    val nb = if (count > 5 ) 5 else count
    println(f"The top ${nb} most dangerous hours are:")

    df.select("Dangerous hours").show(5)
  }

  def getReportByZone(data: DataFrame, lat_min: Float, lat_max: Float, lon_min: Float, lon_max: Float): DataFrame = {
    val nb_report = data.filter($"lon" >= lon_min && $"lon" < lon_max && $"lat" >= lat_min && $"lat" < lat_max).count

    val schema = new StructType()
      .add("lon_min", FloatType)
      .add("lon_max", FloatType)
      .add("lat_min", FloatType)
      .add("lat_max", FloatType)
      .add("suspicious activities", LongType)

    spark.createDataFrame(Seq(Row(lon_min, lon_max, lat_min, lat_max, nb_report)), schema)
  }

  def riskyZone(data: DataFrame, zones: List[List[Float]] = List(List(0, 90, -180, 180), List(-90, 0, -180, 180)), threshold: Integer = 0): Unit = {
    val suspicious = data.filter($"score" < threshold)

    val schema = new StructType()
      .add("lon_min", FloatType)
      .add("lon_max", FloatType)
      .add("lat_min", FloatType)
      .add("lat_max", FloatType)
      .add("suspicious activities", FloatType)

    val df = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
    zones.foreach(coord => {
      df = df.union(getReportByZone(suspicious, coord(0), coord(1), coord(2), coord(3)));
    })
    println("The riskiest zones are :")
    df.show(false)
  }

  def avgNumberOfAlertPerDay(data : DataFrame,threshold: Integer = 0 ): Unit = {
    val df = data.filter($"score" < threshold) // Get the alerts
      .select(date_trunc("day", $"timestamp").as("date")) // Select the date
      .groupBy("date")
      .count()
      .select(avg($"count").as("Number of alerts"))

    println("The average number of drone alerts per day is: ")

    df.select("Number of alerts").show()
  }
 }

