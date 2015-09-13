package com.rai.api

import akka.actor.ActorRef

case class ApiInputs(actor: ActorRef, url: String, params: Map[String, String])
