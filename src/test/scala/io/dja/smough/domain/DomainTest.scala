package io.dja.smough.domain

import org.scalatest.{FunSuite, MustMatchers}
import play.api.libs.json.Json
import io.dja.smough.test.Fixtures._

class DomainTest extends FunSuite
    with MustMatchers {

  test("Encode DTOs -> JSON") {
    Json.toJson(expectedPost) must equal(expectedPostJson)
    Json.toJson(expectedPostCreatedResult) must equal(expectedPostCreatedResultJson)
    Json.toJson(expectedCategory) must equal(expectedCategoryJson)
  }

  test("Decode JSON -> DTO") {
    expectedPostJson.as[Post] must equal(expectedPost)
    expectedPostCreatedResultJson.as[Result] must equal(expectedPostCreatedResult)
    expectedCategoryJson.as[Category] must equal(expectedCategory)
  }
}
