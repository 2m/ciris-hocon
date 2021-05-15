/*
 * Copyright 2018 github.com/2m/ciris-hocon/contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import scala.concurrent.duration._

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HoconSpec extends AnyWordSpec with Matchers with EitherValues {
  import cats.effect.unsafe.implicits.global

  import lt.dvim.ciris.Hocon._

  private val config = ConfigFactory.parseString("""
      |nested {
      |  config {
      |    int = 2
      |    str = labas
      |    dur = 10 ms
      |    bool = true
      |    per = 2 weeks
      |  }
      |}
    """.stripMargin)

  private val hocon = hoconAt(config)("nested.config")

  "ciris-hocon" should {
    "parse Int" in {
      hocon("int").as[Int].load[IO].unsafeRunSync() shouldBe 2
    }
    "parse String" in {
      hocon("str").as[String].load[IO].unsafeRunSync() shouldBe "labas"
    }
    "parse Duration" in {
      hocon("dur").as[java.time.Duration].load[IO].unsafeRunSync() shouldBe java.time.Duration.ofMillis(10)
      hocon("dur").as[FiniteDuration].load[IO].unsafeRunSync() shouldBe 10.millis
    }
    "parse Boolean" in {
      hocon("bool").as[Boolean].load[IO].unsafeRunSync() shouldBe true
    }
    "parse Period" in {
      hocon("per").as[java.time.Period].load[IO].unsafeRunSync() shouldBe java.time.Period.ofWeeks(2)
    }
    "handle missing" in {
      hocon("missing")
        .as[Int]
        .attempt[IO]
        .unsafeRunSync()
        .left
        .value
        .messages
        .toList should contain("Missing nested.config.missing")
    }
    "handle decode error" in {
      hocon("str")
        .as[Int]
        .attempt[IO]
        .unsafeRunSync()
        .left
        .value
        .messages
        .toList should contain("Nested.config.str with value labas cannot be converted to Int")
    }
  }
}
