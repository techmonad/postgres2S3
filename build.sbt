ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val assemblySetting = Seq(
  assembly / assemblyJarName := "postgres2S3-" + (new java.text.SimpleDateFormat("dd-MM-yyyy_hh_mm").format(new java.util.Date)) + ".jar"
)

lazy val root = (project in file("."))
  .settings(
    name := "postgres2S3",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.5.0",
      "org.apache.spark" %% "spark-sql" % "3.3.1" % "provided"
    )
  )
  .settings(assemblySetting: _*)
