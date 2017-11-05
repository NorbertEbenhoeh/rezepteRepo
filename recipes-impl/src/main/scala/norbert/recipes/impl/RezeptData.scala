package norbert.recipes.impl

import norbert.recipes.model.Recipe
import play.api.libs.json._
import play.api.libs.functional.syntax._

//case class RezeptData(rezeptId: Int, text: String, rating: Int, public: Boolean)
//
//object RezeptData {
//  def apply(rezept: Rezept) =
//    new RezeptData(rezept.rezeptId, rezept.text, rezept.rating, rezept.public)
//
//  implicit val rezeptDataWrites = new Writes[RezeptData] {
//    def writes(rezeptData: RezeptData) = Json.obj(
//      "rezeptId" -> rezeptData.rezeptId,
//      "text" -> rezeptData.text,
//      "rating" -> rezeptData.rating,
//      "public" -> rezeptData.public
//    )
//  }
//
//  implicit val rezeptDataReads: Reads[RezeptData] =
//    ((JsPath \ "rezeptId").read[Int] and
//      (JsPath \ "text").read[String] and
//      (JsPath \ "rating").read[Int] and
//      (JsPath \ "public").read[Boolean])(
//      (rezeptId: Int, text: String, rating: Int, public: Boolean) =>
//        new RezeptData(rezeptId, text, rating, public))
//
//  implicit val locationFormat: Format[RezeptData] =
//    Format(rezeptDataReads, rezeptDataWrites)
//}
