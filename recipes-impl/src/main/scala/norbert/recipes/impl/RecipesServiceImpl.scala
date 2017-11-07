package norbert.recipes.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import norbert.recipes.api.RecipeService
import norbert.recipes.model._

/**
  * Implementation of the RezepteService.
  */
class RecipesServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends RecipeService {


  override def recipeTopic(): Topic[RecipeCreated] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(RecipeEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(rezeptEvent: EventStreamElement[RecipeEvent]) = {
    rezeptEvent.event match {
      case RecipeAdded(rezept) => RecipeCreated(rezept)
    }
  }


  override def getRecipes = ServiceCall { _ =>
    // Look up the rezepte entity
    val ref = persistentEntityRegistry.refFor[RecipeEntity]("recipes")

    ref.ask(ReadRecipesCommand("recipes"))
  }

  override def postRecipe = ServiceCall { recipe =>
    val ref = persistentEntityRegistry.refFor[RecipeEntity]("recipes")

    ref.ask(CreateRecipeCommand(recipe))
  }
}
