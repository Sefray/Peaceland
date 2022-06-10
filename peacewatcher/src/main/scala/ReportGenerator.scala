package peaceland {
  package peacewatcher {

    import scala.util.Random

    import java.util.UUID.randomUUID

    import com.github.nscala_time.time.Imports._

    import peacewatcher.{Report, Location, Citizen}

    object ReportGenerator {
      def generateLocation(rd: Random): Location = {
        def generateLocationIntern(
            bound: Double
        ): String = {
          val F = ((rd.between(0, bound)) * 100).round / 100f
          F.toString
        }

        val lon = generateLocationIntern(90)
        val lat = generateLocationIntern(180)

        Location(lon, lat)
      }

      def generateCitizens(rd: Random, n: Int): List[Citizen] = {
        val min_nb_word = 3
        val max_nb_word = 10
		val words = Seq(
					"Yo","Hello","What's up?","Como estas?","You", "rang?", "peace", "love", 
                    "youpi", "hourra", "happy", "smile", "bliss","Dude","literally", "I",
					"puzzle", "linux", "vscode", "tissue", "window", "lamp", "eiffel", "harry", "potter",
					"jacket", "scala", "suicide", "spark", "murder", "kafka", "iwanttoburnmyself", "hdfs",
					"baptman",
                    "riot", "rebellion", "war", "dictator", "guns"
				)

        List
          .range(0, n)
          .map(x =>
            Citizen(
              randomUUID().toString,
              rd.between(-100, 100),
              List.range(0, rd.between(min_nb_word, max_nb_word)).map(w => words(rd.nextInt(words.length)))
            //   List.range(0, 10).map(w => rd.alphanumeric.take(rd.between(min_nb_word, max_nb_word)).mkString(""))
            )
          )
      }

      def generateReport(
          rd: Random,
          peacewatcherId : Int,
          initialTimestamp: DateTime
      ): Report = {
        val max_nb_people = 5

        val location = generateLocation(rd)
        val citizens = generateCitizens(rd, rd.between(1, max_nb_people))
        val timestamp = initialTimestamp

        Report(peacewatcherId, location, citizens, timestamp.toString)
      }
    }
  }
}