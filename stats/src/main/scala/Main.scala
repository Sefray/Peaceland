import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.types.{DataType, StructType, StringType, IntegerType, ArrayType, FloatType, LongType};
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import java.net.URI
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
// import scala.collection.immutable._
import scala.collection.JavaConversions._
// import org.apache.spark.implicits._

object Main{
	val spark = SparkSession.builder.appName("Statistics").config("spark.master", "local").getOrCreate()
	import spark.implicits._

	def main(args: Array[String]): Unit = {

		lazy val data = spark.read.option("inferSchema", "true").option("multiline","true").format("json").load("reports/*.txt")
		data.show(false)
		data.printSchema()

		val flatten_data = data.select($"*", explode($"citizens"))
								.drop($"citizens")
								.withColumn("citizenId",$"col.citizenId")
								.withColumn("citizenScore",$"col.citizenScore")
								.withColumn("words",$"col.words")
								.drop($"col")
								.select($"*",$"pos.*")
        						.drop($"pos")
		flatten_data.show(false)
		flatten_data.printSchema()
		// avgNumberOfAlertPerDay(data);
		// mostDangerousHour(data);
		avgScoreOfRebelliousCitizen(flatten_data)
		riskyZone(flatten_data)
	}

	var suspicious_words = List("riot", "rebellion")

	def saySuspiciousWords(words: Array[String]): Boolean = {
		!words.filter(word => suspicious_words.contains(word)).isEmpty
	}

	def avgScoreOfRebelliousCitizen(data : DataFrame) : DataFrame = {
		val df = data.where(array_contains(data("words"), "riot"))
		.select(avg($"citizenScore"))
		.withColumn("average score", $"avg(citizenScore)")
		.drop($"avg(citizenScore)")

		df.show(false)
		return df
	}

	def mostDangerousHour(data : DataFrame) : Unit = {
	}

	// def coord_to_num(coord : StringType) : Unit = {
	// 	val pattern = "([0-9]+.[0-9]+) [SNEO]"
	// 	val pattern(num, card) = coord
	// }

	def getReportByZone(data : DataFrame, lat_min : Float, lat_max : Float, lon_min : Float, lon_max : Float) : DataFrame = {
		val nb_report = data.filter($"lon" >= lon_min && $"lon" < lon_max && $"lat" >= lat_min && $"lat" < lat_max).count

		val schema = new StructType()
		.add("lon_min",FloatType)
		.add("lon_max",FloatType)
		.add("lat_min",FloatType)
		.add("lat_max",FloatType)
		.add("suspiciois activities",LongType)

		val df = spark.createDataFrame(Seq(Row(lon_min, lon_max, lat_min, lat_max, nb_report)), schema)

		return df
		// return spark.createDataFrame(Seq(Row(lon_min, lon_max, lat_min, lat_max, nb_report)), schema)
	}

	def riskyZone(data : DataFrame, zones : List[List[Float]] = List(List(0, 90, -180, 180), List(-90, 0, -180, 180)), threshold : Integer = 0) : DataFrame = {
		// val suspicious = data.filter($"words".filter(x => x.isin(suspicious_words: _*)).count() != 0)
		val suspicious = data.filter($"citizenScore" < threshold)

		val schema = new StructType()
		.add("lon_min",FloatType)
		.add("lon_max",FloatType)
		.add("lat_min",FloatType)
		.add("lat_max",FloatType)
		.add("suspicious activities",FloatType)

		// return getReportByZone(suspicious, zones(0)(0), zones(0)(1),zones(0)(2),zones(0)(3))

		var df = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
		// zones.foreach(coord : List[Float] => df.union(getReportByZone(suspicious, coord(0), coord(1),coord(2),coord(3))))
		for (coord <- zones)
			df = df.union(getReportByZone(suspicious, coord(0), coord(1),coord(2),coord(3)))
		df.show(false)
		return df
		// val res = 
	}
}
		
//   def avgNumberOfAlertPerDay(data : Dataframe): Unit = {
//     println("The average number of drone alerts per day is: ")
//   }

//   def mostDangerousHour(data : Dataframe) : Unit = {
//     println("The most dangerous hour of the day is: ")
//  }