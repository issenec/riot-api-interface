package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetLeaguesBySummoner(summonerIds: List[Long])
case class GetLeagueEntriesBySummoner(summonerIds: List[Long])
case class GetLeaguesByTeam(teamIds: List[Long])
case class GetLeagueEntriesByTeam(teamIds: List[Long])
case class GetChallengerLeague
case class GetMasterLeague

class League extends RiotApi {
  def receive = {
    case GetAll(freeToPlay) => getAll(freeToPlay)
    case GetById(champ) => getById(champ)
    case res: Response => returnResults(res)
  }

  def getAll(freeToPlay: Boolean) = {
    val url = base_uri + "/api/lol/" + region + "/v1.2/champion"
    if (freeToPlay)
      params += ("freeToPlay" -> "true")
    RiotRetriever.getData(self, url, params)
  }

  def getById(id: Int) = {
    val url = base_uri + "/api/lol/" + region + "/v1.2/champion/" + id.toString
    RiotRetriever.getData(self, url, params)
  }
}
