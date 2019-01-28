package io.dja.smough.database

import io.dja.smough.WithLogger
import io.dja.smough.domain._
import scala.concurrent.ExecutionContext
import scalikejdbc._

class PostStore(session: DBSession, executionContext: ExecutionContext)
  extends WithLogger {

  implicit private val s = session
  implicit private val ec = executionContext

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
      rs.offsetDateTime("created_on"),
      rs.offsetDateTime("updated_on"))
  }
}
