/*
 * Copyright 2018 Martynas Mickevičius
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

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class HoconSpec extends WordSpec with Matchers {
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

  private val nested = hoconAt(config)("nested.config")

  "ciris-hocon" should {
    "parse Int" in {
      nested[Int]("int").orThrow() shouldBe 2
    }
    "parse String" in {
      nested[String]("str").orThrow() shouldBe "labas"
    }
    "parse Duration" in {
      nested[java.time.Duration]("dur").orThrow() shouldBe java.time.Duration.ofMillis(10)
      nested[FiniteDuration]("dur").orThrow() shouldBe 10.millis
    }
    "parse Boolean" in {
      nested[Boolean]("bool").orThrow() shouldBe true
    }
    "parse Period" in {
      nested[java.time.Period]("per").orThrow() shouldBe java.time.Period.ofWeeks(2)
    }
  }

}
