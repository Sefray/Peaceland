package peaceland {
  package peacewatcher {

    import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

    import java.time.Duration
    import java.util.Properties
    import scala.util.Random
    import java.util.UUID.randomUUID
    import org.json4s._
    import org.json4s.jackson.Serialization
    import org.json4s.jackson.Serialization.write
    import com.github.nscala_time.time.Imports._
    import org.slf4j.{Logger, LoggerFactory}
    import peaceland.peacewatcher.ReportGenerator.generateReport

    class PeaceWatcher(id: Int, topic: String, props: Properties)
        extends Runnable  {

      val logger: Logger = LoggerFactory.getLogger(s"PeaceWatcher$id")

      def run(): Unit = {
        logger.info("Start sending message")
        val intervention_duration = 30 // Seconds
        val poll_duration = 2

        implicit val formats = DefaultFormats

        val producer: KafkaProducer[String, String] =
          new KafkaProducer[String, String](props)

        val rd = new Random()

        def fun() : Unit = {
          Thread.sleep(100)
          if (rd.between(0, 100) == 42) {
            logger.info("Sending message")
            producer.send(
              new ProducerRecord[String, String](
                topic,
                randomUUID().toString,
                write(generateReport(rd, id, DateTime.now()))
              )
            )
            producer.flush()
          }
          fun()
        }

        fun()

        // producer.close()
      }
    }
  }
}
