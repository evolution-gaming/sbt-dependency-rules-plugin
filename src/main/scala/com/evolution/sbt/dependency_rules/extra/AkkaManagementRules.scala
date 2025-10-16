package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.AkkaManagement]]
 */
private[dependency_rules] abstract class AkkaManagementRules {

  private final val OrgId: String = "com.lightbend.akka.management"

  /**
   * Checks whether [[sbt.ModuleID]] is an Akka Management module.
   */
  final def isAkkaManagementModule(moduleId: ModuleID): Boolean = {
    moduleId.organization == OrgId
  }

  /**
   * Akka Management modules have to have the same version, otherwise you get a runtime
   * error - this rule allows catching it earlier.
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "AkkaManagementModulesHaveSameVersion",
    selector = isAkkaManagementModule,
  )
}
