package io.dja.smough.domain

import java.time.OffsetDateTime

import org.scalatest.FunSuite
import play.api.libs.json.Json

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
    "id" -> post.id,
    "parent" -> post.parent,
    "title" -> post.title,
    "slug" -> post.slug,
    "body" -> post.body,
    "author" -> post.author,
    "createdOn" -> post.createdOn,
    "updatedOn" -> post.updatedOn
  )

  test("Encode Post -> JSON") {
    assert(Json.toJson(post) == expectedPostJson)
  }

  test("Decode JSON -> Post") {
    assert(expectedPostJson.as[Post] == post)
  }
}
