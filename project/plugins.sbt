addSbtPlugin("com.dwijnand"              % "sbt-dynver"       % "4.0.0")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"     % "2.4.0")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"     % "0.1.11")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"       % "5.6.0")
addSbtPlugin("org.foundweekends"         % "sbt-bintray"      % "0.5.6")
addSbtPlugin("zamblauskas"               % "sbt-examplestest" % "0.2.2")

resolvers += Resolver.bintrayIvyRepo("zamblauskas", "sbt-plugins")
