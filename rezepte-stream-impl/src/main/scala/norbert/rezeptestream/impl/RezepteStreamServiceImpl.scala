package norbert.rezeptestream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import norbert.rezeptestream.api.RezepteStreamService
import norbert.rezepte.api.RezepteService

import scala.concurrent.Future

/**
  * Implementation of the RezepteStreamService.
  */
class RezepteStreamServiceImpl(rezepteService: RezepteService) extends RezepteStreamService {
  def stream = ServiceCall { hellos => ???
//    Future.successful(hellos.mapAsync(8)(rezepteService.getRezepte.invoke()))
  }
}
