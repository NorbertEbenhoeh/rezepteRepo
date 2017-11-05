package norbert.rezepte.impl

import java.time.LocalDateTime

import scala.collection.immutable.Seq

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import norbert.rezepte.model.Rezept
import play.api.libs.json._

class RezepteEntity extends PersistentEntity {

  override type Command = RezepteCommand[_]
  override type Event = RezepteEvent
  override type State = RezepteState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: RezepteState = RezepteState(LocalDateTime.now.toString, Vector.empty[Rezept])

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case RezepteState(_, rezepte) => Actions()
      .onCommand[AddRezept, Done]{

      case (AddRezept(newRezept), ctx, state) =>
        ctx.thenPersist(
          RezeptAdded(newRezept)) { _ => ctx.reply(Done) }
    }.onReadOnlyCommand[GetRezepte, Vector[Rezept]] {

      case (GetRezepte(bla), ctx, state) =>
        // Reply with all rezepte from the state
        ctx.reply(state.rezepte)
    }.onEvent {

      // Event handler for the RezeptAdded event
      case (RezeptAdded(newRezept), state) =>
        RezepteState(LocalDateTime.now().toString, state.rezepte :+ newRezept)
    }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class RezepteState(timestamp: String, rezepte: Vector[Rezept])

object RezepteState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[RezepteState] = Json.format
}

/**
  * This interface defines all the events that the RezepteEntity supports.
  */
sealed trait RezepteEvent extends AggregateEvent[RezepteEvent] {
  def aggregateTag = RezepteEvent.Tag
}

object RezepteEvent {
  val Tag = AggregateEventTag[RezepteEvent]
}

/**
  * An event that represents a change in greeting message.
  */
case class RezeptAdded(rezepte: Rezept) extends RezepteEvent

object RezeptAdded {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[RezeptAdded] = Json.format
}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait RezepteCommand[R] extends ReplyType[R]


case class AddRezept(rezept: Rezept) extends RezepteCommand[Done]

object AddRezept {

  implicit val format: Format[AddRezept] = Json.format
}

case class GetRezepte(name: String) extends RezepteCommand[Vector[Rezept]]

object GetRezepte {

  implicit val format: Format[GetRezepte] = Json.format
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
object RezepteSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Rezept],
    JsonSerializer[AddRezept],
    JsonSerializer[GetRezepte],
    JsonSerializer[RezeptAdded],
    JsonSerializer[RezepteState]
  )
}
