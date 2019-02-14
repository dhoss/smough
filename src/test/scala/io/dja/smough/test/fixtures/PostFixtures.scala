package io.dja.smough.test

import java.time.OffsetDateTime
import java.util.concurrent.Executors

import io.circe.Json
import io.dja.smough.domain.Post
import scalikejdbc._
import io.circe.syntax._

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
    Some(OffsetDateTime.now.withNano(0)))

  val expectedPostsJson = Json.obj(
    expectedPost.slug.get -> Json.obj(
    "id" -> expectedPost.id.asJson,
    "parent" -> expectedPost.parent.asJson,
    "title" -> expectedPost.title.asJson,
    "slug" -> expectedPost.slug.asJson,
    "body" -> expectedPost.body.asJson,
    "author" -> expectedPost.author.asJson,
    "createdOn" -> expectedPost.createdOn.asJson,
    "updatedOn" -> expectedPost.updatedOn.asJson
  ))

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
