package io.dja.smough.app

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{post, _}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.dja.smough.domain.Post
import io.dja.smough.domain.Post._
import io.dja.smough.domain.Result._
import io.dja.smough.post.PostCache

class ApiRoutes(val postCache: PostCache) {

  // TODO: we could probably make this a bit more re-usable by chaining instead of specifying the endpoint in each declaration
  // see: https://doc.akka.io/docs/akka-http/current/introduction.html
  val listPostsEndpoint = path("posts") {
    get {
      complete(postCache.retrieveAllFromCache())
    }
  }

  val findPostEndpoint = path(IntNumber / IntNumber / IntNumber / Segment) {
    (year, month, day, slug) =>
      get {
        complete(StatusCodes.OK, postCache.findBySlug(slug))
      }
  }

  // TODO: handle exceptions etc
  val createPostEndpoint = path("posts") {
    post {
      entity(as[Post]) { post =>
        complete(StatusCodes.Created, postCache.insert(post))
      }
    }
  }

  val updatePostEndpoint = path("posts") {
    put {
      entity(as[Post]) { post =>
        complete(postCache.update(post))
      }
    }
  }

  // TODO: is there a better way than .toInt?
  val deletePostEndpoint = path("posts"/Segment) { id =>
    delete {
      complete(postCache.delete(id.toInt))
    }
  }

  val routes = listPostsEndpoint ~
    findPostEndpoint ~
    createPostEndpoint ~
    updatePostEndpoint ~
    deletePostEndpoint

  def apply() = routes

}