package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetMatch(matchId: Long, includeTimeline: Boolean = false)

class Match extends RiotApi {
  def receive = {
    case GetMatch(matchId, includeTimeline) => getMatch(matchId, includeTimeline)
    case res: Response => printResponse(res)
  }

  def getMatch(matchId: Long, includeTimeline: Boolean) = {
    val url = baseUri + "/api/lol/" + region + "/v2.2/match/" + matchId.toString
    if (includeTimeline)
      params += ("includeTimeline" -> "true")
    RiotRetriever.getData(sender, url, params)
  }
}
