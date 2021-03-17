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

package lt.dvim.ciris

import scala.util.Try

import ciris.api.{Id, Monad}
import ciris.{ConfigValue => _, _}
import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigValue}

object Hocon extends HoconConfigDecoders {
  val HoconPathType: ConfigKeyType[String] = ConfigKeyType[String]("hocon at path")

  def hoconSource(config: Config): ConfigSource[Id, String, Config] =
    ConfigSource.catchNonFatal(HoconPathType)(config.getConfig)

  final case class HoconKey(path: String, key: String) {
    override def toString: String = s"path=$path,key=$key"
  }

  val HoconKeyType: ConfigKeyType[HoconKey] = ConfigKeyType[HoconKey]("hocon at key")

  final class HoconAt(config: Config, path: String) {
    private val hocon: Either[ConfigError, Config] =
      hoconSource(config)
        .read(path)
        .value

    private def hoconKey(key: String): HoconKey =
      HoconKey(path, key)

    private def hoconAt(key: String): Either[ConfigError, ConfigValue] =
      hocon.flatMap { c =>
        Try(c.getValue(key)).toEither.left.map {
          case _: ConfigException.Missing =>
            ConfigError.missingKey(hoconKey(key), HoconKeyType)
          case ex =>
            ConfigError.readException(hoconKey(key), HoconKeyType, ex)
        }
      }

    def apply[Value](key: String)(implicit
        decoder: ConfigDecoder[ConfigValue, Value]
    ): ConfigEntry[Id, HoconKey, ConfigValue, Value] =
      ConfigEntry(
        hoconKey(key),
        HoconKeyType,
        hoconAt(key)
      ).decodeValue[Value]

    override def toString: String =
      s"HoconAt($path)"
  }

  def hoconAt(path: String): HoconAt =
    hoconAt(ConfigFactory.load())(path)

  def hoconAt(config: Config)(path: String): HoconAt =
    new HoconAt(config, path)
}

trait HoconConfigDecoders {
  implicit val stringConfigDecoder: ConfigDecoder[ConfigValue, String] =
    ConfigDecoder.catchNonFatal("String")(value => value.atKey("t").getString("t"))

  implicit val javaTimeDurationConfigDecoder: ConfigDecoder[ConfigValue, java.time.Duration] =
    ConfigDecoder.catchNonFatal("java.time.Duration")(value => value.atKey("t").getDuration("t"))

  implicit val javaPeriodConfigDecoder: ConfigDecoder[ConfigValue, java.time.Period] =
    ConfigDecoder.catchNonFatal("java.time.Period")(value => value.atKey("t").getPeriod("t"))

  implicit def throughStringConfigDecoder[T](implicit dec: ConfigDecoder[String, T]): ConfigDecoder[ConfigValue, T] =
    new ConfigDecoder[ConfigValue, T] {
      override def decode[F[_]: Monad, K, S](
          entry: ConfigEntry[F, K, S, ConfigValue]
      ): F[Either[ConfigError, T]] =
        entry.decodeValue[String].decodeValue[T].value
    }
}
