package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetCurrentGameFor(platformId: String, summonerId: Long)

class CurrentGame extends RiotApi {
  def receive = {
    case GetCurrentGameFor(platformId, summonerId) => getCurrentGameFor(platformId, summonerId)
    case res: Response => printResponse(res)
  }

  def getCurrentGameFor(platformId: String, summonerId: Long) = {
    val url = baseUri + "/observer-mode/rest/consumer/getSpectatorGameInfo/" + platformId + "/" + summonerId.toString
    RiotRetriever.getData(self, url, params)
  }
}
