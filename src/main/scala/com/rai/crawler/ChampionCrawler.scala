package com.rai.crawler

import akka.actor.{Props, ActorSystem}
import com.rai.api._

object ChampionCrawler {
  def main(args: Array[String]) {
    val system = ActorSystem("riotApi")
    val champion = system.actorOf(Props(new Champion))
    while(true) {
      champion ! GetAll()
    }
  }
}
