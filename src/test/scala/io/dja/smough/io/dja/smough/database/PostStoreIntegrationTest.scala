package io.dja.smough.io.dja.smough.database

import io.dja.smough.database.{PostSchema, PostStore}
import io.dja.smough.domain.Post
import io.dja.smough.test.util.IntegrationTest
import org.mockito.ArgumentMatchersSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatest._
import scalikejdbc._
import io.dja.smough.test.Fixtures._
import org.flywaydb.core.Flyway


class PostStoreIntegrationTest extends FunSuite
  with MockitoSugar
  with ArgumentMatchersSugar
  // TODO: this is apparently deprecated so look into replacing it with
  //  http://doc.scalatest.org/2.2.6/#org.scalatest.BeforeAndAfterEachTestData
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with MustMatchers {


  ConnectionPool.singleton(
    jdbcUrl,
    dbUser,
    dbPassword,
    connectionPoolSettings)

  val postStore = new PostStore(session, databaseExecutorContext)

  // TODO: move this to a super class
  override def beforeAll(){
    val flyway = Flyway
        .configure
        .locations("filesystem:src/main/resources/sql/")
        .dataSource(jdbcUrl, dbUser, dbPassword)
        .load
    flyway.clean()
    flyway.migrate()
  }

  override def beforeEach() {
    insertFixtures()
  }

  override def afterEach(){
    deleteFixtures()
  }

  test("insert new row", IntegrationTest) {
    // TODO: dumb and i don't like it
    deleteFixtures()
    DB.localTx { implicit session =>
      sql"""
            INSERT INTO category(id, name) VALUES (1, 'test category')
      """.update.apply()
    }

    postStore.insert(expectedPost)
    val postFromDb = findPostFromDb().get
    assertPostEquals(expectedPost, postFromDb)
  }

  test("update a row", IntegrationTest) {
    val postFromDb = findPostFromDb().get
    val updatedPost = Post(
      postFromDb.parent,
      postFromDb.title,
      postFromDb.slug,
      "updated post body",
      postFromDb.author,
      postFromDb.category,
      postFromDb.createdOn,
      postFromDb.updatedOn,
      postFromDb.id)
    postStore.update(updatedPost)
    assertPostEquals(updatedPost, findPostByIdFromDb(postFromDb.id).get)

    the [IllegalArgumentException] thrownBy {
      val invalidUpdatedPost = Post(
        postFromDb.parent,
        postFromDb.title,
        postFromDb.slug,
        "updated post body",
        postFromDb.author,
        postFromDb.category,
        postFromDb.createdOn,
        postFromDb.updatedOn)
      postStore.update(invalidUpdatedPost)
    } must have message ("id column is required")
  }

  test("find by slug", IntegrationTest) {
    assertPostEquals(expectedPost, postStore.findBySlug("test-post").get)
  }

  // TODO: test pagination
  test("find all posts from db", IntegrationTest) {
    for {
      e <- List(expectedPost)
      a <- postStore.retrieveAll()
    } yield assertPostEquals(e, a)
  }

  test("delete from db", IntegrationTest) {
    // TODO: find a better way to deal with options
    postStore.delete(findPostFromDb().flatMap(_.id).get)
    None must equal (findPostFromDb())
  }

  // TODO: genericize and move these up to a util class
  private def findPostFromDb(): Option[Post] = {
    DB.readOnly { implicit session =>
      sql"""SELECT * FROM post WHERE slug=${expectedPost.slug}"""
        .map(PostSchema.apply).single().apply
    }
  }

  private def findPostByIdFromDb(id: Option[Int]): Option[Post] = {
    DB.readOnly { implicit session =>
      sql"""SELECT * FROM post WHERE id=${id}"""
        .map(PostSchema.apply).single().apply
    }
  }

  private def assertPostEquals(expected: Post, actual: Post) = {
    // TODO: figure out how to check dates or do it explicitly somewhere
    actual must have(
      'title (expectedPost.title),
      'slug (expectedPost.slug),
      'author (expectedPost.author))
  }

  private def postToMap(p: Post): Map[String, Any] = {
    Map(
      "id" -> p.id,
      "title" -> p.title,
      "slug" -> p.slug,
      "body" -> p.body,
      "author" -> p.author,
      "createdOn" -> p.createdOn,
      "updatedOn" -> p.updatedOn)
  }
}
