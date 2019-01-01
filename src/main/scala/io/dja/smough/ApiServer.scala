package io.dja.smough
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext.Implicits.global

object ApiServer extends StreamApp[IO] {

  val postsService = HttpService[IO] {
    case GET -> Root / "posts" =>
      Ok(s"Ok")
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(postsService, "/api")
      .serve
  }
}

