organization := "lt.dvim.ciris-hocon"
name := "ciris-hocon"
description := "Provides HOCON configuration source for Ciris"

scalaVersion := "2.13.13"
crossScalaVersions += "3.3.1"

libraryDependencies ++= Seq(
  "is.cir"        %% "ciris"               % "3.5.0",
  "com.typesafe"   % "config"              % "1.4.3",
  "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % "test",
  "org.typelevel" %% "cats-effect"         % "3.5.3" % "test"
)

scalafmtOnCompile := true

// show full stack traces and test case durations
Test / testOptions += Tests.Argument("-oDF")

enablePlugins(AutomateHeaderPlugin)
organizationName := "github.com/2m/ciris-hocon/contributors"
startYear := Some(2018)
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/2m/ciris-hocon"))
developers += Developer(
  "contributors",
  "Contributors",
  "https://gitter.im/2m/general",
  url("https://github.com/2m/ciris-hocon/contributors")
)
sonatypeProfileName := "lt.dvim"
versionScheme := Some("semver-spec")
