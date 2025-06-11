
lazy val root = (project in file("."))
  .settings(
    name := "storch-plot"
  )

import sbt.*
import Keys.*
import sbt.Def.settings

import scala.collection.Seq
ThisBuild / version := "0.0.2"
ThisBuild / tlBaseVersion := "0.0.2" // your current series x.y
//ThisBuild / CoursierCache := file("D:\\coursier")
ThisBuild / organization := "io.github.mullerhai" //"dev.storch"
ThisBuild / organizationName := "storch.dev"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("mullerhai", "mullerhai")
)
ThisBuild / scalaVersion := "3.6.4"
ThisBuild / tlSonatypeUseLegacyHost := false

import xerial.sbt.Sonatype.sonatypeCentralHost
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommandAndRemaining("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges,
)


ThisBuild / tlSitePublishBranch := Some("main")

ThisBuild / apiURL := Some(new URL("https://storch.dev/api/"))
ThisBuild / tlSonatypeUseLegacyHost := false

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / homepage := Some(new URL("https://storch.dev/api/"))
ThisBuild / scmInfo := Some( ScmInfo( url( "https://github.com/mullerhai/storch-plot" ), "scm:git:https://github.com/mullerhai/storch-plot.git" ) )


ThisBuild  / assemblyMergeStrategy := {
  case v if v.contains("module-info.class")   => MergeStrategy.discard
  case v if v.contains("UnusedStub")          => MergeStrategy.first
  case v if v.contains("aopalliance")         => MergeStrategy.first
  case v if v.contains("inject")              => MergeStrategy.first
  case v if v.contains("jline")               => MergeStrategy.discard
  case v if v.contains("scala-asm")           => MergeStrategy.discard
  case v if v.contains("asm")                 => MergeStrategy.discard
  case v if v.contains("scala-compiler")      => MergeStrategy.deduplicate
  case v if v.contains("reflect-config.json") => MergeStrategy.discard
  case v if v.contains("jni-config.json")     => MergeStrategy.discard
  case v if v.contains("git.properties")      => MergeStrategy.discard
  case v if v.contains("reflect.properties")      => MergeStrategy.discard
  case v if v.contains("compiler.properties")      => MergeStrategy.discard
  case v if v.contains("scala-collection-compat.properties")      => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.13.1"
libraryDependencies += "org.scala-lang.modules" % "scala-swing_3" % "3.0.0"
// https://mvnrepository.com/artifact/io.github.mullerhai/storch-numpy
libraryDependencies += "io.github.mullerhai" %% "storch-numpy" % "0.1.0"
// https://mvnrepository.com/artifact/io.circe/circe-parser
libraryDependencies += "io.circe" %%% "circe-core" % "0.15.0-M1"
libraryDependencies += "io.circe" %%% "circe-generic" % "0.15.0-M1"
libraryDependencies += "io.circe" %%% "circe-parser" % "0.15.0-M1"
// https://mvnrepository.com/artifact/org.apache.commons/commons-compress
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.27.1"
// https://mvnrepository.com/artifact/com.chuusai/shapeless
libraryDependencies += ("com.chuusai" %% "shapeless" % "2.3.13") cross CrossVersion.for3Use2_13
// https://mvnrepository.com/artifact/io.circe/circe-generic-extras
//libraryDependencies += ("io.circe" %% "circe-generic-extras" % "0.14.4") cross CrossVersion.for3Use2_13 exclude ("io.circe","circe-core_2.13") exclude ("io.circe","circe-numbers_2.13") exclude ("io.circe","circe-generic_2.13") exclude ("org.typelevel","cats-kernel_2.13") exclude ("org.typelevel","cats-core_2.13")
//libraryDependencies += "io.circe" %%% "circe-generic-extras" % "0.15.0-M1" //5-RC1"
libraryDependencies += ("io.circe" %% "circe-generic-extras" % "0.14.5-RC1") cross CrossVersion.for3Use2_13 cross CrossVersion.for3Use2_13 exclude ("io.circe","circe-core_2.13") exclude ("io.circe","circe-numbers_2.13") exclude ("io.circe","circe-generic_2.13") exclude ("org.typelevel","cats-kernel_2.13") exclude ("org.typelevel","cats-core_2.13")
libraryDependencies += "org.scalanlp" %% "breeze" % "2.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-alpha.1" % Test
libraryDependencies += "jfree" % "jfreechart" % "1.0.13"
//libraryDependencies += "io.circe" %%% "circe-generic-extras" % "0.15.0-M1" //5-RC1"
//#######################"io.circe" %%% "circe-generic-extras" % "0.14.4"
// https://mvnrepository.com/artifact/io.circe/circe-derivation
//libraryDependencies += ("io.circe" %% "circe-derivation" % "0.13.0-M5") cross CrossVersion.for3Use2_13 exclude ("io.circe","circe-core_2.13") exclude ("io.circe","circe-numbers_2.13") exclude ("org.typelevel","cats-kernel_2.13") exclude ("org.typelevel","cats-core_2.13")
//libraryDependencies += "io.circe" %%% "circe-generic-extras" % "0.15.0-M1"
// https://mvnrepository.com/artifact/org.scalanlp/breeze

// https://mvnrepository.com/artifact/org.scalanlp/breeze
//libraryDependencies += "org.scalanlp" %% "breeze-native" % "2.1.0"
// https://mvnrepository.com/artifact/org.scalanlp/breeze-natives
//libraryDependencies += "org.scalanlp" %% "breeze-natives" % "2.1.0"
//dev.ludovic.netlib.blas.JNIBLAS
// https://mvnrepository.com/artifact/org.scalatest/scalatest
