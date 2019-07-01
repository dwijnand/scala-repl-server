val scalareplsrever = project in file(".")

organization in ThisBuild := "com.dwijnand"
     version in ThisBuild := "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.8"

libraryDependencies += "org.scala-sbt" %% "zinc" % "1.2.5"

run / connectInput := true
run / fork         := true
outputStrategy     := Some(StdoutOutput)

Global / cancelable := true
