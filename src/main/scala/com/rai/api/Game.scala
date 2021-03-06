package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetMyRecentGames()
case class GetRecentGames(summonerId: Long)

class Game extends RiotApi {
  def receive = {
    case GetMyRecentGames() => getMyRecentGames()
    case GetRecentGames(summonerId) => getRecentGames(summonerId)
    case res: Response => printResponse(res)
  }

  def getMyRecentGames() = {
    val url = baseUri + "/api/lol/" + region + "/v1.3/game/by-summoner/" + mySummonerId + "/recent"
    RiotRetriever.getData(sender, url, params)
  }

  def getRecentGames(summonerId: Long) = {
    val url = baseUri + "/api/lol/" + region + "/v1.3/game/by-summoner/" + summonerId.toString + "/recent"
    RiotRetriever.getData(sender, url, params)
  }
}
