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
      restCall(Method.PATCH,  "/api/hello-worlds/:id",   patchHelloWorld _),
      restCall(Method.DELETE, "/api/hello-worlds/:id",   deleteHelloWorld _),

      restCall(Method.GET,    "/api/hello-worlds/:id",   getHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds",       getAllHelloWorlds _),
      // CRUDy DDDified REST without a proper ubiquitious language
      restCall(Method.POST,   "/api/hello-worlds/creation",                         createHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/creation",                     createHelloWorld2 _),
      restCall(Method.POST,   "/api/hello-worlds/creation/:creationId",             createHelloWorld3 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/creation/:creationId",         createHelloWorld4 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/replacement",                  replaceHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/replacement/:replacementId",   replaceHelloWorld2 _),
      restCall(Method.GET,    "/api/hello-worlds/:id/mutation",                     mutateHelloWorld1 _),
      restCall(Method.GET,    "/api/hello-worlds/:id/mutation/:mutationId",         mutateHelloWorld2 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/deactivation",                 deactivateHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id/deactivation/:deactivationId", deactivateHelloWorld2 _),
      //restCall(Method.POST,   "/api/hello-worlds/:id/reactivation/:reactivationId", reactivateHelloWorld _),
      //restCall(Method.GET,    "/api/hello-worlds/:id/creation/:creationId",         getCreateHelloWorld _),
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

// Hello World Service Calls

// Hello World Creation Calls {
  /**
    * Rest api allowing an authenticated user to create a "Hello World" aggregate.
    *
    * @param  helloWorldId  Optional unique identifier of the "Hello World"
    *         creationId    Optional unique identifier of the creation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was created successfully
    *         HTTP 400 Bad Request           if domain validation of the [[CreateHelloWorldRequest]] failed
    *         HTTP 401 Unauthorized          if JSON Web Token is missing
    *         HTTP 403 Forbidden             if authorization failure
    *         HTTP 404 Not Found             if requested resource doesn't exist, or so as to not reveal a 401 or 403
    *         HTTP 409 Conflict              if the "Hello World" already exists with the same unique identity
    *         HTTP 422 Unprocessable Entity  if the aggregate is not in the proper state to perform this action.
    *
    * REST POST endpoints:
    *   /api/hello-worlds
    *   /api/hello-worlds/:id
    *   /api/hello-worlds/creation
    *   /api/hello-worlds/:id/creation
    *   /api/hello-worlds/creation/:creationId
    *   /api/hello-worlds/:id/creation/:creationId
    *
    * Examples:
    * CT="Content-Type: application/json"
    * DATA='{"helloWorld": {"name": "test", "description": "test description"}}'
    * curl -H $CT -X POST -d $DATA http://localhost:9000/api/hello-worlds
    */
  def postHelloWorld1:                                             ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def postHelloWorld2(helloWorldId: String):                       ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def createHelloWorld1:                                           ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def createHelloWorld2(helloWorldId: String):                     ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def createHelloWorld3(creationId: String):                       ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]
  def createHelloWorld4(helloWorldId: String, creationId: String): ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]]

// }

// Hello World Replacement Calls {
  /**
    * Rest api allowing an authenticated user to replace a "Hello World".
    *
    * @param  helloWorldId   The unique identifier of the "Hello World"
    *         replacementId  Optional unique identifier of the replacement subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was replaced successfully
    *         HTTP 400 Bad Request           if domain validation of the [[ReplaceHelloWorldRequest]] failed
    *         HTTP 401 Unauthorized          if JSON Web Token is missing
    *         HTTP 403 Forbidden             if authorization failure (use 404 if authz failure shouldn't be revealed)
    *         HTTP 404 Not Found             if requested resource doesn't exist, or so as to not reveal a 401 or 403
    *         HTTP 422 Unprocessable Entity  if the aggregate is not in the proper state to perform this action
    *
    * REST PUT endpoint:
    *   /api/hello-worlds/:id
    * REST POST endpoints:
    *   /api/hello-worlds/:id/mutation
    *   /api/hello-worlds/:id/mutation/:mutationId
    *
    * Example:
    * CT="Content-Type: application/json"
    * DATA='{"helloWorld": {"name": "test", "description": "different description"}}'
    * curl -H $CT -X PUT -d $DATA http://localhost:9000/api/hello-worlds/cjq5au9sr000caqyayo9uktss
    */
  def putHelloWorld(helloWorldId: String):                             ServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]]
  def replaceHelloWorld1(helloWorldId: String):                        ServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]]
  def replaceHelloWorld2(helloWorldId: String, replacementId: String): ServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]]

