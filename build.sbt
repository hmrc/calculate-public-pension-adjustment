import uk.gov.hmrc.DefaultBuildSettings.itSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.5"

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(itSettings())
  .settings(libraryDependencies ++= AppDependencies.itDependencies)

lazy val microservice = Project("calculate-public-pension-adjustment", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin, ScalafmtPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s"
    ),
    PlayKeys.playDefaultPort := 12802
  )
  .settings(inConfig(Test)(testSettings): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(scoverageSettings)

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)

lazy val scoverageSettings =
  Seq(
    coverageExcludedPackages := """;uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*;testonly;uk\.gov\.hmrc\.calculatepublicpensionadjustment\.config;""",
    coverageExcludedFiles := "<empty>;.*javascript.*;.*Routes.*;.*testonly.*",
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageMinimumStmtTotal := 80
  )
