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
import munit.CatsEffectSuite

class HoconSpec extends CatsEffectSuite {
  import lt.dvim.ciris.Hocon._

  private val config = ConfigFactory.parseString(s"""
      |nested {
      |  config {
      |    int = 2
      |    str = labas
      |    dur = 10 ms
      |    bool = true
      |    per = 2 weeks
      |  }
      |}
      |subst {
      |  int = $${nested.config.int}
      |}
    """.stripMargin)

  private val nested = hoconAt(config)("nested.config")
  private val subst = hoconAt(config)("subst")

  test("parse Int") {
    nested("int").as[Int].load[IO] assertEquals 2
  }
  test("parse String") {
    nested("str").as[String].load[IO] assertEquals "labas"
  }
  test("parse java Duration") {
    nested("dur").as[java.time.Duration].load[IO] assertEquals java.time.Duration.ofMillis(10)
  }
  test("parse scala Duration") {
    nested("dur").as[FiniteDuration].load[IO] assertEquals 10.millis
  }
  test("parse Boolean") {
    nested("bool").as[Boolean].load[IO] assertEquals true
  }
  test("parse Period") {
    nested("per").as[java.time.Period].load[IO] assertEquals java.time.Period.ofWeeks(2)
  }
  test("handle missing") {
    nested("missing")
      .as[Int]
      .attempt[IO]
      .map {
        case Left(err) => err.messages.toList.head
        case Right(_)  => "config loaded"
      }
      .assertEquals("Missing nested.config.missing")
  }
  test("handle decode error") {
    nested("str")
      .as[Int]
      .attempt[IO]
      .map {
        case Left(err) => err.messages.toList.head
        case Right(_)  => "config loaded"
      }
      .assertEquals("Nested.config.str with value labas cannot be converted to Int")
  }
  test("resolve substitutions") {
    subst("int").as[Int].load[IO] assertEquals 2
  }
}
