import org.apache.kafka.clients.consumer.{
  KafkaConsumer,
  ConsumerConfig,
  ConsumerRecords
}

import org.apache.kafka.common.serialization.{StringDeserializer}

import scala.collection.JavaConverters._

import java.time.Duration
import java.util.Properties

import peaceland.peacemaker.PeaceMaker

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

object main {
  def main(args: Array[String]): Unit = {

    val duration = 60 // Second

    val host = scala.util.Properties.envOrElse("PL_KAFKA_HOST", "localhost:9092")
    val topic = scala.util.Properties.envOrElse("PL_ALERT_TOPIC", "alerts")
    val group_id = scala.util.Properties.envOrElse("PL_GROUP_ID", "pl_group")
    val http_url = scala.util.Properties.envOrElse("PL_WEBHOOK_URL", "")

    val props: Properties = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    props.put(ConsumerConfig.GROUP_ID_CONFIG, group_id)
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1)

    val intervention_duration = 5 // Seconds
    val poll_duration = 1

    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topic).asJava)

    val id = new Random().randomUUID()
    val logger: Logger = LoggerFactory.getLogger(s"PeaceWatcher$id")

    def fun() : Unit = {
      val records = consumer.poll(Duration.ofSeconds(poll_duration))
      records.asScala.foreach(record => {
          logger.info(s"Sending alert!")
          
          val json_http = { "content" : record }

          val result = Http(http_url).postData(json_http)
          .header("Content-Type", "application/json")
          .header("Charset", "UTF-8")
          .option(HttpOptions.readTimeout(10000)).asString

          Thread.sleep(intervention_duration * 1000)
      })
      consumer.commitSync()
    }

    Thread.sleep(duration * 1000)
  }
}
