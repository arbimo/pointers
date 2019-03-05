name := "pointers"

version := "0.1"

scalaVersion := "2.13.0-M5"

libraryDependencies += "io.monix" %% "minitest" % "2.3.2" % "test"

testFrameworks += new TestFramework("minitest.runner.Framework")