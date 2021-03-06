package io.dja.smough.app

import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.dja.smough.domain.{Post, Result}
import io.dja.smough.post.PostCache
import io.dja.smough.test.Fixtures._
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

  val postService = mock[PostCache]
  val routes = new ApiRoutes(postService)
  val createPostEntity = HttpEntity(`application/json`,
    s"""
{
  "title": "${expectedPost.title}",
  "slug": "${expectedPost.slug.get}",
  "body": "${expectedPost.body}",
  "author": ${expectedPost.author},
  "category": ${expectedPost.category}
}
      """.stripMargin)

  val updatePostEntity = HttpEntity(`application/json`,
    s"""
{
  "id": ${expectedPost.id.get},
  "title": "${expectedPost.title}",
  "slug": "${expectedPost.slug.get}",
  "body": "${expectedPost.body}",
  "author": ${expectedPost.author},
  "category": ${expectedPost.category}
}
      """.stripMargin)

  before {
    when(postService.retrieveAll())
        .thenReturn(List(expectedPost))
    when(postService.findBySlug(any[String]))
        .thenReturn(Option(expectedPost))
    when(postService.insert(any[Post]))
        .thenReturn(Result("Created `test post`"))
    when(postService.update(any[Post]))
        .thenReturn(Result("Updated `test post`"))
    when(postService.delete(any[Int]))
        .thenReturn(Result("Deleted `test post`"))
    when(postService.findByDay(any[Int], any[Int], any[Int]))
        .thenReturn(List(expectedPost))
    when(postService.findByMonth(any[Int], any[Int]))
        .thenReturn(List(expectedPost))
    when(postService.findByYear(any[Int]))
        .thenReturn(List(expectedPost))
  }

  test("GET /posts") {
    Get("/posts") ~> routes.listPostsEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  test("GET /yyyy/mm/dd/slug") {
    Get("/2019/03/27/test-post") ~> routes.findPostEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostJson)
    }
  }

  test("GET /yyyy/mm/dd") {
    Get("/2019/03/27") ~> routes.postsByDay ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  test("GET /yyyy/mm") {
    Get("/2019/03") ~> routes.postsByMonth ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  test("GET /yyyy") {
    Get("/2019") ~> routes.postsByYear ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  test("POST /posts") {
    Post("/posts", createPostEntity) ~> routes.createPostEndpoint ~> check {
      status must equal(StatusCodes.Created)
      responseAs[JsValue] must equal (expectedPostCreatedResultJson)
    }
  }

  test("PUT /posts") {
    Put("/posts", updatePostEntity) ~> routes.updatePostEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostUpdatedResultJson)
    }
  }

  test("DELETE /posts/id") {
    Delete("/posts/1") ~> routes.deletePostEndpoint ~> check {
      status must equal(StatusCodes.OK)
      responseAs[JsValue] must equal(expectedPostDeletedResultJson)
    }
  }
}