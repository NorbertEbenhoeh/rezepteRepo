package norbert.recipes.impl

import java.time.LocalDateTime

import scala.collection.immutable.Seq

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import norbert.recipes.model.Recipe
import play.api.libs.json._

class RecipeEntity extends PersistentEntity {

  override type Command = RecipesCommand[_]
  override type Event = RecipeEvent
  override type State = RecipeState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: RecipeState = RecipeState(LocalDateTime.now.toString, Vector.empty[Recipe])

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case RecipeState(_, recipes) => Actions()
      .onCommand[AddRecipe, Done]{

      case (AddRecipe(newRecipe), ctx, state) =>
        ctx.thenPersist(
          RecipeAdded(newRecipe)) { _ => ctx.reply(Done) }
    }.onReadOnlyCommand[GetRecipes, Vector[Recipe]] {

      case (GetRecipes(bla), ctx, state) =>
        // Reply with all rezepte from the state
        ctx.reply(state.recipes)
    }.onEvent {

      // Event handler for the RezeptAdded event
      case (RecipeAdded(newRecipe), state) =>
        RecipeState(LocalDateTime.now().toString, state.recipes :+ newRecipe)
    }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class RecipeState(timestamp: String, recipes: Vector[Recipe])

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
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[RecipeAdded] = Json.format
}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait RecipesCommand[R] extends ReplyType[R]


case class AddRecipe(recipe: Recipe) extends RecipesCommand[Done]

object AddRecipe {

  implicit val format: Format[AddRecipe] = Json.format
}

case class GetRecipes(name: String) extends RecipesCommand[Vector[Recipe]]

object GetRecipes {

  implicit val format: Format[GetRecipes] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object RecipesSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Recipe],
    JsonSerializer[AddRecipe],
    JsonSerializer[GetRecipes],
    JsonSerializer[RecipeAdded],
    JsonSerializer[RecipeState]
  )
}
