import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.native.JsonMethods._
import peaceland.office._

import java.util.Properties
import scala.util.Random

object main {
  def main(args: Array[String]): Unit = {
    val host = scala.util.Properties.envOrElse("PL_KAFKA_HOST", "localhost:9092")
    val input_topic = scala.util.Properties.envOrElse("PL_REPORT_TOPIC", "reports")
    val alert_topic = scala.util.Properties.envOrElse("PL_ALERT_TOPIC", "alerts")

    val config: Properties = {
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "peaceland-app")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, host)
      props
    }

    implicit val formats = DefaultFormats

    val builder = new StreamsBuilder()
    builder
      .stream[String, String](input_topic)
      .mapValues(value => parse(value).extract[Report])
      .flatMapValues(report => report.citizens.map(citizen => (report, citizen)))
      .filter((_, value) => value._2.score < 0)
      .mapValues(value =>
        Alert(AlertLocation(value._1.pos.lat, value._1.pos.lon),
          AlertCitizen(value._2.id, value._2.score),
          value._1.timestamp))
      .mapValues(value => write(value))
      .to(alert_topic)

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    streams.cleanUp()

    streams.start()
  }
}