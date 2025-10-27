package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.PekkoManagement]]
 */
private[dependency_rules] abstract class PekkoManagementRules {

  private final val OrgId: String = "org.apache.pekko"

  /**
   * Pekko Management module names, without Scala binary version suffix.
   *
   * Explicit module list is needed because base Pekko, Pekko HTTP and Pekko Management
   * share the organization/group ID but have independent releases and function as
   * separate libraries.
   */
  private final val ModuleNames: Set[String] = Set(
    // module list taken from published jar-file artifacts for the 1.1.1 version
    "pekko-discovery-aws-api",
    "pekko-discovery-aws-api-async",
    "pekko-discovery-consul",
    "pekko-discovery-kubernetes-api",
    "pekko-discovery-marathon-api",
    "pekko-lease-kubernetes",
    "pekko-management",
    "pekko-management-cluster-bootstrap",
    "pekko-management-cluster-http",
    "pekko-management-loglevels-log4j2",
    "pekko-management-loglevels-logback",
    "pekko-management-pki",
  )

  /**
   * Checks whether [[sbt.ModuleID]] is a Pekko Management module.
   */
  final def isPekkoManagementModule(moduleId: ModuleID): Boolean = {
    // Pekko, Pekko HTTP and Pekko Management share orgId but have independent releases
    // and function as separate libraries
    moduleId.organization == OrgId && ModuleNames.contains(nameWithoutScalaBinVersion(moduleId))
  }

  /**
   * Pekko Management modules have to have the same version, otherwise you get a runtime
   * error - this rule allows catching it earlier.
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "PekkoManagementModulesHaveSameVersion",
    selector = isPekkoManagementModule,
  )
}
