import org.typelevel.sbt.gha.WorkflowStep.Run
import org.typelevel.sbt.gha.WorkflowStep.Sbt

val Scala213 = "2.13.16"
val Scala3 = "3.3.6"

ThisBuild / githubOwner := "igor-ramazanov-typelevel"
ThisBuild / githubRepository := "vault"

ThisBuild / githubWorkflowPublishPreamble := List.empty
ThisBuild / githubWorkflowUseSbtThinClient := true
ThisBuild / githubWorkflowPublish := List(
  Run(
    commands = List("echo \"$PGP_SECRET\" | gpg --import"),
    id = None,
    name = Some("Import PGP key"),
    env = Map("PGP_SECRET" -> "${{ secrets.PGP_SECRET }}"),
    params = Map(),
    timeoutMinutes = None,
    workingDirectory = None
  ),
  Sbt(
    commands = List("+ publish"),
    id = None,
    name = Some("Publish"),
    cond = None,
    env = Map("GITHUB_TOKEN" -> "${{ secrets.GB_TOKEN }}"),
    params = Map.empty,
    timeoutMinutes = None,
    preamble = true
  )
)
ThisBuild / gpgWarnOnFailure := false

ThisBuild / tlBaseVersion := "3.7"
ThisBuild / crossScalaVersions := Seq(Scala3, Scala213)
ThisBuild / tlVersionIntroduced := Map("3" -> "3.0.3")
ThisBuild / tlMimaPreviousVersions ~= (_.filterNot(_ == "3.2.0"))
ThisBuild / licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))
ThisBuild / startYear := Some(2021)
ThisBuild / tlSiteApiUrl := Some(url("https://www.javadoc.io/doc/org.typelevel/vault_2.13/latest/org/typelevel/vault/"))

ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)

val JDK8 = JavaSpec.temurin("8")
val JDK17 = JavaSpec.temurin("17")

ThisBuild / githubWorkflowJavaVersions := Seq(JDK8, JDK17)

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "vault",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core" % catsV,
      "org.typelevel" %%% "cats-effect" % catsEffectV,
      "org.typelevel" %%% "cats-laws" % catsV % Test,
      "org.typelevel" %%% "discipline-munit" % disciplineMunitV % Test,
      "org.typelevel" %%% "scalacheck-effect-munit" % scalacheckEffectV % Test,
      "org.typelevel" %%% "munit-cats-effect" % munitCatsEffectV % Test
    ),
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
  .nativeSettings(
    tlVersionIntroduced := List("2.13", "3").map(_ -> "3.2.2").toMap
  )

val catsV = "2.13.0"
val catsEffectV = "3.7-4972921"
val disciplineMunitV = "2.0.0"
val scalacheckEffectV = "2.1.0-M1"
val munitCatsEffectV = "2.2.0-M1"
val kindProjectorV = "0.13.3"

// Scalafmt
addCommandAlias("fmt", "; Compile / scalafmt; Test / scalafmt; scalafmtSbt")
addCommandAlias("fmtCheck", "; Compile / scalafmtCheck; Test / scalafmtCheck; scalafmtSbtCheck")
