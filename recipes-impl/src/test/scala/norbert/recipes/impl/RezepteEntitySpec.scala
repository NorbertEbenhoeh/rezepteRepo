package norbert.recipes.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

//class RezepteEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {
//
//  private val system = ActorSystem("RezepteEntitySpec",
//    JsonSerializerRegistry.actorSystemSetupFor(RezepteSerializerRegistry))
//
//  override protected def afterAll(): Unit = {
//    TestKit.shutdownActorSystem(system)
//  }
//
//  private def withTestDriver(block: PersistentEntityTestDriver[RezepteCommand[_], RezepteEvent, RezepteState] => Unit): Unit = {
//    val driver = new PersistentEntityTestDriver(system, new RezepteEntity, "rezepte-1")
//    block(driver)
//    driver.getAllIssues should have size 0
//  }
//
//  "rezepte entity" should {
//
//    "say hello by default" in withTestDriver { driver =>
//      val outcome = driver.run(Hello("Alice"))
//      outcome.replies should contain only "Hello, Alice!"
//    }
//
//    "allow updating the greeting message" in withTestDriver { driver =>
//      val outcome1 = driver.run(UseGreetingMessage("Hi"))
//      outcome1.events should contain only GreetingMessageChangedOrRezeptAdded("Hi")
//      val outcome2 = driver.run(Hello("Alice"))
//      outcome2.replies should contain only "Hi, Alice!"
//    }
//
//  }
//}
