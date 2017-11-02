package norbert.rezepte.model

import play.api.libs.json.{Format, Json}

case class Rezept(rezeptId: Int, text: String, rating: Int, public: Boolean)

object Rezept {
  implicit val format: Format[Rezept] = Json.format[Rezept]
}