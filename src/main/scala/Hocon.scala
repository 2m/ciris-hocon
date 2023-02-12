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

package lt.dvim.ciris

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

import cats.Show
import ciris._
import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigValue => HoconConfigValue}

object Hocon extends HoconConfigDecoders {

  final class HoconAt(config: Config, path: String) {
    def apply(name: String): ConfigValue[Effect, HoconConfigValue] =
      Try(config.getValue(fullPath(name))).fold(
        errHandler(name),
        ConfigValue.loaded(key(name), _)
      )

    def list(name: String): ConfigValue[Effect, HoconConfigValue] =
      Try(config.getList(fullPath(name))).fold(
        errHandler(name),
        ConfigValue.loaded(key(name), _)
      )

    private def errHandler(name: String): Throwable => ConfigValue[Effect, HoconConfigValue] = {
      case _: ConfigException.Missing => ConfigValue.missing(key(name))
      case ex                         => ConfigValue.failed(ConfigError(ex.getMessage))
    }

    private def key(name: String) = ConfigKey(fullPath(name))
    private def fullPath(name: String) = s"$path.$name"
  }

  def hoconAt(path: String): HoconAt =
    hoconAt(ConfigFactory.load())(path)

  def hoconAt(config: Config)(path: String): HoconAt =
    new HoconAt(config.resolve(), path)
}

trait HoconConfigDecoders {
  implicit val stringHoconDecoder: ConfigDecoder[HoconConfigValue, String] =
    ConfigDecoder[HoconConfigValue].map(_.atKey("t").getString("t"))

  private implicit val show: Show[HoconConfigValue] = new Show[HoconConfigValue]() {
    def show(t: HoconConfigValue): String = t.toString
  }

  implicit val listStringHoconDecoder: ConfigDecoder[HoconConfigValue, List[String]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[String]") { c =>
      Try(asScalaList(c.atKey("t").getStringList("t"))).toOption
    }

  implicit val listIntHoconDecoder: ConfigDecoder[HoconConfigValue, List[Int]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[Int]") { c =>
      Try(asScalaList(c.atKey("t").getIntList("t")).map(_.intValue())).toOption
    }

  implicit val listLongHoconDecoder: ConfigDecoder[HoconConfigValue, List[Long]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[Long]") { c =>
      Try(asScalaList(c.atKey("t").getLongList("t")).map(_.longValue())).toOption
    }

  implicit val listBooleanHoconDecoder: ConfigDecoder[HoconConfigValue, List[Boolean]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[Boolean]") { c =>
      Try(asScalaList(c.atKey("t").getBooleanList("t")).map(_.booleanValue())).toOption
    }

  implicit val listDoubleHoconDecoder: ConfigDecoder[HoconConfigValue, List[Double]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[Double]") { c =>
      Try(asScalaList(c.atKey("t").getDoubleList("t")).map(_.doubleValue())).toOption
    }

  implicit val listJavaDurationHoconDecoder: ConfigDecoder[HoconConfigValue, List[java.time.Duration]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[java.time.Duration]") { c =>
      Try(asScalaList(c.atKey("t").getDurationList("t"))).toOption
    }

  implicit val listDurationHoconDecoder: ConfigDecoder[HoconConfigValue, List[FiniteDuration]] =
    ConfigDecoder[HoconConfigValue].mapOption("List[FiniteDuration]") { c =>
      Try {
        asScalaList(c.atKey("t").getDurationList("t"))
          .map(_.toNanos)
          .map(scala.concurrent.duration.Duration.fromNanos)
      }.toOption
    }

  implicit val javaTimeDurationHoconDecoder: ConfigDecoder[HoconConfigValue, java.time.Duration] =
    ConfigDecoder[HoconConfigValue].map(_.atKey("t").getDuration("t"))

  implicit val javaPeriodHoconDecoder: ConfigDecoder[HoconConfigValue, java.time.Period] =
    ConfigDecoder[HoconConfigValue].map(_.atKey("t").getPeriod("t"))

  implicit def throughStringHoconDecoder[T](implicit d: ConfigDecoder[String, T]): ConfigDecoder[HoconConfigValue, T] =
    stringHoconDecoder.as[T]

  private def asScalaList[T](collection: java.util.Collection[T]): List[T] = {
    val builder = List.newBuilder[T]
    val it = collection.iterator()
    while (it.hasNext) builder += it.next()
    builder.result()
  }
}
