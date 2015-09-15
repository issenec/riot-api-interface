package com.rai.api

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.jackson.JsonMethods._
import uk.co.robinmurphy.http.Response

trait RiotApi extends Actor {
  val conf = ConfigFactory.load("riot.conf")
  val key = conf.getString("dev.key")
  val region = conf.getString("dev.region")
  val mySummonerId = conf.getString("dev.summoner.id")

  /** Starting and default values */
  val baseUri = "https://" + region + ".api.pvp.net"
  var params = Map[String, String]()

  def returnResults(res: Response) = {
    /** This parses using json4s, but will need to edit based on what crawler needs. Returning print for now.
      * Also need to check the status code and do error handling (400, 401, 429, 500, 503). */
    val json = parse(res.body)
    println(res.toString)
  }
}
