package com.redbubble.hawk.params

import scala.collection.immutable.Seq

object HttpMethod {
  def httpMethod(method: String): Option[HttpMethod] = method.toUpperCase match {
    case Options.httpRequestLineMethod => Some(Options)
    case Connect.httpRequestLineMethod => Some(Connect)
    case Head.httpRequestLineMethod => Some(Head)
    case Get.httpRequestLineMethod => Some(Get)
    case Post.httpRequestLineMethod => Some(Post)
    case Put.httpRequestLineMethod => Some(Put)
    case Delete.httpRequestLineMethod => Some(Delete)
    case Trace.httpRequestLineMethod => Some(Trace)
    case Patch.httpRequestLineMethod => Some(Patch)
    case _ => None
  }

  /**
    * These HTTP methods support `hawk.1.payload` validation.
    */
  def payloadValidationMethods: Seq[HttpMethod] = List(Put, Post, Patch)
}

sealed trait HttpMethod {
  def httpRequestLineMethod: String
}

case object Options extends HttpMethod {
  override val httpRequestLineMethod = "OPTIONS"
}

case object Connect extends HttpMethod {
  override val httpRequestLineMethod = "CONNECT"
}

case object Head extends HttpMethod {
  override val httpRequestLineMethod = "HEAD"
}

case object Get extends HttpMethod {
  override val httpRequestLineMethod = "GET"
}

case object Post extends HttpMethod {
  override val httpRequestLineMethod = "POST"
}

case object Put extends HttpMethod {
  override val httpRequestLineMethod = "PUT"
}

case object Delete extends HttpMethod {
  override val httpRequestLineMethod = "DELETE"
}

case object Trace extends HttpMethod {
  override val httpRequestLineMethod = "TRACE"
}

case object Patch extends HttpMethod {
  override val httpRequestLineMethod = "PATCH"
}
