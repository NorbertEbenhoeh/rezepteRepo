package norbert.rezepte.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import norbert.rezepte.api.RezepteService
import norbert.rezepte.model._

/**
  * Implementation of the RezepteService.
  */
class RezepteServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends RezepteService {


  override def rezepteTopic(): Topic[RezeptCreated] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(RezepteEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(rezeptEvent: EventStreamElement[RezepteEvent]) = {
    rezeptEvent.event match {
      case RezeptAdded(rezept) => RezeptCreated(rezept)
    }
  }


  override def getRezepte = ServiceCall { _ =>
    // Look up the rezepte entity
    val ref = persistentEntityRegistry.refFor[RezepteEntity]("rezepte")

    ref.ask(GetRezepte("rezept"))
  }

  override def postRezepte = ServiceCall { rezept =>
    val ref = persistentEntityRegistry.refFor[RezepteEntity]("rezepte")

    ref.ask(AddRezept(rezept))
  }
}
