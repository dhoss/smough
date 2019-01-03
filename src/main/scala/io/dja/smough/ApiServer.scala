package io.dja.smough
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import doobie.util.transactor.Transactor

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

class DatabaseStore(val transactor: Transactor[cats.effect.IO]) {
  import doobie._
  import doobie.implicits._
  import scala.concurrent.ExecutionContext
  import cats.effect.IO

  implicit val cs = IO.contextShift(ExecutionContext.global)

  case class Post(id: Int, title: String, body: String, author: Int, createdOn: Int, updatedOn: Int)

  def all(): Option[List[Post]] = ???

}