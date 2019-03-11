package io.dja.smough

import java.time.OffsetDateTime

import play.api.libs.json.Json

package object domain {
  // We don't need to pass in an ID unless we're constructing the Post DTO
  // Therefore, it's Optional and defaults to None at the end of the argument list
  // so it's less convenient to add manually.
  case class Post(
    parent: Option[Int] = None,
    title: String,
    slug: Option[String] = None,
    body: String,
    author: Int,
    createdOn: Option[OffsetDateTime] = None,
    updatedOn: Option[OffsetDateTime] = None,
    id: Option[Int] = None)

  object Post {
    implicit val postFormat = Json.format[Post]
  }

  class StringToSlug(val s: String) {
    def toSlug = s.replaceAll("[^A-Za-z1-9]", "-")
  }

  implicit def sluggify(s: String) = new StringToSlug(s)

  case class Result(message: String)
  object Result {
    implicit val resultFormat = Json.format[Result]
  }
}
