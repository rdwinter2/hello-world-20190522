package com.example.helloworld.api

//import com.example.common.regex.Matchers
import com.example.common.response.ErrorResponse
import com.example.common.utils.JsonFormats._
import com.example.common.validation.ValidationViolationKeys._

import ai.x.play.json.Jsonx
import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{
  KafkaProperties,
  PartitionKeyStrategy
}
import com.lightbend.lagom.scaladsl.api.deser.{
  DefaultExceptionSerializer,
  PathParamSerializer
}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.wix.accord.Validator
import com.wix.accord.dsl._
import com.wix.accord.Descriptions._
import java.time.{Duration, Instant}
import java.util.UUID
import julienrf.json.derived
import play.api.{Environment, Mode}
import play.api.libs.json._

//object HelloWorldService  {
//  val TOPIC_NAME = "agg.event.hello_world"
//}

/**
  * The Hello World service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the HelloWorldService.
  */
trait HelloWorldService extends Service {

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("hello-world").withCalls(
      // CRUDy REST
      restCall(Method.POST,   "/api/hello-worlds",       postHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id",   postHelloWorld2 _),
      restCall(Method.PUT,    "/api/hello-worlds/:id",   putHelloWorld _),
      //restCall(Method.PATCH,  "/api/hello-worlds/:id",   patchHelloWorld _),
      //restCall(Method.DELETE, "/api/hello-worlds/:id",   deleteHelloWorld _),

      restCall(Method.GET,    "/api/hello-worlds/:id",   getHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds",       getAllHelloWorlds _),
      // CRUDy DDDified REST without a proper ubiquitious language
      restCall(Method.POST,   "/api/hello-worlds/creation",                         createHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/creation",                     createHelloWorld2 _),
      restCall(Method.POST,   "/api/hello-worlds/creation/:creationId",             createHelloWorld3 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/creation/:creationId",         createHelloWorld4 _),
      //restCall(Method.POST,   "/api/hello-worlds/:id/replacement/:replacementId",    replaceHelloWorld _),
      //restCall(Method.POST,   "/api/hello-worlds/:id/deactivation/:deactivationId", deactivateHelloWorld _),
      //restCall(Method.POST,   "/api/hello-worlds/:id/reactivation/:reactivationId", reactivateHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds/:id/creation/:creationId",         getCreateHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds/:id/amelioration/:ameliorationId", getAmeliorateHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds/:id/deactivation/:deactivationId", getDeactivateHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds/:id/reactivation/:reactivationId", getReactivateHelloWorld _)
      // DDDified REST using the bounded context's ubiquitious language
      //restCall(Method.POST,    "/api/hello-worlds",                     createHelloWorldWithSystemGeneratedId _),
      //restCall(Method.POST,    "/api/hello-worlds/:id/creation/:creationId",   createHelloWorld _),
      //restCall(Method.POST, "/api/hello-worlds/:id/archival/:archivalId", archiveHelloWorld _),
      //restCall(Method.POST, "/api/hello-worlds/:id/reactivation/:reactivationId", reactivateHelloWorld _),
      //restCall(Method.POST, "/api/hello-worlds/:id/enhancement/:enhancementId", enhanceHelloWorld _),
//      pathCall("/api/hello-worlds/stream", streamHelloWorlds _),
      //restCall(Method.GET, "/api/hello-worlds/:id", getHelloWorld _),
      //restCall(Method.GET, "/api/hello-worlds", getAllHelloWorlds _)
    )
      .withAutoAcl(true)
      .withExceptionSerializer(new DefaultExceptionSerializer(Environment.simple(mode = Mode.Prod)))
      .withTopics(
        topic("helloWorld-HelloWorldMessageBrokerEvent", this.helloWorldMessageBrokerEvents)
      )
    // @formatter:on
  }

// Hello World Service Call

  /**
    * Rest api allowing an authenticated user to create a "Hello World" aggregate.
    *
    * @return HTTP 200 status code if the "Hello World" was created successfully.
    *         HTTP 404 status code if one or more items in the [[CreateHelloWorldRequest]] failed vaildation.
    *         HTTP 409 status code if the "Hello World" already exists with the same identity.
    *
    * Example:
    * curl -H "Content-Type: application/json" -X POST -d '{"helloWorld": {"name": "test", "description": "test description"}}' http://localhost:9000/api/hello-worlds
    */
  def postHelloWorld1: ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def postHelloWorld2(helloWorldId: String): ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]

  def putHelloWorld(helloWorldId: String): ServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]]
  //def patchHelloWorld(helloWorldId: String): ServiceCall[PatchHelloWorldRequest, Either[ErrorResponse, PatchHelloWorldResponse]]
  //def deleteHelloWorld(helloWorldId: String): ServiceCall[NotUsed, Either[ErrorResponse, DeleteHelloWorldResponse]]
  def getHelloWorld(helloWorldId: String): ServiceCall[NotUsed, Either[ErrorResponse, GetHelloWorldResponse]]
  //def getHelloWorlds: ServiceCall[NotUsed, Either[ErrorResponse, GetHelloWorldsResponse]]

