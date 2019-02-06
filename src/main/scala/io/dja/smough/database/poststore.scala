package io.dja.smough.database

import io.dja.smough.WithLogger
import io.dja.smough.domain._
import scala.concurrent.ExecutionContext
import scalikejdbc._

class PostStore(session: DBSession, executionContext: ExecutionContext)
  extends WithLogger {

  implicit private val s = session
  implicit private val ec = executionContext

  def insertIntoDb(post: Post) = DB.localTx { implicit s =>
    log.info(s"Inserting ${post} into database")
    sql"""
          INSERT INTO post(
            parent,
            title,
            slug,
            body,
            author,
            created_on,
            updated_on)
          VALUES(
            ${post.parent},
            ${post.title},
            ${post.slug},
            ${post.body},
            ${post.author},
            now(),
            now())
      """.update.apply()
  }

  def updateInDb(post: Post) = DB.localTx { implicit s =>
    log.info(s"Updating ${post} in database")
    // TODO: this needs to be built so only the fields changed get updated
    // or maybe consider looking into the sql dsl thing
    // may need to consider an "UpdatePost" DTO and start doing things message-y
    sql"""
          UPDATE post
          SET
            parent=${post.parent},
            title=${post.title},
            body=${post.body},
            updated_on=NOW()
          WHERE id=${post.id}
       """.update.apply()
  }

  // TODO: consider making this generic
  private case class PostField(name: String, value: Any)
  private case class UpdateFields(fields: List[PostField])

  def findBySlugFromDb(slug: String): Option[Post] =  DB.readOnly { implicit s =>
    log.info(s"Loading ${slug} from database")
    sql"""select id, parent, title, slug, body, author, created_on, updated_on from post where slug=${slug}"""
      .map(PostSchema.apply).single().apply
  }

  def retrieveAllFromDb(): List[Post] = DB.readOnly { implicit s =>
    log.info("Loading all posts from database")
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
      rs.intOpt("parent"),
      rs.string("title"),
      rs.string("slug"),
      rs.string("body"),
      rs.int("author"),
      rs.offsetDateTimeOpt("created_on"),
      rs.offsetDateTimeOpt("updated_on"),
    rs.intOpt("id"))
  }
}
