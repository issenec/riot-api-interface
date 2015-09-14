package com.rai.api

import com.rai.api.RankedMatchTypes._
import com.rai.api.Seasons._
import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetMatchlist(summonerId: Long, championIds: List[Int] = List(),
                        rankedQueues: List[RankedMatchTypes] = List(), seasons: List[Seasons] = List())

/** Since I dont use begin/endTime or begin/endIndex yet, I haven't implemented them */
class Matchlist extends RiotApi {
  def receive = {
    case GetMatchlist(summonerId, championIds, rankedQueues, seasons) =>
      getMatchlist(summonerId, championIds, rankedQueues, seasons)
    case res: Response => returnResults(res)
  }

  def getMatchlist(summonerId: Long, championIds: List[Int], rankedQueues: List[RankedMatchTypes],
                   seasons: List[Seasons]) = {
    val url = baseUri + "/api/lol/" + region + "/v2.2/matchlist/by-summoner/" + summonerId.toString
    if (!championIds.isEmpty)
      params += ("championIds" -> championIds.mkString(","))
    if (!rankedQueues.isEmpty)
      params += ("rankedQueues" -> rankedQueues.mkString(","))
    if (!seasons.isEmpty)
      params += ("seasons" -> seasons.mkString(","))
    RiotRetriever.getData(self, url, params)
  }
}
