addSbtPlugin("com.dwijnand"              % "sbt-dynver"       % "4.0.0")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"     % "2.2.1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"     % "0.1.10")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"       % "5.3.1")
addSbtPlugin("org.foundweekends"         % "sbt-bintray"      % "0.5.5")
addSbtPlugin("zamblauskas"               % "sbt-examplestest" % "0.1.2+6-d6241596")

resolvers += Resolver.bintrayIvyRepo("2m", "sbt-plugins")
