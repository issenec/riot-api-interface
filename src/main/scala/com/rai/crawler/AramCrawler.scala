package com.rai.crawler

import akka.actor.{Props, ActorSystem}
import com.rai.api._

object AramCrawler {
  def main(args: Array[String]) {
    val system = ActorSystem("AramCrawler")
    val game = system.actorOf(Props(new Game))
    game ! GetMyRecentGames()

    /** Foreach summoner id:
      * 1) Get all "gameMode": "ARAM" games and take note of their "gameId". Call Match endpoint with this. SendToES!
      * 2) Get all summonerIds in "gameMode": "ARAM". Add this to SUMMONERS_SET_TO_ITERATE if not COMPLETED.
      *   2a) For summoner, get their rank?
      * 3) Get all relevant statistics as running count */

    /** Gather algorithm:
      * 1  GetMyRecentGames => Response = res
      * 2  for game in res.body.games:
      * 3    if game["gameMode"] = "ARAM":
      * 4      GetMatch(game["gameId"], true) => saveToEs(aram)
      * 5      for player in game["fellowPlayers"]:
      * 6        GetSummaryFor(player) => saveToEs(summoner)
      * 7        GetRecentGames(player) => goto 2
      */
  }
}
