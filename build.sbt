import sbtbuildinfo.BuildInfoKey.action
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoOptions, buildInfoPackage}
import sbtbuildinfo.{BuildInfoKey, BuildInfoOption}
import com.softwaremill.SbtSoftwareMillCommon.commonSmlBuildSettings

import sbt._
import Keys._

import scala.util.Try
import scala.sys.process.Process
import complete.DefaultParsers._

val doobieVersion = "1.0.0-RC2"
val http4sVersion = "0.23.12"
val circeVersion = "0.14.2"
val sttpVersion = "3.7.0"
val prometheusVersion = "0.16.0"
val tapirVersion = "1.0.1"
val macwireVersion = "2.5.7"

val httpDependencies = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % sttpVersion,
  "com.softwaremill.sttp.client3" %% "slf4j-backend" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion
)

val monitoringDependencies = Seq(
  "io.prometheus" % "simpleclient" % prometheusVersion,
  "io.prometheus" % "simpleclient_hotspot" % prometheusVersion,
  "com.softwaremill.sttp.client3" %% "prometheus-backend" % sttpVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion
)

val jsonDependencies = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.client3" %% "circe" % sttpVersion
)

val loggingDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.codehaus.janino" % "janino" % "3.1.7",
  "de.siegmar" % "logback-gelf" % "4.0.2"
)

val configDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.1"
)

val baseDependencies = Seq(
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "com.softwaremill.common" %% "tagging" % "2.3.3",
  "com.softwaremill.quicklens" %% "quicklens" % "1.8.8"
)

val apiDocsDependencies = Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
)

val emailDependencies = Seq(
  "com.sun.mail" % "javax.mail" % "1.6.2" exclude ("javax.activation", "activation")
)

val scalatest = "org.scalatest" %% "scalatest" % "3.2.12" % Test
val macwireDependencies = Seq(
  "com.softwaremill.macwire" %% "macrosautocats" % macwireVersion
).map(_ % Provided)

val unitTestingStack = Seq(scalatest)

val embeddedPostgres = "com.opentable.components" % "otj-pg-embedded" % "1.0.1" % Test

val commonDependencies = baseDependencies ++ unitTestingStack ++ loggingDependencies ++ configDependencies

lazy val commonSettings = commonSmlBuildSettings ++ Seq(
  organization := "com.softwaremill.bootzooka",
  scalaVersion := "2.13.8",
  libraryDependencies ++= commonDependencies
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    action("lastCommitHash") {
      import scala.sys.process._
      // if the build is done outside of a git repository, we still want it to succeed
      Try("git rev-parse HEAD".!!.trim).getOrElse("?")
    }
  ),
  buildInfoOptions += BuildInfoOption.ToJson,
  buildInfoOptions += BuildInfoOption.ToMap,
  buildInfoPackage := "com.softwaremill.bootzooka.version",
  buildInfoObject := "BuildInfo"
)

def haltOnCmdResultError(result: Int) {
  if (result != 0) {
    throw new Exception("Build failed.")
  }
}

def now(): String = {
  import java.text.SimpleDateFormat
  import java.util.Date
  new SimpleDateFormat("yyyy-MM-dd-hhmmss").format(new Date())
}

lazy val rootProject = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "bootzooka"
  )
  .aggregate(backend)

lazy val backend: Project = (project in file("backend"))
  .settings(
    name := "bootzooka",
    libraryDependencies ++= httpDependencies ++ jsonDependencies ++ apiDocsDependencies ++ monitoringDependencies ++ emailDependencies ++ macwireDependencies,
    Compile / mainClass := Some("com.softwaremill.bootzooka.Main")
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
  .settings(Revolver.settings)
  .settings(buildInfoSettings)
  .enablePlugins(JavaServerAppPackaging)
