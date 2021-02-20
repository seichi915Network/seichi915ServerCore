ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "1.0.0"
ThisBuild / description := "seichi915Server コアプラグイン"

resolvers ++= Seq(
  "hub.spigotmc.org" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
  "oss.sonatype.org" at "https://oss.sonatype.org/content/repositories/snapshots",
  "maven.elmakers.com" at "https://maven.elmakers.com/repository/",
  "papermc.io" at "https://papermc.io/repo/repository/maven-public/",
  "maven.playpro.com" at "https://maven.playpro.com",
  "maven.enginehub.org" at "https://maven.enginehub.org/repo/",
  "repo.onarandombox.com" at "https://repo.onarandombox.com/content/repositories/multiverse/"
)

libraryDependencies ++= Seq(
  "com.destroystokyo.paper" % "paper-api" % "1.16.5-R0.1-SNAPSHOT",
  "net.coreprotect" % "coreprotect" % "19.4",
  "com.sk89q.worldguard" % "worldguard-bukkit" % "7.0.4",
  "com.onarandombox.multiversecore" % "Multiverse-Core" % "4.2.2",
  "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-effect" % "2.3.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", _ @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "plugin.yml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "config.yml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "Syntax.java" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "seichi915ServerCore",
    assemblyOutputPath in assembly := baseDirectory.value / "target" / "build" / s"seichi915ServerCore-${version.value}.jar"
  )
