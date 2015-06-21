name := """MapleScala"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.2.6"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.6"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"
libraryDependencies += "io.github.nremond" %% "pbkdf2-scala" % "0.5"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"