package norbert.rezepte.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import norbert.rezepte.api.RezepteService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class RezepteLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new RezepteApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new RezepteApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[RezepteService])
}

abstract class RezepteApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[RezepteService](wire[RezepteServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = RezepteSerializerRegistry

  // Register the rezepte persistent entity
  persistentEntityRegistry.register(wire[RezepteEntity])
}
