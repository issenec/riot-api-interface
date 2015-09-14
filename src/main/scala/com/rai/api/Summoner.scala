package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetObjectsByName(summonerNames: List[String])
case class GetObjectsById(summonerIds: List[Long])
case class GetMasteries(summonerIds: List[Long])
case class GetNames(summonerIds: List[Long])
case class GetRunes(summonerIds: List[Long])

/** Since I dont use begin/endTime or begin/endIndex yet, I haven't implemented them */
class Summoner extends RiotApi {
  val summonerUrl = baseUri + "/api/lol/" + region + "/v1.4/summoner/"

  def receive = {
    case GetObjectsByName(summonerNames) => getObjectsByName(summonerNames)
    case GetObjectsById(summonerIds) => getObjectsById(summonerIds)
    case GetMasteries(summonerIds) => getMasteries(summonerIds)
    case GetNames(summonerIds) => getNames(summonerIds)
    case GetRunes(summonerIds) => getRunes(summonerIds)
    case res: Response => returnResults(res)
  }

  def getObjectsByName(summonerNames: List[String]) = {
    val url = summonerUrl + "by-name/" + summonerNames.mkString(",")
    RiotRetriever.getData(self, url, params)
  }

  def getObjectsById(summonerIds: List[Long]) = {
    val url = summonerUrl + summonerIds.mkString(",")
    RiotRetriever.getData(self, url, params)
  }

  def getMasteries(summonerIds: List[Long]) = {
    val url = summonerUrl + summonerIds.mkString(",") + "/masteries"
    RiotRetriever.getData(self, url, params)
  }

  def getNames(summonerIds: List[Long]) = {
    val url = summonerUrl + summonerIds.mkString(",") + "/name"
    RiotRetriever.getData(self, url, params)
  }

  def getRunes(summonerIds: List[Long]) = {
    val url = summonerUrl + summonerIds.mkString(",") + "/runes"
    RiotRetriever.getData(self, url, params)
  }
}
