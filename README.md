# [ciris-hocon][] [![scaladex-badge][]][scaladex] [![travis-badge][]][travis] [![gitter-badge][]][gitter]

[ciris-hocon]:        https://github.com/2m/ciris-hocon
[scaladex]:           https://index.scala-lang.org/2m/ciris-hocon
[scaladex-badge]:     https://index.scala-lang.org/2m/ciris-hocon/latest.svg
[travis]:             https://travis-ci.com/2m/ciris-hocon
[travis-badge]:       https://travis-ci.com/2m/ciris-hocon.svg?branch=master
[gitter]:             https://gitter.im/2m/ciris-hocon
[gitter-badge]:       https://badges.gitter.im/2m/ciris-hocon.svg

`ciris-hocon` provides a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) configuration source for [Ciris](https://cir.is/) configuration loading library.

The implementation of this library was created by following the excellent [Ciris documentation](https://cir.is/docs/supporting-new-sources).

## Setup

Add the dependency to your project build settings:

```sbt
libraryDependencies += "lt.dvim.ciris-hocon" %% "ciris-hocon" % "0.1"
```

Or a snapshot from a [snapshot repository](https://bintray.com/2m/snapshots/ciris-hocon) for which you will need an additional resolver:

```sbt
resolvers += Resolver.bintrayRepo("2m", "snapshots")
```

| version  | scala | ciris      |
|----------|-------|------------|
| 0.1      | 2.12  | 0.12.1     |
| 0.2      | 2.13  | 0.13.0-RC1 |

## Example usage

This library provides configuration sources as well as decoders from [`ConfigValue`](https://lightbend.github.io/config/latest/api/?com/typesafe/config/ConfigValue.html) values.

```scala
import ciris._
import lt.dvim.ciris.Hocon._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import java.time.Period

val config = ConfigFactory.parseString("""
    |rate {
    |  elements = 2
    |  burst-duration = 100 millis
    |  check-interval = 2 weeks
    |}
  """.stripMargin)

case class Rate(elements: Int, burstDuration: FiniteDuration, checkInterval: Period)

val hocon = hoconAt(config)("rate")
val rate = loadConfig(
  hocon[Int]("elements"),
  hocon[FiniteDuration]("burst-duration"),
  hocon[Period]("check-interval")
)(Rate.apply).orThrow()

rate.burstDuration shouldBe 100.millis
rate.checkInterval shouldBe Period.ofWeeks(2)
```

## Licence

Copyright 2018 Martynas Mickevičius

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
