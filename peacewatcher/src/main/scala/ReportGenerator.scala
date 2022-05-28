package peacewatcher {

  import scala.util.Random

  import java.util.UUID.randomUUID

  import com.github.nscala_time.time.Imports._

  import peacewatcher.{Report, Location, Citizen}

  object ReportGenerator {
    def generateLocation(rd: Random): Location = {
      def generateLocationIntern(
          bound: Double,
          a: String,
          b: String
      ): String = {
        val F = ((rd.between(-bound, bound)) * 100).round / 100f
        val L = if (rd.nextInt() % 2 == 0) a else b
        F.toString + " " + L
      }

      val lon = generateLocationIntern(90, "N", "S")
      val lat = generateLocationIntern(180, "E", "W")

      Location(lon, lat)
    }

    def generateCitizens(rd: Random, n: Int): List[Citizen] = {
      List
        .range(0, n)
        .map(x =>
          Citizen(
            randomUUID().toString,
            rd.between(-100, 100),
            List.range(0, 10).map(w => rd.nextString(rd.between(3, 10)))
          )
        )
    }

    def generateReport(
        rd: Random,
        initialTimestamp: DateTime
    ): Report = {
      val nb_peacewatcher = 20
      val nb_people = 5
      val duration = 1 // Hours

      val peacewatcherId = rd.nextInt(nb_peacewatcher)
      val location = generateLocation(rd)
      val citizens = generateCitizens(rd, rd.between(1, nb_people))
      val timestamp = initialTimestamp + rd.between(0, 60 * duration).minutes

      Report(peacewatcherId, location, citizens, timestamp.toString)
    }
  }
}
