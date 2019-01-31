package io.dja.smough

import java.time.OffsetDateTime

import io.circe.syntax._
import io.circe._

package object domain {
  // TODO: clean up formatting
  case class Post(id: Option[Int], parent: Option[Int], title: String, slug: String, body: String, author: Int, createdOn: Option[OffsetDateTime], updatedOn: Option[OffsetDateTime])
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
      } yield Post(Some(id), parent, title, slug, body, author, Some(createdOn), Some(updatedOn))
    }
  }
}
