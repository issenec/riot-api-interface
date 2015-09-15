package com.rai.api

import com.rai.api.MatchTypes._
import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

/** NOTE: I know that the following messages are in the Riot API.
  * However, I don't really have a need for them. If someone needs them,
  * contact me @ the Riot API forums (Randomodnar)
  */
//case class GetLeaguesBySummoner(summonerIds: List[Long])
//case class GetLeagueEntriesBySummoner(summonerIds: List[Long])
//case class GetLeaguesByTeam(teamIds: List[Long])
//case class GetLeagueEntriesByTeam(teamIds: List[Long])

/** Valid values are RANKED_SOLO_5x5, RANKED_TEAM_3x3, RANKED_TEAM_5x5 */
case class GetMasterLeague(leagueType: MatchTypes = RANKED_SOLO_5X5)
case class GetChallengerLeague(leagueType: MatchTypes = RANKED_SOLO_5X5)


class League extends RiotApi {
  val leagueUrl = baseUri + "/api/lol/" + region + "/v2.5/league/"

  def receive = {
    case GetChallengerLeague(leagueType) => getChallengerLeague(leagueType)
    case GetMasterLeague(leagueType) => getMasterLeague(leagueType)
    case res: Response => returnResults(res)
  }

  def getMasterLeague(leagueType: MatchTypes) = {
    val url = leagueUrl + "master"
    /** Need to switch to String */
    params += ("type" -> leagueType.toString)
    RiotRetriever.getData(self, url, params)
  }

  def getChallengerLeague(leagueType: MatchTypes) = {
    val url = leagueUrl + "challenger"
    params += ("type" -> leagueType.toString)
    RiotRetriever.getData(self, url, params)
  }
}
