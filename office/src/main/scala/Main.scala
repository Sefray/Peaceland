import java.time.Duration
import java.util.Properties

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.scala.ImplicitConversions._

import scala.util.Random

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import peaceland.office._

object main {
  def main(args: Array[String]): Unit = {

    val duration = 300 // Seconds

    val seed = 42
    val host = scala.util.Properties.envOrElse("PL_KAFKA_HOST", "localhost:9092")
    val input_topic = scala.util.Properties.envOrElse("PL_REPORT_TOPIC", "reports")
    val alert_topic = scala.util.Properties.envOrElse("PL_ALERT_TOPIC", "alerts")

    val config: Properties = {
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "peaceland-app")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, host)
      props
    }

    val rd : Random = new Random(seed)

    implicit val formats = DefaultFormats

    val builder = new StreamsBuilder()
    builder
    .stream[String, String](input_topic)
    .map((key, value) => parse(value).extract[Report])
    .flatMap(report => report.citizens.map(citizen => (report, citizen)))
    .filter((report, citizen) => citizen.score < -50)
    .map((report, citizen) => write(Alert(AlertLocation(report.pos.lat, report.pos.lon),
                                    AlertCitizen(citizen.id, citizen.score),
                                    report.timestamp)))
    .to(alert_topic)

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    streams.cleanUp()

    streams.start()
  }
}