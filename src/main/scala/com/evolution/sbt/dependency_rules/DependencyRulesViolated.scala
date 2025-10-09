package com.evolution.sbt.dependency_rules

import sbt.FeedbackProvidedException

import scala.util.control.NoStackTrace

/**
 * Fails the [[DependencyRulesPlugin.autoImport.dependencyRulesCheck]] task if there are
 * rule violations found
 */
final class DependencyRulesViolated private[dependency_rules] (message: String)
extends RuntimeException(message)
with FeedbackProvidedException // so the exception is printed only once by sbt
with NoStackTrace
