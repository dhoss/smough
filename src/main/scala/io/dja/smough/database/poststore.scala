package io.dja.smough.database

import io.dja.smough.WithLogger
import io.dja.smough.domain._
import scala.concurrent.ExecutionContext
import scalikejdbc._

class PostStore(session: DBSession, executionContext: ExecutionContext)
  extends WithLogger {

  implicit private val s = session
  implicit private val ec = executionContext

  def insert(post: Post) = DB.localTx { implicit s =>
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

  def update(post: Post) = DB.localTx { implicit s =>
    log.info(s"Updating ${post} in database")
    if (post.id.isEmpty) {
      throw new IllegalArgumentException("id column is required")
    }
    // TODO: this needs to be built so only the fields changed get updated
    sql"""
          UPDATE post
          SET
            parent = ${post.parent},
            title = ${post.title},
            slug = ${post.slug},
            body = ${post.body},
            updated_on = NOW()
          WHERE id = ${post.id}
       """.update.apply()
  }

  def delete(id: Int) = DB.localTx { implicit s =>
    log.info(s"Deleting Post(${id}) from database")
    sql"""
          DELETE FROM post WHERE id=${id}
       """.update.apply()
  }

  def findBySlug(slug: String): Option[Post] =  DB.readOnly { implicit s =>
    log.info(s"Loading ${slug} from database")
    sql"""select id, parent, title, slug, body, author, created_on, updated_on from post where slug=${slug}"""
      .map(PostSchema.apply).single().apply
  }

  // TODO: add pagination
  def retrieveAll(): List[Post] = DB.readOnly { implicit s =>
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
      rs.stringOpt("slug"),
      rs.string("body"),
      rs.int("author"),
      // Wrapping in option is required because we can't set withNano otherwise
      Option(rs.offsetDateTime("created_on").withNano(0)),
      Option(rs.offsetDateTime("updated_on").withNano(0)),
      rs.intOpt("id"))
  }
}
