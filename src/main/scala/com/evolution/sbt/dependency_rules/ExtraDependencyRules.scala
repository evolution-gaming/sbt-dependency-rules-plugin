package com.evolution.sbt.dependency_rules

/**
 * Predefined useful dependency rules
 */
object ExtraDependencyRules {

  /**
   * Akka-related dependency rules (base Akka itself, not including derived projects with
   * separate versioning, like Akka-HTTP).
   *
   * @see
   *   [[https://github.com/akka/akka-core]]
   */
  object Akka extends extra.AkkaRules

  /**
   * Akka HTTP-related dependency rules.
   *
   * @see
   *   [[https://github.com/akka/akka-http]]
   */
  object AkkaHttp extends extra.AkkaHttpRules

  /**
   * Akka Management-related dependency rules.
   *
   * @see
   *   [[https://github.com/akka/akka-management]]
   */
  object AkkaManagement extends extra.AkkaManagementRules

  /**
   * Pekko-related dependency rules (base Pekko itself, not including derived projects
   * with separate versioning, like Pekko-HTTP).
   *
   * @see
   *   [[https://github.com/apache/pekko]]
   */
  object Pekko extends extra.PekkoRules

  /**
   * Pekko HTTP-related dependency rules.
   *
   * @see
   *   [[https://github.com/apache/pekko-http]]
   */
  object PekkoHttp extends extra.PekkoHttpRules

  /**
   * Pekko Management-related dependency rules.
   *
   * @see
   *   [[https://github.com/apache/pekko-management]]
   */
  object PekkoManagement extends extra.PekkoManagementRules
}
