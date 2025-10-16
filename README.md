# sbt-dependency-rules-plugin

SBT plugin for enforcing rules on project dependencies.

[![Build Status](https://github.com/evolution-gaming/sbt-dependency-rules-plugin/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/evolution-gaming/sbt-dependency-rules-plugin/actions/workflows/ci.yml?query=branch%3Amain)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.evolution/sbt-dependency-rules-plugin_2.12_1.0)](https://central.sonatype.com/artifact/com.evolution/sbt-dependency-rules-plugin_2.12_1.0)

SBT version support:

| Supported major SBT version | Built against concrete SBT Version | Build JDK version |
|-----------------------------|------------------------------------|-------------------|
| 1                           | 1.4.5                              | 1.8               |
| 2                           | 2.0.0-RC6                          | 1.8               |

If your SBT version is earlier than the build SBT version, the plugin might still work, but the support is not
guaranteed.

1. [Getting Started](#getting-started)
2. [Recipes](#recipes)
3. [How it Works](#how-it-works)
4. [For Contributors](#for-contributors)
5. [Reference](#reference)

## Getting Started

### 1. Installation

To get started, add the following line to your `project/plugins.sbt`:

```scala
addSbtPlugin("com.evolution" % "sbt-dependency-rules-plugin" % "LATEST_VERSION")
```

Replace `LATEST_VERSION` with the latest version of the plugin, which you can find
on [Maven Central](https://central.sonatype.com/artifact/com.evolution/sbt-dependency-rules-plugin_2.12_1.0).

The plugin is an `AutoPlugin`, so it is automatically enabled.

### 2. Adding Rules

By default, the rule set for dependencies is empty. Let's add some!

Add a wildcard import to the top of your `build.sbt` to get access to all the plugin functionality:

```scala
import com.evolution.sbt.dependency_rules.*
```

Supposing you have a Pekko-based project, let us add some rules to all the SBT modules in the project:

```scala
ThisBuild / dependencyRules ++= Vector(
  // Ensure all Pekko dependencies have the same version to avoid annoying runtime errors!
  ExtraDependencyRules.Pekko.ModulesHaveSameVersion,
  // Ensure we do not get accidental transitive Akka dependencies
  ExtraDependencyRules.Akka.Banned,
)
```

### 3. Checking for Violations

To check if your project's dependencies violate any of the defined rules, run the following task from your SBT console:

```
sbt dependencyRulesCheck
```

If there are any violations, the build will fail with an error message detailing the violations:

```shell
[error] [myPekkoApp][compile][AkkaBanned] banned: com.typesafe.akka:akka-actor_2.13:2.6.21
[error] (dependencyRulesCheck) com.evolution.sbt.dependency_rules.DependencyRulesViolated: AkkaBanned:compile
```

Here we have the `AkkaBanned` rule violated in the `compile` scope in the `myPekkoApp` module.

### 4. Investigating Violations

Use the
trusty [SBT dependency tree functionality](https://www.baeldung.com/scala/sbt-dependency-tree#sbt-dependency-tree)!

```shell
sbt "myPekkoApp/whatDependsOn com.typesafe.akka akka-actor_2.13 2.6.21"
```

## Recipes

### How to Prohibit a Library Dependency

You can ban a library entirely from your project.
For example, to ban the Apache Log4j 1 library, you can define a rule like this:

```scala
import com.evolution.sbt.dependency_rules.*
import sbt.*

ThisBuild / dependencyRules += DependencyRule.banned(
  name = "NoLog4j1",
  // selector works on module coordinates: organization, name, revision
  // see SBT documentation for moduleFilter syntax: https://www.scala-sbt.org/1.x/docs/Update-Report.html#ModuleFilter
  selector = moduleFilter(organization = "log4j")
)
```

### How to Ensure Consistent Versions of Modules for a Library

Let's say you want to ensure that all Typelevel Cats modules have the same version:

```scala
import com.evolution.sbt.dependency_rules.*
import sbt.*

ThisBuild / dependencyRules += DependencyRule.sameVersion(
  name = "CatsModulesHaveSameVersion",
  // selector works on module coordinates: organization, name, revision
  // see SBT documentation for moduleFilter syntax: https://www.scala-sbt.org/1.x/docs/Update-Report.html#ModuleFilter
  selector = moduleFilter(
    organization = "org.typelevel",
    name = "cats-*",
  ),
)
```

### How to Customize the Scope of a Rule

By default, rules are checked against the `Compile` and `Test` configurations. You can customize the scope of a rule
using the `withScope` or `modifyScope` methods:

```scala
import com.evolution.sbt.dependency_rules.*
import sbt.*

ThisBuild / dependencyRules += ExtraDependencyRules.Pekko.ModulesHaveSameVersion.modifyScope(_ - Test)
```

## How it Works

The `sbt-dependency-rules-plugin` works by analyzing the classpath of your project for each configuration (e.g.,
`Compile`, `Test`). It then applies the defined rules to the resolved dependencies.

Each rule has a `logic` component that performs the actual verification. If the logic finds a violation, it reports an
error, and the `dependencyRulesCheck` task fails.

The plugin comes with a set of predefined rules in `ExtraDependencyRules` for common use cases, and you can
create your own custom rules.

## For Contributors

### Build Requirements

The GitHub Actions CI build is done using JDK 1.8, but locally you can use any JDK up to 17 (including).

### Useful SBT commands

To reformat code using scalafmt, run:

```shell
sbt fmt
```

Code formatting is verified in build commands, and PRs with malformed code will not be accepted.

A fast build without `scripted` tests can be run with:

```shell
sbt buildFast
```

A full build with `scripted` tests can be run with:

```shell
sbt buildFull
```

### How to Make a Release

- Being on the up-to-date main branch, create a release tag:

```shell
git tag v1.2.3
```

- Push the tag:

```shell
git push origin v1.2.3
```

- This will trigger the release GitHub Action.
  If it succeeds, the release ends up on Maven Central with GitHub release notes generated automatically from PRs info.
- If the release GitHub Action fails, the tag will be deleted on remote.
  After deleting the tag locally, fix the main branch and do the process again:

```shell
git tag -d v1.2.3
```

## Reference

### Settings

* `dependencyRules: SettingKey[Seq[DependencyRule]]`

  Dependency rules to be enforced.

### Tasks

* `dependencyRulesCheck: TaskKey[Unit]`

  Check the project's dependencies against the rules.

### Predefined Rules

The `ExtraDependencyRules` object provides the following predefined rules:

* `ExtraDependencyRules.Akka.ModulesHaveSameVersion`: Ensures all Akka modules have the same version.
* `ExtraDependencyRules.Akka.Banned`: Bans all Akka modules.
* `ExtraDependencyRules.Pekko.ModulesHaveSameVersion`: Ensures all Pekko modules have the same version.
* `ExtraDependencyRules.Pekko.Banned`: Bans all Pekko modules.

### Creating Custom Rules

You can create custom rules using the factory methods in the `DependencyRule` companion object:

* `DependencyRule.sameVersion(name: String, scope: Set[Configuration], selector: ModuleID => Boolean)`: Requires all
  selected dependencies to have the same version.
* `DependencyRule.banned(name: String, scope: Set[Configuration], selector: ModuleID => Boolean)`: Prohibits selected
  dependencies.
* `DependencyRule(name: String, scope: Set[Configuration], logic: DependencyRuleLogic)`: Creates a rule with custom
  logic.