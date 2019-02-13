package io.dja.smough

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.dja.smough.service.PostService

object ApiRoutes {

  private var postService: PostService = _

  def apply(ps: PostService) = postService = ps

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

}