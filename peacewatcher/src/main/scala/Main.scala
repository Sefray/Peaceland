package peaceland

import org.apache.kafka.clients.producer.{
  KafkaProducer,
  ProducerConfig,
  ProducerRecord
}
import org.apache.kafka.common.serialization.{StringSerializer}

import java.util.Properties

import peacewatcher.ReportGenerator.generateReport
import peacewatcher.PeaceWatcher

object Main {

  def main(args: Array[String]): Unit = {

    val duration = 60

    val props: Properties = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])

    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)

    val nb_peacewatcher = 2
    val topic = "reports"

    List
    .range(0, nb_peacewatcher)
    .map(id => new Thread(new PeaceWatcher(id, topic, props)))
    .foreach(t => t.start)

    Thread.sleep(duration * 1000)
  }
}
