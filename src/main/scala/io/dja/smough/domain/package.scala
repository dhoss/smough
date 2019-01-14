package io.dja.smough

import java.time.Instant

import io.circe.syntax._
import io.circe._

package object domain {
  case class Post(id: Int, parent: Option[Int], title: String, body: String, author: Int, createdOn: Instant, updatedOn: Instant)
  object Post {
    implicit val encoder: Encoder[Post] = (a: Post) => {
      Json.obj(
        "id" -> a.id.asJson,
        "parent" -> a.parent.asJson,
        "title" -> a.title.asJson,
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
        body <- c.downField("body").as[String]
        author <- c.downField("author").as[Int]
        createdOn <- c.downField("createdOn").as[Instant]
        updatedOn <- c.downField("updatedOn").as[Instant]
      } yield Post(id, parent, title, body, author, createdOn, updatedOn)
    }
  }
}
