package norbert.recipes.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import norbert.recipes.api.RecipeService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class RecipesLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new RecipesApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new RecipesApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[RecipeService])
}

abstract class RecipesApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[RecipeService](wire[RecipesServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = RecipesSerializerRegistry

  // Register the rezepte persistent entity
  persistentEntityRegistry.register(wire[RecipeEntity])
}
