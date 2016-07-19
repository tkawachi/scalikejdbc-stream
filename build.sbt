val commonSettings = Seq(
  organization := "com.github.tkawachi",
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/tkawachi/scalikejdbc-akka-stream-test/"),
    "scm:git:github.com:tkawachi/scalikejdbc-akka-stream-test.git"
  )),

  scalaVersion := "2.11.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint"
  ),

  doctestTestFramework := DoctestTestFramework.ScalaTest
)

lazy val root = project.in(file("."))
  .settings(commonSettings :_*)
  .settings(
    name := "scalikejdbc-stream-root",
    publishArtifact := false
  )
  .aggregate(stream, fs2)

lazy val stream = project.in(file("stream"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream",
    description := "Scalikejdbc stream",
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc" % "2.4.1",
      "com.h2database" % "h2" % "1.4.191" % "test",
      "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
    )
  )

lazy val fs2 = project.in(file("fs2"))
  .settings(commonSettings: _*)
  .settings(
    name := "scalikejdbc-stream-fs2",
    description := "Scalikejdbc stream FS2",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "0.9.0-M6",
      "com.h2database" % "h2" % "1.4.191" % "test",
      "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
    )
  )
  .dependsOn(stream)
