package io.dja.smough.test

import java.time.OffsetDateTime
import java.util.concurrent.Executors

import io.dja.smough.domain.Post
import scalikejdbc._
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

// TODO: make a trait, implement it
object PostFixtures {

  val expectedPost = Post(
    None,
    "test post",
    Some("test-post"),
    "this is a test post",
    1,
    Some(OffsetDateTime.now.withNano(0)),
    Some(OffsetDateTime.now.withNano(0)),
    Some(1)
  )

  val expectedPostJson: JsValue = Json.obj(
    "id" -> expectedPost.id,
    "parent" -> expectedPost.parent,
    "title" -> expectedPost.title,
    "slug" -> expectedPost.slug,
    "body" -> expectedPost.body,
    "author" -> expectedPost.author,
    "createdOn" -> expectedPost.createdOn,
    "updatedOn" -> expectedPost.updatedOn)

  val expectedPostCreatedResponseJson: JsValue = Json.obj(
    "message" -> s"Created `${expectedPost.title}`"
  )

  val expectedPostsJson: JsValue = Json.obj(
    expectedPost.slug.get -> expectedPostJson)

  val connectionPoolSettings = ConnectionPoolSettings(
    initialSize = 1,
    maxSize = 10)

  // TODO: get from config
  val jdbcUrl = "jdbc:postgresql://localhost:5432/smough_test"
  val dbUser = "smough_test"
  val dbPassword = "smough_test"

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  def expectedPostCache(): mutable.HashMap[String, Post] = {
    val expectedPostCache = new mutable.HashMap[String, Post]
    expectedPostCache += (expectedPost.slug.get -> expectedPost)
  }

  def insertFixtures(): Unit = {
    DB.localTx { implicit session =>
      sql"""
         INSERT INTO post(title, slug, body, author, created_on, updated_on)
         VALUES(
            ${expectedPost.title},
            ${expectedPost.slug},
            ${expectedPost.body},
            ${expectedPost.author},
            ${expectedPost.createdOn},
            ${expectedPost.updatedOn})
       """.update.apply()
    }
  }

  def deleteFixtures(): Unit = {
    DB.localTx { implicit session =>
      sql"""
            DELETE FROM post
       """.update.apply()
    }
  }
}
