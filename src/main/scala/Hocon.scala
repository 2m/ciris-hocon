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

import scala.util.Try

import ciris._
import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigValue => HoconConfigValue}

object Hocon extends HoconConfigDecoders {

  final class HoconAt(config: Config, path: String) {
    def apply(name: String): ConfigValue[Effect, HoconConfigValue] =
      Try(config.getValue(fullPath(name))).fold(
        {
          case _: ConfigException.Missing => ConfigValue.missing(key(name))
          case ex                         => ConfigValue.failed(ConfigError(ex.getMessage))
        },
        ConfigValue.loaded(key(name), _)
      )

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

  implicit val javaTimeDurationHoconDecoder: ConfigDecoder[HoconConfigValue, java.time.Duration] =
    ConfigDecoder[HoconConfigValue].map(_.atKey("t").getDuration("t"))

  implicit val javaPeriodHoconDecoder: ConfigDecoder[HoconConfigValue, java.time.Period] =
    ConfigDecoder[HoconConfigValue].map(_.atKey("t").getPeriod("t"))

  implicit def throughStringHoconDecoder[T](implicit d: ConfigDecoder[String, T]): ConfigDecoder[HoconConfigValue, T] =
    stringHoconDecoder.as[T]
}
