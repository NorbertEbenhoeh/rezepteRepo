package norbert.rezeptestream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import norbert.rezeptestream.api.RezepteStreamService
import norbert.rezepte.api.RezepteService
import com.softwaremill.macwire._

class RezepteStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new RezepteStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new RezepteStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[RezepteStreamService])
}

abstract class RezepteStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[RezepteStreamService](wire[RezepteStreamServiceImpl])

  // Bind the RezepteService client
  lazy val rezepteService = serviceClient.implement[RezepteService]
}
