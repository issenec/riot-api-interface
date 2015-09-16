package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetTeamsBySummoner(summonerIds: List[Long])
case class GetTeams(teamIds: List[Long])

/** Since I dont use begin/endTime or begin/endIndex yet, I haven't implemented them */
class Team extends RiotApi {
  val teamUrl = baseUri + "/api/lol/" + region + "/v2.4/team/"

  def receive = {
    case GetTeamsBySummoner(summonerIds) => getTeamsBySummoner(summonerIds)
    case GetTeams(teamIds) => getTeams(teamIds)
    case res: Response => printResponse(res)
  }

  def getTeamsBySummoner(summonerIds: List[Long]) = {
    val url = teamUrl + "by-summoner/" + summonerIds.mkString(",")
    RiotRetriever.getData(sender, url, params)
  }

  def getTeams(teamIds: List[Long]) = {
    val url = teamUrl + teamIds.mkString(",")
    RiotRetriever.getData(sender, url, params)
  }
}
