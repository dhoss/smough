package io.dja.smough.domain

import java.time.OffsetDateTime

import io.circe.Json
import org.scalatest.FunSuite
import io.circe.syntax._

class DomainTest extends FunSuite {

  // TODO: move this somewhere common
  val post = Post(
    None,
    "test post",
    Some("test-post"),
    "this is a test",
    1,
    Some(OffsetDateTime.now),
    Some(OffsetDateTime.now),
    Some(1))

  val expectedPostJson = Json.obj(
    "id" -> post.id.asJson,
    "parent" -> post.parent.asJson,
    "title" -> post.title.asJson,
    "slug" -> post.slug.asJson,
    "body" -> post.body.asJson,
    "author" -> post.author.asJson,
    "createdOn" -> post.createdOn.asJson,
    "updatedOn" -> post.updatedOn.asJson
  )

  test("Encode Post -> JSON") {
    assert(post.asJson == expectedPostJson)
  }

  test("Decode JSON -> Post") {
    // TODO: find out a better way than just Right(post)
    // can you call .get on Right or something?
    assert(expectedPostJson.as[Post] == Right(post))
  }
}