// }

// Hello World Mutation Calls {
  /**
    * Rest api allowing an authenticated user to mutate a "Hello World".
    *
    * @param  helloWorldId  The unique identifier of the "Hello World"
    *         mutationId    Optional unique identifier of the mutation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was mutated successfully
    *         HTTP 400 Bad Request           if domain validation of the [[MutateHelloWorldRequest]] failed
    *         HTTP 401 Unauthorized          if JSON Web Token is missing
    *         HTTP 403 Forbidden             if authorization failure (use 404 if authz failure shouldn't be revealed)
    *         HTTP 404 Not Found             if requested resource doesn't exist, or so as to not reveal a 401 or 403
    *         HTTP 422 Unprocessable Entity  if the aggregate is not in the proper state to perform this action
    *
    * REST PATCH endpoint:
    *   /api/hello-worlds/:id
    * REST POST endpoints:
    *   /api/hello-worlds/:id/replacement
    *   /api/hello-worlds/:id/replacement/:replacementId
    *
    * Example:
    * CT="Content-Type: application/json"
    * DATA='[{"op": "replace", "path": "/name", "value": "new name"}]'
    * curl -H $CT -X PATCH -d $DATA http://localhost:9000/api/hello-worlds/cjq5au9sr000caqyayo9uktss
    */
  def patchHelloWorld(helloWorldId: String):                       ServiceCall[MutateHelloWorldRequest, Either[ErrorResponse, MutateHelloWorldResponse]]
  def mutateHelloWorld1(helloWorldId: String):                     ServiceCall[MutateHelloWorldRequest, Either[ErrorResponse, MutateHelloWorldResponse]]
  def mutateHelloWorld2(helloWorldId: String, mutationId: String): ServiceCall[MutateHelloWorldRequest, Either[ErrorResponse, MutateHelloWorldResponse]]

// }

// Hello World Deactivation Calls {
  /**
    * Rest api allowing an authenticated user to deactivate a "Hello World".
    *
    * @param  helloWorldId    The unique identifier of the "Hello World"
    *         deactivationId  Optional unique identifier of the deactivation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was deactivated successfully
    *         HTTP 400 Bad Request           if domain validation of the [[DeactivateHelloWorldRequest]] failed
    *         HTTP 401 Unauthorized          if JSON Web Token is missing
    *         HTTP 403 Forbidden             if authorization failure (use 404 if authz failure shouldn't be revealed)
    *         HTTP 404 Not Found             if requested resource doesn't exist, or so as to not reveal a 401 or 403
    *         HTTP 422 Unprocessable Entity  if the aggregate is not in the proper state to perform this action
    *
    * REST DELETE endpoint:
    *   /api/hello-worlds/:id
    * REST POST endpoints:
    *   /api/hello-worlds/:id/deactivation
    *   /api/hello-worlds/:id/deactivation/:deactivationId
    *
    * Example:
    * CT="Content-Type: application/json"
    * curl -H $CT -X DELETE http://localhost:9000/api/hello-worlds/cjq5au9sr000caqyayo9uktss
    */
  def patchHelloWorld(helloWorldId: String):                               ServiceCall[DeactivateHelloWorldRequest, Either[ErrorResponse, DeactivateHelloWorldResponse]]
  def deactivateHelloWorld1(helloWorldId: String):                         ServiceCall[DeactivateHelloWorldRequest, Either[ErrorResponse, DeactivateHelloWorldResponse]]
  def deactivateHelloWorld2(helloWorldId: String, deactivationId: String): ServiceCall[DeactivateHelloWorldRequest, Either[ErrorResponse, DeactivateHelloWorldResponse]]

