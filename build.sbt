name := "comebol"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick"           % "3.3.3",
  "com.h2database"      % "h2"              % "1.4.200",
  "ch.qos.logback"      % "logback-classic" % "1.2.3"
)
