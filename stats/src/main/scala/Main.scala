import org.apache.spark._

object Main{
  def main(args: Array[String]): Unit =
    {
      val data = spark.read.format("json")
      .load("hdfs://localhost:9000/topics/report/");
    }

  def stat1(data): Unit =
  {
    println("")
  }
}