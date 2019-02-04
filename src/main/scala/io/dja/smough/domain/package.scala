package io.dja.smough

import java.time.OffsetDateTime

import io.circe.syntax._
import io.circe._

package object domain {
  // We don't need to pass in an ID unless we're constructing the Post DTO
  // Therefore, it's Optional and defaults to None at the end of the argument list
  // so it's less convenient to add manually.
  case class Post(
    parent: Option[Int],
    title: String,
    slug: String,
    body: String,
    author: Int,
    createdOn: Option[OffsetDateTime] = None,
    updatedOn: Option[OffsetDateTime] = None,
    id: Option[Int] = None)

  object Post {
    implicit val encoder: Encoder[Post] = (a: Post) => {
      Json.obj(
        "id" -> a.id.asJson,
        "parent" -> a.parent.asJson,
        "title" -> a.title.asJson,
        "slug" -> a.slug.asJson,
        "body" -> a.body.asJson,
        "author" -> a.author.asJson,
        "createdOn" -> a.createdOn.asJson,
        "updatedOn" -> a.updatedOn.asJson
      )
    }

    implicit val decoder: Decoder[Post] = (c: HCursor) => {
      for {
        id <- c.downField("id").as[Int]
        parent <- c.downField("parent").as[Option[Int]]
        title <- c.downField("title").as[String]
        slug <- c.downField("slug").as[String]
        body <- c.downField("body").as[String]
        author <- c.downField("author").as[Int]
        createdOn <- c.downField("createdOn").as[OffsetDateTime]
        updatedOn <- c.downField("updatedOn").as[OffsetDateTime]
      } yield Post(parent, title, slug, body, author, Some(createdOn), Some(updatedOn), Some(id))
    }
  }
}
