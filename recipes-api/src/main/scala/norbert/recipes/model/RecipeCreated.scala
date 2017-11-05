package norbert.recipes.model

import play.api.libs.json.{Format, Json}

case class RecipeCreated(recipe: Recipe)

object RecipeCreated {
  implicit val format: Format[RecipeCreated] = Json.format[RecipeCreated]
}