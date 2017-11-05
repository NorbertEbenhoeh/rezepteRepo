package norbert.recipesstream.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import norbert.recipes.model.Recipe

/**
  * The rezepte stream interface.
  *
  * This describes everything that Lagom needs to know about how to serve and
  * consume the RezepteStream service.
  */
trait RecipesStreamService extends Service {

  def stream: ServiceCall[Source[NotUsed, NotUsed], Source[Vector[Recipe], NotUsed]]

  override final def descriptor = {
    import Service._

    named("recipes-stream")
      .withCalls(
        namedCall("stream", stream)
      ).withAutoAcl(true)
  }
}

