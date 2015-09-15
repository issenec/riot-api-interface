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


object RiotRetriever {
  implicit val timeout = Timeout(10.seconds)
  val conf = ConfigFactory.load("riot.conf")
  val key = conf.getString("dev.key")

  val sprayHttpClient = new SprayHttpClient
  val system = ActorSystem("riotApi")
  val riotActor = system.actorOf(Props(new Actor {
    def receive = {
      case ApiInputs(actor, url, params) =>
        sprayHttpClient.get(url, params, Map[String, String]()).map { res => actor ! res }
    }
  }))
  val throttler = system.actorOf(Props(new TimerBasedThrottler(8 msgsPer 10.seconds)))
  throttler ! SetTarget(Some(riotActor))

  def getData(actor: ActorRef, url: String, params: Map[String, String]) = {
    val msg = ApiInputs(actor, url, params + ("api_key" -> key))
    throttler ! msg
  }
}
