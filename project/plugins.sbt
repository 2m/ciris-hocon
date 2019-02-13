addSbtPlugin("com.dwijnand"              % "sbt-dynver"       % "3.2.0")
addSbtPlugin("com.geirsson"              % "sbt-scalafmt"     % "1.5.1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"     % "0.1.5")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"       % "5.1.0")
addSbtPlugin("org.foundweekends"         % "sbt-bintray"      % "0.5.4")
addSbtPlugin("zamblauskas"               % "sbt-examplestest" % "0.1.2")

resolvers += Resolver.bintrayIvyRepo("zamblauskas", "sbt-plugins")
