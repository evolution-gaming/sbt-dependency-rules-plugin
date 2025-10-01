package com.evolution.sbt.dependency_rules

import sbt.*

import scala.collection.immutable

class DependencyRuleLogicTests extends munit.FunSuite {
  import DependencyRuleLogicTests.*

  test("narrowClasspath") {
    var observedDeps: Vector[ModuleID] = Vector.empty

    val logic: DependencyRuleLogic = (dependencies: immutable.IndexedSeq[sbt.ModuleID]) => {
      observedDeps = dependencies.toVector
      Right(())
    }

    logic
      .narrowClasspath(_ != dep2)
      .verifyClasspath(Vector(dep1, dep2, dep3))

    assertEquals(observedDeps, Vector(dep1, dep3))
  }

  test("Banned - fail on non-empty classpath") {
    val result = DependencyRuleLogic.Banned.verifyClasspath(Vector(dep1, dep2))
    assertEquals(result, Left("banned: come.acme:dep1:1.0.0, org.test:dep2:1.0.0"))
  }

  test("Banned - succeed on empty classpath") {
    val result = DependencyRuleLogic.Banned.verifyClasspath(Vector.empty)
    assertEquals(result, Right(()))
  }

  test("SameVersion - succeed on empty") {
    val result = DependencyRuleLogic.SameVersion.verifyClasspath(Vector.empty)
    assertEquals(result, Right(()))
  }

  test("SameVersion - succeed if all versions the same") {
    val result = DependencyRuleLogic.SameVersion.verifyClasspath(Vector(dep1, dep2))
    assertEquals(result, Right(()))
  }

  test("SameVersion - fail on heterogeneous versions") {
    val result =
      DependencyRuleLogic.SameVersion.verifyClasspath(Vector(dep1, dep2.withRevision("1.0.1"), dep3))
    assertEquals(
      result,
      Left("inconsistent module versions: 1.0.1 (org.test:dep2), 1.0.0 (come.acme:dep1, com.acme:dep3)"),
    )
  }
}

private object DependencyRuleLogicTests {
  val dep1 = "come.acme" % "dep1" % "1.0.0"
  val dep2 = "org.test" % "dep2" % "1.0.0"
  val dep3 = "com.acme" % "dep3" % "1.0.0"
}
