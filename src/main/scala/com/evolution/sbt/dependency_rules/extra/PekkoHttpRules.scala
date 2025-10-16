package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.PekkoHttp]]
 */
private[dependency_rules] abstract class PekkoHttpRules {

  private final val OrgId: String = "org.apache.pekko"

  /**
   * Pekko HTTP module names, without Scala binary version suffix.
   *
   * Explicit module list is needed because base Pekko, Pekko HTTP and Pekko Management
   * share the organization/group ID but have independent releases and function as
   * separate libraries.
   */
  private final val ModuleNames: Set[String] = Set(
    // module list taken from published jar-file artifacts for the 1.2.0 version
    "pekko-http",
    "pekko-http-caching",
    "pekko-http-core",
    "pekko-http-cors",
    "pekko-http-jackson",
    "pekko-http-spray-json",
    "pekko-http-testkit",
    "pekko-http-testkit-munit",
    "pekko-http-xml",
    "pekko-parsing",
  )

  /**
   * Checks whether [[sbt.ModuleID]] is a Pekko HTTP module.
   */
  final def isPekkoHttpModule(moduleId: ModuleID): Boolean = {
    // Pekko, Pekko HTTP and Pekko Management share orgId but have independent releases
    // and function as separate libraries
    moduleId.organization == OrgId && ModuleNames.contains(nameWithoutScalaBinVersion(moduleId))
  }

  /**
   * Pekko HTTP modules have to have the same version, otherwise you get a runtime error -
   * this rule allows catching it earlier.
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "PekkoHttpModulesHaveSameVersion",
    selector = isPekkoHttpModule,
  )
}
