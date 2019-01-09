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

// Hello World State

final case class HelloWorldState(
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

final case class HelloWorldAggregate(
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
final case class CreateHelloWorldReply(
  helloWorldAggregate: HelloWorldAggregate)

object CreateHelloWorldReply {
  implicit val format: Format[CreateHelloWorldReply] = Json.format
}
// }

// The create Hello World command {
final case class CreateHelloWorldCommand(
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

final case class ReplaceHelloWorldCommand(ReplaceHelloWorldCommand: String, replaceHelloWorldRequest: ReplaceHelloWorldRequest)
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

final case class HelloWorldCreatedEvent(helloWorldAggregate: HelloWorldAggregate)
    extends HelloWorldEvent

object HelloWorldCreatedEvent {
  implicit val format: Format[HelloWorldCreatedEvent] = Json.format
}

//final case class HelloWorldDestroyedEvent(helloWorldId: String)
//    extends HelloWorldEvent
//
//object HelloWorldDestroyedEvent {
//  implicit val format: Format[HelloWorldDestroyedEvent] = Json.format
//}

final case class HelloWorldReplacedEvent(
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