package io.dja.smough

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.dja.smough.service.PostService

class ApiRoutes(val postService: PostService) {

  val listPostsEndpoint = path("posts") {
    get {
      complete(postService.retrieveAllFromCache())
    }
  }

  val findPostEndpoint = path("posts"/Segment) { slug =>
    get {
      complete(postService.findBySlug(slug))
    }
  }

  val routes = listPostsEndpoint ~ findPostEndpoint

  def apply() = routes

}