import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{DataType, StructType, StringType, IntegerType, ArrayType};
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import java.net.URI
import org.apache.spark.sql.functions._
// import org.apache.spark.implicits._

object Main{
	def main(args: Array[String]): Unit =
	{
		val spark = SparkSession.builder.appName("Statistics").config("spark.master", "local").getOrCreate()
		import spark.implicits._

		// val arrayStructSchema = new StructType()
		// .add("id",StringType)
		// .add("pos",ArrayType(new StructType()
		// 	.add("lat", StringType)
		// 	.add("lon", StringType)))
		// .add("citizens",ArrayType(new StructType()
		// 	.add("citizenId",StringType)
		// 	.add("citizenScore",IntegerType)
		// 	.add("words",ArrayType(StringType))))
		// .add("timestamp", StringType)

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
		// mostRiskyZone(data);
		// AvgScoreOfRebelliousCitizen(data);
	}

	def mostRiskyZone(data : Dataframe) : Unit  = {
		val suspicious = data.filter($"words".filter(x => x == "riot" || x == "rebellion").count() != 0)

		val firstZone = suspicious.filter($"lon" >= -180 && $"lon" < 0 && $"lat" >= -90 && $"lat" < 0).count.toInt
		val secondZone = suspicious.filter($"lon" >= -180 && $"lon" < 0 && $"lat" >= 0 && $"lat" <= 90).count.toInt
		val thirdZone = suspicious.filter($"lon" >= 0 && $"lon" <= 180 && $"lat" >= -90 && $"lat" < 0).count.toInt
		val fourthZone = suspicious.filter($"lon" >= 0 && $"lon" <= 180 && $"lat" >= 0 && $"lat" <= 90).count.toInt

		val list = List(firstZone, secondZone, thirdZone, fourthZone)
		val zoneMax = list.zipWithIndex.maxBy(_._1)

		if (zoneMax._2 == 0) {
		println("First Zone \n The number of suspicious received is " + inIncredibly)
		}
		else if (zoneMax._2 == 1) {
		println("Second Zone \n The number of suspicious received is " + inAmazingly)
		}
		else if (zoneMax._2 == 2) {
		println("Third Zone \n The number of suspicious received is " inImmensely)
		}
		else {
		println("Fourth Zone \n The number of suspicious received is " inEminently)
		}
	}
}

//   def avgNumberOfAlertPerDay(data : Dataframe): Unit = {
//     println("The average number of drone alerts per day is: ")
//   }

//   def mostDangerousHour(data : Dataframe) : Unit = {
//     println("The most dangerous hour of the day is: ")
//  }


//   def AvgScoreOfRebelliousCitizen(data : DataFrame) : Unit = {

//     val allPeopleScore = data.filter($"words".filter(x => x == "riot" || x == "rebellion").count() != 0) // FIXME: the way of gettings the words field may be wrong
//                     .drop("date", "hour", "drone", "latitude", "longitude", "words").distinct()
//                     .select($"citizenScore").map(row => row.getInt(0)).collectAsList()
//                     .foldLeft(0)(_+_)
//     val avgScore = allPeopleScore / allPeopleScore.size()
//     println("Average score of citizens not respecting the peace: " + avgScore)
//   }

// } 