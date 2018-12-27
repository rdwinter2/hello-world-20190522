<!--- to transclude content use the following syntax at the beginning of a line "transclude::file_name.ext::[an optional regular expression]" -->
<!--- Never directly edit transcluded content, always edit the source file -->
Hello World
=============

This project has been generated using the rdwinter2/lagom-scala.g8 template.

For instructions on running and testing the project, see https://www.lagomframework.com/get-started-scala.html.

To generate a new project execute the following and supply values for (name, plural_name, organization, version, and package):

```bash
sbt new rdwinter2/lagom-scala.g8
```

After running `git init` or cloning from a repository `cd` into the directory and run `./custom-hooks/run-after-clone.sh`.

The REST call identifiers for the Hello World project are defined as:

<!--- transclude::api/HelloWorldService.scala::[override final def descriptor = {] cjq712drj000096yayluvkpqt -->

```scala
  override final def descriptor = {
    import Service._
    // @formatter:off
    named("hello-world").withCalls(
      // CRUDy plain REST
      restCall(Method.POST,   "/api/hello-worlds",     postHelloWorld1 _),
      restCall(Method.POST,   "/api/hello-worlds/:id", postHelloWorld2 _),
      restCall(Method.PUT,    "/api/hello-worlds/:id", putHelloWorld _),
      restCall(Method.PATCH,  "/api/hello-worlds/:id", patchHelloWorld _),
      restCall(Method.DELETE, "/api/hello-worlds/:id", deleteHelloWorld _),
      restCall(Method.GET,    "/api/hello-worlds/:id", getHelloWorld _),
      restCall(Method.GET,    "/api/hello-worlds",     getAllHelloWorlds _),
      // CRUDy DDDified REST without a proper ubiquitious language
      // Create
      restCall(Method.POST, "/api/hello-worlds/creation",                                createHelloWorld1 _),
      restCall(Method.POST, "/api/hello-worlds/:id/creation",                            createHelloWorld2 _),
      restCall(Method.POST, "/api/hello-worlds/creation/:creationId",                    createHelloWorld3 _),
      restCall(Method.POST, "/api/hello-worlds/:id/creation/:creationId",                createHelloWorld4 _),
      restCall(Method.GET,  "/api/hello-worlds/:id/creation/:creationId",                getCreationHelloWorld _),
      pathCall(             "/api/hello-worlds/:id/creation/:creationId/stream",         streamCreationHelloWorld _),
      // Read
      // Update
      restCall(Method.POST, "/api/hello-worlds/:id/replacement",                         replaceHelloWorld1 _),
      restCall(Method.POST, "/api/hello-worlds/:id/replacement/:replacementId",          replaceHelloWorld2 _),
      restCall(Method.GET,  "/api/hello-worlds/:id/replacement/:replacementId",          getReplacementHelloWorld _),
      pathCall(             "/api/hello-worlds/:id/replacement/:replacementId/stream",   streamReplacementHelloWorld _),
      restCall(Method.POST, "/api/hello-worlds/:id/mutation",                            mutateHelloWorld1 _),
      restCall(Method.POST, "/api/hello-worlds/:id/mutation/:mutationId",                mutateHelloWorld2 _),
      restCall(Method.GET,  "/api/hello-worlds/:id/mutation/:mutationId",                getMutationHelloWorld _),
      pathCall(             "/api/hello-worlds/:id/mutation/:mutationId/stream",         streamMutationHelloWorld _),
      // Delete
      restCall(Method.POST, "/api/hello-worlds/:id/deactivation",                        deactivateHelloWorld1 _),
      restCall(Method.POST, "/api/hello-worlds/:id/deactivation/:deactivationId",        deactivateHelloWorld2 _),
      restCall(Method.GET,  "/api/hello-worlds/:id/deactivation/:deactivationId",        getDeactivationHelloWorld _),
      pathCall(             "/api/hello-worlds/:id/deactivation/:deactivationId/stream", streamDeactivationHelloWorld _),
      // Undelete
      restCall(Method.POST, "/api/hello-worlds/:id/reactivation",                        reactivateHelloWorld1 _),
      restCall(Method.POST, "/api/hello-worlds/:id/reactivation/:reactivationId",        reactivateHelloWorld2 _),
      restCall(Method.GET,  "/api/hello-worlds/:id/reactivation/:reactivationId",        getReactivationHelloWorld _),
      pathCall(             "/api/hello-worlds/:id/reactivation/:reactivationId/stream", streamReactivationHelloWorld _),
      // DDDified REST using the bounded context's ubiquitious language
      //restCall(Method.POST, "/api/hello-worlds/:id/description-enhancement/:enhancementId", enhanceDescriptionHelloWorld _),
//      pathCall("/api/ff hello-worlds/stream", streamHelloWorlds _),
    )
      .withAutoAcl(true)
      .withExceptionSerializer(new DefaultExceptionSerializer(Environment.simple(mode = Mode.Prod)))
      .withTopics(
        topic("helloWorld-HelloWorldMessageBrokerEvent", this.helloWorldMessageBrokerEvents)
      )
    // @formatter:on
  }
```

<!--- transclude cjq712drj000096yayluvkpqt -->
NOTE: For naming resources in a domain driven design (DDD) manner, focus on domain events not low-level create, read, update, and delete (CRUD) operations.

From [Roy Fielding's dissertation](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm#sec_5_2_1_1):
> The key abstraction of information in REST is a resource. Any information that can be named can be a resource: a document or image, a temporal service (e.g. "today's weather in Los Angeles"), a collection of other resources, a non-virtual object (e.g. a person), and so on. In other words, any concept that might be the target of an author's hypertext reference must fit within the definition of a resource. A resource is a conceptual mapping to a set of entities, not the entity that corresponds to the mapping at any particular point in time.

From [REST API Design - Resource Modeling](https://www.thoughtworks.com/insights/blog/rest-api-design-resource-modeling):
> The way to escape low-level CRUD is to create business operation or business process resources, or what we can call as "intent" resources that express a business/domain level "state of wanting something" or "state of the process towards the end result". But to do this you need to ensure you identify the true owners of all your state. In a world of four-verb (AtomPub-style) CRUD, it's as if you allow random external parties to mess around with your resource state, through PUT and DELETE, as if the service were just a low-level database. PUT puts too much internal domain knowledge into the client. The client shouldn't be manipulating internal representation; it should be a source of user intent.
> HTTP verb PUT can be used for idempotent resource updates (or resource creations in some cases) by the API consumer. However, use of PUT for complex state transitions can lead to synchronous cruddy CRUD. It also usually throws away a lot of information that was available at the time the update was triggered - what was the real business domain event that triggered this update? With “[REST without PUT](https://www.thoughtworks.com/radar/techniques/rest-without-put)” technique, the idea is that consumers are forced to post new 'nounified' request resources.

The algebraic data type for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[Hello World algebraic data type {] cjq712dzb000196yacybtff7y -->

```scala
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
```

<!--- transclude cjq712dzb000196yacybtff7y -->

With regular expression validation matchers:

<!--- transclude::api/HelloWorldService.scala::[object Matchers {] cjq712e7h000296yapfe0jlvo -->

```scala
object Matchers {
  val Email =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
  val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
  val Name = """^[a-zA-Z0-9\-\.\_\~]{1,128}$"""
  val Description = """^.{1,2048}$"""
  val Motivation = """^.{1,2048}$"""
  val Op = """^add|remove|replace|move|copy|test$"""
}
```

<!--- transclude cjq712e7h000296yapfe0jlvo -->

And supporting algebraic data types:

<!--- transclude::api/HelloWorldService.scala::[Supporting algebraic data types {] cjq712efv000396ya7j7b3j2o -->

```scala
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

case class Mutation(
  op: String,
  path: String,
  value: Option[String]
  )

object Mutation {
  implicit val format: Format[Mutation] = Jsonx.formatCaseClass

  val mutationValidator: Validator[Mutation] =
    validator[Mutation] { mutation =>
      mutation.op is notEmpty
      mutation.path is notEmpty
      mutation.op should matchRegexFully(Matchers.Op)
    }
}
// }
```

<!--- transclude cjq712efv000396ya7j7b3j2o -->

The REST resource for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class HelloWorldResource(] cjq712enu000496yagg9ofmtb -->

```scala
case class HelloWorldResource(
  helloWorld: HelloWorld
)
```

<!--- transclude cjq712enu000496yagg9ofmtb -->

The DDD aggregate for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldAggregate(] cjq712evy000596ya564e6irf -->

```scala
case class HelloWorldAggregate(
  helloWorldIdentity: Identity,
  helloWorldResource: HelloWorldResource
)
```

<!--- transclude cjq712evy000596ya564e6irf -->

The state for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override type State = .*] cjq712f3r000696yawp9ws28m -->

```scala
  override type State = Option[HelloWorldState]
```

<!--- transclude cjq712f3r000696yawp9ws28m -->

And uses the following HelloWorldState algebraic data type:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldState(] cjq712fbt000796yawom5nmdq -->

```scala
case class HelloWorldState(
  helloWorldAggregate: HelloWorldAggregate,
  status: HelloWorldStatus.Status = HelloWorldStatus.NONEXISTENT
)
```

<!--- transclude cjq712fbt000796yawom5nmdq -->

The initial state for all DDD aggregates is:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override def initialState: .*] cjq712fke000896yaa3whv1q5 -->

```scala
  override def initialState: Option[HelloWorldState] = None
```

<!--- transclude cjq712fke000896yaa3whv1q5 -->

The possible statuses for the Hello World aggregate are defined to be:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[object HelloWorldStatus extends Enumeration {] cjq712fs1000996yavlu2tj1p -->

```scala
object HelloWorldStatus extends Enumeration {
  val NONEXISTENT, ACTIVE, ARCHIVED, UNKNOWN = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
//  implicit val pathParamSerializer: PathParamSerializer[Status] =
//    PathParamSerializer.required("helloWorldStatus")(withName)(_.toString)
}
```

<!--- transclude cjq712fs1000996yavlu2tj1p -->

The finite state machine (FSM) for the DDD aggregate is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[Finite State Machine (FSM) {] cjq712fzw000a96yalbthyiq7 -->

```scala

```

<!--- transclude cjq712fzw000a96yalbthyiq7 -->

The entity for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[final class HelloWorldEntity extends PersistentEntity {] cjq712g8u000b96yalle0g8xr -->

```scala

```

<!--- transclude cjq712g8u000b96yalle0g8xr -->

For CRUDy operations the following subordinate, nounified, resources are created:
* Creation
* Replacement
* Mutation: http://jsonpatch.com/
* Deactivation
* Reactivation

Creation
--------
A Creation request takes a desired HelloWorld algebraic data type and responds with the created HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not created the service responses with an ErrorResponse. The following REST calls can be used. Identifiers are optional. If specified all identifiers must adhere to the Matcher for Id. Otherwise, the service will create and use a collision resistant unique identifier.

<!--- transclude::api/HelloWorldService.scala::[Hello World Creation Calls {] cjq712gh3000c96yay7rhzwli -->

```scala
// Hello World Creation Calls {
  /**
    * Rest api allowing an authenticated user to create a "Hello World" aggregate.
    *
    * @param  helloWorldId  Optional unique identifier of the "Hello World"
    *         creationId    Optional unique identifier of the creation subordinate resource
    *
    * @return HTTP 201 Created               if the "Hello World" was created successfully
    *         HTTP 202 Accepted              if the request has been accepted, but the processing is not complete
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
  // Retrieve status of creation request
  def getCreationHelloWorld(helloWorldId: String, creationId: String):    ServiceCall[NotUsed, Either[ErrorResponse, CreationHelloWorldResponse]]
  def streamCreationHelloWorld(helloWorldId: String, creationId: String): ServiceCall[NotUsed, Source[CreationHelloWorldResponse, NotUsed]]
// }
```

<!--- transclude cjq712gh3000c96yay7rhzwli -->

The Matcher for identifiers is defined to be:

<!--- transclude::api/HelloWorldService.scala::[val Id = .*] cjq712goy000d96yai01moimp -->

```scala
  val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
```

<!--- transclude cjq712goy000d96yai01moimp -->

The create Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class CreateHelloWorldRequest(] cjq712gxu000e96yat2hz1m13 -->

```scala
case class CreateHelloWorldRequest(
    helloWorld: HelloWorld
)
```

<!--- transclude cjq712gxu000e96yat2hz1m13 -->

And the create Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class CreateHelloWorldResponse(] cjq712h5x000f96yapecu53jh -->

```scala
case class CreateHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjq712h5x000f96yapecu53jh -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[Hello World Creation Calls {] cjq712he2000g96ya9kd1u0qb -->

```scala
// Hello World Creation Calls {
  override def postHelloWorld
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        val creationId = Cuid.createCuid()
        logger.info(
          s"Posting 'Hello World' with identifier $helloWorldId...")
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld1
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        val creationId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }
  override def createHelloWorld2(helloWorldId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val creationId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld3(creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld4(helloWorldId: String, creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  def createHelloWorldInternal(helloWorldId: String, creationId: String)
    : ServerServiceCall[CreateHelloWorldRequest, CreateHelloWorldResponse] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { createHelloWorldRequest =>
        val username = tokenContent.username
        logger.info(s"User $username is creating a Hello World ")
        logger.info(
          s"Creating 'Hello World' with input $createHelloWorldRequest...")
        val validationResult = validate(createHelloWorldRequest)
        validationResult match {
          case failure: Failure =>
            throw new TransportException(TransportErrorCode.BadRequest,
                                         "request validation failure")
          case _ =>
        }
        val helloWorldAggregate =
          HelloWorldAggregate(helloWorldId, HelloWorldResource(createHelloWorldRequest.helloWorld))
        val helloWorldResource =
          HelloWorldResource(createHelloWorldRequest.helloWorld)
        val helloWorldEntityRef =
          registry.refFor[HelloWorldEntity](helloWorldId.toString)
        logger.info(s"Publishing event $helloWorldAggregate")
        val topic = pubSubRegistry.refFor(TopicId[HelloWorldResource])
        topic.publish(helloWorldResource)
        helloWorldEntityRef
          .ask(CreateHelloWorldCommand(helloWorldAggregate))
          .map { _ =>
            mapToCreateHelloWorldResponse(helloWorldId, helloWorldResource)
          }
      }
    }

  private def mapToCreateHelloWorldResponse(
      helloWorldId: String,
      helloWorldResource: HelloWorldResource): CreateHelloWorldResponse = {
    CreateHelloWorldResponse(helloWorldId,
                             helloWorldResource.helloWorld)
  }

  private def mapToCreateHelloWorldResponse(
      helloWorldState: HelloWorldState): CreateHelloWorldResponse = {
    CreateHelloWorldResponse(helloWorldState.helloWorldAggregate map { _.helloWorldId } getOrElse "No identifier",
                             helloWorldState.helloWorldAggregate map { _.helloWorldResource.helloWorld} getOrElse HelloWorld("No name", Some("No description")))
  }
// }
```

<!--- transclude cjq712he2000g96ya9kd1u0qb -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World command {] cjq712hmf000h96yaf3tu88pw -->

```scala
// The create Hello World command {
case class CreateHelloWorldCommand(
  helloWorldAggregate: HelloWorldAggregate)
    extends HelloWorldCommand[Either[ServiceError, CreateHelloWorldReply]]

object CreateHelloWorldCommand {
  implicit val format: Format[CreateHelloWorldCommand] = Json.format
}
// }
```

<!--- transclude cjq712hmf000h96yaf3tu88pw -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World reply {] cjq712hu1000i96yarucgtrsn -->

```scala
// The create Hello World reply {
case class CreateHelloWorldReply(
  helloWorldAggregate: HelloWorldAggregate)

object CreateHelloWorldReply {
  implicit val format: Format[CreateHelloWorldReply] = Json.format
}
// }
```

<!--- transclude cjq712hu1000i96yarucgtrsn -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {] cjq712i28000j96yakf000bxz -->

```scala

```

<!--- transclude cjq712i28000j96yakf000bxz -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[] cjq712iag000k96yaifqppiop -->

```scala
package com.example.helloworld.impl

import com.example.common.authentication.AuthenticationServiceComposition._
import com.example.common.authentication.TokenContent
import com.example.common.utils.JsonFormats._
import com.example.common.response.{
  ErrorResponse,
  ErrorResponses => ER
}
import com.example.common.utils.Marshaller
//import com.example.common.validation.ValidationUtil._
import com.example.helloworld.api._
import com.example.helloworld.impl.ServiceErrors._
import com.example.helloworld.impl.ServiceErrors.ServiceError

import akka.{Done, NotUsed}
import akka.persistence.query.Offset
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.datastax.driver.core._
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.internal.client.CircuitBreakerMetricsProviderImpl
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.{
  TransportErrorCode,
  TransportException,
  NotFound,
  RequestHeader,
  ResponseHeader
}
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.{
  CassandraReadSide,
  CassandraSession,
  CassandraPersistenceComponents
}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{
  AggregateEvent,
  AggregateEventShards,
  AggregateEventTag,
  EventStreamElement,
  PersistentEntity,
  PersistentEntityRegistry,
  ReadSideProcessor
}
import com.lightbend.lagom.scaladsl.playjson.{
  JsonSerializer,
  JsonSerializerRegistry
}
import com.lightbend.lagom.scaladsl.pubsub.{
  PubSubComponents,
  PubSubRegistry,
  TopicId
}
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.rp.servicediscovery.lagom.scaladsl.LagomServiceLocatorComponents
import com.softwaremill.macwire._
import com.wix.accord._
import com.wix.accord.dsl._
import com.wix.accord.Descriptions._
import cool.graph.cuid._
import scala.util.Try
import java.util.UUID
import julienrf.json.derived
import org.slf4j.LoggerFactory
import play.api.{Environment, LoggerConfigurator}
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.http.HeaderNames
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

// Hello World Service Implementation

class HelloWorldServiceImpl(
    registry: PersistentEntityRegistry,
    helloWorldRepository: HelloWorldRepository,
    pubSubRegistry: PubSubRegistry //,
//    helloWorldService: HelloWorldService
)(implicit ec: ExecutionContext)
    extends HelloWorldService
    with Marshaller {
  private val logger = LoggerFactory.getLogger(classOf[HelloWorldServiceImpl])

// Hello World Creation Calls {
  override def postHelloWorld
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        val creationId = Cuid.createCuid()
        logger.info(
          s"Posting 'Hello World' with identifier $helloWorldId...")
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld1
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        val creationId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }
  override def createHelloWorld2(helloWorldId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val creationId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld3(creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        val helloWorldId = Cuid.createCuid()
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  override def createHelloWorld4(helloWorldId: String, creationId: String)
    : ServiceCall[CreateHelloWorldRequest, Either[ErrorResponse, CreateHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, createHelloWorldRequest) =>
        this
          .createHelloWorldInternal(helloWorldId, creationId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, createHelloWorldRequest).map {
            case (responseHeader, response) => (Ok, Right(response))
          }
      }
    }

  def createHelloWorldInternal(helloWorldId: String, creationId: String)
    : ServerServiceCall[CreateHelloWorldRequest, CreateHelloWorldResponse] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { createHelloWorldRequest =>
        val username = tokenContent.username
        logger.info(s"User $username is creating a Hello World ")
        logger.info(
          s"Creating 'Hello World' with input $createHelloWorldRequest...")
        val validationResult = validate(createHelloWorldRequest)
        validationResult match {
          case failure: Failure =>
            throw new TransportException(TransportErrorCode.BadRequest,
                                         "request validation failure")
          case _ =>
        }
        val helloWorldAggregate =
          HelloWorldAggregate(helloWorldId, HelloWorldResource(createHelloWorldRequest.helloWorld))
        val helloWorldResource =
          HelloWorldResource(createHelloWorldRequest.helloWorld)
        val helloWorldEntityRef =
          registry.refFor[HelloWorldEntity](helloWorldId.toString)
        logger.info(s"Publishing event $helloWorldAggregate")
        val topic = pubSubRegistry.refFor(TopicId[HelloWorldResource])
        topic.publish(helloWorldResource)
        helloWorldEntityRef
          .ask(CreateHelloWorldCommand(helloWorldAggregate))
          .map { _ =>
            mapToCreateHelloWorldResponse(helloWorldId, helloWorldResource)
          }
      }
    }

  private def mapToCreateHelloWorldResponse(
      helloWorldId: String,
      helloWorldResource: HelloWorldResource): CreateHelloWorldResponse = {
    CreateHelloWorldResponse(helloWorldId,
                             helloWorldResource.helloWorld)
  }

  private def mapToCreateHelloWorldResponse(
      helloWorldState: HelloWorldState): CreateHelloWorldResponse = {
    CreateHelloWorldResponse(helloWorldState.helloWorldAggregate map { _.helloWorldId } getOrElse "No identifier",
                             helloWorldState.helloWorldAggregate map { _.helloWorldResource.helloWorld} getOrElse HelloWorld("No name", Some("No description")))
  }
// }

  val Ok: ResponseHeader =  ResponseHeader.Ok
        .withHeader("Server", "Hello World service")

  override def putHelloWorld(helloWorldId: String): ServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { (requestHeader, replaceHelloWorldRequest) =>
        val replacementId = Cuid.createCuid()
        logger.info(
          s"Putting 'Hello World' with identifier $helloWorldId...")
        this
          .replaceHelloWorldInternal(helloWorldId, replacementId)
          .handleRequestHeader(requestHeader => requestHeader)
          .invokeWithHeaders(requestHeader, replaceHelloWorldRequest).map {
            case (responseHeader, Right(response)) => (Ok, Right(response))
          }
      }
    }

  def replaceHelloWorldInternal(helloWorldId: String, replacementId: String)
    : ServerServiceCall[ReplaceHelloWorldRequest, Either[ErrorResponse, ReplaceHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { replaceHelloWorldRequest =>
        val username = tokenContent.username
        logger.info(s"User $username is replacing Hello World $helloWorldId with a successor resource ...")
        logger.info(
          s"Replacing 'Hello World' with input $replaceHelloWorldRequest...")
        val validationResult = validate(replaceHelloWorldRequest)
        validationResult match {
          case failure: Failure =>
            throw new TransportException(TransportErrorCode.BadRequest,
                                        "request validation failure")
          case _ =>
        }
//        val helloWorldAggregate =
//          HelloWorldAggregate(helloWorldId, replaceHelloWorldRequest.helloWorld)
//        val helloWorldResource =
//          HelloWorldResource(replaceHelloWorldRequest.helloWorld)
      val helloWorldEntityRef =
          registry.refFor[HelloWorldEntity](helloWorldId.toString)
//        logger.info(s"Publishing event $helloWorldAggregate")
        helloWorldEntityRef
          .ask(ReplaceHelloWorldCommand(helloWorldId, replaceHelloWorldRequest))
          .map {
            case Right(replaceHelloWorldRequest) =>
              mapToReplaceHelloWorldResponse(replaceHelloWorldRequest)
//            case Left(errorResponse) => throw CommandFailed(???)
          }
//        val topic = pubSubRegistry.refFor(TopicId[HelloWorldResource])
//        topic.publish(helloWorldResource)
      }
    }
//  override def improveHelloWorldDescription(helloWorldId: String)
//    : ServiceCall[ImproveHelloWorldDescriptionRequest, ImproveHelloWorldDescriptionResponse]
//    authenticated { (tokenContent, _) =>
//      ServerServiceCall { (helloWorldId, improveHelloWorldDescriptionRequest: ImproveHelloWorldDescriptionRequest) =>
//      logger.info(
//        s"Improving the description of 'Hello World' with id $helloWorldId by setting it to $improveHelloWorldDescriptionRequest.description...")
//      val validationResult = validate(improveHelloWorldDescriptionRequest)
//      validationResult match {
//        case failure: Failure =>
//          throw new TransportException(TransportErrorCode.BadRequest,
//                                       "request validation failure")
//        case _ =>
//      }
//      val helloWorldEntityRef =
//        registry.refFor[HelloWorldEntity](helloWorldId.toString)
//      helloWorldEntityRef.ask(ImproveHelloWorldDescriptionCommand(ImproveHelloWorldDescriptionRequest))
//          .map { _ =>
//            mapToImproveHelloWorldDescriptionResponse(helloWorldResource)
//          }
//      }
//    }

  override def getHelloWorld(
      helloWorldId: String): ServiceCall[NotUsed, Either[ErrorResponse, GetHelloWorldResponse]] =
    authenticated { (tokenContent, _) =>
      ServerServiceCall { _ =>
        logger.info(s"Looking up 'Hello World' with ID $helloWorldId...")
        val helloWorldEntityRef =
          registry.refFor[HelloWorldEntity](helloWorldId.toString)
        helloWorldEntityRef.ask(GetHelloWorldQuery).map {
          case HelloWorldState(_, HelloWorldStatus.NONEXISTENT, _) =>
            throw NotFound(s"Hello World $helloWorldId not found")
          case HelloWorldState(Some(helloWorldAggregate), HelloWorldStatus.ACTIVE, _) =>
            Right(mapToGetHelloWorldResponse(helloWorldAggregate))
          case HelloWorldState(_, HelloWorldStatus.ARCHIVED, _) =>
            throw NotFound(s"Hello World $helloWorldId archived")
          case HelloWorldState(_, _, _) =>
            throw NotFound(s"Hello World $helloWorldId in unknown state")
        }
      }
    }

  private def mapToGetHelloWorldResponse(
      helloWorldAggregate: HelloWorldAggregate): GetHelloWorldResponse = {
    GetHelloWorldResponse(helloWorldAggregate.helloWorldId,
                          helloWorldAggregate.helloWorldResource.helloWorld)
  }

  //override def getAllHelloWorlds
  //  : ServiceCall[NotUsed, GetAllHelloWorldsResponse] = ServiceCall { _ =>
  //  logger.info("Looking up all 'Hello Worlds'...")
  //  helloWorldRepository.selectAllHelloWorlds.map(helloWorlds =>
  //    GetAllHelloWorldsResponse(helloWorlds.map(mapToHelloWorldResource)))
  //}

  private def mapToHelloWorldResource(
      helloWorldAggregate: HelloWorldAggregate): HelloWorldResource = {
    HelloWorldResource(helloWorldAggregate.helloWorldResource.helloWorld)
  }

  private def mapToReplaceHelloWorldResponse(replaceHelloWorldRequest: ReplaceHelloWorldRequest): ReplaceHelloWorldResponse = {
    ReplaceHelloWorldResponse(helloWorldAggregate.helloWorldId,
                              helloWorldAggregate.helloWorldResource.helloWorld)
  }

  override def helloWorldMessageBrokerEvents
    : Topic[HelloWorldMessageBrokerEvent] =
    TopicProducer.taggedStreamWithOffset(HelloWorldEvent.Tag.allTags.toList) {
      (tag, offset) =>
        logger.info("Creating HelloWorldEvent Topic...")
        registry
          .eventStream(tag, offset)
          .filter {
            _.event match {
              case x @ (_: HelloWorldCreatedEvent) => true
              case _                               => false
            }
          }
          .mapAsync(1)(convertEvent)
    }

  private def convertEvent(
      eventStreamElement: EventStreamElement[HelloWorldEvent])
    : Future[(HelloWorldMessageBrokerEvent, Offset)] = {
    eventStreamElement match {
      case EventStreamElement(id, HelloWorldCreatedEvent(helloWorldAggregate), offset) =>
        Future.successful {
          (HelloWorldCreated(
             helloWorldAggregate.helloWorldId,
             helloWorldAggregate.helloWorldResource.helloWorld
           ),
           offset)
        }
    }
  }

//  override def streamHelloWorlds
//    : ServiceCall[NotUsed, Source[HelloWorldResource, NotUsed]] = ServiceCall {
//    _ =>
      //val topic = pubSubRegistry.refFor(TopicId[HelloWorldResource])
//      Future.successful(topicHelloWorldCreatedEvent.subscriber)
//  }
}

// Hello World Entity

final class HelloWorldEntity extends PersistentEntity {

  //private val publishedHelloWorldCreatedEvent = pubSubRegistry.refFor(TopicId[HelloWorldCreatedEvent])

  override type Command = HelloWorldCommand[_]
  override type Event = HelloWorldEvent
  override type State = Option[HelloWorldState]

  type OnCommandHandler[M] = PartialFunction[(Command, CommandContext[M], State), Persist]
  type ReadOnlyHandler[M] = PartialFunction[(Command, ReadOnlyCommandContext[M], State), Unit]

  override def initialState: Option[HelloWorldState] = None

  // Finite State Machine (FSM) {
  override def behavior: Behavior = {
    case None => nonexistentHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ACTIVE => activeHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ARCHIVED => archivedHelloWorld
    case Some(state) => unknownHelloWorld
  }

  private val nonexistentHelloWorld = {
    getHelloWorldAction orElse {
      Actions()
        .onCommand[CreateHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { createHelloWorldCommand }
        .onCommand[ReplaceHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replyHelloWorldDoesNotExist }
        .onEvent {
          case (HelloWorldCreatedEvent(helloWorldAggregate), state) => HelloWorldState(Some(helloWorldAggregate), HelloWorldStatus.ACTIVE, 1)
          case (_, state) => state
        }
    }
  }

  private val activeHelloWorld = {
    getHelloWorldAction orElse {
      Actions()
        .onCommand[CreateHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replyConflict }
        .onCommand[ReplaceHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replaceHelloWorldCommand }
        .onEvent {
          case (HelloWorldReplacedEvent(helloWorldId, replacementHelloWorldResource, motivation), state) =>
          // for {state(Some(a),_,_)<-
            HelloWorldState(Some(HelloWorldAggregate(helloWorldId, replacementHelloWorldResource)), HelloWorldStatus.ACTIVE, 1)
          case (_, state) => state
        }
    }
  }

  private val archivedHelloWorld = {
    getHelloWorldAction orElse {
      Actions()
        .onCommand[CreateHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replyConflict }
        .onEvent {
          case (_, state) => state
        }
    }
  }

  private val unknownHelloWorld = {
    getHelloWorldAction orElse {
      Actions()
        .onCommand[CreateHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replyConflict }
        .onCommand[ReplaceHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] { replyConflict }
    }
  }
  // }

  private def getHelloWorldAction = Actions()
    .onReadOnlyCommand[GetHelloWorldQuery.type, HelloWorldState] {
      case (GetHelloWorldQuery, ctx, state) => ctx.reply(state)
    }

  private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {
    case (CreateHelloWorldCommand(helloWorldAggregate), ctx, state) =>
      ctx.thenPersist(HelloWorldCreatedEvent(helloWorldAggregate)) { evt =>
        ctx.reply(Right(helloWorldAggregate))
      }
  }

  private def replaceHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {
    case (ReplaceHelloWorldCommand(replaceHelloWorldRequest), ctx, state) =>
      ctx.thenPersist(HelloWorldReplacedEvent(replaceHelloWorldRequest)) { evt =>
        ctx.reply(Right(HelloWorldAggregate("3",replaceHelloWorldRequest.replacementHelloWorldResource)))
      }
  }

  private val notCreated = {
    getHelloWorldAction orElse {
    Actions()
      .onCommand[CreateHelloWorldCommand, Either[ServiceError, HelloWorldAggregate]] {
        case (CreateHelloWorldCommand(helloWorldAggregate), ctx, state) =>
          ctx.thenPersist(HelloWorldCreatedEvent(helloWorldAggregate)) { evt =>
            ctx.reply(Right(helloWorldAggregate))
          }
      }
    }
  }

  private def replyConflict[R]: OnCommandHandler[Either[ServiceError, R]] = {
    case (_, ctx, _) =>
      ctx.reply(Left(HelloWorldConflict))
      ctx.done
  }

  private def created(helloWorldAggregate: HelloWorldAggregate) = {
    getHelloWorldAction orElse {
    Actions()
//      .onCommand[DestroyHelloWorldCommand.type, Done] {
//        case (DestroyHelloWorldCommand, ctx, Some(u)) =>
//          ctx.thenPersist(HelloWorldDestroyedEvent(u.id))(_ => ctx.reply(Done))
//      }
//      .onCommand[ImproveHelloWorldDescripionCommand.type, Done] {
//        case (ImproveHelloWorldDescripionCommand, ctx, Some(u)) =>
//          ctx.thenPersist(HelloWorldDescripionImprovedEvent(improveHelloWorldDescripionRequest))(_ => ctx.reply(Done))
//      }
//      .onEvent {
//        case (HelloWorldDestroyedEvent(_), Some(u)) =>
//          None
//      }
//      .onEvent {
//        case (HelloWorldDescripionImprovedEvent(_), Some(u)) =>
//          None
//      }
    }
  }

  private def replyHelloWorldDoesNotExist[R]: OnCommandHandler[Either[ServiceError, R]] = {
    case (_, ctx, _) =>
      ctx.reply(Left(HelloWorldDoesNotExist))
      ctx.done
  }

}

// Hello World State

case class HelloWorldState(
  helloWorldAggregate: HelloWorldAggregate,
  status: HelloWorldStatus.Status = HelloWorldStatus.NONEXISTENT
) {
  def withStatus (status: HelloWorldStatus.Status) = copy(status = status)
}

object HelloWorldState {
  implicit val format: Format[HelloWorldState] = Json.format
  val nonexistent = HelloWorldState(None, HelloWorldStatus.NONEXISTENT, 0)
}

// Hello World Status

object HelloWorldStatus extends Enumeration {
  val NONEXISTENT, ACTIVE, ARCHIVED, UNKNOWN = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
//  implicit val pathParamSerializer: PathParamSerializer[Status] =
//    PathParamSerializer.required("helloWorldStatus")(withName)(_.toString)
}

// Hello World Aggregate

case class HelloWorldAggregate(
  helloWorldIdentity: Identity,
  helloWorldResource: HelloWorldResource
)

object HelloWorldAggregate {
  implicit val format: Format[HelloWorldAggregate] = Json.format
}

sealed trait HelloWorldCommand[R] extends ReplyType[R]

case object GetHelloWorldQuery
    extends HelloWorldCommand[HelloWorldState] {
  implicit val format: Format[GetHelloWorldQuery.type] = singletonFormat(
    GetHelloWorldQuery)
}

// The create Hello World reply {
case class CreateHelloWorldReply(
  helloWorldAggregate: HelloWorldAggregate)

object CreateHelloWorldReply {
  implicit val format: Format[CreateHelloWorldReply] = Json.format
}
// }

// The create Hello World command {
case class CreateHelloWorldCommand(
  helloWorldAggregate: HelloWorldAggregate)
    extends HelloWorldCommand[Either[ServiceError, CreateHelloWorldReply]]

object CreateHelloWorldCommand {
  implicit val format: Format[CreateHelloWorldCommand] = Json.format
}
// }

//case object DestroyHelloWorldCommand
//    extends HelloWorldCommand
//    with ReplyType[Done] {
//  implicit val format: Format[DestroyHelloWorldCommand.type] = singletonFormat(DestroyHelloWorldCommand)
//}

case class ReplaceHelloWorldCommand(ReplaceHelloWorldCommand: String, replaceHelloWorldRequest: ReplaceHelloWorldRequest)
    extends HelloWorldCommand[Either[ServiceError, HelloWorldAggregate]]

object ReplaceHelloWorldCommand {
  implicit val format: Format[ReplaceHelloWorldCommand] = Json.format
}

sealed trait HelloWorldEvent extends AggregateEvent[HelloWorldEvent] {
  override def aggregateTag = HelloWorldEvent.Tag
}

object HelloWorldEvent {
  val NumShards = 4
  val Tag: AggregateEventShards[HelloWorldEvent] =
    AggregateEventTag.sharded[HelloWorldEvent](NumShards)

  implicit val format: Format[HelloWorldEvent] =
    derived.flat.oformat((__ \ "type").format[String])
}

case class HelloWorldCreatedEvent(helloWorldAggregate: HelloWorldAggregate)
    extends HelloWorldEvent

object HelloWorldCreatedEvent {
  implicit val format: Format[HelloWorldCreatedEvent] = Json.format
}

//case class HelloWorldDestroyedEvent(helloWorldId: String)
//    extends HelloWorldEvent
//
//object HelloWorldDestroyedEvent {
//  implicit val format: Format[HelloWorldDestroyedEvent] = Json.format
//}

case class HelloWorldReplacedEvent(
  helloWorldId: String,
  replacementHelloWorldResource: HelloWorldResource,
  motivation: Option[String])
    extends HelloWorldEvent

object HelloWorldReplacedEvent {
  implicit val format: Format[HelloWorldReplacedEvent] = Json.format
}

// Hello World Application Loader

trait HelloWorldComponents
    extends LagomServerComponents
    with CassandraPersistenceComponents
    with PubSubComponents {
  implicit def executionContext: ExecutionContext

  def environment: Environment

  override lazy val lagomServer: LagomServer =
    serverFor[HelloWorldService](wire[HelloWorldServiceImpl])
  lazy val helloWorldRepository: HelloWorldRepository =
    wire[HelloWorldRepository]
  override lazy val jsonSerializerRegistry: HelloWorldSerializerRegistry.type =
    HelloWorldSerializerRegistry

  persistentEntityRegistry.register(wire[HelloWorldEntity])
  readSide.register(wire[HelloWorldEventProcessor])
}

abstract class HelloWorldApplication(context: LagomApplicationContext)
    extends LagomApplication(context)
    with HelloWorldComponents
    with AhcWSComponents
    with LagomKafkaComponents {

  // To bind to another Lagom service
  // lazy val otherService = serviceClient.implement[OtherService]
  //lazy val helloWorldService: HelloWorldService = serviceClient.implement[HelloWorldService]
}

class HelloWorldApplicationLoader extends LagomApplicationLoader {
  override def loadDevMode(
      context: LagomApplicationContext): LagomApplication = {
    // Workaround for logback.xml not being detected, see https://github.com/lagom/lagom/issues/534
    val environment = context.playContext.environment
    LoggerConfigurator(environment.classLoader).foreach {
      _.configure(environment)
    }
    // end workaround
    new HelloWorldApplication(context) with LagomDevModeComponents
  }

  override def load(context: LagomApplicationContext): LagomApplication =
    new HelloWorldApplication(context) with LagomServiceLocatorComponents {
      override lazy val circuitBreakerMetricsProvider =
        new CircuitBreakerMetricsProviderImpl(actorSystem)
    }

  override def describeService = Some(readDescriptor[HelloWorldService])
}

// Hello World Repository

private[impl] class HelloWorldRepository(session: CassandraSession)(
    implicit ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(classOf[HelloWorldRepository])

  def selectAllHelloWorlds: Future[Seq[HelloWorldAggregate]] = {
    logger.info("Querying all 'Hello Worlds'...")
    session.selectAll("""
      SELECT id, hello_world FROM hello_world
    """).map(rows => rows.map(row => convertToHelloWorldAggregate(row)))
  }

  def selectHelloWorld(id: String) = {
    logger.info(s"Querying 'Hello World' with ID $id...")
    session.selectOne("SELECT id, hello_world FROM hello_world WHERE id = ?",
                      id)
  }

  private def convertToHelloWorldAggregate(
      helloWorldRow: Row): HelloWorldAggregate = {
    HelloWorldAggregate(
      helloWorldRow.getString("id"),
      HelloWorldResource(
      Json
        .fromJson[HelloWorld](
          Json.parse(helloWorldRow.getString("hello_world")))
        .get
      )
//      implicitly[Format[HelloWorld]].reads(Json.parse(helloWorldRow.getString("hello-world")))
//          .toOption
//          .flatten
//          .getOrElse(Set.empty[HelloWorld])
    )
  }
}

private[impl] class HelloWorldEventProcessor(
    session: CassandraSession,
    readSide: CassandraReadSide)(implicit ec: ExecutionContext)
    extends ReadSideProcessor[HelloWorldEvent] {
  private val logger =
    LoggerFactory.getLogger(classOf[HelloWorldEventProcessor])

  private var insertHelloWorldStatement: PreparedStatement = _
  private var destroyHelloWorldStatement: PreparedStatement = _
  private var insertHelloWorldByNameStatement: PreparedStatement = _
  private var insertHelloWorldSummaryStatement: PreparedStatement = _

  override def buildHandler
    : ReadSideProcessor.ReadSideHandler[HelloWorldEvent] = {
    readSide
      .builder[HelloWorldEvent]("helloWorldEventOffset")
      .setGlobalPrepare(createTables)
      .setPrepare(_ => prepareStatements())
      .setEventHandler[HelloWorldCreatedEvent](e => {
        insertHelloWorld(e.event.helloWorldAggregate)
      })
//      .setEventHandler[HelloWorldDestroyedEvent](e => {
//        destroyHelloWorld(e.event.helloWorldId)
//      })
      .build
  }

  override def aggregateTags: Set[AggregateEventTag[HelloWorldEvent]] =
    HelloWorldEvent.Tag.allTags

  private def createTables() = {
    logger.info("Creating tables...")
    for {
      _ <- session.executeCreateTable("""
          |CREATE TABLE IF NOT EXISTS hello_world (
          | id text PRIMARY KEY,
          | hello_world text
          |);
        """.stripMargin)
      _ <- session.executeCreateTable(
        """
          |CREATE TABLE IF NOT EXISTS hello_world_summary (
          | id text PRIMARY KEY,
          | name text
          |);
        """.stripMargin)
      _ <- session.executeCreateTable(
        """
          |CREATE TABLE IF NOT EXISTS hello_world_by_name (
          | id text,
          | name text PRIMARY KEY
          |);
        """.stripMargin)
    } yield Done
  }

  private def prepareStatements() = {
    logger.info("Preparing statements...")
    for {
      insertHelloWorld <- session.prepare("""
          |INSERT INTO hello_world(
          | id,
          | hello_world
          | ) VALUES (
          | ?, ?);
        """.stripMargin)
      destroyHelloWorld <- session.prepare("""
          |DELETE FROM hello_world
          |WHERE id = ?;
        """.stripMargin)
      insertHelloWorldSummary <- session.prepare(
        """
          |INSERT INTO hello_world_summary(
          | id,
          | name
          | ) VALUES (
          | ?, ?);
        """.stripMargin)
      insertHelloWorldByName <- session.prepare(
        """
          |INSERT INTO hello_world_by_name(
          | id,
          | name
          | ) VALUES (
          | ?, ?);
        """.stripMargin)
    } yield {
      insertHelloWorldStatement = insertHelloWorld
      destroyHelloWorldStatement = destroyHelloWorld
      insertHelloWorldSummaryStatement = insertHelloWorldSummary
      insertHelloWorldByNameStatement = insertHelloWorldByName
      Done
    }
  }

  private def insertHelloWorld(helloWorldAggregate: HelloWorldAggregate) = {
    logger.info(s"Inserting $helloWorldAggregate...")
    Future.successful(
      List(
        insertHelloWorldStatement.bind(helloWorldAggregate.helloWorldId,
                                       implicitly[Format[HelloWorld]]
                                         .writes(helloWorldAggregate.helloWorldResource.helloWorld)
                                         .toString)
        //insertHelloWorldSummaryStatement
        //  .bind(helloWorldAggregate.id, helloWorldAggregate.helloWorld.name),
        //insertHelloWorldByNameStatement
        //  .bind(helloWorldAggregate.id, helloWorldAggregate.helloWorld.name)
      ))
  }

  private def destroyHelloWorld(helloWorldId: String) = {
    logger.info(s"Deleting $helloWorldId...")
    Future.successful(
      List(
        destroyHelloWorldStatement.bind(helloWorldId)
        //insertHelloWorldSummaryStatement
        //  .bind(helloWorldAggregate.id, helloWorldAggregate.helloWorld.name),
        //insertHelloWorldByNameStatement
        //  .bind(helloWorldAggregate.id, helloWorldAggregate.helloWorld.name)
      ))
  }

}

// Hello World Serializer Registry

object HelloWorldSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    // Data structures
    JsonSerializer[HelloWorld],
    JsonSerializer[HelloWorldResource],
    JsonSerializer[HelloWorldAggregate],
    // Create
    JsonSerializer[CreateHelloWorldRequest],
    JsonSerializer[CreateHelloWorldCommand],
    JsonSerializer[CreateHelloWorldReply],
    JsonSerializer[HelloWorldCreatedEvent],
    JsonSerializer[CreateHelloWorldResponse],
    // Replace
    JsonSerializer[ReplaceHelloWorldRequest],
    JsonSerializer[ReplaceHelloWorldCommand],
    JsonSerializer[ReplaceHelloWorldReply],
    JsonSerializer[HelloWorldReplacedEvent],
    JsonSerializer[ReplaceHelloWorldResponse],
    // Mutate
    // Deactivate
    // Reactivate
    //

//    JsonSerializer[HelloWorldStatus],
    // Get One
    JsonSerializer[HelloWorldCreated],
    JsonSerializer[GetHelloWorldQuery.type],
    JsonSerializer[GetHelloWorldResponse],
    // Get All
    JsonSerializer[GetAllHelloWorldsResponse]
  )
}

/**
  * ServiceErrors object acts as a enumeration of pre-defined errors that can be used as response for the public REST api.
  *
  * Internally these errors can be created by a read action to the Read-side or a message sent to the persistent entities.
  * It defines all errors related to [[Cart]], [[Bundle]] and [[Item]].
  */
object ServiceErrors {
  type ServiceError = ErrorResponse

  final val CartNotFound: ServiceError = ErrorResponse(404, "Not found", "Cart not found.")
  final val HelloWorldConflict: ServiceError = ErrorResponse(409, "Conflict", "Hello World already exists for this user.")
  final val HelloWorldDoesNotExist: ServiceError = ErrorResponse(404, "Not found", "Hello World does not exist.")
  final val CartCannotBeUpdated: ServiceError = ErrorResponse(400, "Bad request", "Cart cannot be updated.")

  final val BundleNotFound: ServiceError = ErrorResponse(404, "Not Found", "Bundle not found.")
  final val BundleConflict: ServiceError = ErrorResponse(409, "Conflict", "Bundle already exists with this name.")

  final val ItemsNotFoundInInventory: ServiceError = ErrorResponse(404, "Not Found", "One or more items were not found in the inventory.")
  final val ItemCannotBeRemoved: ServiceError = ErrorResponse(400, "Bad request", "Item is being used by a bundle, remove bundle first.")
  final val ItemNotFound: ServiceError = ErrorResponse(404, "Not Found", "Item not found.")
  final val ItemConflict: ServiceError = ErrorResponse(409, "Conflict", "Item already exists with this name.")
  final val ItemNegativeQuantity: ServiceError = ErrorResponse(400, "Bad request", "Item quantity cannot be negative.")
}
```

<!--- transclude cjq712iag000k96yaifqppiop -->

Replacement
-----------
A Replacement request takes the new desired HelloWorld algebraic data type and responds with the replaced HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not replaced the service responses with an ErrorResponse. The following REST calls can be used. The Hello World identifier is required, but the replacementId is optional. If specified all identifiers must adhere to the Matcher for Id.

<!--- transclude::api/HelloWorldService.scala::[Hello World Replacement Calls {] cjq712iho000l96yavqvs9a2l -->

```scala
// Hello World Replacement Calls {
  /**
    * Rest api allowing an authenticated user to replace a "Hello World".
    *
    * @param  helloWorldId   The unique identifier of the "Hello World"
    *         replacementId  Optional unique identifier of the replacement subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was replaced successfully
    *         HTTP 202 Accepted              if the request has been accepted, but the processing is not complete
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
  // Retrieve status of replacement request
  def getReplacementHelloWorld(helloWorldId: String, replacementId: String):    ServiceCall[NotUsed, Either[ErrorResponse, ReplacementHelloWorldResponse]]
  def streamReplacementHelloWorld(helloWorldId: String, replacementId: String): ServiceCall[NotUsed, Source[ReplacementHelloWorldResponse, NotUsed]]
// }
```

<!--- transclude cjq712iho000l96yavqvs9a2l -->

The replace Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldRequest(] cjq712ipm000m96yajb9sdnlq -->

```scala
case class ReplaceHelloWorldRequest(
    replacementHelloWorld: HelloWorld,
    motivation: Option[String]
)
```

<!--- transclude cjq712ipm000m96yajb9sdnlq -->

And the replace Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldResponse(] cjq712ixg000n96yaef5av2d9 -->

```scala
case class ReplaceHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjq712ixg000n96yaef5av2d9 -->

Mutation
--------

<!--- transclude::api/HelloWorldService.scala::[Hello World Mutation Calls {] cjq712j5h000o96ya9m4he4jb -->

```scala
// Hello World Mutation Calls {
  /**
    * Rest api allowing an authenticated user to mutate a "Hello World".
    *
    * @param  helloWorldId  The unique identifier of the "Hello World"
    *         mutationId    Optional unique identifier of the mutation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was mutated successfully
    *         HTTP 202 Accepted              if the request has been accepted, but the processing is not complete
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
  // Retrieve status of mutation request
  def getMutationHelloWorld(helloWorldId: String, mutationId: String):    ServiceCall[NotUsed, Either[ErrorResponse, MutationHelloWorldResponse]]
  def streamMutationHelloWorld(helloWorldId: String, mutationId: String): ServiceCall[NotUsed, Source[MutationHelloWorldResponse, NotUsed]]
// }
```

<!--- transclude cjq712j5h000o96ya9m4he4jb -->


Deactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Deactivation Calls {] cjq712jdv000p96yajft1r6ou -->

```scala
// Hello World Deactivation Calls {
  /**
    * Rest api allowing an authenticated user to deactivate a "Hello World".
    *
    * @param  helloWorldId    The unique identifier of the "Hello World"
    *         deactivationId  Optional unique identifier of the deactivation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was deactivated successfully
    *         HTTP 202 Accepted              if the request has been accepted, but the processing is not complete
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
  // Retrieve status of deactivation request
  def getDeactivationHelloWorld(helloWorldId: String, deactivationId: String):    ServiceCall[NotUsed, Either[ErrorResponse, DeactivationHelloWorldResponse]]
  def streamDeactivationHelloWorld(helloWorldId: String, deactivationId: String): ServiceCall[NotUsed, Source[DeactivationHelloWorldResponse, NotUsed]]
// }
```

<!--- transclude cjq712jdv000p96yajft1r6ou -->

Reactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Reactivation Calls {] cjq712jmn000q96yaa94hsyhq -->

```scala
// Hello World Reactivation Calls {
  /**
    * Rest api allowing an authenticated user to reactivate a "Hello World".
    *
    * @param  helloWorldId    The unique identifier of the "Hello World"
    *         reactivationId  Optional unique identifier of the reactivation subordinate resource
    *
    * @return HTTP 200 OK                    if the "Hello World" was reactivated successfully
    *         HTTP 202 Accepted              if the request has been accepted, but the processing is not complete
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
  // Retrieve status of reactivation request
  def getReactivationHelloWorld(helloWorldId: String, reactivationId: String):    ServiceCall[NotUsed, Either[ErrorResponse, ReactivationHelloWorldResponse]]
  def streamReactivationHelloWorld(helloWorldId: String, reactivationId: String): ServiceCall[NotUsed, Source[ReactivationHelloWorldResponse, NotUsed]]
// }
```

<!--- transclude cjq712jmn000q96yaa94hsyhq -->

Read
----

<!--- transclude::api/HelloWorldService.scala::[Hello World Get Calls {] cjq712jv8000r96yav03e67u2 -->

```scala
// Hello World Get Calls {
  /**
    * Rest api allowing an authenticated user to get a "Hello World" with the given surrogate key.
    *
    * @param helloWorldId    The unique identifier of the "Hello World"
    *
    * @return HTTP 200 OK                    if the "Hello World" was retrieved successfully
    *
    * Example:
    * CT="Content-Type: application/json"
    * curl -H $CT http://localhost:9000/api/hello-worlds/cjq5au9sr000caqyayo9uktss
    */
  def getHelloWorld(helloWorldId: String): ServiceCall[NotUsed, Either[ErrorResponse, GetHelloWorldResponse]]

  /**
    * Get all "Hello Worlds".
    *
    * @return A list of "Hello World" resources.
    *
    * Example:
    * curl http://localhost:9000/api/hello-worlds
    */
  def getAllHelloWorlds(page: Option[String]): ServiceCall[NotUsed, utils.PagingState[GetAllHelloWorldsResponse]]
  def getAllHelloWorlds:                       ServiceCall[NotUsed, GetAllHelloWorldsResponse]
// }
```

<!--- transclude cjq712jv8000r96yav03e67u2 -->

```bash
-- With Bearer Auth Token
export AT=`./get-auth-token.sh`
sed 's/\r//'  hello-worlds.csv | perl -MText::CSV -MJSON::MaybeXS=encode_json -lne '$c=Text::CSV->new;$c->parse($_);@C=$c->fields if $.==1;@F=$c->fields;@L{@C}=@F;$J{helloWorld}=\%L;$l=encode_json \%J;`curl --show-error --header \"Authorization: Bearer ${'AT'}\" -H \"Content-Type: application/json\" -X POST -d \047$l\047 http://localhost:9000/api/hello-worlds/$F[0]/create-hello-world`unless $.==1;'

```

```
For REST calls with DDD/CQRS/ES only use GET and POST
GET for queries
  pagination and expand for large resources
 POST for commands
   "Use POST APIs to create new subordinate resources" https://restfulapi.net/http-methods/
   A DDD command can be thought of as a subordinate resource to the DDD aggregate entity
   The command "could" have an identity and be queryable, for instance an async req/resp.
   A Saga needs to be implemented in this manner
   Command body should include a unique identifier, can be a span id
```

# Distributed tracing

https://zipkin.io/pages/instrumenting.html

Annotation

An Annotation is used to record an occurrence in time. There’s a set of core annotations used to define the beginning and end of an RPC request:

cs - Client Send. The client has made the request. This sets the beginning of the span.
sr - Server Receive: The server has received the request and will start processing it. The difference between this and cs will be combination of network latency and clock jitter.
ss - Server Send: The server has completed processing and has sent the request back to the client. The difference between this and sr will be the amount of time it took the server to process the request.
cr - Client Receive: The client has received the response from the server. This sets the end of the span. The RPC is considered complete when this annotation is recorded.
When using message brokers instead of RPCs, the following annotations help clarify the direction of the flow:

ms - Message Send: The producer sends a message to a broker.
mr - Message Receive: A consumer received a message from a broker.
Unlike RPC, messaging spans never share a span ID. For example, each consumer of a message is a different child span of the producing span.

Trace Id

The overall 64 or 128-bit ID of the trace. Every span in a trace shares this ID.

Span Id

The ID for a particular span. This may or may not be the same as the trace id.

Parent Id

This is an optional ID that will only be present on child spans. That is the span without a parent id is considered the root of the trace.

HTTP Tracing

HTTP headers are used to pass along trace information.

The B3 portion of the header is so named for the original name of Zipkin: BigBrotherBird.

Ids are encoded as hex strings:

X-B3-TraceId: 128 or 64 lower-hex encoded bits (required)
X-B3-SpanId: 64 lower-hex encoded bits (required)
X-B3-ParentSpanId: 64 lower-hex encoded bits (absent on root span)
X-B3-Sampled: Boolean (either “1” or “0”, can be absent)
X-B3-Flags: “1” means debug (can be absent)
For more information on B3, please see its specification.

Timestamps are microseconds

https://github.com/openzipkin/b3-propagation

Single Header
A single header named b3 standardized in late 2018 for use in JMS and w3c tracestate. Design and rationale are captured here. Check or update our support page for adoption status.

In simplest terms b3 maps propagation fields into a hyphen delimited string.

b3={TraceId}-{SpanId}-{SamplingState}-{ParentSpanId}, where the last two fields are optional.

For example, the following state encoded in multiple headers:

X-B3-TraceId: 80f198ee56343ba864fe8b2a57d3eff7
X-B3-ParentSpanId: 05e3ac9a4f6e3b90
X-B3-SpanId: e457b5a2e4d86bd1
X-B3-Sampled: 1
Becomes one b3 header, for example:

b3: 80f198ee56343ba864fe8b2a57d3eff7-e457b5a2e4d86bd1-1-05e3ac9a4f6e3b90
https://github.com/bizreach/play-zipkin-tracing

https://github.com/deltaprojects/lagom-opentracing

libraryDependencies += "com.deltaprojects" % "lagom-opentracing_2.12" % "0.2.3"


# JWT

* User gets authenticated and gets a ticket of some type (JWT, Kerberos, ...)
* User wants to use your Self-Contained System (SCS) and exchanges any token that verifies their identity with a JWT for your SCS that adds the roles they have withing your SCS.
* The JWT has a

# Versioning

Semantic versioning: https://semver.org/
1. MAJOR version when you make incompatible API changes,
2. MINOR version when you add functionality in a backwards-compatible manner, and
3. PATCH version when you make backwards-compatible bug fixes.

RESTful versioning:
1. MAJOR: The resource has changed so much it is considered a different resource, thus it has a different URI. Version 1 of http://myservice/helloworld/123 becomes  http://example.com/helloworld/v2/123 in version 2.
2. MINOR: Http Accept header encodes minor version number. Requesting major version 1 and minor version 0 of a resource should have an "Accept" header of "application/vnd.helloworld.hal+json;version=1.0" and use URI http://myservice/helloworld/123. Requesting major version 3 and minor version 2 of a resource should have an "Accept" header of "application/vnd.helloworld.hal+json;version=3.2" and use URI http://myservice/helloworld/v3/123. A mismatch between the major version in the header and in the URI will return an error.
3. PATCH: Requests will not specify patch version, but the full semantic version information is avaliable for every request.

Deprecation: A request to a prior version that is still supported will include information on what the latest version is and when the requested version will be decomissioned.

# HATEOAS

https://www.oreilly.com/ideas/how-a-restful-api-represents-resources
https://phlyrestfully.readthedocs.io/en/latest/halprimer.html
https://blog.apisyouwonthate.com/getting-started-with-json-hyper-schema-184775b91f
https://blog.apisyouwonthate.com/getting-started-with-json-hyper-schema-part-2-ca9d7ffdf6f6

Hypermedia Application Language (HAL)

{"_links":{"self":{"href":"/"},"curies": [...]

_embedded:
_links:
  self:
    href:
  first:
  prev:
  next:
  last:
  # DDD commands allowed
  archive:
    href: ""
    targetMediaType: "vnd.helloworld.hal+json;version=3.2"
    targetSchema: { "$ref": "#"} # point to the root document or {"$ref": "https://schemas.dashron.com/users"}
    methods: [POST]

targetSchema - request body is identical to the target representation
submissionSchema - request body does not match the target representation
headerSchema -

    targetSchema:
      id:
        type: String
      name:
        type: String

  # Curies for templating hrefs
  curies: [{
    name:
    href:
    templated: true
  }],

{
  "_links": {
    "self": { "href": "/orders" },
    "curies": [{
      "name": "acme",
      "href": "http://docs.acme.com/relations/{rel}",
      "templated": true
    }],
    "acme:widgets": { "href": "/widgets" }
  }
}
https://stackoverflow.com/questions/28154998/can-anyone-provide-a-good-explanation-of-curies-and-how-to-use-them

Need links to stable self that includes transactionClock in identity as well as current.
http://myservice/helloworld/123/c12341895a3
https://json-schema.org/latest/json-schema-hypermedia.html
http://json-schema.org/

JSON Hyper-Schema document
{
    "type": "object",
    "properties": {
        "id": {
            "type": "number"
        },
        "title": {
            "type": "string"
        },
        "urlSlug": {
            "type": "string"
        },
        "post": {
            "type": "string"
        }
    },
    "required": ["id"],
    "base": "http://api.dashron.com/",
    "links": [{
        "rel": "self",
        "href": "posts/{id}",
        "templateRequired": ["id"]
    }]
}

Example application that uses schema on the client side: A React component for building Web forms from JSON Schema.
https://github.com/mozilla-services/react-jsonschema-form

https://speakerdeck.com/olivergierke/ddd-and-rest-domain-driven-apis-for-the-web?slide=42

ETags

Aggregate Root -> Collection/Item Resource
Relations -> Links
IDs -> URIs
@Version -> ETags
Last Modified Property -> Last Modified Header

According to the HTTP/1.1 Spec:

The POST method is used to request that the origin server accept the entity enclosed in the request as a new subordinate of the resource identified by the Request-URI in the Request-Line

In other words, POST is used to create.

The PUT method requests that the enclosed entity be stored under the supplied Request-URI. If the Request-URI refers to an already existing resource, the enclosed entity SHOULD be considered as a modified version of the one residing on the origin server. If the Request-URI does not point to an existing resource, and that URI is capable of being defined as a new resource by the requesting user agent, the origin server can create the resource with that URI."

That is, PUT is used to create or update.

So, which one should be used to create a resource? Or one needs to support both?

http://dontpanic.42.nl/2012/04/rest-and-ddd-incompatible.html

Representational State Transfer is a software architectural style that defines a set of constraints to be used for creating web services. Web services that conform to the REST architectural style, termed RESTful web services, provide interoperability between computer systems on the Internet. Wikipedia

How to name a REST resource:
https://www.thoughtworks.com/insights/blog/rest-api-design-resource-modeling

“The key abstraction of information in REST is a resource. Any information that can be named can be a resource: a document or image, a temporal service (e.g. "today's weather in Los Angeles"), a collection of other resources, a non-virtual object (e.g. a person), and so on. In other words, any concept that might be the target of an author's hypertext reference must fit within the definition of a resource. A resource is a conceptual mapping to a set of entities, not the entity that corresponds to the mapping at any particular point in time.” - Roy Fielding’s dissertation.

"The POST method is used to request that the origin server accept the entity enclosed in the request as a new subordinate of the resource identified by the Request-URI in the Request-Line." HTTP/1.1 Spec
