package norbert.recipes.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import norbert.recipes.model.{Recipe, RecipeCreated}
import play.api.libs.json.{Format, Json}

object RecipeService {
  val TOPIC_NAME = "recipes"
}

/**
  * The recipe service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the RecipeService.
  */
trait RecipeService extends Service {

  def getRecipes: ServiceCall[NotUsed, Vector[Recipe]]

  def postRecipe : ServiceCall[Recipe, Done]

  /**
    * This gets published to Kafka.
    */
  def recipeTopic(): Topic[RecipeCreated]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("recipe")
      .withCalls(
        restCall(Method.GET,"/api/recipes", getRecipes ),
        restCall(Method.POST,"/api/recipes", postRecipe _)
      )
      .withTopics(
        topic(RecipeService.TOPIC_NAME, recipeTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[RecipeCreated](_.recipe.recipeId.toString)
          )
        )
      .withAutoAcl(true)
    // @formatter:on
  }
}