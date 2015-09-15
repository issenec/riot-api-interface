package com.rai.crawler

import akka.actor.{Props, ActorSystem}
import com.rai.api._

object AramCrawler {
  def main(args: Array[String]) {
    val system = ActorSystem("AramCrawler")
    val game = system.actorOf(Props(new Game))
    game ! GetMyRecentGames()

    /** Foreach summoner id:
      * 1) Get all "gameMode": "ARAM" games and take note of their "gameId". Call Match endpoint with this.
      * 2) Get all summonerIds in "gameMode": "ARAM". Add this to SUMMONERS_SET_TO_ITERATE if not COMPLETED
      *   2a) For summoner, get their rank?
      * 3) Get all relevant statistics as running count */

    /** Elasticsearch structures:
      * 1)  */
  }
}
