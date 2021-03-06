package com.rai.api

import akka.actor.Actor
import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.source.StringDocumentSource
import com.typesafe.config.ConfigFactory
import org.elasticsearch.common.settings.ImmutableSettings
import uk.co.robinmurphy.http.Response


trait RiotApi extends Actor {
  val conf = ConfigFactory.load("riot.conf")
  val esConf = ConfigFactory.load("elasticsearch.conf")

  val key = conf.getString("dev.key")
  val region = conf.getString("dev.region")
  val mySummonerId = conf.getString("dev.summoner.id")

  val uri = ElasticsearchClientUri(conf.getString("es.address"))
  val settings = ImmutableSettings.settingsBuilder().put("cluster.name", conf.getString("es.cluster")).build()
  val esClient = ElasticClient.remote(settings, uri)

  /** Starting and default values */
  val baseUri = "https://" + region + ".api.pvp.net"
  var params = Map[String, String]()

  def printResponse(res: Response) = {
    println(res.toString)
  }

  def sendToEs(res: Response, recordId: String, dataType: String) = {
    val indexLocation = esConf.getString("es.index") + "/" + dataType
    esClient.execute {
      index into indexLocation id recordId doc StringDocumentSource(res.body)
    }
  }
}
