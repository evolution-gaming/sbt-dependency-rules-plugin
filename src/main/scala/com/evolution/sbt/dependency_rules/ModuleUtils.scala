package com.evolution.sbt.dependency_rules

import sbt.ModuleID

/**
 * Internal utils for working with [[sbt.ModuleID]]
 */
private[dependency_rules] object ModuleUtils {
  private val ScalaBinVersionRegex = """[0-9][0-9.]*""".r

  /**
   * Formats [[sbt.ModuleID]] as `organization:name`
   */
  def fmtOrgWithName(moduleId: ModuleID): String = {
    s"${ moduleId.organization }:${ moduleId.name }"
  }

  /**
   * Formats [[sbt.ModuleID]] as `organization:name:version`
   */
  def fmtOrgWithNameWithVersion(moduleId: ModuleID): String = {
    s"${ moduleId.organization }:${ moduleId.name }:${ moduleId.revision }"
  }

  /**
   * Returns [[sbt.ModuleID]] name, stripping Scala binary version suffix, if it's there
   */
  def nameWithoutScalaBinVersion(moduleId: ModuleID): String = {
    val name = moduleId.name
    val tokens = moduleId.name.split('_')

    if (tokens.length == 1) {
      name
    } else if (!ScalaBinVersionRegex.pattern.matcher(tokens.last).matches()) {
      name
    } else {
      tokens.dropRight(1).mkString("_")
    }
  }
}
