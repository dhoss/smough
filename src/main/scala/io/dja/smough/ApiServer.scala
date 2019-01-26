package io.dja.smough

import java.util.concurrent.Executors

import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.dja.smough.database.PostStore
import scalikejdbc._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.dja.smough.service.PostService

import scala.concurrent.ExecutionContext

object ApiServer extends WithLogger {

  implicit private val system: ActorSystem = ActorSystem("smough-rest-api")
  implicit private val executor: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  val connectionPoolSettings = ConnectionPoolSettings(
    initialSize = 1,
    maxSize = 10 //config.getInt("jdbc.maxConnections")
  )

  ConnectionPool.singleton(
    "jdbc:postgresql://localhost:5432/smough",
    "smough",
    "smough",
    connectionPoolSettings)

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  private val postStore = new PostStore(session, databaseExecutorContext)
  private val postService = new PostService(postStore)

  private val listPostsEndpoint = path("posts") {
    get {
      complete(postService.retrieveAllFromCache())
    }
  }

  private val findPostEndpoint = path("posts"/Segment) { slug =>
    get {
      complete(postService.findBySlug(slug))
    }
  }

  private val routes = listPostsEndpoint ~ findPostEndpoint

  private val bindingFuture =
    Http().bindAndHandle(routes, "0.0.0.0", 8080)

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

