package io.dja.smough.domain

import java.time.OffsetDateTime

import org.scalatest.FunSuite

class DomainTest extends FunSuite {

  test("Constructing Post DTO works") {
    // TODO: move this somewhere common
    val expectedPostWithId = Post(
      None,
      "test post",
      "test-post",
      "this is a test",
      1,
      Some(OffsetDateTime.now),
      Some(OffsetDateTime.now),
      Some(1))
    val actualPostWithId = Post(
      expectedPostWithId.parent,
      expectedPostWithId.title,
      expectedPostWithId.slug,
      expectedPostWithId.body,
      expectedPostWithId.author,
      expectedPostWithId.createdOn,
      expectedPostWithId.updatedOn,
      expectedPostWithId.id
    )

    assert(expectedPostWithId == actualPostWithId)

    // ensure nothing gets thrown if we leave out optional fields
    Post(None, "test post", "test-post", "this is a test", 1)
    Post(None, "test post", "test-post", "this is a test", 1, Some(OffsetDateTime.now))
    Post(None, "test post", "test-post", "this is a test", 1, Some(OffsetDateTime.now), Some(OffsetDateTime.now))
  }

}
