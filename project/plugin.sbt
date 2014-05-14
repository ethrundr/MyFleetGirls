// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.1")

libraryDependencies += "mysql" % "mysql-connector-java" % "[5.1,)" // Add SQLDriver

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "[1.7,)")
