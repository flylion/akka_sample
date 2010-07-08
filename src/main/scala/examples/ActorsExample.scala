package examples

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
object ActorsExample{
	
	def main(args: Array[String]){
		val actor = actorOf[VanillaActor].start
		
		//fire and forget
		actor ! "fire-and-forget"
		
		//send and receive eventually
		val reply = actor !! "send-and-receive-eventually"
		reply match {
			case s @ Some(_) => println(s.get)
			case None => println("No response")
		}
		
		//send and receive eventually with a timeout
		val replyTwo = actor !! ("send-and-receive-eventually-with-timeout", 1000)
		println(replyTwo.getOrElse("response timed out"))
		
		//HotSwap
		actor ! HotSwap(Some({
			case _ => println("you've been HotSwapped!")
		}))
		actor ! "fire-and-forget"
		actor ! "11111"
		actor ! HotSwap(Some({
			case _ => println("you've been HotSwapped again!")
		}))
		actor ! "fire-and-forget"
		actor ! "2222"
		actor ! HotSwap(None)
		actor ! "fire-and-forget"
	
		actor.stop
	}
	
}

class VanillaActor extends Actor{
	
	def receive = {
		case "fire-and-forget" => println("fire-and-forget received")
		case "send-and-receive-eventually" => reply("right back at ya")
		case "send-and-receive-eventually-with-timeout" =>
			Thread.sleep(500)
			reply("right back at ya")
		case _ => println("unknown message received")
	}
	
}