name := "pointers"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "io.monix" %% "minitest" % "2.3.2" % "test"

testFrameworks += new TestFramework("minitest.runner.Framework")