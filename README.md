<!--- to transclude content use the following syntax at the beginning of a line "transclude::file_name.ext::[an optional regular expression]" -->
<!--- Never directly edit transcluded content, always edit the source file -->
Hello World
=============

## <a name="overview"></a>Overview

This is a simple Hello World self-contained system (SCS) to demonstrate the use of the Lagom framework. It has a simple CRUD interface as well as methods for doing a proper Domain Driven Design (DDD) ubiquitious language (UL).

## Table of Contents
1. [Overview](#overview)
2. [Description](#description)
3. [System Design](#systemdesign)
3. [Domain Model](#domainmodel)
   * [External Event Flow](#externaleventflow)
   * [Internal Event Flow](#internaleventflow)
   * [Conceptual Data Model](#conceptualdatamodel)
2. [Microservices](#microservices)
   * [Hello World](#helloworldmicroservice)
   * [Tag](#tagmicroservice)
   * [Tag Hello World Saga](#taghelloworldsagamicroservice)
   * [Authorization](#authorizationmicroservice)
2. [Glossary - Domain](#glossarydomain)
2. [Glossary - Technical](#glossarytechnical)
3. [References](#references)
4. [Notes](#notes)

## <a name="description"></a>Description

This project has been generated using the rdwinter2/lagom-scala.g8 template.

For instructions on running and testing the project, see [Using Lagom with Scala](https://www.lagomframework.com/get-started-scala.html).

To generate a new project execute the following and supply values for (name, plural_name, organization, version, and package):

```bash
sbt new rdwinter2/lagom-scala.g8
```

After running `git init` or cloning from a repository `cd` into the directory and run `./custom-hooks/run-after-clone.sh`.

## <a name="systemdesign"></a>System Design

The design of Hello World is modeled using an Events-First Domain Driven Design (E1DDD). By focusing on events first, we can better understand the essence of the domain. "When you start modeling events, it forces you to think about the behavior of the system, as opposed to thinking about structure inside the system." [Greg Young](https://www.youtube.com/watch?v=LDW0QWie21s)

Hello World is designed as a [Self Contained System](#selfcontainedsystem) (SCS) and uses the [autonomous bubble pattern](#autonomousbubblepattern) to isolate itself from other systems within the System of Systems.

As a SCS Hello World adheres to certain characteristics to promote autonomy. The [first three SCS characteristics](https://scs-architecture.org/) are:
> 1. Each SCS is an [autonomous web application](https://scs-architecture.org/#autonomous). For the SCS's domain, all data, the logic to process that data and all code to render the web interface is contained within the SCS. An SCS can fulfill its primary use cases on its own, without having to rely on other systems being available.
> 2. Each SCS is owned by [one team](https://scs-architecture.org/#one-team). This does not necessarily mean that only one team can change the code, but the owning team has the final say on what goes into the code base, for example by merging pull-requests.
> 3. Communication with other SCSs or 3rd party systems is [asynchronous wherever possible](https://scs-architecture.org/#async). Specifically, other SCSs or external systems should not be accessed synchronously within the SCS's own request/response cycle. This decouples the systems, reduces the effects of failure, and thus supports autonomy. The goal is decoupling concerning time: An SCS should work even if other SCSs are temporarily offline. This can be achieved even if the communication on the technical level is synchronous, e.g. by replicating data or buffering requests.

Another important aspect of SCS is that it follows [Promise Theory](http://markburgess.org/PromiseMethod.pdf). Autonomous agents that make voluntarily promises tend to converge to a desired state. When obligations are imposed the system transitions from a state of certainty to one that is less certain. What this means for our SCS is that its primary capabilities should take in to account methods for making progress even when external dependencies are unavailable.

By using the [autonomous bubble pattern](#autonomousbubblepattern) Hello World is [shielded from the entanglements of the legacy world](https://www.thoughtworks.com/radar/techniques/autonomous-bubble-pattern). This increases autonomy, reduces development friction, and improves architecture modernization efforts.

From [Eric Evans](http://domainlanguage.com/wp-content/uploads/2016/04/GettingStartedWithDDDWhenSurroundedByLegacySystemsV1.pdf)
> The bubble isolates that work so the team can evolve a model that addresses the chosen area, relatively unconstrained by the concepts of the legacy systems.
> The context boundary of the bubble is established with the popular anticorruption layer (ACL). This boundary isolates your new work from the larger system, allowing you to have a very different model in your context than exists just on the other side of the border.

## <a name="domainmodel"></a>Domain Model

### <a name="externaleventflow"></a>External Event Flow



### <a name="internaleventflow"></a>Internal Event Flow

### <a name="conceptualdatamodel"></a>Conceptual Data Model


## <a name="microservices"></a>Microservices

### <a name="helloworldmicroservice"></a>Hello World

### <a name="tagmicroservice"></a>Tag

### <a name="taghelloworldsagamicroservice"></a>Tag Hello World Saga

### <a name="authorizationmicroservice"></a>Authorization

The authorization microservice exchanges an authentication token attesting to the identity of the user, for an authorization token which includes claims for what the user is allowed to do in this SCS.

The SCS can be programmed to accept a variety of authentication tokens or tickets. For now, it just accepts a JSON Web Token (JWT) with a user name claim.

The token given during the exchange is also a JWT and includes role names this user is authorized to have.

The authorization microservice also has commands for an administrative user to add roles and to assign roles to users.

### <a name="authenticationmicroservice"></a>Authentication

The Hello World SCS includes a simple username/password login authentication microservice. It is included only for completeness and actually belongs in an enterprise or federated identity service.


## <a name="glossarydomain"></a>Glossary - Domain

## <a name="glossarytechnical"></a>Glossary - Technical

<a name="autonomousbubblepattern"></a>Autonomous Bubble Pattern
  : "This approach involves creating a fresh context for new application development that is shielded from the entanglements of the legacy world. This is a step beyond just using an anticorruption layer. It gives the new bubble context full control over its backing data, which is then asynchronously kept up-to-date with the legacy systems. It requires some work to protect the boundaries of the bubble and keep both worlds consistent, but the resulting autonomy and reduction in development friction is a first bold step toward a modernized future architecture." [ThoughtWorks](https://www.thoughtworks.com/radar/techniques/autonomous-bubble-pattern)

<a name="domaindrivendesign"></a>Domain Driven Design (DDD)
  : "an approach to software development for complex needs by connecting the implementation to an evolving model. The premise of domain-driven design is the following: (1) placing the project's primary focus on the core domain and domain logic; (2) basing complex designs on a model of the domain; (3) initiating a creative collaboration between technical and domain experts to iteratively refine a conceptual model that addresses particular domain problems." [Wikipedia](https://en.wikipedia.org/wiki/Domain-driven_design)

<a name="eventsfirstdomaindrivendesign"></a>Events-First Domain Driven Design (E1DDD)
  : "The term Events-First Domain-Driven Design was coined by Russ Miles, and is the name for set of design principles that has emerged in our industry over the last few years and has proven to be very useful in building distributed systems at scale. These principles help us to shift the focus from the nouns (the domain objects) to the verbs (the events) in the domain. A shift of focus gives us a greater starting point for understanding the essence of the domain from a data flow and communications perspective, and puts us on the path toward a scalable event-driven design." [Reactive Microsystems](https://www.oreilly.com/library/view/reactive-microsystems/9781491994368/ch04.html)

<a name="promisetheory"></a>Promise Theory
  : "Promise theory is about modelling causation, change and balance between communicating agents (human or machine), It is about finding the necessary and sufficient conditions for cooperation between distributed agents.[Some Notes about Promise Theory](http://markburgess.org/PromiseMethod.pdf)

<a name="selfcontainedsystem"></a>Self-contained System (SCS)
  : "The Self-contained System (SCS) approach is an architecture that focuses on a separation of the functionality into many independent systems, making the complete logical system a collaboration of many smaller software systems." [scs-architecture.org](https://scs-architecture.org/index.html)

Domain Model
  : "A conceptual model of the domain that incorporates both behavior and data." [Wikipedia](https://en.wikipedia.org/wiki/Domain_model)

## <a name="references"></a>References

[Handling aggregate root with deep object hierarchy](https://softwareengineering.stackexchange.com/questions/355954/handling-aggregate-root-with-deep-object-hierarchy)

## <a name="notes"></a>Notes


The REST call identifiers for the Hello World project are defined as:

<!--- transclude::api/HelloWorldService.scala::[override final def descriptor = {] cjqngqk030000t5yatj8hqrbi -->

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

<!--- transclude cjqngqk030000t5yatj8hqrbi -->
NOTE: For naming resources in a domain driven design (DDD) manner, focus on domain events not low-level create, read, update, and delete (CRUD) operations.

From [Roy Fielding's dissertation](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm#sec_5_2_1_1):
> The key abstraction of information in REST is a resource. Any information that can be named can be a resource: a document or image, a temporal service (e.g. "today's weather in Los Angeles"), a collection of other resources, a non-virtual object (e.g. a person), and so on. In other words, any concept that might be the target of an author's hypertext reference must fit within the definition of a resource. A resource is a conceptual mapping to a set of entities, not the entity that corresponds to the mapping at any particular point in time.

From [REST API Design - Resource Modeling](https://www.thoughtworks.com/insights/blog/rest-api-design-resource-modeling):
> The way to escape low-level CRUD is to create business operation or business process resources, or what we can call as "intent" resources that express a business/domain level "state of wanting something" or "state of the process towards the end result". But to do this you need to ensure you identify the true owners of all your state. In a world of four-verb (AtomPub-style) CRUD, it's as if you allow random external parties to mess around with your resource state, through PUT and DELETE, as if the service were just a low-level database. PUT puts too much internal domain knowledge into the client. The client shouldn't be manipulating internal representation; it should be a source of user intent.
> HTTP verb PUT can be used for idempotent resource updates (or resource creations in some cases) by the API consumer. However, use of PUT for complex state transitions can lead to synchronous cruddy CRUD. It also usually throws away a lot of information that was available at the time the update was triggered - what was the real business domain event that triggered this update? With “[REST without PUT](https://www.thoughtworks.com/radar/techniques/rest-without-put)” technique, the idea is that consumers are forced to post new 'nounified' request resources.

From HTTP/1.1 Spec:
> The POST method is used to request that the origin server accept the entity enclosed in the request as a new subordinate of the resource identified by the Request-URI in the Request-Line.

From [DDD & REST - Domain-Driven APIs for the web](https://speakerdeck.com/olivergierke/ddd-and-rest-domain-driven-apis-for-the-web?slide=42)
> Prefer explicit state transitions over poking at your resources using PATCH.
> |-----------|----------|
> | Aggregate Root / Repository | Collection / Item Resources |
> | Relations | Links |
> | IDs | URIs |
> | @Version | ETags |
> | Last Modified Property | Last Modified Header |

The algebraic data type for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[Hello World algebraic data type {] cjqngqk9t0001t5yabax9mp17 -->

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

<!--- transclude cjqngqk9t0001t5yabax9mp17 -->

With regular expression validation matchers:

<!--- transclude::api/HelloWorldService.scala::[object Matchers {] cjqngqki40002t5yaeqtxl654 -->

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

<!--- transclude cjqngqki40002t5yaeqtxl654 -->

And supporting algebraic data types:

<!--- transclude::api/HelloWorldService.scala::[Supporting algebraic data types {] cjqngqkql0003t5yao1cic6o7 -->

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

<!--- transclude cjqngqkql0003t5yao1cic6o7 -->

The REST resource for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class HelloWorldResource(] cjqngql050004t5yatjt6lqlh -->

```scala
case class HelloWorldResource(
  helloWorld: HelloWorld
)
```

<!--- transclude cjqngql050004t5yatjt6lqlh -->

The DDD aggregate for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldAggregate(] cjqngql910005t5yartwvf86y -->

```scala
case class HelloWorldAggregate(
  helloWorldIdentity: Identity,
  helloWorldResource: HelloWorldResource
)
```

<!--- transclude cjqngql910005t5yartwvf86y -->

The state for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override type State = .*] cjqngqli70006t5yasu87gv72 -->

```scala
  override type State = Option[HelloWorldState]
```

<!--- transclude cjqngqli70006t5yasu87gv72 -->

And uses the following HelloWorldState algebraic data type:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldState(] cjqngqlra0007t5yapsd9wx4k -->

```scala
case class HelloWorldState(
  helloWorldAggregate: HelloWorldAggregate,
  status: HelloWorldStatus.Status = HelloWorldStatus.NONEXISTENT
)
```

<!--- transclude cjqngqlra0007t5yapsd9wx4k -->

The initial state for all DDD aggregates is:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override def initialState: .*] cjqngqm1c0008t5yawwn44pc9 -->

```scala
  override def initialState: Option[HelloWorldState] = None
```

<!--- transclude cjqngqm1c0008t5yawwn44pc9 -->

The possible statuses for the Hello World aggregate are defined to be:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[object HelloWorldStatus extends Enumeration {] cjqngqmb50009t5ya2lxof9uc -->

```scala
object HelloWorldStatus extends Enumeration {
  val NONEXISTENT, ACTIVE, ARCHIVED, UNKNOWN = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
//  implicit val pathParamSerializer: PathParamSerializer[Status] =
//    PathParamSerializer.required("helloWorldStatus")(withName)(_.toString)
}
```

<!--- transclude cjqngqmb50009t5ya2lxof9uc -->

The finite state machine (FSM) for the DDD aggregate is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override def behavior: Behavior = {] cjqngqmk7000at5yauo78bbfx -->

```scala
  override def behavior: Behavior = {
    case None => nonexistentHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ACTIVE => activeHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ARCHIVED => archivedHelloWorld
    case Some(state) => unknownHelloWorld
  }
```

<!--- transclude cjqngqmk7000at5yauo78bbfx -->

The persistent entity for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[final class HelloWorldEntity extends PersistentEntity {] cjqngqmst000bt5yajnrkdkpv -->

```scala
final class HelloWorldEntity extends PersistentEntity {

  //private val publishedHelloWorldCreatedEvent = pubSubRegistry.refFor(TopicId[HelloWorldCreatedEvent])

  override type Command = HelloWorldCommand[_]
  override type Event = HelloWorldEvent
  override type State = Option[HelloWorldState]

  type OnCommandHandler[M] = PartialFunction[(Command, CommandContext[M], State), Persist]
  type ReadOnlyHandler[M] = PartialFunction[(Command, ReadOnlyCommandContext[M], State), Unit]

  override def initialState: Option[HelloWorldState] = None

  // Finite State Machine (FSM)
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
```

<!--- transclude cjqngqmst000bt5yajnrkdkpv -->

For CRUDy operations the following subordinate, nounified, resources are created:
*   Creation
*   Replacement
*   Mutation: [JSON Patch](http://jsonpatch.com/)
*   Deactivation
*   Reactivation

Creation
--------
A Creation request takes a desired HelloWorld algebraic data type and responds with the created HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not created the service responses with an ErrorResponse. The following REST calls can be used. Identifiers are optional. If specified all identifiers must adhere to the Matcher for Id. Otherwise, the service will create and use a collision resistant unique identifier.

<!--- transclude::api/HelloWorldService.scala::[Hello World Creation Calls {] cjqngqn2d000ct5yae25sk72q -->

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
    *         HTTP 413 Payload Too Large     if request size exceeds a defined limit
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

<!--- transclude cjqngqn2d000ct5yae25sk72q -->

The Matcher for identifiers is defined to be:

<!--- transclude::api/HelloWorldService.scala::[val Id = .*] cjqngqncc000dt5yaep9v2ulo -->

```scala
  val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
```

<!--- transclude cjqngqncc000dt5yaep9v2ulo -->

The create Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[Create Hello World Request payload {] cjqngqnly000et5yaxngu80jc -->

```scala
// Create Hello World Request payload {
//type CreateHelloWorldRequest = String
//implicit val createHelloWorldRequestValidator
//    : Validator[CreateHelloWorldRequest] { r =>
//    r has size > 0
//    r has size <= maxRequestSize
//    }

case class ValidCreateHelloWorldRequest(
    helloWorld: HelloWorld
) {}

case object ValidCreateHelloWorldRequest {
  implicit val format: Format[ValidCreateHelloWorldRequest] = Jsonx.formatCaseClass

  implicit val validCreateHelloWorldRequestValidator
    : Validator[ValidCreateHelloWorldRequest] =
    validator[ValidCreateHelloWorldRequest] { createHelloWorldRequest =>
      createHelloWorldRequest.helloWorld is valid(HelloWorld.helloWorldValidator)
    }
}
// }
```

<!--- transclude cjqngqnly000et5yaxngu80jc -->

And the create Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class CreateHelloWorldResponse(] cjqngqnur000ft5yao2dz6odg -->

```scala
case class CreateHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjqngqnur000ft5yao2dz6odg -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[Hello World Creation Calls {] cjqngqo3d000gt5yaqixemen3 -->

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

<!--- transclude cjqngqo3d000gt5yaqixemen3 -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World command {] cjqngqoc0000ht5yafcc7eova -->

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

<!--- transclude cjqngqoc0000ht5yafcc7eova -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World reply {] cjqngqokc000it5ya4qx4vj87 -->

```scala
// The create Hello World reply {
case class CreateHelloWorldReply(
  helloWorldAggregate: HelloWorldAggregate)

object CreateHelloWorldReply {
  implicit val format: Format[CreateHelloWorldReply] = Json.format
}
// }
```

<!--- transclude cjqngqokc000it5ya4qx4vj87 -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {] cjqngqotv000jt5ya49d5zr3k -->

```scala

```

<!--- transclude cjqngqotv000jt5ya49d5zr3k -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {] cjqngqp2t000kt5ya37nt9m9a -->

```scala

```

<!--- transclude cjqngqp2t000kt5ya37nt9m9a -->

Replacement
-----------
A Replacement request takes the new desired HelloWorld algebraic data type and responds with the replaced HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not replaced the service responses with an ErrorResponse. The following REST calls can be used. The Hello World identifier is required, but the replacementId is optional. If specified all identifiers must adhere to the Matcher for Id.

<!--- transclude::api/HelloWorldService.scala::[Hello World Replacement Calls {] cjqngqpd0000lt5yatzs7gdpy -->

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
    *         HTTP 413 Payload Too Large     if request size exceeds a defined limit
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

<!--- transclude cjqngqpd0000lt5yatzs7gdpy -->

The replace Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldRequest(] cjqngqplv000mt5ya52yk8ybe -->

```scala
case class ReplaceHelloWorldRequest(
    replacementHelloWorld: HelloWorld,
    motivation: Option[String]
)
```

<!--- transclude cjqngqplv000mt5ya52yk8ybe -->

And the replace Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldResponse(] cjqngqpu9000nt5yanlshsthu -->

```scala
case class ReplaceHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjqngqpu9000nt5yanlshsthu -->

Mutation
--------

<!--- transclude::api/HelloWorldService.scala::[Hello World Mutation Calls {] cjqngqq2x000ot5yaafvu0hs7 -->

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
    *         HTTP 413 Payload Too Large     if request size exceeds a defined limit
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

<!--- transclude cjqngqq2x000ot5yaafvu0hs7 -->

Deactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Deactivation Calls {] cjqngqqbe000pt5ya7ysceilj -->

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
    *         HTTP 413 Payload Too Large     if request size exceeds a defined limit
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

<!--- transclude cjqngqqbe000pt5ya7ysceilj -->

Reactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Reactivation Calls {] cjqngqqmi000qt5yauajslo6t -->

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
    *         HTTP 413 Payload Too Large     if request size exceeds a defined limit
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

<!--- transclude cjqngqqmi000qt5yauajslo6t -->

Read
----

<!--- transclude::api/HelloWorldService.scala::[Hello World Get Calls {] cjqngqqwf000rt5yakxuzc836 -->

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

<!--- transclude cjqngqqwf000rt5yakxuzc836 -->

```bash
-- With Bearer Auth Token
export AT=`./get-auth-token.sh`
sed 's/\r//'  hello-worlds.csv | perl -MText::CSV -MJSON::MaybeXS=encode_json -lne '$c=Text::CSV->new;$c->parse($_);@C=$c->fields if $.==1;@F=$c->fields;@L{@C}=@F;$J{helloWorld}=\%L;$l=encode_json \%J;`curl --show-error --header \"Authorization: Bearer ${'AT'}\" -H \"Content-Type: application/json\" -X POST -d \047$l\047 http://localhost:9000/api/hello-worlds/$F[0]/create-hello-world`unless $.==1;'

```

For REST calls with DDD/CQRS/ES only use GET and POST
GET for queries
  pagination and expand for large resources
 POST for commands
   "Use POST APIs to create new subordinate resources" [HTTP Methods](https://restfulapi.net/http-methods/)
   A DDD command can be thought of as a subordinate resource to the DDD aggregate entity
   The command "could" have an identity and be queryable, for instance an async req/resp.
   A Saga needs to be implemented in this manner
   Command body should include a unique identifier, can be a span id

Distributed tracing
-------------------

[Zipkin](https://zipkin.io/pages/instrumenting.html)

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

[b3-propagation](https://github.com/openzipkin/b3-propagation)

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
[play-zipkin-tracing](https://github.com/bizreach/play-zipkin-tracing)

[OpenTracing for the Lagom Framework](https://github.com/deltaprojects/lagom-opentracing)

libraryDependencies += "com.deltaprojects" % "lagom-opentracing_2.12" % "0.2.3"

JWT
----

*   User gets authenticated and gets a ticket of some type (JWT, Kerberos, ...)
*   User wants to use your Self-Contained System (SCS) and exchanges any token that verifies their identity with a JWT for your SCS that adds the roles they have withing your SCS.
*   The JWT has a

Versioning
-----------

Semantic versioning: [Semantic Versioning 2.0.0](https://semver.org/)
1. MAJOR version when you make incompatible API changes,
2. MINOR version when you add functionality in a backwards-compatible manner, and
3. PATCH version when you make backwards-compatible bug fixes.

RESTful versioning:
1. MAJOR: The resource has changed so much it is considered a different resource, thus it has a different URI. Version 1 of `http://myservice/helloworld/123` becomes  `http://example.com/helloworld/v2/123` in version 2.
2. MINOR: Http Accept header encodes minor version number. Requesting major version 1 and minor version 0 of a resource should have an "Accept" header of "application/vnd.helloworld.hal+json;version=1.0" and use URI http://myservice/helloworld/123. Requesting major version 3 and minor version 2 of a resource should have an "Accept" header of "application/vnd.helloworld.hal+json;version=3.2" and use URI `http://myservice/helloworld/v3/123`. A mismatch between the major version in the header and in the URI will return an error.
3. PATCH: Requests will not specify patch version, but the full semantic version information is avaliable for every request.

Deprecation: A request to a prior version that is still supported will include information on what the latest version is and when the requested version will be decomissioned.

HATEOAS
-------

[How a RESTful API represents resources](https://www.oreilly.com/ideas/how-a-restful-api-represents-resources)
[HAL Primer](https://phlyrestfully.readthedocs.io/en/latest/halprimer.html)
[Getting started with JSON Hyper-Schema](https://blog.apisyouwonthate.com/getting-started-with-json-hyper-schema-184775b91f)
[Getting started with JSON Hyper-Schema: Part 2](https://blog.apisyouwonthate.com/getting-started-with-json-hyper-schema-part-2-ca9d7ffdf6f6)

Hypermedia Application Language (HAL)

```json
{"_links":{"self":{"href":"/"},"curies": [...]
```

```yaml
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
```

targetSchema - request body is identical to the target representation
submissionSchema - request body does not match the target representation
headerSchema -

```yaml
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
```

```json
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
```

Need links to stable self that includes transactionClock in identity as well as current.
`http://myservice/helloworld/123/c12341895a3`

[JSON Hyper-Schema: A Vocabulary for Hypermedia Annotation of JSON](https://json-schema.org/latest/json-schema-hypermedia.html)
[JSON Schema](http://json-schema.org/)

JSON Hyper-Schema document

```json
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
```

Example application that uses schema on the client side: A React component for building Web forms from JSON Schema.
[react-jsonschema-form](https://github.com/mozilla-services/react-jsonschema-form)

[DDD & REST — Domain-Driven APIs for the web](https://speakerdeck.com/olivergierke/ddd-and-rest-domain-driven-apis-for-the-web?slide=42)

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

[REST and DDD: incompatible?](http://dontpanic.42.nl/2012/04/rest-and-ddd-incompatible.html)

Representational State Transfer is a software architectural style that defines a set of constraints to be used for creating web services. Web services that conform to the REST architectural style, termed RESTful web services, provide interoperability between computer systems on the Internet. Wikipedia

### License

MIT

### Author Information

| Author                | E-mail                        |
|-----------------------|-------------------------------|
|  |   |
