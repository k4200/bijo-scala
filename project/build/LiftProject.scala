import sbt._
import de.element34.sbteclipsify._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) with Eclipsify {
  val liftVersion = "2.2"
	  
  //https://repository.jboss.org/nexus/index.html#welcome
  //val jbossRepo = "JBoss Public" at "https://repository.jboss.org/nexus/content/groups/public"
  
  // uncomment the following if you want to use the snapshot repo
  // val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
//    "net.liftweb" %% "lift-oauth" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-oauth-mapper" % liftVersion % "compile->default",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "junit" % "junit" % "4.5" % "test->default",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.6" % "test->default",
    "com.h2database" % "h2" % "1.2.138",
    "net.databinder" %% "dispatch" % "0.7.8"
    //"com.sun.media" % "jai_imageio" % "1.1"
  ) ++ super.libraryDependencies
  
  //override val jettyPort = 80
}
