import com.evolution.sbt.dependency_rules._

version := "0.1"
scalaVersion := "2.13.17"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-persistence" % "2.6.21" exclude ("com.typesafe.akka", "akka-actor_2.13")

// checking for transitive dependency with sbt moduleFilter syntax
dependencyRules += DependencyRule.banned(
  name = "NoAkkaActor",
  selector = moduleFilter(
    organization = "com.typesafe.akka",
    name = "akka-actor*",
  ),
)
