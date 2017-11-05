package norbert.rezeptestream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import norbert.rezepte.model.Rezept

/**
  * The rezepte stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the RezepteStream service.
  */
trait RezepteStreamService extends Service {

  def stream: ServiceCall[Source[NotUsed, NotUsed], Source[Vector[Rezept], NotUsed]]

  override final def descriptor = {
    import Service._

    named("rezepte-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

