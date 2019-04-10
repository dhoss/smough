package io.dja.smough.post

import io.dja.smough.Logger
import io.dja.smough.domain._

import scala.concurrent.ExecutionContext
import collection.JavaConverters._
import java.sql.{Connection, DriverManager}

import org.jooq._
import org.jooq.impl._
import org.jooq.impl.DSL._
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import io.dja.smough.database.Tables._
import io.dja.smough.database.tables.records.PostRecord
import scalikejdbc.DB

// TODO: refactor this into a generic trait and make this an implementation of said trait
class PostStore(c: Connection) extends Logger {


  private val query = DSL.using(c, SQLDialect.POSTGRES_10)

  def insert(post: Post) = {
    log.info(s"Inserting ${post} into database")

    populatePostRecordFields(post).store()
  }

  // TODO: handle issues when record to update doesn't exist
  def update(post: Post) =  {
    log.info(s"Updating ${post} in database")

    populatePostRecordFields(
      post, query.fetchOne(POST, POST.ID.eq(post.id.get))).store()
  }

  private def populatePostRecordFields(
      p: Post,
      r: PostRecord = query.newRecord(POST)): PostRecord = {
    r.setParent(p.parent.orNull)
    r.setTitle(p.title)
    r.setSlug(p.slug.orNull)
    r.setBody(p.body)
    r.setAuthor(p.author)
    r.setCategory(p.category)
    r.setPublishedOn(p.publishedOn.orNull)
    r.setCreatedOn(p.createdOn.orNull)
    r.setUpdatedOn(p.updatedOn.orNull)
    r
  }

  def delete(id: Int) = {
    log.info(s"Deleting Post(${id}) from database")

    query.fetchOne(POST, POST.ID.eq(id)).delete()
  }

  // TODO: maybe curry findBy*?
  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Loading ${slug} from database")

  }

  // TODO: this SQL needs to be moved to a common method or something
  def findById(id: Int): Option[Post] = DB.readOnly { implicit s =>
    log.info(s"Loading ${id} from database")
    sql"""select id, parent, title, slug, body, author, category, created_on, updated_on from post where id=${id}"""
      .map(PostSchema.apply).single().apply
  }

  def findByYear(year: Int): List[Post] = DB.readOnly { implicit s =>
    log.info(s"Finding posts for ${year} from database")
    sql"""
          select id, parent, title, slug, body, author, category, created_on, updated_on from post where extract(year from published_on)=${year}
      """.map(PostSchema.apply).list.apply
  }

  // TODO: be sure to add tests for dates out of range
  def findByMonth(year: Int, month: Int): List[Post] = DB.readOnly { implicit s =>
    log.info(s"Finding posts for ${year}/${month} from database")
    sql"""
          select
            id, parent, title, slug, body, author, category, created_on, updated_on
          from
            post
         where extract(year from published_on)=${year}
         and extract(month from published_on)=${month}
      """.map(PostSchema.apply).list.apply
  }

  // TODO: be sure to add tests for dates out of range
  def findByDay(year: Int, month: Int, day: Int): List[Post] = DB.readOnly { implicit s =>
    log.info(s"Finding posts for ${year}/${month} from database")
    sql"""
          select
            id, parent, title, slug, body, author, category, created_on, updated_on
          from
            post
         where extract(year from published_on)=${year}
         and extract(month from published_on)=${month}
         and extract(day from published_on)=${day}
      """.map(PostSchema.apply).list.apply
  }

  // TODO: add pagination
  def retrieveAll(): List[Post] = DB.readOnly { implicit s =>
    log.info("Loading all posts from database")
    // TODO: Move this to a construct select method
    sql"""
         select id, parent, title, slug, body, author, category, created_on, updated_on from post order by created_on desc
       """.map(PostSchema.apply).list.apply
  }
}