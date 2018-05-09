package com.faccuo.wordcount

import cats.effect.Effect
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl


class WordCountService[F[_] : Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case req@POST -> Root / "upload" =>
        Ok(
          req.body
            .through(fs2.text.utf8Decode andThen fs2.text.lines)
            .flatMap[String] { line =>
            fs2.Stream.emits(line.toLowerCase.split("""\W+"""))
          }.filter(_.nonEmpty).fold(scala.collection.mutable.Map.empty[String, Int]) { (acc, word) =>
            acc.put(word, acc.getOrElse(word, 0) + 1)
            acc
          }.map(_.asJson)
        )
    }
  }

}
