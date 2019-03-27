package io.dja.smough

import java.time.OffsetDateTime

import play.api.libs.json._
import play.api.libs.functional.syntax._

package object domain {
  // We don't need to pass in an ID unless we're constructing the Post DTO
  case class Post(
    parent: Option[Int] = None,
    title: String,
    slug: Option[String] = None,
    body: String,
    author: Int,
    category: Int,
    publishedOn: Option[OffsetDateTime] = None,
    createdOn: Option[OffsetDateTime] = None,
    updatedOn: Option[OffsetDateTime] = None,
    id: Option[Int] = None)

  object Post {
    implicit val postWrites = new Writes[Post] {
      def writes(post: Post) = Json.obj(
        "parent" -> post.parent,
        "title" -> post.title,
        "slug" -> post.slug,
        "body" -> post.body,
        "author" -> post.author,
        "category" -> post.category,
        "publishedOn" -> post.publishedOn,
        "createdOn" -> post.createdOn,
        "updatedOn" -> post.updatedOn,
        "id" -> post.id
      )
    }

    // https://stackoverflow.com/questions/43031412/no-json-formatter-for-optionstring
    implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]]{
      override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

      override def writes(o: Option[T]): JsValue = o match {
        case Some(t) => implicitly[Writes[T]].writes(t)
        case None => JsNull
      }
    }

    // https://stackoverflow.com/questions/22191574/outputting-null-for-optiont-in-play-json-serialization-when-value-is-none
    implicit val postReads: Reads[Post] = (
        (JsPath \ "parent").readNullable[Int] and
        (JsPath \ "title").read[String] and
        (JsPath \ "slug").readNullable[String] and
        (JsPath \ "body").read[String] and
        (JsPath \ "author").read[Int] and
        (JsPath \ "category").read[Int] and
        (JsPath \ "publishedOn").readNullable[OffsetDateTime] and
        (JsPath \ "createdOn").readNullable[OffsetDateTime] and
        (JsPath \ "updatedOn").readNullable[OffsetDateTime] and
        (JsPath \ "id").readNullable[Int]
    )(Post.apply _)
  }

  class StringToSlug(val s: String) {
    def toSlug = s.replaceAll("[^A-Za-z1-9]", "-")
  }

  implicit def sluggify(s: String) = new StringToSlug(s)

  case class Result(message: String)
  object Result {
    implicit val resultFormat = Json.format[Result]
  }

  case class Category(id: Option[Int] = None, name: String)
  object Category {
    implicit val categoryFormat = Json.format[Category]
  }
}
