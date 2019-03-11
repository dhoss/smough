package io.dja.smough.app

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.dja.smough.ApiRoutes
import io.dja.smough.domain.{Post, Result}
import io.dja.smough.service.PostService
import io.dja.smough.test.PostFixtures._
import org.mockito.ArgumentMatchersSugar
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite, MustMatchers}
import play.api.libs.json.JsValue

class ApiRoutesTest extends FunSuite
  with MockitoSugar
  with ArgumentMatchersSugar
  with BeforeAndAfter
  with MustMatchers
  with ScalatestRouteTest {

  val postService = mock[PostService]
  val routes = new ApiRoutes(postService)
  val jsonEntity = HttpEntity(`application/json`,
    s"""
{
  "id": null,
  "parent": null,
  "title": "${expectedPost.title}",
  "slug": "${expectedPost.slug.get}",
  "body": "${expectedPost.body}",
  "author": ${expectedPost.author},
  "createdOn": "${expectedPost.createdOn.get}",
  "updatedOn": "${expectedPost.updatedOn.get}"
}
      """.stripMargin)

  before {
    when(postService.retrieveAllFromCache())
        .thenReturn(expectedPostCache)
    when(postService.findBySlug(any[String]))
        .thenReturn(Option(expectedPost))
    when(postService.insert(any[Post]))
        .thenReturn(Result("Created `test post`"))
    when(postService.update(any[Post]))
        .thenReturn(Result("Updated `test post`"))
  }

  test("GET /posts") {
    Get("/posts") ~> routes.listPostsEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  test("GET /posts/slug") {
    Get("/posts/test-post") ~> routes.findPostEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostJson)
    }
  }

  test("POST /posts") {
    Post("/posts", jsonEntity) ~> routes.createPostEndpoint ~> check {
      status must equal(StatusCodes.Created)
      responseAs[JsValue] must equal (expectedPostCreatedResponseJson)
    }
  }

  test("PUT /posts") {
    Put("/posts", jsonEntity) ~> routes.updatePostEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostUpdatedResponseJson)
    }
  }

}