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

import ciris._
import ciris.api.Id
import com.typesafe.config.{Config, ConfigException, ConfigFactory}

import scala.util.Try
import scala.util.control.NonFatal

object Hocon {

  val HoconPathType = ConfigKeyType[String]("hocon at path")

  val hoconSource: ConfigSource[Id, String, Config] =
    ConfigSource.catchNonFatal(HoconPathType) {
      case path => ConfigFactory.load().getConfig(path)
    }

  final case class HoconKey(path: String, key: String) {
    override def toString: String = s"path=$path,key=$key"
  }

  val HoconKeyType = ConfigKeyType[HoconKey]("hocon at key")

  final class HoconAt(path: String) {
    private val hocon: Either[ConfigError, Config] =
      hoconSource
        .read(path)
        .value

    private def hoconKey(key: String): HoconKey =
      HoconKey(path, key)

    private def hoconAt(key: String): Either[ConfigError, String] =
      hocon.flatMap { props =>
        Try(props.getString(key)).toEither.left.map {
          case _: ConfigException.Missing =>
            ConfigError.missingKey(hoconKey(key), HoconKeyType)
          case NonFatal(ex) =>
            ConfigError.readException(hoconKey(key), HoconKeyType, ex)
        }
      }

    def apply[Value](key: String)(
        implicit decoder: ConfigDecoder[String, Value]
    ): ConfigEntry[Id, HoconKey, String, Value] =
      ConfigEntry(
        hoconKey(key),
        HoconKeyType,
        hoconAt(key)
      ).decodeValue[Value]

    override def toString: String =
      s"HoconAt($path)"
  }

  def hoconAt(path: String): HoconAt =
    new HoconAt(path)
}
