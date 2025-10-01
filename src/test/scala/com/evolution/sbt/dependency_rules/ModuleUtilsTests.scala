package com.evolution.sbt.dependency_rules

import sbt.*

class ModuleUtilsTests extends munit.FunSuite {
  import ModuleUtils.*

  test("nameWithoutScalaBinVersion") {
    assertEquals(
      nameWithoutScalaBinVersion("org.apache.pekko" % "pekko-actor_2.13" % "1.2.1"),
      "pekko-actor",
    )
    assertEquals(
      nameWithoutScalaBinVersion("org.apache.pekko" % "pekko-actor_3" % "1.2.1"),
      "pekko-actor",
    )
    assertEquals(
      nameWithoutScalaBinVersion("com.acme" % "sfraud_detection_2.13" % "1.0.0"),
      "sfraud_detection",
    )

    assertEquals(
      nameWithoutScalaBinVersion("org.slf4j" % "slf4j-api" % "2.0.17"),
      "slf4j-api",
    )
    assertEquals(
      nameWithoutScalaBinVersion("com.acme" % "acme_tools" % "1.0.0"),
      "acme_tools",
    )
  }
}
