package io.dja.smough
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object ApiServer extends WithLogger {
  implicit private val system: ActorSystem = ActorSystem("smough-rest-api")
  implicit private val executor: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

  val postsEndpoint = path("posts") {
    get
    complete(
      HttpResponse(
        entity = HttpEntity(
          ContentType(MediaTypes.`application/json`), "[]")))
  }

  private val bindingFuture =
    Http().bindAndHandle(postsEndpoint, "0.0.0.0", 8080)

  def main(args: Array[String]): Unit = {
    bindingFuture.onComplete {
      case Success(serverBinding) =>
        log.info(s"listening to ${serverBinding.localAddress}")
      case Failure(error) => println(s"error: ${error.getMessage}")
    }
  }


}

import org.slf4j.{LoggerFactory, Logger => SLogger}

trait WithLogger {
  lazy val log: SLogger = LoggerFactory.getLogger(getClass)
}