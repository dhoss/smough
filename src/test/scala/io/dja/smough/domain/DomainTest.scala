package io.dja.smough.domain

import org.scalatest.{FunSuite, MustMatchers}
import play.api.libs.json.Json
import io.dja.smough.test.PostFixtures._

class DomainTest extends FunSuite
    with MustMatchers {

  test("Encode Post -> JSON") {
    Json.toJson(expectedPost) must equal(expectedPostJson)
  }

  test("Decode JSON -> Post") {
    expectedPostJson.as[Post] must equal(expectedPost)
  }
}
