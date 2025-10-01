package com.evolution.sbt.dependency_rules

import sbt.*

/**
 * Dependency rule - verified using the
 * [[DependencyRulesPlugin.autoImport.dependencyRulesCheck]] task.
 *
 * Check the companion object for custom rule creation helpers!
 *
 * For an existing rule, parameters can be adjusted using `withX` and `modifyX` methods:
 * {{{
 *   // creates a new rule which is not checked against the Test scope classpath
 *   ExtraDependencyRules.Akka.ModulesHaveSameVersion.modifyScope(_ - Test)
 * }}}
 *
 * @param name
 *   rule name - must be a stable, unique, human-readable string
 * @param scope
 *   rule scope - set of [[sbt.Configuration]] (`Compile`, `Test`, etc.) for which
 *   classpaths are verified
 * @param logic
 *   rule classpath verification logic - applied for each configured [[sbt.Configuration]]
 *   scope separately
 *
 * @see
 *   [[ExtraDependencyRules]] for predefined rules
 */
final class DependencyRule(
  val name: String,
  val scope: Set[Configuration],
  val logic: DependencyRuleLogic,
) {
  // not a case class to avoid potential issues with bincompat

  /**
   * Creates a rule copy with modified [[DependencyRule.name]]
   */
  def withName(newName: String): DependencyRule = {
    copy(name = newName)
  }

  /**
   * Creates a rule copy with modified [[DependencyRule.scope]]
   */
  def withScope(newScope: Set[Configuration]): DependencyRule = {
    copy(scope = newScope)
  }

  /**
   * Creates a rule copy with modified [[DependencyRule.scope]]
   */
  def withScope(config1: Configuration, configRest: Configuration*): DependencyRule = {
    copy(scope = configRest.toSet + config1)
  }

  /**
   * Creates a rule copy with modified [[DependencyRule.scope]]
   */
  def modifyScope(f: Set[Configuration] => Set[Configuration]): DependencyRule = {
    copy(scope = f(scope))
  }

  /**
   * Creates a rule copy with modified [[DependencyRule.logic]]
   */
  def withLogic(newLogic: DependencyRuleLogic): DependencyRule = {
    copy(logic = newLogic)
  }

  /**
   * Creates a rule copy with modified [[DependencyRule.logic]]
   */
  def modifyLogic(f: DependencyRuleLogic => DependencyRuleLogic): DependencyRule = {
    copy(logic = f(logic))
  }

  private def copy(
    name: String = this.name,
    scope: Set[Configuration] = this.scope,
    logic: DependencyRuleLogic = this.logic,
  ): DependencyRule = {
    new DependencyRule(
      name = name,
      scope = scope,
      logic = logic,
    )
  }
}

object DependencyRule {

  /**
   * Default rule scope - compile and test
   *
   * @see
   *   [[DependencyRule.scope]]
   */
  val DefaultScope: Set[Configuration] = Set(Compile, Test)

  /**
   * Creates a new [[DependencyRule]] with custom verification logic.
   *
   * @param name
   *   rule name, see [[DependencyRule.name]]
   * @param scope
   *   rule scope, see [[DependencyRule.scope]]
   * @param logic
   *   rule classpath verification logic, see [[DependencyRule.logic]]
   */
  def apply(
    name: String,
    scope: Set[Configuration] = DefaultScope,
    logic: DependencyRuleLogic,
  ): DependencyRule = {
    new DependencyRule(
      name = name,
      scope = scope,
      logic = logic,
    )
  }

  /**
   * Creates a new [[DependencyRule]] which requires all selected dependencies to have the
   * same version.
   *
   * @param name
   *   rule name, see [[DependencyRule.name]]
   * @param scope
   *   rule scope, see [[DependencyRule.scope]]
   * @param selector
   *   selects which dependencies are required to have the same version; sbt moduleFilter
   *   syntax can be used here
   *
   * @see
   *   [[https://www.scala-sbt.org/1.x/docs/Update-Report.html#ModuleFilter]]
   */
  def sameVersion(
    name: String,
    scope: Set[Configuration] = DefaultScope,
    selector: ModuleID => Boolean,
  ): DependencyRule = {
    apply(name = name, scope = scope, logic = DependencyRuleLogic.SameVersion.narrowClasspath(selector))
  }

  /**
   * Creates a new [[DependencyRule]] which prohibits all selected dependencies.
   *
   * @param name
   *   rule name, see [[DependencyRule.name]]
   * @param scope
   *   rule scope, see [[DependencyRule.scope]]
   * @param selector
   *   selects which dependencies are prohibited; sbt moduleFilter syntax can be used here
   *
   * @see
   *   [[https://www.scala-sbt.org/1.x/docs/Update-Report.html#ModuleFilter]]
   */
  def banned(
    name: String,
    scope: Set[Configuration] = DefaultScope,
    selector: ModuleID => Boolean,
  ): DependencyRule = {
    apply(name = name, scope = scope, logic = DependencyRuleLogic.Banned.narrowClasspath(selector))
  }
}
