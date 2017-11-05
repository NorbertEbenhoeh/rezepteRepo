package norbert.rezepte.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import norbert.rezepte.model.{Rezept, RezeptCreated}
import play.api.libs.json.{Format, Json}

object RezepteService {
  val TOPIC_NAME = "rezepte"
}

/**
  * The rezepte service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the RezepteService.
  */
trait RezepteService extends Service {

  def getRezepte: ServiceCall[NotUsed, Vector[Rezept]]

  def postRezepte : ServiceCall[Rezept, Done]

  /**
    * This gets published to Kafka.
    */
  def rezepteTopic(): Topic[RezeptCreated]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("rezepte")
      .withCalls(
        restCall(Method.GET,"/api/rezepte", getRezepte ),
        restCall(Method.POST,"/api/rezepte", postRezepte _)
      )
      .withTopics(
        topic(RezepteService.TOPIC_NAME, rezepteTopic _)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .addProperty(
            KafkaProperties.partitionKeyStrategy,
            PartitionKeyStrategy[RezeptCreated](_.rezept.rezeptId.toString)
          )
        )
      .withAutoAcl(true)
    // @formatter:on
  }
}