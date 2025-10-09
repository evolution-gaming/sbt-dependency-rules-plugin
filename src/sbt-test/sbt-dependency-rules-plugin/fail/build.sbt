import com.evolution.sbt.dependency_rules._

version := "0.1"
scalaVersion := "2.13.17"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.6.21"

// checking for transitive dependency with sbt moduleFilter syntax
dependencyRules += DependencyRule.banned(
  name = "NoAkkaActor",
  selector = moduleFilter(
    organization = "com.typesafe.akka",
    name = "akka-actor*",
  ),
)

TaskKey[Unit]("checkTaskLogs") := {
  val projectName = name.value
  val lastLogFile: File = BuiltinCommands.lastLogFile(state.value).get
  val lastLog: String = IO.read(lastLogFile)
  val lastLogLines = lastLog.linesIterator.toArray

  val ruleFailureStr = "banned: com.typesafe.akka:akka-actor_2.13:2.6.21"
  val projectConfigRuleFailureLine =
    s"[error] [$projectName][compile][NoAkkaActor] banned: com.typesafe.akka:akka-actor_2.13:2.6.21"
  val finalTaskFailureLine =
    "[error] (dependencyRulesCheck) com.evolution.sbt.dependency_rules.DependencyRulesViolated: NoAkkaActor:compile"

  if (lastLogLines.count(_.contains(ruleFailureStr)) != 1) {
    sys.error("rule failure description should be printed exactly once")
  }

  if (!lastLogLines.contains(projectConfigRuleFailureLine)) {
    sys.error("missing project config rule failure line")
  }

  if (!lastLogLines.contains(finalTaskFailureLine)) {
    sys.error("missing final task failure line")
  }
}
