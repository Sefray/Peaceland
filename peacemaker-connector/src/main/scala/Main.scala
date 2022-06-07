import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.json4s.DefaultFormats
import org.json4s.jackson.{JsonMethods, Serialization}
import org.slf4j.{Logger, LoggerFactory}
import peaceland.peacewatcher.Alert

import java.time.Duration
import java.util.Properties
import java.util.UUID.randomUUID
import scala.collection.JavaConverters._

object main {

  def formatToString(alert: Alert): String = {
    s"""
       |:rotating_light::warning:**ALERT**:warning::rotating_light:
       |
       |A peacemaker:teddy_bear:needs to bring peace to:
       |**${alert.citizen.id}**
       |At https://www.google.com/maps/search/?api=1&query=${alert.pos.lat}%2C${alert.pos.lon}
       |
       |${
      alert.citizen.score match {
        case s if s > -25 => ":yellow_circle: Threat to peace level 1 :yellow_circle:"
        case s if s > -60 => ":orange_circle: Threat to peace level 2 :orange_circle:"
        case _ => ":red_circle: Threat to peace level 3 :red_circle:"
      }
    }
       |
       |:bouquet::cherry_blossom: Be at peace :hibiscus::bouquet:
       |
       |""".stripMargin
  }

  def main(args: Array[String]): Unit = {

    implicit val formats: DefaultFormats.type = DefaultFormats

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

    val poll_duration = 1

    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topic).asJava)

    val id = randomUUID()
    val logger: Logger = LoggerFactory.getLogger(s"PeaceMakerConnector$id")

    val client = HttpClients.createDefault()

    def fun(): Unit = {
      val records = consumer.poll(Duration.ofSeconds(poll_duration))

      records.asScala.foreach(f = record => {
        logger.info(s"Sending alert!")

        val record_json = JsonMethods.parse(record.value())
        val alert: Alert = record_json.extract[Alert]

        val content = formatToString(alert)
        val json_object = Map("content" -> content)
        val json_body: String = Serialization.write(json_object)
        logger.info(json_body)

        val httpPost = new HttpPost(http_url)
        httpPost.addHeader("Content-Type", "application/json")
        httpPost.addHeader("Charset", "UTF-8")
        httpPost.setEntity(new StringEntity(json_body))


        logger.info(httpPost.toString)

        val result: HttpResponse = client.execute(httpPost);

        logger.info(result.getStatusLine.getStatusCode.toString)


        if (result.getStatusLine.getStatusCode < 300)
          consumer.commitSync()
        else
          logger.error(s"Error ${result.getStatusLine.getStatusCode} on alert ${EntityUtils.toString(result.getEntity)}")

      })
      Thread.sleep(5000L)
      fun()
    }

    fun()
  }
}
