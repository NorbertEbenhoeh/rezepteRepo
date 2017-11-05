package norbert.rezepte.model

import play.api.libs.json.{Format, Json}

case class RezeptCreated(rezept: Rezept)

object RezeptCreated {
  implicit val format: Format[RezeptCreated] = Json.format[RezeptCreated]
}