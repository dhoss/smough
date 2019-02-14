package io.dja.smough.app

import org.mockito.ArgumentMatchersSugar
import org.scalatest.{BeforeAndAfter, FunSuite, MustMatchers}
import org.scalatest.mockito.MockitoSugar
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.dja.smough.ApiRoutes
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.testkit.TestDuration
import io.dja.smough.service.PostService
import org.mockito.Mockito.{times, verify, verifyNoMoreInteractions, when}

import scala.concurrent.duration._
import io.dja.smough.test.PostFixtures._

class ApiRoutesTest extends FunSuite
  with MockitoSugar
  with ArgumentMatchersSugar
  with BeforeAndAfter
  with MustMatchers
  with ScalatestRouteTest {

  val postService = mock[PostService]
  val routes = new ApiRoutes(postService)

  before {
    implicit val timeout = RouteTestTimeout(5.seconds dilated)
    when(postService.retrieveAllFromCache()).thenReturn(expectedPostCache)
  }

  test("/posts") {
    Get("/posts") ~> routes.listPostsEndpoint ~> check {
      responseAs[String] must equal(expectedPostsJson.noSpaces)
    }
  }
}
