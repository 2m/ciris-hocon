organization := "lt.dvim.ciris-hocon"
name := "ciris-hocon"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "is.cir"        %% "ciris-core" % "0.13.0-RC1",
  "com.typesafe"   % "config"     % "1.4.0",
  "org.scalatest" %% "scalatest"  % "3.1.1" % "test"
)

scalafmtOnCompile := true

// show full stack traces and test case durations
testOptions in Test += Tests.Argument("-oDF")

organizationName := "Martynas Mickevičius"
startYear := Some(2018)
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/2m/ciris-hocon"))
scmInfo := Some(ScmInfo(url("https://github.com/2m/ciris-hocon"), "git@github.com:2m/ciris-hocon.git"))
developers += Developer(
  "contributors",
  "Contributors",
  "https://gitter.im/2m/ciris-hocon",
  url("https://github.com/2m/ciris-hocon/graphs/contributors")
)
bintrayOrganization := Some("2m")
bintrayRepository := (if (isSnapshot.value) "snapshots" else "maven")

enablePlugins(AutomateHeaderPlugin)
