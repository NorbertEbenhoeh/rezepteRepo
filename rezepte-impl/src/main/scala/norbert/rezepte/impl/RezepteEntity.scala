package norbert.rezepte.impl

import java.time.LocalDateTime

import scala.collection.immutable.Seq

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import norbert.rezepte.model.Rezept
import play.api.libs.json._

/**
  * This is an event sourced entity. It has a state, [[RezepteState]], which
  * stores what the greeting should be (eg, "Hello").
  *
  * Event sourced entities are interacted with by sending them commands. This
  * entity supports two commands, a [[UseGreetingMessage]] command, which is
  * used to change the greeting, and a [[Hello]] command, which is a read
  * only command which returns a greeting to the name specified by the command.
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines one event, the [[GreetingMessageChangedOrRezeptAdded]] event,
  * which is emitted when a [[UseGreetingMessage]] command is received.
  */
class RezepteEntity extends PersistentEntity {

  override type Command = RezepteCommand[_]
  override type Event = RezepteEvent
  override type State = RezepteState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: RezepteState = RezepteState("Hello", LocalDateTime.now.toString, Rezept(1, "Sehr sehr lecker", 3, public = true))

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case RezepteState(message, _, rezept) => Actions()
      .onCommand[UseGreetingMessage, Done] {

      // Command handler for the UseGreetingMessage command
      case (UseGreetingMessage(newMessage), ctx, state) =>
        // In response to this command, we want to first persist it as a
        // GreetingMessageChanged event
        ctx.thenPersist(
          GreetingMessageChangedOrRezeptAdded(newMessage, state.rezept, rezeptAdded = false)) { _ =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(Done)
        }
    }.onCommand[AddRezept, Done]{

      case (AddRezept(newRezept), ctx, state) =>

        ctx.thenPersist(
          GreetingMessageChangedOrRezeptAdded(state.message, newRezept, rezeptAdded = true)) {_ => ctx.reply(Done) }
    }.onReadOnlyCommand[Hello, String] {

      // Command handler for the Hello command
      case (Hello(name), ctx, state) =>
        // Reply with a message built from the current message, and the name of
        // the person we're meant to say hello to.
        ctx.reply(s"$message, $name! The state is $state")
    }.onEvent {

      // Event handler for the GreetingMessageChanged event
      case (GreetingMessageChangedOrRezeptAdded(newMessage, _, rezeptAdded), state) if rezeptAdded =>
        // We simply update the current state to use the greeting message from
        // the event.
        RezepteState(newMessage, LocalDateTime.now().toString, state.rezept)
      case (GreetingMessageChangedOrRezeptAdded(_, newRezept, rezeptAdded), state) if rezeptAdded =>
        RezepteState(state.message, LocalDateTime.now().toString, newRezept)
    }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class RezepteState(message: String, timestamp: String, rezept: Rezept)

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
case class GreetingMessageChangedOrRezeptAdded(message: String, rezept: Rezept, rezeptAdded: Boolean) extends RezepteEvent

object GreetingMessageChangedOrRezeptAdded {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChangedOrRezeptAdded] = Json.format
}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait RezepteCommand[R] extends ReplyType[R]

/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class UseGreetingMessage(message: String) extends RezepteCommand[Done]

object UseGreetingMessage {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[UseGreetingMessage] = Json.format
}

/**
  * A command to say hello to someone using the current greeting message.
  *
  * The reply type is String, and will contain the message to say to that
  * person.
  */
case class Hello(name: String) extends RezepteCommand[String]

object Hello {

  /**
    * Format for the hello command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[Hello] = Json.format
}

case class AddRezept(rezept: Rezept) extends RezepteCommand[Done]

object AddRezept {

  implicit val format: Format[AddRezept] = Json.format
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
    JsonSerializer[UseGreetingMessage],
    JsonSerializer[Hello],
    JsonSerializer[Rezept],
    JsonSerializer[AddRezept],
    JsonSerializer[GreetingMessageChangedOrRezeptAdded],
    JsonSerializer[RezepteState]
  )
}
