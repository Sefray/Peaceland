package peaceland

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringSerializer}

import java.util.Properties

import scala.util.Random

import java.util.UUID.randomUUID

import com.github.nscala_time.time.Imports._

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

final case class Location(lat : String, lon : String)
final case class Citizen(id : String, score : Int, words : List[String])
final case class DroneReport(id : Int, pos : Location, citizens : List[Citizen], timestamp : String)

object Main {

  def generateLocation(rd : Random) : Location = {
    def generateLocationIntern(max : Int, a : String, b : String) : String = {
      val F = ((rd.nextDouble() * max - max / 2) * 100).round / 100f
      val L = if (rd.nextInt() % 2 == 0) a else b
      F.toString + " " + L
    }
    
    val lon = generateLocationIntern(180, "N", "S")
    val lat = generateLocationIntern(360, "E", "W")

    Location(lon, lat)
  }

  def generateCitizens(rd : Random, n : Int) : List[Citizen] = {
    List
    .range(0, n)
    .map(x => Citizen(randomUUID().toString, rd.between(-100, 100), List.range(0, 10).map(w => rd.nextString(rd.between(3,10)))))
  }

  def generateDroneReport(rd : Random, initialTimestamp : DateTime) : DroneReport = {

    val nb_drone = 20
    val nb_people = 5
    val duration = 1 // Hours

    val droneId = rd.nextInt(nb_drone)
    val location = generateLocation(rd)
    val citizens = generateCitizens(rd, rd.between(1, nb_people))
    val timestamp = initialTimestamp + rd.between(0, 60 * duration).minutes

    DroneReport(droneId, location, citizens, timestamp.toString)
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

    val duration = 1 // in hours

    val initialTimeStamp = DateTime.now()
    
    val nb_report = 10
    val reports =  List
    .range(0, nb_report)
    .map(x => generateDroneReport(rd, initialTimeStamp))
    .map(drone => write(drone))
    .map(serializedDrone => new ProducerRecord[String, String](topic, randomUUID().toString, serializedDrone))
    .foreach(x => producer.send(x))
    
    producer.flush() // Wait until all messages have been sent

    producer.close()
  }
}
