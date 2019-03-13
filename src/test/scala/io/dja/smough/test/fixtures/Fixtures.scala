package io.dja.smough.test

import java.time.OffsetDateTime
import java.util.concurrent.Executors

import io.dja.smough.domain.{Category, Post, Result}
import scalikejdbc._
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

// TODO: make a trait, implement it
object Fixtures {

  val expectedPost = Post(
    parent = None,
    title = "test post",
    slug = Some("test-post"),
    body = "this is a test post",
    author  = 1,
    category = 1,
    publishedOn = Some(OffsetDateTime.now.withNano(0)),
    createdOn = Some(OffsetDateTime.now.withNano(0)),
    updatedOn = Some(OffsetDateTime.now.withNano(0)),
    id = Some(1)
  )
  val expectedPostJson: JsValue = Json.obj(
    "id" -> expectedPost.id,
    "parent" -> expectedPost.parent,
    "title" -> expectedPost.title,
    "slug" -> expectedPost.slug,
    "body" -> expectedPost.body,
    "author" -> expectedPost.author,
    "category" -> expectedPost.category,
    "publishedOn" -> expectedPost.publishedOn,
    "createdOn" -> expectedPost.createdOn,
    "updatedOn" -> expectedPost.updatedOn)

  val expectedPostCreatedResult = Result(s"Created `${expectedPost.title}`")
  val expectedPostCreatedResultJson: JsValue = Json.obj(
    "message" -> s"Created `${expectedPost.title}`"
  )

  val expectedPostUpdatedResultJson: JsValue = Json.obj(
    "message" -> s"Updated `${expectedPost.title}`"
  )

  val expectedPostDeletedResultJson: JsValue = Json.obj(
    "message" -> s"Deleted `${expectedPost.title}`"
  )

  val expectedPostsJson: JsValue = Json.obj(
    expectedPost.slug.get -> expectedPostJson)

  val expectedCategory = Category(
    id = Some(1), name = "test category")
  val expectedCategoryJson = Json.obj(
    "id" -> expectedCategory.id.get,
    "name" -> expectedCategory.name)

  // TODO: possibly put into config
  val connectionPoolSettings = ConnectionPoolSettings(
    initialSize = 1,
    maxSize = 10)

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
            INSERT INTO category(id, name) VALUES (1, 'test category')
      """.update.apply()

      sql"""
            INSERT INTO post(title, slug, body, author, category, published_on, created_on, updated_on)
            VALUES(
              ${expectedPost.title},
              ${expectedPost.slug},
              ${expectedPost.body},
              ${expectedPost.author},
              ${expectedPost.category},
              ${expectedPost.publishedOn},
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

      sql"""
            DELETE FROM category
         """.update.apply()
    }
  }
}
