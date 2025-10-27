package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.Pekko]]
 */
private[dependency_rules] abstract class PekkoRules {

  private final val OrgId: String = "org.apache.pekko"

  /**
   * Base Pekko module names, without Scala binary version suffix.
   *
   * Explicit module list is needed because base Pekko, Pekko HTTP and Pekko Management
   * share the organization/group ID but have independent releases and function as
   * separate libraries.
   */
  private final val ModuleNames: Set[String] = Set(
    // module list taken from published jar-file artifacts for the 1.2.1 version
    "pekko-actor",
    "pekko-actor-testkit-typed",
    "pekko-actor-typed",
    "pekko-cluster",
    "pekko-cluster-metrics",
    "pekko-cluster-sharding",
    "pekko-cluster-sharding-typed",
    "pekko-cluster-tools",
    "pekko-cluster-typed",
    "pekko-coordination",
    "pekko-discovery",
    "pekko-distributed-data",
    "pekko-multi-node-testkit",
    "pekko-osgi",
    "pekko-persistence",
    "pekko-persistence-query",
    "pekko-persistence-tck",
    "pekko-persistence-testkit",
    "pekko-persistence-typed",
    "pekko-pki",
    "pekko-protobuf-v3",
    "pekko-remote",
    "pekko-serialization-jackson",
    "pekko-slf4j",
    "pekko-stream",
    "pekko-stream-testkit",
    "pekko-stream-typed",
    "pekko-testkit",
  )

  /**
   * Checks whether [[sbt.ModuleID]] is a Pekko module.
   *
   * @see
   *   [[ExtraDependencyRules.Pekko]] doc about scope
   */
  final def isPekkoModule(moduleId: ModuleID): Boolean = {
    // Pekko, Pekko HTTP and Pekko Management share orgId but have independent releases
    // and function as separate libraries
    moduleId.organization == OrgId && ModuleNames.contains(nameWithoutScalaBinVersion(moduleId))
  }

  /**
   * Pekko modules have to have the same version, otherwise you get a runtime error - this
   * rule allows catching it earlier.
   *
   * @see
   *   [[ExtraDependencyRules.Pekko]] doc about scope
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "PekkoModulesHaveSameVersion",
    selector = isPekkoModule,
  )

  /**
   * Prohibits Pekko dependencies.
   *
   * Could be used to catch accidental Pekko dependencies in Akka-only modules.
   *
   * @see
   *   [[ExtraDependencyRules.Pekko]] doc about scope
   */
  final val Banned: DependencyRule = DependencyRule.banned(
    name = "PekkoBanned",
    selector = isPekkoModule,
  )
}
