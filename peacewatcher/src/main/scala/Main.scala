package peaceland

import org.apache.kafka.clients.producer.{
  KafkaProducer,
  ProducerConfig,
  ProducerRecord
}
import org.apache.kafka.common.serialization.{StringSerializer}

import java.util.Properties

import scala.util.Random

import java.util.UUID.randomUUID

import com.github.nscala_time.time.Imports._

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import peacewatcher.ReportGenerator.generateReport

object Main {

  def main(args: Array[String]): Unit = {
    val props: Properties = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])

    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)

    implicit val formats = DefaultFormats

    val seed = 42
    val nb_report = 2
    val topic = "reports"

    val rd = new Random(seed)
    val initialTimeStamp = DateTime.now()

    val reports = List
      .range(0, nb_report)
      .map(x => generateReport(rd, initialTimeStamp))
      .map(report =>
        new ProducerRecord[String, String](
          topic,
          randomUUID().toString,
          write(report)
        )
      )
      .foreach(record => producer.send(record))

    producer.flush()

    producer.close()
  }
}
