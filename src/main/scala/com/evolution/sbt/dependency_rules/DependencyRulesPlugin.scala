package com.evolution.sbt.dependency_rules

import sbt.*
import sbt.Keys.*

import scala.collection.mutable

/**
 * Verifies project dependencies (including transitive ones) against a configured set of
 * rules.
 *
 * Use `dependencyRules` setting to add rules.
 *
 * Run `dependencyRulesCheck` task to verify dependencies - it fails if any violation is
 * found.
 *
 * @see
 *   [[DependencyRule]]
 * @see
 *   [[ExtraDependencyRules]]
 */
object DependencyRulesPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    val dependencyRules = settingKey[Seq[DependencyRule]](
      "dependency rules - run dependencyRulesCheck to verify",
    )
    val dependencyRulesCheck = taskKey[Unit](
      "verify dependencies against configured rules (dependencyRules) and fail if violated",
    )
  }

  import autoImport.*

  override lazy val globalSettings: Seq[Def.Setting[?]] = Seq(
    // Provide default values in globalSettings:
    // https://www.scala-sbt.org/1.x/docs/Plugins-Best-Practices.html#Provide+default+values+in
    dependencyRules := Vector.empty,
  )

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    dependencyRulesCheck := checkTask.value,
  )

  private lazy val checkTask = Def.task {
    val log = streams.value.log
    val rules = dependencyRules.value
    val project = thisProject.value
    val updateReport = (thisProject / update).value

    val executionPlan = toExecutionPlan(rules)
    val violationReporter = new ViolationReporter(project)

    executionPlan.foreach {
      case (config, configRules) =>
        runRulesForConfig(violationReporter, updateReport, config, configRules)
    }

    violationReporter.finalize(log)
  }

  private def runRulesForConfig(
    violationReporter: ViolationReporter,
    updateReport: UpdateReport,
    config: Configuration,
    rules: Vector[DependencyRule],
  ): Unit = {
    val deps = updateReport.configuration(config).toVector.flatMap(_.allModules)

    rules.foreach { rule =>
      rule.logic.verifyClasspath(deps).left.foreach { errorStr =>
        violationReporter.add(config, rule, errorStr)
      }
    }
  }

  private def toExecutionPlan(
    rules: Seq[DependencyRule],
  ): Vector[(Configuration, Vector[DependencyRule])] = {
    val resultBuilder = mutable.HashMap.empty[Configuration, Vector[DependencyRule]]

    rules.foreach { rule =>
      rule.scope.foreach { config =>
        resultBuilder.put(config, resultBuilder.getOrElse(config, Vector.empty) :+ rule)
      }
    }

    // this way `compile` always comes before `test`
    resultBuilder.toVector.sortBy { case (config, _) => config.name.toLowerCase }
  }

  /**
   * Collects and reports dependency rule violations in the context of a project
   *
   * Mutable, non-thread-safe.
   *
   * Usage:
   *   - use [[add]] to register violations
   *   - call [[finalize]] at the end - it logs an aggregated list of violations and
   *     throws [[DependencyRulesViolated]] if there are any
   */
  private final class ViolationReporter(project: ResolvedProject) {
    /*
    Collecting only the first violation for each rule.
    This way, we avoid reporting multiple repeating violations for "nested" config scopes.
    I.e., for `compile` and `test`.
    Because of rule sorting in toExecutionPlan, `compile` always takes precedence over `test`.
     */
    private val data = mutable.LinkedHashMap.empty[DependencyRule, (Configuration, String)]

    /**
     * Register a dependency rule violation.
     *
     * @param config
     *   classpath [[Configuration]] at fault
     * @param rule
     *   rule which was violated
     * @param errorStr
     *   error description
     */
    def add(config: Configuration, rule: DependencyRule, errorStr: String): Unit = {
      if (!data.contains(rule)) {
        data.put(rule, (config, errorStr))
        ()
      }
    }

    /**
     * Finalizes the reporting process.
     *
     * Logs an aggregated list of violations and throws [[DependencyRulesViolated]] if
     * there are any.
     *
     * Should be called at the end of the analysis for the project.
     *
     * @param log
     *   task logger
     */
    def finalize(log: Logger): Unit = {
      if (data.nonEmpty) {
        val reportedRuleConfigs = mutable.ArrayBuffer.empty[String]

        data.foreach {
          case (rule, (config, errorStr)) =>
            reportedRuleConfigs += s"${ rule.name }:$config"
            /*
            The task logger by default doesn't print the project in which things happen, so we need to print the
            project name.
            Otherwise, violations from different modules can't be distinguished from each other.
             */
            log.error(s"[${ project.id }][$config][${ rule.name }] $errorStr")
        }

        throw new DependencyRulesViolated(s"${ reportedRuleConfigs.mkString(", ") }")
      }
    }
  }
}