// }

// Hello World Reactivation Calls {
  /**
    * Rest api allowing an authenticated user to reactivate a "Hello World".
    *
    * @param  helloWorldId    The unique identifier of the "Hello World"
    *         reactivationId  Optional unique identifier of the reactivation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was reactivated successfully
    *         HTTP 400 Bad Request           if domain validation of the [[ReactivateHelloWorldRequest]] failed
    *         HTTP 401 Unauthorized          if JSON Web Token is missing
    *         HTTP 403 Forbidden             if authorization failure (use 404 if authz failure shouldn't be revealed)
    *         HTTP 404 Not Found             if requested resource doesn't exist, or so as to not reveal a 401 or 403
    *         HTTP 422 Unprocessable Entity  if the aggregate is not in the proper state to perform this action
    *
    * REST POST endpoints:
    *   /api/hello-worlds/:id/reactivation
    *   /api/hello-worlds/:id/reactivation/:reactivationId
    *
    * Example:
    * CT="Content-Type: application/json"
    * curl -H $CT -X POST http://localhost:9000/api/hello-worlds/cjq5au9sr000caqyayo9uktss/reactivation
    */
  def patchHelloWorld(helloWorldId: String):                               ServiceCall[ReactivateHelloWorldRequest, Either[ErrorResponse, ReactivateHelloWorldResponse]]
  def reactivateHelloWorld1(helloWorldId: String):                         ServiceCall[ReactivateHelloWorldRequest, Either[ErrorResponse, ReactivateHelloWorldResponse]]
  def reactivateHelloWorld2(helloWorldId: String, reactivationId: String): ServiceCall[ReactivateHelloWorldRequest, Either[ErrorResponse, ReactivateHelloWorldResponse]]

// }

// Hello World Get Calls {
  /**
    * Rest api allowing an authenticated user to get a "Hello World" with the given surrogate key.
    *
    * @param helloWorldId The ID of the "Hello World" to get
    *
    * @return HTTP 200 status code with the current state of the "Hello World" resource.
    *
    * Example:
    * curl http://localhost:9000/api/hello-worlds/123e4567-e89b-12d3-a456-426655440000
    */
  def getHelloWorld(helloWorldId: String): ServiceCall[NotUsed, Either[ErrorResponse, GetHelloWorldResponse]]

  //def getHelloWorld(
  //    helloWorldId: String): ServiceCall[NotUsed, GetHelloWorldResponse]

// }

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
  halLinks: Seq[HalLink]
  )

object HypertextApplicationLanguage {
  implicit val format: Format[HypertextApplicationLanguage] = Jsonx.formatCaseClass
}

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
    replacementHelloWorld: HelloWorld,
    motivation: Option[String]
) {}

case object ReplaceHelloWorldRequest {
  implicit val format: Format[ReplaceHelloWorldRequest] = Jsonx.formatCaseClass

  implicit val replaceHelloWorldRequestValidator
    : Validator[ReplaceHelloWorldRequest] =
    validator[ReplaceHelloWorldRequest] { replaceHelloWorldRequest =>
      replaceHelloWorldRequest.replacementHelloWorld is valid(HelloWorld.helloWorldValidator)
      replaceHelloWorldRequest.motivation.each should matchRegexFully(Matchers.Motivation)
    }
}

// Response

case class CreateHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)

object CreateHelloWorldResponse {
  implicit val format: Format[CreateHelloWorldResponse] = Jsonx.formatCaseClass
}

case class ReplaceHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
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
