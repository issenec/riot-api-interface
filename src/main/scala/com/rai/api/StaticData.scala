package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetChampions(dataById: Boolean = false, version: String = "5.17.1", champData: Boolean = false)
case class GetChampionById(id: Int, version: String = "5.17.1", champData: Boolean = false)
case class GetItems(version: String = "5.17.1", itemListData: Boolean = false)
case class GetItemById(id: Int, version: String = "5.17.1", itemData: Boolean = false)
case class GetMap(version: String = "5.17.1")
case class GetMasteries(version: String = "5.17.1", masteryListData: Boolean = false)
case class GetMasteryById(id: Int, version: String = "5.17.1", masteryData: Boolean = false)
case class GetRealm()  /** Could use to get current DDragon version */
case class GetRunes(version: String = "5.17.1", runeListData: Boolean = false)
case class GetRuneById(id: Int, version: String = "5.17.1", runeData: Boolean = false)
case class GetSummonerSpells(dataById: Boolean = false, version: String = "5.17.1", spellData: Boolean = false)
case class GetSummonerSpellById(id: Int, version: String = "5.17.1", spellData: Boolean = false)
case class GetVersions()


/** NOTE: There is a 'locale' argument, but I don't use it.
  * The 'version' argument is currently hard-coded to 5.17.1.
  * championData, runeData, and spellData have a lot of options. I only provide one: 'all'.
  * As a result, I converted it to a Boolean. If you need a specific field, call 'all' and then get it.
  * Since I have no possible use for languages, I dont have the endpoint for language-strings and languages. */
class StaticData extends RiotApi {
  val staticUrl = "https://global.api.pvp.net/api/lol/static-data/" + region + "/v1.2/"

  def receive = {
    case GetChampions(dataById, version, champData) => getChampions(dataById, version, champData)
    case GetChampionById(id, version, champData) => getChampionById(id, version, champData)
    case GetItems(version, itemListData) => getItems(version, itemListData)
    case GetItemById(id, version, itemData) => getItemById(id, version, itemData)
    case GetMap(version) => getMap(version)
    case GetMasteries(version, masteryListData) => getMasteries(version, masteryListData)
    case GetMasteryById(id, version, masteryData) => getMasteryById(id, version, masteryData)
    case GetRealm => getRealm
    case GetRunes(version, runeListData) => getRunes(version, runeListData)
    case GetRuneById(id, version, runeData) => getRuneById(id, version, runeData)
    case GetSummonerSpells(dataById, version, spellData) => getSummonerSpells(dataById, version, spellData)
    case GetSummonerSpellById(id, version, spellData) => getSummonerSpellById(id, version, spellData)
    case GetVersions => getVersions
    case res: Response => printResponse(res)
  }

  def getChampions(dataById: Boolean, version: String, champData: Boolean) = {
    val url = staticUrl + "champion"
    if (dataById)
      params += ("dataById" -> "true")
    params += ("version" -> version)
    if (champData)
      params += ("champData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getChampionById(id: Int, version: String, champData: Boolean) = {
    val url = staticUrl + "champion/"  + id.toString
    params += ("version" -> version)
    if (champData)
      params += ("champData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getItems(version: String, itemListData: Boolean) = {
    val url = staticUrl + "item"
    params += ("version" -> version)
    if (itemListData)
      params += ("itemListData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getItemById(id: Int, version: String, itemData: Boolean) = {
    val url = staticUrl + "item/"  + id.toString
    params += ("version" -> version)
    if (itemData)
      params += ("itemData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getMap(version: String) = {
    val url = staticUrl + "map"
    params += ("version" -> version)
    RiotRetriever.getData(self, url, params)
  }

  def getMasteries(version: String, masteryListData: Boolean) = {
    val url = staticUrl + "mastery"
    params += ("version" -> version)
    if (masteryListData)
      params += ("masteryListData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getMasteryById(id: Int, version: String, masteryData: Boolean) = {
    val url = staticUrl + "mastery/"  + id.toString
    params += ("version" -> version)
    if (masteryData)
      params += ("masteryData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getRealm = {
    val url = staticUrl + "realm"
    RiotRetriever.getData(self, url, params)
  }

  def getRunes(version: String, runeListData: Boolean) = {
    val url = staticUrl + "rune"
    params += ("version" -> version)
    if (runeListData)
      params += ("runeListData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getRuneById(id: Int, version: String, runeData: Boolean) = {
    val url = staticUrl + "rune/"  + id.toString
    params += ("version" -> version)
    if (runeData)
      params += ("runeData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getSummonerSpells(dataById: Boolean, version: String, spellData: Boolean) = {
    val url = staticUrl + "summoner-spell"
    if (dataById)
      params += ("dataById" -> "true")
    params += ("version" -> version)
    if (spellData)
      params += ("spellData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getSummonerSpellById(id: Int, version: String, spellData: Boolean) = {
    val url = staticUrl + "summoner-spell/"  + id.toString
    params += ("version" -> version)
    if (spellData)
      params += ("spellData" -> "all")
    RiotRetriever.getData(self, url, params)
  }

  def getVersions = {
    val url = staticUrl + "versions"
    RiotRetriever.getData(self, url, params)
  }
}
