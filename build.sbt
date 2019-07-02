val _versions = new {
  val scalikejdbc = "3.3.2"
}

val commonSettings = Seq(
  organization := "com.github.tkawachi",
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/tkawachi/scalikejdbc-akka-stream-test/"),
    "scm:git:github.com:tkawachi/scalikejdbc-akka-stream-test.git"
  )),

  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint"
  ),

  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "test",
    "com.h2database" % "h2" % "1.4.197" % "test",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.scalikejdbc" %% "scalikejdbc" % _versions.scalikejdbc % "test"
  )
)

lazy val root = project.in(file("."))
  .settings(commonSettings :_*)
  .settings(
    name := "scalikejdbc-stream-root",
    publishArtifact := false
  )
  .aggregate(stream, fs2, akka, scalaz)

lazy val stream = project.in(file("stream"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream",
    description := "Scalikejdbc stream",
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc-core" % _versions.scalikejdbc
    )
  )

lazy val fs2 = project.in(file("fs2"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream-fs2",
    description := "Scalikejdbc stream FS2",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "1.0.5"
    )
  )
  .dependsOn(stream)

lazy val akka = project.in(file("akka"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream-akka",
    description := "Scalikejdbc stream Akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.5.23"
    )
  )
  .dependsOn(stream)

lazy val scalaz = project.in(file("scalaz"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream-scalaz",
    description := "Scalikejdbc stream Akka",
    libraryDependencies ++= Seq(
      "org.scalaz.stream" %% "scalaz-stream" % "0.8.6"
    )
  )
  .dependsOn(stream)
