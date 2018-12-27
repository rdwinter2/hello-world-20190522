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

<!--- transclude::api/HelloWorldService.scala::[override final def descriptor = {] cjq71d4ma0000ivya8lfmmmqw -->

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

<!--- transclude cjq71d4ma0000ivya8lfmmmqw -->
NOTE: For naming resources in a domain driven design (DDD) manner, focus on domain events not low-level create, read, update, and delete (CRUD) operations.

From [Roy Fielding's dissertation](https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm#sec_5_2_1_1):
> The key abstraction of information in REST is a resource. Any information that can be named can be a resource: a document or image, a temporal service (e.g. "today's weather in Los Angeles"), a collection of other resources, a non-virtual object (e.g. a person), and so on. In other words, any concept that might be the target of an author's hypertext reference must fit within the definition of a resource. A resource is a conceptual mapping to a set of entities, not the entity that corresponds to the mapping at any particular point in time.

From [REST API Design - Resource Modeling](https://www.thoughtworks.com/insights/blog/rest-api-design-resource-modeling):
> The way to escape low-level CRUD is to create business operation or business process resources, or what we can call as "intent" resources that express a business/domain level "state of wanting something" or "state of the process towards the end result". But to do this you need to ensure you identify the true owners of all your state. In a world of four-verb (AtomPub-style) CRUD, it's as if you allow random external parties to mess around with your resource state, through PUT and DELETE, as if the service were just a low-level database. PUT puts too much internal domain knowledge into the client. The client shouldn't be manipulating internal representation; it should be a source of user intent.
> HTTP verb PUT can be used for idempotent resource updates (or resource creations in some cases) by the API consumer. However, use of PUT for complex state transitions can lead to synchronous cruddy CRUD. It also usually throws away a lot of information that was available at the time the update was triggered - what was the real business domain event that triggered this update? With “[REST without PUT](https://www.thoughtworks.com/radar/techniques/rest-without-put)” technique, the idea is that consumers are forced to post new 'nounified' request resources.

The algebraic data type for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[Hello World algebraic data type {] cjq71d4uk0001ivyaqvxddyvw -->

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

<!--- transclude cjq71d4uk0001ivyaqvxddyvw -->

With regular expression validation matchers:

<!--- transclude::api/HelloWorldService.scala::[object Matchers {] cjq71d52f0002ivyasxztswy5 -->

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

<!--- transclude cjq71d52f0002ivyasxztswy5 -->

And supporting algebraic data types:

<!--- transclude::api/HelloWorldService.scala::[Supporting algebraic data types {] cjq71d5b00003ivyac4a8vy2v -->

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

<!--- transclude cjq71d5b00003ivyac4a8vy2v -->

The REST resource for Hello World is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class HelloWorldResource(] cjq71d5jk0004ivya232ylblv -->

```scala
case class HelloWorldResource(
  helloWorld: HelloWorld
)
```

<!--- transclude cjq71d5jk0004ivya232ylblv -->

The DDD aggregate for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldAggregate(] cjq71d5rm0005ivya3p3761ar -->

```scala
case class HelloWorldAggregate(
  helloWorldIdentity: Identity,
  helloWorldResource: HelloWorldResource
)
```

<!--- transclude cjq71d5rm0005ivya3p3761ar -->

The state for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override type State = .*] cjq71d6050006ivyad2grf126 -->

```scala
  override type State = Option[HelloWorldState]
```

<!--- transclude cjq71d6050006ivyad2grf126 -->

And uses the following HelloWorldState algebraic data type:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[case class HelloWorldState(] cjq71d68j0007ivyahumm73c0 -->

```scala
case class HelloWorldState(
  helloWorldAggregate: HelloWorldAggregate,
  status: HelloWorldStatus.Status = HelloWorldStatus.NONEXISTENT
)
```

<!--- transclude cjq71d68j0007ivyahumm73c0 -->

The initial state for all DDD aggregates is:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override def initialState: .*] cjq71d6gi0008ivyaz7id724c -->

```scala
  override def initialState: Option[HelloWorldState] = None
```

<!--- transclude cjq71d6gi0008ivyaz7id724c -->

The possible statuses for the Hello World aggregate are defined to be:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[object HelloWorldStatus extends Enumeration {] cjq71d6oy0009ivyafv554i6v -->

```scala
object HelloWorldStatus extends Enumeration {
  val NONEXISTENT, ACTIVE, ARCHIVED, UNKNOWN = Value
  type Status = Value

  implicit val format: Format[Value] = enumFormat(this)
//  implicit val pathParamSerializer: PathParamSerializer[Status] =
//    PathParamSerializer.required("helloWorldStatus")(withName)(_.toString)
}
```

<!--- transclude cjq71d6oy0009ivyafv554i6v -->

The finite state machine (FSM) for the DDD aggregate is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[override def behavior: Behavior = {] cjq71d6xa000aivya5jvc811q -->

```scala
  override def behavior: Behavior = {
    case None => nonexistentHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ACTIVE => activeHelloWorld
    case Some(state) if state.status == HelloWorldStatus.ARCHIVED => archivedHelloWorld
    case Some(state) => unknownHelloWorld
  }
```

<!--- transclude cjq71d6xa000aivya5jvc811q -->

The persistent entity for Hello World is defined as:

<!--- transclude::impl/HelloWorldServiceImpl.scala::[final class HelloWorldEntity extends PersistentEntity {] cjq71d752000bivyagrolqv9n -->

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

<!--- transclude cjq71d752000bivyagrolqv9n -->

For CRUDy operations the following subordinate, nounified, resources are created:
* Creation
* Replacement
* Mutation: http://jsonpatch.com/
* Deactivation
* Reactivation

Creation
--------
A Creation request takes a desired HelloWorld algebraic data type and responds with the created HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not created the service responses with an ErrorResponse. The following REST calls can be used. Identifiers are optional. If specified all identifiers must adhere to the Matcher for Id. Otherwise, the service will create and use a collision resistant unique identifier.

<!--- transclude::api/HelloWorldService.scala::[Hello World Creation Calls {] cjq71d7da000civyam2fs2940 -->

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

<!--- transclude cjq71d7da000civyam2fs2940 -->

The Matcher for identifiers is defined to be:

<!--- transclude::api/HelloWorldService.scala::[val Id = .*] cjq71d7m3000divya5e57fdvu -->

```scala
  val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
```

<!--- transclude cjq71d7m3000divya5e57fdvu -->

The create Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class CreateHelloWorldRequest(] cjq71d7uc000eivya5y5tfd4a -->

```scala
case class CreateHelloWorldRequest(
    helloWorld: HelloWorld
)
```

<!--- transclude cjq71d7uc000eivya5y5tfd4a -->

And the create Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class CreateHelloWorldResponse(] cjq71d829000fivya4tjb4iyt -->

```scala
case class CreateHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjq71d829000fivya4tjb4iyt -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[Hello World Creation Calls {] cjq71d8ag000givyad69jtgr4 -->

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

<!--- transclude cjq71d8ag000givyad69jtgr4 -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World command {] cjq71d8jk000hivyagk3wei66 -->

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

<!--- transclude cjq71d8jk000hivyagk3wei66 -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[The create Hello World reply {] cjq71d8rg000iivyad1ttzu63 -->

```scala
// The create Hello World reply {
case class CreateHelloWorldReply(
  helloWorldAggregate: HelloWorldAggregate)

object CreateHelloWorldReply {
  implicit val format: Format[CreateHelloWorldReply] = Json.format
}
// }
```

<!--- transclude cjq71d8rg000iivyad1ttzu63 -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {] cjq71d8zb000jivya9cp20tck -->

```scala

```

<!--- transclude cjq71d8zb000jivya9cp20tck -->

<!--- transclude::impl/HelloWorldServiceImpl.scala::[private def createHelloWorldCommand: OnCommandHandler[Either[ServiceError, HelloWorldAggregate]] = {] cjq71d97q000kivya1oq2pmrb -->

```scala

```

<!--- transclude cjq71d97q000kivya1oq2pmrb -->

Replacement
-----------
A Replacement request takes the new desired HelloWorld algebraic data type and responds with the replaced HelloWorldResource plus the supporing algebraic data types of Identity and HypertextApplicationLanguage. If the Hello World resource is not replaced the service responses with an ErrorResponse. The following REST calls can be used. The Hello World identifier is required, but the replacementId is optional. If specified all identifiers must adhere to the Matcher for Id.

<!--- transclude::api/HelloWorldService.scala::[Hello World Replacement Calls {] cjq71d9fl000livyaa4r0ooc7 -->

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

<!--- transclude cjq71d9fl000livyaa4r0ooc7 -->

The replace Hello World request is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldRequest(] cjq71d9nn000mivyadvuzdavd -->

```scala
case class ReplaceHelloWorldRequest(
    replacementHelloWorld: HelloWorld,
    motivation: Option[String]
)
```

<!--- transclude cjq71d9nn000mivyadvuzdavd -->

And the replace Hello World response is defined as:

<!--- transclude::api/HelloWorldService.scala::[case class ReplaceHelloWorldResponse(] cjq71d9vi000nivyaj1ep6zsy -->

```scala
case class ReplaceHelloWorldResponse(
    helloWorldId: Identity,
    helloWorld: HelloWorld,
    helloWorldHal: Option[HypertextApplicationLanguage]
)
```

<!--- transclude cjq71d9vi000nivyaj1ep6zsy -->

Mutation
--------

<!--- transclude::api/HelloWorldService.scala::[Hello World Mutation Calls {] cjq71da3x000oivyacc5h34rn -->

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

<!--- transclude cjq71da3x000oivyacc5h34rn -->


Deactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Deactivation Calls {] cjq71dabq000pivyai5e89vz9 -->

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

<!--- transclude cjq71dabq000pivyai5e89vz9 -->

Reactivation
------------

<!--- transclude::api/HelloWorldService.scala::[Hello World Reactivation Calls {] cjq71dajt000qivya5hf3prbw -->

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

<!--- transclude cjq71dajt000qivya5hf3prbw -->

Read
----

<!--- transclude::api/HelloWorldService.scala::[Hello World Get Calls {] cjq71darz000rivyagwesx7ng -->

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

<!--- transclude cjq71darz000rivyagwesx7ng -->

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
