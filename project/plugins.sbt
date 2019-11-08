addSbtPlugin("com.dwijnand"              % "sbt-dynver"       % "4.0.0")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"     % "2.2.1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"     % "0.1.8")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"       % "5.3.0")
addSbtPlugin("org.foundweekends"         % "sbt-bintray"      % "0.5.5")
addSbtPlugin("zamblauskas"               % "sbt-examplestest" % "0.1.2")

resolvers += Resolver.bintrayIvyRepo("zamblauskas", "sbt-plugins")
