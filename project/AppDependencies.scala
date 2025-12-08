import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.4.0"
  private val hmrcMongoVersion = "2.11.0"
  private val taxyearVersion   = "6.0.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"           % hmrcMongoVersion,
    "com.beachape"            %% "enumeratum-play-json"         % "1.9.1",
    "org.typelevel"           %% "cats-core"                    % "2.13.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-30" % "4.3.0",
    "uk.gov.hmrc.objectstore" %% "object-store-client-play-30"  % "2.5.0",
    "org.apache.xmlgraphics"   % "fop"                          % "2.11",
    "uk.gov.hmrc"             %% "tax-year"                     % taxyearVersion,
    "uk.gov.hmrc"             %% "crypto-json-play-30"          % "8.4.0",
    "commons-io"               % "commons-io"                   % "2.21.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-30"  % bootstrapVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.8"         % Test,
    "org.scalatest"       %% "scalatest"               % "3.2.19"         % Test,
    "org.scalacheck"      %% "scalacheck"              % "1.19.0"         % Test,
    "org.scalatestplus"   %% "scalacheck-1-17"         % "3.2.18.0"       % Test,
    "org.scalatestplus"   %% "mockito-4-11"            % "3.2.18.0"       % Test,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % Test,
    "org.apache.pdfbox"    % "pdfbox"                  % "2.0.35"         % Test
  )

  val itDependencies = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )
}
