package peaceland

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringSerializer}

import java.util.Properties

import scala.util.Random

import java.util.UUID.randomUUID

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

final case class DroneReport(droneId : Int)

object Main {

  def generateDroneReport(rd : Random) : DroneReport = {
    
    val nb_drone = 20

    val droneId = rd.nextInt(nb_drone)

    


    DroneReport(droneId)
  }

  def main(args: Array[String]): Unit = {
    val props: Properties = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])

    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)

    implicit val formats = DefaultFormats

    val seed = 42
    val rd = new Random(seed)
    
    val topic = "kenobi"
    
    val nb_report = 10
    val reports =  List
    .range(0, nb_report)
    .map(x => generateDroneReport(rd))
    .map(drone => write(drone))
    .map(serializedDrone => new ProducerRecord[String, String](topic, randomUUID().toString, serializedDrone))
    .foreach(x => producer.send(x))
    
    producer.flush() // Wait until all messages have been sent

    producer.close()
  }
}
