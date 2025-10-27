package com.evolution.sbt.dependency_rules.extra

import com.evolution.sbt.dependency_rules.*
import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

/**
 * @see
 *   [[ExtraDependencyRules.Akka]]
 */
private[dependency_rules] abstract class AkkaRules {

  private final val OrgId: String = "com.typesafe.akka"

  /**
   * Base Akka module names, without Scala binary version suffix.
   *
   * Explicit module list is needed because base Akka and Akka HTTP share the
   * organization/group ID but have independent releases and function as separate
   * libraries.
   */
  private final val ModuleNames: Set[String] = Set(
    // module list taken from published jar-file artifacts for the 2.6.21 version
    "akka-actor",
    "akka-actor-testkit-typed",
    "akka-actor-typed",
    "akka-cluster",
    "akka-cluster-metrics",
    "akka-cluster-sharding",
    "akka-cluster-sharding-typed",
    "akka-cluster-tools",
    "akka-cluster-typed",
    "akka-coordination",
    "akka-discovery",
    "akka-distributed-data",
    "akka-multi-node-testkit",
    "akka-osgi",
    "akka-persistence",
    "akka-persistence-query",
    "akka-persistence-tck",
    "akka-persistence-testkit",
    "akka-persistence-typed",
    "akka-pki",
    "akka-protobuf",
    "akka-protobuf-v3",
    "akka-remote",
    "akka-serialization-jackson",
    "akka-slf4j",
    "akka-stream",
    "akka-stream-testkit",
    "akka-stream-typed",
    "akka-testkit",
  )

  /**
   * Checks whether [[sbt.ModuleID]] is an Akka module.
   *
   * @see
   *   [[ExtraDependencyRules.Akka]] doc about scope
   */
  final def isAkkaModule(moduleId: ModuleID): Boolean = {
    // Akka and Akka HTTP share orgId but have independent releases and function as separate libraries
    moduleId.organization == OrgId && ModuleNames.contains(nameWithoutScalaBinVersion(moduleId))
  }

  /**
   * Akka modules have to have the same version, otherwise you get a runtime error - this
   * rule allows catching it earlier.
   *
   * @see
   *   [[ExtraDependencyRules.Akka]] doc about scope
   */
  final val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
    name = "AkkaModulesHaveSameVersion",
    selector = isAkkaModule,
  )

  /**
   * Prohibits Akka dependencies.
   *
   * Could be used to catch accidental Akka dependencies in Pekko-only modules.
   *
   * @see
   *   [[ExtraDependencyRules.Akka]] doc about scope
   */
  final val Banned: DependencyRule = DependencyRule.banned(
    name = "AkkaBanned",
    selector = isAkkaModule,
  )
}
