name := "sbt-dependency-rules-plugin"
organization := "com.evolution"

description := "SBT plugin for enforcing rules on project dependencies"
startYear := Some(2025)
homepage := Some(url("https://github.com/evolution-gaming/sbt-dependency-rules-plugin"))
licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT")))

organizationName := "Evolution"
organizationHomepage := Some(url("https://evolution.com"))

// Maven Central requires <developers> in published pom.xml files
developers := List(
  Developer(
    id = "migesok",
    name = "Mikhail Sokolov",
    email = "mikhail.g.sokolov@gmail.com",
    url = url("https://github.com/migesok"),
  ),
)

scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/evolution-gaming/sbt-dependency-rules-plugin"),
  connection = "git@github.com:evolution-gaming/sbt-dependency-rules-plugin.git",
))

// DO NOT CHANGE THIS SETTING UNLESS YOU FULLY UNDERSTAND THE CONSEQUENCES!
// For clients to be able to reuse rules in derived plugins, full binary and source compatibility between
// releases should be guaranteed.
//
// WARNING: BinaryCompatible is used instead of BinaryAndSourceCompatible because BinaryAndSourceCompatible fails
// on new methods added to objects, which doesn't really break neither source, nor binary compatibility.
// So the source compatibility should be guaranteed manually.
versionPolicyIntention := Compatibility.BinaryCompatible

// setting up sbt 1 and 2 cross-build
// https://www.scala-sbt.org/2.x/docs/en/changes/migrating-from-sbt-1.x.html#cross-building-sbt-plugin-with-sbt-1x
crossScalaVersions += "3.8.4" // version used by SBT 2.0.12
pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.12.15"
    case "3" => "2.0.12"
  }
}

// scalac and scaladoc options
autoAPIMappings := true
scalacOptions := {
  val curOptions = scalacOptions.value

  scalaBinaryVersion.value match {
    case "2.12" =>
      // options for 2.12 come mostly from com.evolution:sbt-scalac-opts-plugin
      curOptions ++ Seq(
        "-Xsource:3",
      )

    case "3" =>
      // Discarding scalacOptions coming from com.evolution:sbt-scalac-opts-plugin,
      // because it adds an option which fails compilation on 3.8.x:
      //
      //  [warn] Option -Xfatal-warnings is a deprecated alias: use -Werror instead
      //  [error] No warnings can be incurred under -Werror
      //  [warn] one warning found
      //  [error] one error found
      Seq(
        "-Xkind-projector:underscores",

        // disable new brace-less syntax:
        // https://alexn.org/blog/2022/10/24/scala-3-optional-braces/
        "-no-indent",

        // improve error messages:
        "-explain",
        "-explain-types",
      )
  }
}
// common options
scalacOptions ++= Seq(
  "-release:17", // sbt 2 requires JDK 17+
  "-deprecation",
)

// migesok: tried several test frameworks, but only munit worked here:
// - scalatest with AnyFreeSpec gave weird compilation errors on valid test code
// - utest 0.9.1 didn't work on Java 8, only 11+, but Java 8 is required for sbt 1
libraryDependencies += "org.scalameta" %% "munit" % "1.3.6" % Test
// disable parallel test execution and bufferization to have nice linear stdout:
// https://scalameta.org/munit/docs/tests.html#run-tests-in-parallel
Test / parallelExecution := false
Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "-b")

enablePlugins(SbtPlugin)
// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++= Seq(
  "-Xmx1024M",
  "-Dplugin.version=" + version.value,
  "-Dsbt.color=never", // to get reliable test results on sbt log
)

addCommandAlias("fmt", "+all scalafmtAll scalafmtSbt")
addCommandAlias("buildFast", "+all scalafmtCheckAll scalafmtSbtCheck versionPolicyCheck Compile/doc test")
addCommandAlias("buildFull", "; buildFast; +scripted")

def crossSettings[T](scalaVersion: String, if3: T, if2: T): T = {
  scalaVersion match {
    case version if version.startsWith("3") => if3
    case _ => if2
  }
}
