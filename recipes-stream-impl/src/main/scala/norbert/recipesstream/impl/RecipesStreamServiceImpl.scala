package norbert.recipesstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import norbert.recipesstream.api.RecipesStreamService
import norbert.recipes.api.RecipeService

import scala.concurrent.Future

/**
  * Implementation of the RezepteStreamService.
  */
class RecipesStreamServiceImpl(recipesService: RecipeService) extends RecipesStreamService {
  def stream = ServiceCall { hellos => ???
//    Future.successful(hellos.mapAsync(8)(rezepteService.getRezepte.invoke()))
  }
}
