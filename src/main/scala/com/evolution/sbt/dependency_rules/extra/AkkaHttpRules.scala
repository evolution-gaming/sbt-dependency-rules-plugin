package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.AkkaHttp]]
 */
private[dependency_rules] abstract class AkkaHttpRules {

  private final val OrgId: String = "com.typesafe.akka"

  /**
   * Akka HTTP module names, without Scala binary version suffix.
   *
   * Explicit module list is needed because base Akka and Akka HTTP share the
   * organization/group ID but have independent releases and function as separate
   * libraries.
   */
  private final val ModuleNames: Set[String] = Set(
    // module list taken from published jar-file artifacts for the 10.2.10 version
    "akka-http",
    "akka-http-caching",
    "akka-http-core",
    "akka-http-jackson",
    "akka-http-spray-json",
    "akka-http-testkit",
    "akka-http-xml",
    "akka-http2-support",
    "akka-parsing",
  )

  /**
   * Checks whether [[sbt.ModuleID]] is an Akka HTTP module.
   */
  final def isAkkaHttpModule(moduleId: ModuleID): Boolean = {
    // Akka and Akka HTTP share orgId but have independent releases and function as separate libraries
    moduleId.organization == OrgId && ModuleNames.contains(nameWithoutScalaBinVersion(moduleId))
  }

  /**
   * Akka HTTP modules have to have the same version, otherwise you get a runtime error -
   * this rule allows catching it earlier.
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "AkkaHttpModulesHaveSameVersion",
    selector = isAkkaHttpModule,
  )
}
