package norbert.recipesstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import norbert.recipesstream.api.RecipesStreamService
import norbert.recipes.api.RecipeService
import com.softwaremill.macwire._

class RecipesStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new RecipesStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new RecipesStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[RecipesStreamService])
}

abstract class RecipesStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[RecipesStreamService](wire[RecipesStreamServiceImpl])

  // Bind the RezepteService client
  lazy val rezepteService = serviceClient.implement[RecipeService]
}
