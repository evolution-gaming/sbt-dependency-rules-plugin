package com.evolution.sbt.dependency_rules

import com.evolution.sbt.dependency_rules.ModuleUtils.*
import sbt.*

import scala.collection.immutable

/**
 * [[DependencyRule]] classpath verification logic
 *
 * See the companion object for predefined verifiers and use them as examples when
 * writing a custom one.
 */
abstract class DependencyRuleLogic {

  /**
   * Performs the classpath verification.
   *
   * Must not throw exceptions!
   *
   * Rule violations should be reported with error strings inside `Either.Left`.
   *
   * @param dependencies
   *   classpath Maven/Ivy dependencies to verify
   */
  def verifyClasspath(dependencies: immutable.IndexedSeq[ModuleID]): Either[String, Unit]

  /**
   * Creates a new [[DependencyRuleLogic]] which uses the same verification logic but on a
   * filtered classpath.
   *
   * @param selector
   *   classpath selector to narrow the application of verification logic; sbt
   *   moduleFilter syntax can be used here
   *
   * @see
   *   [[https://www.scala-sbt.org/1.x/docs/Update-Report.html#ModuleFilter]]
   */
  final def narrowClasspath(selector: ModuleID => Boolean): DependencyRuleLogic = {
    new DependencyRuleLogic.Narrow(this, selector)
  }
}

private[dependency_rules] object DependencyRuleLogic {
  private final class Narrow(
    inner: DependencyRuleLogic,
    selector: ModuleID => Boolean,
  ) extends DependencyRuleLogic {
    override def verifyClasspath(dependencies: immutable.IndexedSeq[sbt.ModuleID]): Either[String, Unit] = {
      inner.verifyClasspath(dependencies.filter(selector))
    }
  }

  /**
   * [[DependencyRuleLogic]] prohibiting all dependencies on the classpath.
   *
   * Use it together with [[DependencyRuleLogic.narrowClasspath]] to ban specific
   * dependencies.
   *
   * @see
   *   [[DependencyRule.banned]]
   */
  object Banned extends DependencyRuleLogic {
    override def verifyClasspath(dependencies: immutable.IndexedSeq[sbt.ModuleID]): Either[String, Unit] = {
      if (dependencies.nonEmpty) {
        Left(s"banned: ${ dependencies.map(fmtOrgWithNameWithVersion).mkString(", ") }")
      } else {
        Right(())
      }
    }
  }

  /**
   * [[DependencyRuleLogic]] that requires all dependencies on the classpath to have the
   * same version.
   *
   * Use it together with [[DependencyRuleLogic.narrowClasspath]] to enforce it for
   * specific dependencies.
   *
   * @see
   *   [[DependencyRule.sameVersion]]
   */
  object SameVersion extends DependencyRuleLogic {
    override def verifyClasspath(dependencies: immutable.IndexedSeq[sbt.ModuleID]): Either[String, Unit] = {
      val versionToModules: Map[String, Seq[ModuleID]] = dependencies.groupBy(_.revision)

      if (versionToModules.size > 1) {
        Left(s"inconsistent module versions: ${ inconsistentVersionDescr(versionToModules) }")
      } else {
        Right(())
      }
    }
  }

  private def inconsistentVersionDescr(versionToModules: Map[String, Seq[ModuleID]]): String = {
    // sorting to print the version with the smallest number of artifacts first because it is most
    // likely the odd one
    val versionToModulesToPrint = versionToModules.toVector.sortBy { case (_, modules) => modules.size }

    versionToModulesToPrint.map {
      case (version, modules) =>
        val moduleListCroppedMaxSize = 2
        val modulesCroppedStr = modules.take(moduleListCroppedMaxSize).map(fmtOrgWithName).mkString(", ")
        val isModuleListCropped = modules.size > moduleListCroppedMaxSize
        val remModulesCount = modules.size - moduleListCroppedMaxSize
        s"$version ($modulesCroppedStr${ if (!isModuleListCropped) "" else s",... $remModulesCount more" })"
    }.mkString(", ")
  }
}
