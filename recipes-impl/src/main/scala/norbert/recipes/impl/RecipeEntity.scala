package norbert.recipes.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import norbert.recipes.model.Recipe

class RecipeEntity extends PersistentEntity {

  override type Command = RecipesCommand[_]
  override type Event = RecipeEvent
  override type State = RecipeState

  /**
    * The initial state. This is used if there is no snapshotted state to be found.
    */
  override def initialState: RecipeState = RecipeState(Set.empty[Recipe])

  /**
    * An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  override def behavior: Behavior = {
    case RecipeState(_) => Actions()
      .onCommand[CreateRecipeCommand, Done]{

      case (CreateRecipeCommand(newRecipe), ctx, state) =>
        val recipeId = if (state.recipes.isEmpty) 1 else state.recipes.map(_.recipeId).max + 1
        ctx.thenPersist(
          RecipeAdded(Recipe(recipeId = recipeId, title = newRecipe.title,
            text = newRecipe.text, rating = 0, public = newRecipe.public))) {
          _ => ctx.reply(Done)
        }
    }.onReadOnlyCommand[ReadRecipesCommand, Set[Recipe]] {

      case (ReadRecipesCommand(bla), ctx, state) =>
        // Reply with all rezepte from the state
        ctx.reply(state.recipes)
    }.onEvent {

      // Event handler for the RezeptAdded event
      case (RecipeAdded(newRecipe), state) =>
        RecipeState(state.recipes + newRecipe)
    }
  }
}