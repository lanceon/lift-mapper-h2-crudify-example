name := "Lift H2 CRUDify example"

version := "0.0.1"

organization := "com.webitoria"

scalaVersion := "2.10.0"

resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases" at "http://oss.sonatype.org/content/repositories/releases"
    )

seq(com.github.siasia.WebPlugin.webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "net.liftweb"       % "lift-mapper_2.10"    % liftVersion        % "compile",
    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"    % "logback-classic"     % "1.0.13",
    "com.h2database"    % "h2"                  % "1.3.172"
  )
}

port in container.Configuration := 8081

