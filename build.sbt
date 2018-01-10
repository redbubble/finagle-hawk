organization := "com.redbubble"

name := "finagle-hawk"

enablePlugins(GitVersioning, GitBranchPrompt)

git.useGitDescribe := true

bintrayOrganization := Some("redbubble")

bintrayRepository := "open-source"

bintrayPackageLabels := Seq("scala", "hawk", "hmac", "authentication", "finagle", "finch")

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint",
  //"-Yno-predef",
  //"-Ywarn-unused-import", // gives false positives
  "-Xfatal-warnings",
  "-Ywarn-value-discard",
  "-Ypartial-unification"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Twitter" at "http://maven.twttr.com",
  Resolver.bintrayRepo("redbubble", "open-source")
)

lazy val rbUtilsVersion = "0.2.10"
lazy val catsVersion = "1.0.1"
lazy val mouseVersion = "0.16"
lazy val shapelessVersion = "2.3.3"
lazy val circeVersion = "0.9.0"
lazy val finagleVersion = "17.12.0"
lazy val jodaTimeVersion = "2.9.9"
lazy val jodaConvertVersion = "1.9.2"
lazy val specsVersion = "4.0.2"

libraryDependencies ++= Seq(
  "com.redbubble" %% "rb-scala-utils" % rbUtilsVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "mouse" % mouseVersion,
  "com.chuusai" %% "shapeless" % shapelessVersion,
  "joda-time" % "joda-time" % jodaTimeVersion,
  "org.joda" % "joda-convert" % jodaConvertVersion,
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "finagle-stats" % finagleVersion,
  "org.specs2" %% "specs2-core" % specsVersion % "test",
  "org.specs2" %% "specs2-scalacheck" % specsVersion % "test"
)
