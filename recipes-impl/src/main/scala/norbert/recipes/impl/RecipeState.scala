package norbert.recipes.impl

import norbert.recipes.model.Recipe
import play.api.libs.json.{Format, Json}

/**
  * The current state held by the persistent entity.
  */
case class RecipeState(recipes: Set[Recipe])

object RecipeState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[RecipeState] = Json.format
}