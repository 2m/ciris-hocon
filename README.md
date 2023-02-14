# [ciris-hocon][] [![scaladex-badge][]][scaladex] [![ci-badge][]][ci] [![gitter-badge][]][gitter]

[ciris-hocon]:        https://github.com/2m/ciris-hocon
[scaladex]:           https://index.scala-lang.org/2m/ciris-hocon
[scaladex-badge]:     https://index.scala-lang.org/2m/ciris-hocon/latest.svg
[ci]:                 https://github.com/2m/ciris-hocon/actions
[ci-badge]:           https://github.com/2m/ciris-hocon/workflows/ci/badge.svg
[gitter]:             https://gitter.im/vlovgr/ciris
[gitter-badge]:       https://badges.gitter.im/vlovgr/ciris.svg

`ciris-hocon` provides a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) configuration source for [Ciris](https://cir.is/) configuration loading library.

The implementation of this library was created by following the excellent [Ciris documentation](https://github.com/vlovgr/ciris/blob/v2.0.0-RC3/docs/src/main/mdoc/configurations.md#sources).

## Setup

Add the dependency to your project build settings:

```sbt
libraryDependencies += "lt.dvim.ciris-hocon" %% "ciris-hocon" % "1.1.0"
```

Or a snapshot from a [snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/lt/dvim/ciris-hocon/).

| version    | scala       | ciris      |
|------------|-------------|------------|
| 0.1        | 2.12        | 0.12.1     |
| 0.2.1      | 2.13        | 0.13.0-RC1 |
| 1.0.x      | 2.13, 3     | 2.x.x      |
| 1.1.x      | 2.13, 3     | 3.x.x      |  

## Example usage

This library provides configuration sources as well as decoders from [`ConfigValue`](https://lightbend.github.io/config/latest/api/?com/typesafe/config/ConfigValue.html) values.

```scala
import java.time.Period
import scala.concurrent.duration._

import cats.effect.IO
import cats.implicits._
import com.typesafe.config.ConfigFactory

import lt.dvim.ciris.Hocon._

val config = ConfigFactory.parseString("""
    |rate {
    |  elements = 2
    |  burst-duration = 100 millis
    |  check-interval = 2 weeks
    |  values = [ first, second ]
    |}
  """.stripMargin)

case class Rate(
  elements: Int,
  burstDuration: FiniteDuration,
  checkInterval: Period,
  values: List[String]
)

val hocon = hoconAt(config)("rate")
(
  hocon("elements").as[Int],
  hocon("burst-duration").as[FiniteDuration],
  hocon("check-interval").as[Period],
  hocon("values").as[List[String]]
).parMapN(Rate.apply).load[IO].map { rate =>
  assertEquals(rate.burstDuration, 100.millis)
  assertEquals(rate.checkInterval, Period.ofWeeks(2))
}
```
