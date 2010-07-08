import sbt._

class AkkaMeetupProject(info: ProjectInfo) extends DefaultProject(info){
    val guiceyfruit = "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
    val databinder = "DataBinder" at "http://databinder.net/repo"
    val configgy = "Configgy" at "http://www.lag.net/repo"
    val multiverse = "Multiverse" at "http://multiverse.googlecode.com/svn/maven-repository/snapshots"
	val multiverseReleases = "Multiverse Releases" at "http://multiverse.googlecode.com/svn/maven-repository/releases/"
    val jBoss = "jBoss" at "http://repository.jboss.org/maven2"
    val akka_repo = "Akka Maven Repository" at "http://scalablesolutions.se/akka/repository"

    val akka = "se.scalablesolutions.akka" % "akka-core_2.7.7" % "0.7.1"
    val akka_redis = "se.scalablesolutions.akka" % "akka-persistence-redis_2.7.7" % "0.7.1"
    val akka_util = "se.scalablesolutions.akka" % "akka-util_2.7.7" % "0.7.1"
}