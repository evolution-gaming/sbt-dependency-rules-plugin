package com.evolution.sbt.dependency_rules

import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

//TODO: #5 add more built-in rules: Akka-HTTP, Pekko-HTTP, loggers, etc.
//TODO: #2 mention in the documentation
/**
 * Predefined useful dependency rules
 */
object ExtraDependencyRules {

  /**
   * Akka-related dependency rules (base Akka itself, not including derived projects with
   * separate versioning, like Akka-HTTP).
   */
  object Akka {

    /**
     * Akka modules organization ID.
     *
     * @see
     *   [[Akka]] doc about scope
     */
    val OrgId: String = "com.typesafe.akka"

    /**
     * Akka module names, without Scala binary version suffix.
     *
     * @see
     *   [[Akka]] doc about scope
     */
    val ModuleNames: Set[String] = Set(
      "akka-actor",
      "akka-actor-testkit-typed",
      "akka-actor-typed",
      "akka-bill-of-materials",
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
      "akka-scala-nightly",
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
     *   [[Akka]] doc about scope
     */
    def isAkkaModule(moduleId: ModuleID): Boolean = {
      // akka and akka-http share org but have different version policies
      moduleId.organization == OrgId && ModuleNames.contains(
        nameWithoutScalaBinVersion(moduleId),
      )
    }

    /**
     * Akka modules have to have the same version, otherwise you get a runtime error -
     * this rule allows catching it earlier.
     *
     * @see
     *   [[Akka]] doc about scope
     */
    val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
      name = "AkkaModulesHaveSameVersion",
      selector = isAkkaModule,
    )

    /**
     * Prohibits Akka dependencies.
     *
     * Could be used to catch accidental Akka dependencies in Pekko-only modules.
     *
     * @see
     *   [[Akka]] doc about scope
     */
    val Banned: DependencyRule = DependencyRule.banned(
      name = "AkkaBanned",
      selector = isAkkaModule,
    )
  }

  /**
   * Pekko-related dependency rules (base Pekko itself, not including derived projects
   * with separate versioning, like Pekko-HTTP).
   */
  object Pekko {

    /**
     * Pekko modules organization ID.
     *
     * @see
     *   [[Pekko]] doc about scope
     */
    val OrgId: String = "org.apache.pekko"

    /**
     * Pekko module names, without Scala binary version suffix.
     *
     * @see
     *   [[Pekko]] doc about scope
     */
    val ModuleNames: Set[String] = Set(
      "pekko-actor",
      "pekko-actor-testkit-typed",
      "pekko-actor-typed",
      "pekko-bill-of-materials",
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
      "pekko-scala-nightly",
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
     *   [[Pekko]] doc about scope
     */
    def isPekkoModule(moduleId: ModuleID): Boolean = {
      // akka and akka-http share org but have different version policies
      moduleId.organization == OrgId && ModuleNames.contains(
        nameWithoutScalaBinVersion(moduleId),
      )
    }

    /**
     * Pekko modules have to have the same version, otherwise you get a runtime error -
     * this rule allows catching it earlier.
     *
     * @see
     *   [[Pekko]] doc about scope
     */
    val ModulesHaveSameVersion: DependencyRule = DependencyRule.sameVersion(
      name = "PekkoModulesHaveSameVersion",
      selector = isPekkoModule,
    )

    /**
     * Prohibits Pekko dependencies.
     *
     * Could be used to catch accidental Pekko dependencies in Akka-only modules.
     *
     * @see
     *   [[Pekko]] doc about scope
     */
    val Banned: DependencyRule = DependencyRule.banned(
      name = "PekkoBanned",
      selector = isPekkoModule,
    )
  }
}
