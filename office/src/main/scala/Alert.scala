package peaceland {
  package office {
    final case class AlertLocation(lat: String, lon: String)
    final case class AlertCitizen(id: String, score: Int)
    final case class Alert(
        pos: AlertLocation,
        citizen: AlertCitizen,
        timestamp: String
    )
  }
}
