package io.dja.smough.app

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.dja.smough.ApiRoutes
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

  before {
   // implicit val timeout = RouteTestTimeout(5.seconds dilated)
    when(postService.retrieveAllFromCache()).thenReturn(expectedPostCache)
    when(postService.findBySlug(any[String])).thenReturn(Option(expectedPost))
  }

  // TODO add status code check
  test("GET /posts") {
    Get("/posts") ~> routes.listPostsEndpoint ~> check {
      //responseAs[Map[String, Post]] must equal(expectedPostsJson)
      responseAs[JsValue] must equal(expectedPostsJson)
    }
  }

  // TODO add status code check
  test("GET /posts/slug") {
    Get("/posts/test-post") ~> routes.findPostEndpoint ~> check {
      //responseAs[Map[String, Post]] must equal(expectedPostJson)
      responseAs[JsValue] must equal(expectedPostJson)
    }
  }

  test("POST /posts") {
    Post("/posts", expectedPostJson.toString) ~> routes.createPostEndpoint ~> check {
      status mustEqual StatusCodes.Created
      responseAs[JsValue] must equal (expectedPostCreatedResponseJson)
    }
  }
}