  /**
    * Rest api allowing an authenticated user to create a "Hello World" aggregate.
    *
    * @return HTTP 200 status code if the "Hello World" was created successfully.
    *         HTTP 404 status code if one or more items in the [[CreateHelloWorldRequest]] failed vaildation.
    *         HTTP 409 status code if the "Hello World" already exists with the same identity.
    *
    * Example:
    * curl -H "Content-Type: application/json" -X POST -d '{"helloWorld": {"name": "test", "description": "test description"}}' http://localhost:9000/api/hello-worlds
    */
  //def createHelloWorldWithSystemGeneratedId
  //  : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]

  /**
    * Rest api allowing an authenticated user to create a "Hello World" aggregate.
    *
    * @param helloWorldId unique identifier of the "Hello World" to be created.
    * @return HTTP 200 status code if the "Hello World" was created successfully.
    *         HTTP 404 status code if one or more items in the [[CreateHelloWorldRequest]] failed vaildation.
    *
    * Example:
    * curl -H "Content-Type: application/json" -X POST -d '{"helloWorld": {"name": "test", "description": "test description"}}' http://localhost:9000/api/hello-worlds/{id}/creation/{creationId}
    */

  //  restCall(Method.POST,   "/api/hello-worlds/creation",                         createHelloWorld1 _),
  def createHelloWorld1
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  //  restCall(Method.POST,   "/api/hello-worlds/:id/creation",                     createHelloWorld2 _),
  def createHelloWorld2(helloWorldId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  //  restCall(Method.POST,   "/api/hello-worlds/creation/:creationId",             createHelloWorld3 _),
  def createHelloWorld3(creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  //  restCall(Method.POST,   "/api/hello-worlds/:id/creation/:creationId",         createHelloWorld4 _),
  def createHelloWorld4(helloWorldId: String, creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]

  //def destroyHelloWorld(helloWorldId: String)
  //  : ServiceCall[NotUsed, Done]

//  def improveHelloWorldDescription(helloWorldId: String)
//    : ServiceCall[ImproveHelloWorldDescriptionRequest, ImproveHelloWorldDescriptionResponse]

  /**
    * Get a "Hello World" with the given surrogate key ID.
    *
    * @param helloWorldId The ID of the "Hello World" to get.
    * @return HTTP 200 status code with the current state of the "Hello World" resource.
    *
    * Example:
    * curl http://localhost:9000/api/hello-worlds/123e4567-e89b-12d3-a456-426655440000
    */
  //def getHelloWorld(
  //    helloWorldId: String): ServiceCall[NotUsed, GetHelloWorldResponse]

  /**
    * Get all "Hello Worlds".
    *
    * @return A list of "Hello World" resources.
    *
    * Example:
    * curl http://localhost:9000/api/hello-worlds
    */
//  def getAllHelloWorlds(page: Option[String]): ServiceCall[NotUsed, utils.PagingState[GetAllHelloWorldsResponse]]
  //def getAllHelloWorlds: ServiceCall[NotUsed, GetAllHelloWorldsResponse]

//  def streamHelloWorlds
//    : ServiceCall[NotUsed, Source[HelloWorldResource, NotUsed]]

// Hello World Topic

  def helloWorldMessageBrokerEvents: Topic[HelloWorldMessageBrokerEvent]

}

// Hello World regex matchers

object Matchers {
  val Email =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
  val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
  val Name = """^[a-zA-Z0-9\-\.\_\~]{1,128}$"""
  val Description = """^.{1,2048}$"""
  val Motivation = """^.{1,2048}$"""
}

// Hello World algebraic data type {
//
// An algebraic data type is a kind of composite type.
// They are built up from Product types and Sum types.
//
// Product types - a tuple or record (this and that)
//   class ScalaPerson(val name: String, val age: Int)
//
// Sum types - a disjoint union or variant type (this or that)
//   sealed trait Pet
//   case class Cat(name: String) extends Pet
//   case class Fish(name: String, color: String) extends Pet
//   case class Squid(name: String, age: Int) extends Pet

case class HelloWorld(
  name: String,
  description: Option[String])

object HelloWorld {
  implicit val format: Format[HelloWorld] = Jsonx.formatCaseClass

  val helloWorldValidator: Validator[HelloWorld] =
    validator[HelloWorld] { helloWorld =>
      helloWorld.name is notEmpty
      helloWorld.name should matchRegexFully(Matchers.Name)
      helloWorld.description.each should matchRegexFully(Matchers.Description)
    }
}
// }

// Supporting algebraic data types {
case class Identity(
  identifier: String,
  revision: Option[Int],    // a monotonically increasing counter of changes
  hash: Option[String])

object Identity {
  implicit val format: Format[Identity] = Jsonx.formatCaseClass

  val identityValidator: Validator[Identity] =
    validator[Identity] { identity =>
      identity.identifier is notEmpty
      identity.identifier should matchRegexFully(Matchers.Id)
      // need Option[Int]
      //identity.revision should be >= 0
    }
}

case class HypertextApplicationLanguage(
  )

case class HalLink(
  rel: String,
  href: String,
  deprecation: Option[String] = None,
  name: Option[String] = None,
  profile: Option[String] = None,
  title: Option[String] = None,
  hreflang: Option[String] = None,
  `type`: Option[String] = None,
  templated: Boolean = false) {

  def withDeprecation(url: String) = this.copy(deprecation = Some(url))
  def withName(name: String) = this.copy(name = Some(name))
  def withProfile(profile: String) = this.copy(profile = Some(profile))
  def withTitle(title: String) = this.copy(title = Some(title))
  def withHreflang(lang: String) = this.copy(hreflang = Some(lang))
  def withType(mediaType: String) = this.copy(`type` = Some(mediaType))
}

object HalLink {
  implicit val format: Format[HalLink] = Jsonx.formatCaseClass
}

// }

// Resource

case class HelloWorldResource(
  helloWorld: HelloWorld
)

object HelloWorldResource {
  implicit val format: Format[HelloWorldResource] = Jsonx.formatCaseClass

  val helloWorldResourceValidator: Validator[HelloWorldResource] =
    validator[HelloWorldResource] { helloWorldResource =>
      helloWorldResource.helloWorld is valid(HelloWorld.helloWorldValidator)
    }
}

// Request

// TODO: include span ID as the unique identity of a CreateHelloWorldRequest

case class CreateHelloWorldRequest(
    helloWorld: HelloWorld
) {}

case object CreateHelloWorldRequest {
  implicit val format: Format[CreateHelloWorldRequest] = Jsonx.formatCaseClass

  implicit val createHelloWorldRequestValidator
    : Validator[CreateHelloWorldRequest] =
    validator[CreateHelloWorldRequest] { createHelloWorldRequest =>
      createHelloWorldRequest.helloWorld is valid(HelloWorld.helloWorldValidator)
    }
}

case class ReplaceHelloWorldRequest(
    replacementHelloWorldResource: HelloWorldResource,
    motivation: Option[String]
) {}

case object ReplaceHelloWorldRequest {
  implicit val format: Format[ReplaceHelloWorldRequest] = Jsonx.formatCaseClass

  implicit val replaceHelloWorldRequestValidator
    : Validator[ReplaceHelloWorldRequest] =
    validator[ReplaceHelloWorldRequest] { replaceHelloWorldRequest =>
      replaceHelloWorldRequest.replacementHelloWorldResource is valid(HelloWorldResource.helloWorldResourceValidator)
      replaceHelloWorldRequest.motivation.each should matchRegexFully(Matchers.Motivation)
    }
}

// Response

case class CreateHelloWorldResponse(
    helloWorldId: String,
    helloWorld: HelloWorld
)

object CreateHelloWorldResponse {
  implicit val format: Format[CreateHelloWorldResponse] = Json.format
}

case class ReplaceHelloWorldResponse(
    helloWorldId: String,
    helloWorld: HelloWorld
)

object ReplaceHelloWorldResponse {
  implicit val format: Format[ReplaceHelloWorldResponse] = Json.format
}

case class GetHelloWorldResponse(
    helloWorldId: String,
    helloWorld: HelloWorld
)

object GetHelloWorldResponse {
  implicit val format: Format[GetHelloWorldResponse] = Json.format
}

case class GetAllHelloWorldsResponse(helloWorlds: Seq[HelloWorldResource])

object GetAllHelloWorldsResponse {
  implicit val format: Format[GetAllHelloWorldsResponse] = Json.format
}

// Message Broker Event
// One service to many other services

sealed trait HelloWorldMessageBrokerEvent {
  val helloWorldId: String
}

case class HelloWorldCreated(
    helloWorldId: String,
    helloWorld: HelloWorld
) extends HelloWorldMessageBrokerEvent

object HelloWorldCreated {
  implicit val format: Format[HelloWorldCreated] = Json.format
}

//case class HelloWorldBrokerEvent(event: HelloWorldEventType,
//                          id: String,
//                          data: Map[String, String] = Map.empty[String, String])

object HelloWorldMessageBrokerEvent {
  implicit val format: Format[HelloWorldMessageBrokerEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}

//object HelloWorldEventTypes extends Enumeration {
//  type HelloWorldEventType = Value
//  val REGISTERED, DELETED, VERIFIED, UNVERIFIED = Value
//
//  implicit val format: Format[HelloWorldEventType] = enumFormat(HelloWorldEventTypes)
//}
