package examples

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor.ActiveObject._
import se.scalablesolutions.akka.config.ScalaConfig._
import se.scalablesolutions.akka.config._
import se.scalablesolutions.akka.remote.RemoteServer

import java.net.URL;  


/**
This shows off supervisor actors.  A worker attempts an HTTP GET request from Worker.productionHostOne, which results in a
java.net.ConnectException.  The supervisor sees this and restarts the worker, which then switches hosts to Worker.productionHostTwo,
which should work.

In order for this example to work your must have two entries in your hosts file.  prod-1 should point somewhere that won't accept a connection,
and prod-2 should point somewhere that will.  Here is my hosts file:

216.34.181.45	prod-2 #this is slashdot.org's ip
127.0.0.1	prod-1 # I have nothing running on port 80 on my local machine

**/
object FaultToleranceExample{
	
	def main(args: Array[String]){
		
		//start the supervisor
		val supervisor = actorOf[MySupervisor].start
		
		//start the worker
		val worker = actorOf[Worker].start
		
		//link the supervisor and worker
		supervisor ! SuperviseMe(worker)
		
		//send a Get message, which will fail
		worker ! Get
		
		//wait for the failure to pass and the host to change
		Thread.sleep(5000)
		
		//attempt the GET again, which will work
		worker ! Get
		
		//stop the actors
		worker.stop
		supervisor.stop
		()
	}
	
}

case object Get{}

//the companion class for the worker that has two hosts used by the worker
object Worker{
	val productionHostOne = "http://prod-1"
	val productionHostTwo = "http://prod-2"
}

//actor that receives only one message (Get), which prints out the results of an HTTP GET request to the console
class Worker extends Actor{
	import self._
	private var host = Worker.productionHostOne

	//setup the lifeCycle, which is Permanent, meaning on any fault, it will be restarted
	lifeCycle = Some(LifeCycle(Permanent))
	
	def receive = {
		case Get => 
		println(scala.io.Source.fromURL(new URL(host)).mkString)
	}
	
	//this method is called before the supervisor restarts the actor. 
	override def preRestart(reason: Throwable) = {
		reason match {
			case e: java.net.UnknownHostException => 
				println("UnknownHostException encountered, changing host")
			case _ => println("**************************"+"unknown exception")
		}
	}

	//this method is called after the actor is restarted - we change the host here and print it.
	override def postRestart(reason: Throwable) {
		host = Worker.productionHostTwo
	 	println("**************************"+"Worker Restarted:"+host)
	}
	
}

case class SuperviseMe(worker: ActorRef){}

//this class supervises actors.  It accepts the SuperviseMe message which will link the passed actor to the supervisor instance
class MySupervisor extends Actor{
	    import self._
		//this supervisor will trap all exceptions and restart the actor
		trapExit = List(classOf[java.net.UnknownHostException])
		
		//the OneForOneStrategy means that only the actor that threw the exception will be restarted
		//if the AllForOneStrategy had been selected then this supervisor would have restarted all of its actors
		faultHandler = Some(OneForOneStrategy(3, 1000))
		def receive = {
			case SuperviseMe(worker: ActorRef) => link(worker)
			case _ => println("***********nothing!")
		}
	
}