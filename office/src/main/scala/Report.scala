package peaceland {
  package office {
    final case class ReportLocation(lat: String, lon: String)
    final case class ReportCitizen(id: String, score: Int, words: List[String])
    final case class Report(
        peaceWatcherId: Int,
        pos: ReportLocation,
        citizens: List[ReportCitizen],
        timestamp: String
    )
  }
}
