package com.faccuo.wordcount

import cats.effect.IO
import org.http4s
import org.http4s._
import org.http4s.implicits._

class WordCountSpec extends org.specs2.mutable.Specification {

  "upload" >> {
    "should return 200 and word count for simple input" >> {
      val content = fs2.Stream.emits(
        """
          |This is a simple test for our word count solution.
          |We should get the correct word count for this text.
        """.stripMargin.getBytes)
      val upload = Request[IO](method = Method.POST, uri = Uri.uri("/upload"), body = content)
      val call = new WordCountService[IO].service.orNotFound(upload)
      implicit val mapDecoder = http4s.circe.jsonOf[IO, Map[String, Int]]

      call.unsafeRunSync().status must beEqualTo(Status.Ok)
      call.as[Map[String, Int]].unsafeRunSync() must beEqualTo(
        Map(
          "for" -> 2,
          "test" -> 1,
          "this" -> 2,
          "is" -> 1,
          "our" -> 1,
          "should" -> 1,
          "correct" -> 1,
          "a" -> 1,
          "text" -> 1,
          "solution" -> 1,
          "count" -> 2,
          "simple" -> 1,
          "get" -> 1,
          "word" -> 2,
          "we" -> 1,
          "the" -> 1
        )
      )
    }
  }
}
