package norbert.recipes.model

import play.api.libs.json.{Format, Json}

case class Recipe(recipeId: Int, title: String, text: String, rating: Int, public: Boolean)

object Recipe {
  implicit val format: Format[Recipe] = Json.format[Recipe]
}