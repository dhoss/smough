package io.dja.smough.io.dja.smough.database

import java.time.OffsetDateTime
import java.util.concurrent.Executors

import io.dja.smough.database.{PostSchema, PostStore}
import io.dja.smough.domain.Post
import io.dja.smough.test.util.IntegrationTest
import org.mockito.ArgumentMatchersSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import scalikejdbc._

import scala.concurrent.ExecutionContext

class PostStoreIntegrationTest extends FunSuite
  with MockitoSugar
  with ArgumentMatchersSugar
  with BeforeAndAfterEach {

  val connectionPoolSettings = ConnectionPoolSettings(
    initialSize = 1,
    maxSize = 10)

  // TODO: /!\ MOVE THESE /!\
  ConnectionPool.singleton(
    "jdbc:postgresql://localhost:5432/smough_test",
    "smough_test",
    "smough_test",
    connectionPoolSettings)

  lazy val session: DBSession = AutoSession
  lazy val databaseExecutorContext: ExecutionContext =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))

  val postStore = new PostStore(session, databaseExecutorContext)

  val expectedPost = Post(
    None,
    "test post",
    "test-post",
    "this is a test post",
    1,
    Some(OffsetDateTime.now.withNano(0)),
    Some(OffsetDateTime.now.withNano(0)))

  override def beforeEach() {
    // TODO: maybe make these fixtures
    insertFixtures()
  }

  override def afterEach {
    // TODO: maybe make these fixtures
    deleteFixtures()
  }

  test("insert new row", IntegrationTest) {
    deleteFixtures()
    postStore.insertIntoDb(expectedPost)
    val postFromDb = DB.readOnly { implicit session =>
      sql"""SELECT * FROM post WHERE slug=${expectedPost.slug}"""
        .map(PostSchema.apply).single().apply
    }
    assertPostEquals(expectedPost, postFromDb.get)
  }

  test("find by slug", IntegrationTest) {
    assertPostEquals(expectedPost, postStore.findBySlugFromDb("test-post").get)
  }

  test("find all posts from db", IntegrationTest) {
    for {
      e <- List(expectedPost)
      a <- postStore.retrieveAllFromDb()
    } yield assertPostEquals(e, a)
  }

  private def assertPostEquals(expected: Post, actual: Post): Boolean = {
    postToMap(expected) == postToMap(actual)
  }

  private def postToMap(p: Post): Map[String, Any] = {
    Map(
      "title" -> p.title,
      "slug" -> p.slug,
      "body" -> p.body,
      "author" -> p.author,
      "createdOn" -> p.createdOn,
      "updatedOn" -> p.updatedOn)
  }

  private def insertFixtures(): Unit = {
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

  private def deleteFixtures(): Unit = {
    DB.localTx { implicit session =>
      sql"""
            DELETE FROM post
       """.update.apply()
    }
  }
}
