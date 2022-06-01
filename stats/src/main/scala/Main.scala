import org.apache.spark.sql.{DataFrame, SparkSession}

object Main{
  def main(args: Array[String]): Unit =
    {
      val spark = SparkSession.builder.appName("Statistics").getOrCreate()
      val data = spark.read.format("json")
      .load("hdfs://localhost:9000/topics/report/");

      stat1(data);
    }

  def stat1(data : DataFrame): Unit =
  {
    println("")
  }
}