package io.dja.smough

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.dja.smough.domain.Post
import io.dja.smough.domain.Post._
import io.dja.smough.domain.Result._
import io.dja.smough.service.PostService

class ApiRoutes(val postService: PostService) {

  // TODO: we could probably make this a bit more re-usable by chaining instead of specifying the endpoint in each declaration
  // see: https://doc.akka.io/docs/akka-http/current/introduction.html
  val listPostsEndpoint = path("posts") {
    get {
      complete(postService.retrieveAllFromCache())
    }
  }

  val findPostEndpoint = path("posts"/Segment) { slug =>
    get {
      complete(StatusCodes.OK, postService.findBySlug(slug))
    }
  }

  // TODO: handle exceptions etc
  val createPostEndpoint = path("posts") {
    post {
      entity(as[Post]) { post =>
        complete(StatusCodes.Created, postService.insert(post))
      }
    }
  }

  val updatePostEndpoint = path("posts") {
    put {
      entity(as[Post]) { post =>
        complete(postService.update(post))
      }
    }
  }

  // TODO: is there a better way than .toInt?
  val deletePostEndpoint = path("posts"/Segment) { id =>
    delete {
      complete(postService.delete(id.toInt))
    }
  }

  val routes = listPostsEndpoint ~
    findPostEndpoint ~
    createPostEndpoint ~
    updatePostEndpoint ~
    deletePostEndpoint

  def apply() = routes

}