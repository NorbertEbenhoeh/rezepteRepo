package norbert.recipes.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import norbert.recipes.model.Recipe
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the events that the RezepteEntity supports.
  */
sealed trait RecipeEvent extends AggregateEvent[RecipeEvent] {
  def aggregateTag = RecipeEvent.Tag
}

object RecipeEvent {
  val Tag = AggregateEventTag[RecipeEvent]
}

/**
  * An event that represents a change in greeting message.
  */
case class RecipeAdded(rezepte: Recipe) extends RecipeEvent

object RecipeAdded {

  /**
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[RecipeAdded] = Json.format
}