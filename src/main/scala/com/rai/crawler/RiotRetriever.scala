package com.rai.crawler

import akka.actor._
import akka.contrib.throttle.Throttler._
import akka.contrib.throttle.TimerBasedThrottler
import akka.util.Timeout
import com.rai.api.ApiInputs
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import uk.co.robinmurphy.http._


class RiotActor extends Actor {
  val sprayHttpClient = new SprayHttpClient

  def receive = {
    case ApiInputs(actor, url, params) => {
      sprayHttpClient.get(url, params, Map[String, String]()).map { res => actor ! res }
    }
  }
}


object RiotRetriever {
  implicit val timeout = Timeout(10.seconds)
  val conf = ConfigFactory.load("riot.conf")
  val key = conf.getString("dev.key")

  val system = ActorSystem("riotApi")
  val riotActor = system.actorOf(Props[RiotActor])
  val throttler = system.actorOf(Props(classOf[TimerBasedThrottler], 5 msgsPer 10.second))
  throttler ! SetTarget(Some(riotActor))

  def getData(actor: ActorRef, url: String, params: Map[String, String]) = {
    val msg = ApiInputs(actor, url, params + ("api_key" -> key))
    throttler ! msg
  }
}
