package norbert.recipes.model

import play.api.libs.json.{Format, Json}

case class CreateRecipe(title: String, text: String, public: Boolean)

object CreateRecipe {
  implicit val format: Format[CreateRecipe] = Json.format[CreateRecipe]
}