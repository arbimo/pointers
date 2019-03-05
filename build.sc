import mill._
import scalalib._
import $ivy.`ch.epfl.scala::mill-bloop:1.2.5`
import mill.define.Target
import mill.util.Loose


def gitBasedVersion = T.input {
  os.proc('git, "describe", "--tags").call().out.string.trim()
}


object deps {
  val kindProjector = ivy"org.spire-math::kind-projector:0.9.8"
  val acyclic = ivy"com.lihaoyi::acyclic:0.1.7"

  val minitest = ivy"io.monix::minitest:2.3.2"
  val minitestLaws = ivy"io.monix::minitest-laws:2.3.2"
}
import deps._

trait PointersModule extends ScalaModule {
  def scalaVersion = "2.12.8"
  def scalacOptions = build.scalacOptions

  override def compileIvyDeps = Agg(kindProjector, acyclic)
  override def scalacPluginIvyDeps = Agg(kindProjector, acyclic)

  trait MiniTests extends super.Tests {
    def ivyDeps = Agg(minitest, minitestLaws)
    def testFrameworks = Seq("minitest.runner.Framework")
  }
}



object core extends PointersModule {

}
object tests extends PointersModule with TestModule {
  override def moduleDeps = Seq(core)
  override def ivyDeps = Agg(minitest, minitestLaws)
  override def testFrameworks = Seq("minitest.runner.Framework")

}

object build extends Module {
  // mill bloop.integrations.mill.Bloop/install
  def bloopInstall(ev: mill.eval.Evaluator) = T.command { bloop.integrations.mill.Bloop.install(ev) }

  val scalacOptions = Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.

    // language features
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds",             // Allow higher-kinded types

    // warnings
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard",               // Warn when non-Unit expression results are unused.

    // Acyclic compiler plugin
    "-P:acyclic:force"
  )
}