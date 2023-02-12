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
      |    listInt = [ 1, 2, 3, 4 ]
      |    listString = [ a, b, c, d ]
      |    listBool = [ true, false, true ]
      |    listDouble = [ 1.12, 2.34, 2.33 ]
      |    listDur = [ 10 ms, 15 ms, 1 s ]
      |    invalidList = [ 1, a, true ]
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
  test("parse List[Int]") {
    nested.list("listInt").as[List[Int]].load[IO] assertEquals List(1, 2, 3, 4)
  }
  test("parse List[Long]") {
    nested.list("listInt").as[List[Long]].load[IO] assertEquals List(1L, 2, 3, 4)
  }
  test("parse List[String]") {
    nested.list("listString").as[List[String]].load[IO] assertEquals List("a", "b", "c", "d")
  }
  test("parse List[Bool]") {
    nested.list("listBool").as[List[Boolean]].load[IO] assertEquals List(true, false, true)
  }
  test("parse List[Double]") {
    nested.list("listDouble").as[List[Double]].load[IO] assertEquals List(1.12, 2.34, 2.33)
  }
  test("parse List[java Duration]") {
    nested.list("listDur").as[List[java.time.Duration]].load[IO] assertEquals List(
      java.time.Duration.ofMillis(10),
      java.time.Duration.ofMillis(15),
      java.time.Duration.ofSeconds(1)
    )
  }
  test("parse List[scala Duration]") {
    nested.list("listDur").as[List[FiniteDuration]].load[IO] assertEquals List(10.millis, 15.millis, 1.second)
  }
  test("handle decode error for invalid list") {
    nested
      .list("invalidList")
      .as[List[Int]]
      .attempt[IO]
      .map {
        case Left(error) => error.messages.toList.head
        case Right(_)    => "config loaded"
      }
      .assertEquals(
        "Nested.config.invalidList with value SimpleConfigList([1,\"a\",true]) cannot be converted to List[Int]"
      )
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
