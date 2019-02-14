package io.dja.smough.app

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.dja.smough.database.PostStore
import io.dja.smough.service.PostService
import io.dja.smough.{ApiRoutes, WithLogger}
import scalikejdbc.{AutoSession, ConnectionPool, ConnectionPoolSettings, DBSession}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object ApiServer extends WithLogger {

  implicit private val system: ActorSystem = ActorSystem("smough-rest-api")
  implicit private val executor: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  val connectionPoolSettings = ConnectionPoolSettings(
    initialSize = 1,
    maxSize = 10)

  // TODO: pull these from config
  var jdbcString = "jdbc:postgresql://localhost:5432/smough"
  var dbUser = "smough"
  var dbPassword = "smough"
  ConnectionPool.singleton(
    jdbcString,
    dbUser,
    dbPassword,
    connectionPoolSettings)

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  private val postStore = new PostStore(session, databaseExecutorContext)
  private val postService = new PostService(postStore)
  private val routes = new ApiRoutes(postService)

  private val bindingFuture =
    Http().bindAndHandle(routes(), "0.0.0.0", 8080) // TODO: make these config
  def main(args: Array[String]): Unit = {
    bindingFuture.onComplete {
      case Success(serverBinding) => {
        log.info(s"listening to ${serverBinding.localAddress}")
        postService.loadPosts()
      }
      case Failure(error) => log.error(s"error: ${error.getMessage}")
    }
  }
}
