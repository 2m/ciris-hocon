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

  test("parse Int") {
    hocon("int").as[Int].load[IO] assertEquals 2
  }
  test("parse String") {
    hocon("str").as[String].load[IO] assertEquals "labas"
  }
  test("parse java Duration") {
    hocon("dur").as[java.time.Duration].load[IO] assertEquals java.time.Duration.ofMillis(10)
  }
  test("parse scala Duration") {
    hocon("dur").as[FiniteDuration].load[IO] assertEquals 10.millis
  }
  test("parse Boolean") {
    hocon("bool").as[Boolean].load[IO] assertEquals true
  }
  test("parse Period") {
    hocon("per").as[java.time.Period].load[IO] assertEquals java.time.Period.ofWeeks(2)
  }
  test("handle missing") {
    hocon("missing")
      .as[Int]
      .attempt[IO]
      .map {
        case Left(err) => err.messages.toList.head
        case Right(_)  => "config loaded"
      }
      .assertEquals("Missing nested.config.missing")
  }
  test("handle decode error") {
    hocon("str")
      .as[Int]
      .attempt[IO]
      .map {
        case Left(err) => err.messages.toList.head
        case Right(_)  => "config loaded"
      }
      .assertEquals("Nested.config.str with value labas cannot be converted to Int")
  }
}
