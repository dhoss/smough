package io.dja.smough

import java.time.OffsetDateTime

import io.circe.syntax._
import io.circe._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

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

  class PostBuilder {

    // TODO: turn these into value objects?
    private var parent: Option[Int] = None
    private var title: Option[String] = None
    private var slug: Option[String] = None
    private var body: Option[String] = None
    private var author: Option[Int] = None
    private var createdOn: Option[OffsetDateTime] = None
    private var updatedOn: Option[OffsetDateTime] = None
    private var id: Option[Int] = None

    def withParent(p: Int) = { parent = Some(p); this }
    def withTitle(t: String) = { title = Some(t); this }
    def withSlug(s: String) = { slug = Some(s); this }
    def withBody(b: String) = { body = Some(b); this }
    def withAuthor(a: Int) = { author = Some(a); this }
    def withCreatedOn(c: OffsetDateTime) = { createdOn = Some(c); this }
    def withUpdatedOn(u: OffsetDateTime) = { updatedOn = Some(u); this }
    def withId(i: Int) = { id = Some(i); this }

    def build() = new Post(
      parent, title.get, slug.get, body.get, author.get, createdOn, updatedOn, id)
  }

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

  // TODO: see if we can tighten up types here
  case class UpdatePost(id: Int, updateMap: mutable.HashMap[String, Any]) {
    override def toString(): String = {
      val s = ArrayBuffer[String]()
      for ((k, v) <- updateMap) {
        s.append(s"${k}=${v}")
      }
      s.prepend(s"id=${id}")
      s.mkString(",")
    }
  }
}
