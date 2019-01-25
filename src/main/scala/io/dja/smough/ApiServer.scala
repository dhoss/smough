package io.dja.smough
import java.util.NoSuchElementException
import java.util.concurrent.Executors

import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scalikejdbc._
import akka.http.scaladsl.marshalling.Marshal

import scala.collection.mutable
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

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  private val postStore = new PostStore(session, databaseExecutorContext)

  private val listPostsEndpoint = path("posts") {
    get {
      complete(postStore.retrieveAllFromCache())
    }
  }

  private val findPostEndpoint = path("posts"/Segment) { slug =>
    get {
      complete(postStore.findBySlug(slug))
    }
  }

  private val routes = listPostsEndpoint ~ findPostEndpoint

  private val bindingFuture =
    Http().bindAndHandle(routes, "0.0.0.0", 8080)

  def main(args: Array[String]): Unit = {
    bindingFuture.onComplete {
      case Success(serverBinding) => {
        log.info(s"listening to ${serverBinding.localAddress}")
        postStore.loadPosts()
      }
      case Failure(error) => log.error(s"error: ${error.getMessage}")
    }
  }
}

import io.dja.smough.domain._
class PostStore(session: DBSession, executionContext: ExecutionContext)
  extends WithLogger {

  implicit private val s = session
  implicit private val ec = executionContext

  // TODO: deflate post objects into a hashmap or maybe flatbuffer?
  private val postCache = mutable.HashMap[String, Post]()

  def loadPosts(): Unit = {
    log.info("Determining whether or not to load posts into memory")
    if (postCache.isEmpty) {
      log.info("Post cache is empty, loading posts")
      for (post <- retrieveAllFromDb()) {
        postCache += (post.slug -> post)
      }
      log.info("Loading posts complete.")
    }
  }

  def retrieveAllFromCache(): mutable.HashMap[String, Post] = {
    log.info("Retrieving posts from cache")
    postCache
  }

  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Attempting to find ${slug} in cache")
    Option(postCache.getOrElseUpdate(slug, findBySlugFromDb(slug).get))
  }

  def findBySlugFromDb(slug: String): Option[Post] =  DB.readOnly { implicit s =>
    log.info(s"Loading ${slug} from db")
    sql"""select id, parent, title, slug, body, author, created_on, updated_on from post where slug=${slug}"""
      .map(PostSchema.apply).single().apply
  }

  def retrieveAllFromDb(): List[Post] = DB.readOnly { implicit s =>
    log.info("Loading all posts from db")
    // TODO: Move this to a construct select method
    sql"""
         select id, parent, title, slug, body, author, created_on, updated_on from post order by created_on desc
       """.map(PostSchema.apply).list.apply
  }

}

object PostSchema extends SQLSyntaxSupport[Post] {
  override val tableName = "post"

  def apply(rs: WrappedResultSet): Post = {
    Post(
      rs.int("id"),
      rs.intOpt("parent"),
      rs.string("title"),
      rs.string("slug"),
      rs.string("body"),
      rs.int("author"),
      rs.dateTime("created_on").toInstant,
      rs.dateTime("updated_on").toInstant)
  }
}