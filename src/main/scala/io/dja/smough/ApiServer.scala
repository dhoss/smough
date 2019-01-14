package io.dja.smough
import java.util.concurrent.Executors

import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scalikejdbc._
import akka.http.scaladsl.marshalling.Marshal

import scala.concurrent.{ExecutionContext, Future}

object ApiServer extends WithLogger {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
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
    //config.getString("jdbc.url"), config.getString("jdbc.username"), config.getString("jdbc.password"), connectionPoolSettings)

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
      //config.getInt("jdbc.maxConnections")))

  private val postStore = new PostStore(session, databaseExecutorContext)

  private val postsEndpoint = path("posts") {
    get {
      complete(postStore.all())
    }
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

import io.dja.smough.domain._
class PostStore(session: DBSession, executionContext: ExecutionContext) {

  implicit private val s = session
  implicit private val ec = executionContext

  private val p = PostSchema.syntax("p")

  def all(): List[Post] = DB.readOnly { implicit s =>
    sql"""
         select * from post order by created_on desc
       """.map(PostSchema(p.resultName)).list.apply
  }

}

object PostSchema extends SQLSyntaxSupport[Post] {
  override val tableName = "post"

  def apply(p: ResultName[Post])(rs: WrappedResultSet): Post = {
    Post(
      rs.int(p.id),
      rs.intOpt(p.parent),
      rs.string(p.title),
      rs.string(p.body),
      rs.int(p.author),
      rs.int(p.createdOn),
      rs.int(p.updatedOn))
  }
}