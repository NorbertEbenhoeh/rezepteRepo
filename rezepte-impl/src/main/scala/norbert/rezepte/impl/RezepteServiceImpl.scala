package norbert.rezepte.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import norbert.rezepte.api
import norbert.rezepte.api.RezepteService

/**
  * Implementation of the RezepteService.
  */
class RezepteServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends RezepteService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the rezepte entity for the given ID.
    val ref = persistentEntityRegistry.refFor[RezepteEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the rezepte entity for the given ID.
    val ref = persistentEntityRegistry.refFor[RezepteEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(RezepteEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[RezepteEvent]) = {
    helloEvent.event match {
      case GreetingMessageChangedOrRezeptAdded(msg, rezept, rezeptAdded) => api.GreetingMessageChanged(helloEvent.entityId, msg, rezept, rezeptAdded)
//      case RezeptAdded(rezept) => api.RezeptAdded(Rezept(rezept.rezeptId, rezept.text, rezept.rating, rezept.public))
    }
  }


  override def getRezepte = ServiceCall { _ =>
    ???
//    // Look up the rezepte entity
//    vel ref = persistentEntityRegistry.refFor[RezepteEntity]()
//
//    ref.ask
  }

  override def postRezepte = ServiceCall { rezept =>
    val ref = persistentEntityRegistry.refFor[RezepteEntity](rezept.rezeptId.toString)

    ref.ask(AddRezept(rezept))
  }
}
