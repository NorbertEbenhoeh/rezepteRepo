package norbert.recipes.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import norbert.recipes.model.{CreateRecipe, Recipe}
import play.api.libs.json.{Format, Json}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait RecipesCommand[R] extends ReplyType[R]


case class CreateRecipeCommand(recipe: CreateRecipe) extends RecipesCommand[Done]

object CreateRecipeCommand {

  implicit val format: Format[CreateRecipeCommand] = Json.format
}

case class ReadRecipesCommand(name: String) extends RecipesCommand[Set[Recipe]]

object ReadRecipesCommand {

  implicit val format: Format[ReadRecipesCommand] = Json.format
}
