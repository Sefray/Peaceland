package drone {

  final case class Location(lat: String, lon: String)
  final case class Citizen(id: String, score: Int, words: List[String])
  final case class DroneReport(
      id: Int,
      pos: Location,
      citizens: List[Citizen],
      timestamp: String
  )

}
