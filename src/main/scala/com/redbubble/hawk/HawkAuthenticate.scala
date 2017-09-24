package com.redbubble.hawk

import com.redbubble.hawk.params._
import com.redbubble.hawk.parse.RequestAuthorisationHeaderParser
import com.redbubble.hawk.util.Time
import com.redbubble.hawk.validate.Base64Ops.base64Encode
import com.redbubble.hawk.validate.HawkMacValidator.{validate => validateMac}
import com.redbubble.hawk.validate.HawkTimeValidator.{validate => validateTime}
import com.redbubble.hawk.validate.Maccer._
import com.redbubble.hawk.validate._
import org.joda.time.Duration

sealed trait ValidationMethod {
  def identifier: String
}

case object HeaderValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.header"
}

case object PayloadValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.payload"
}

trait RequestValid

object RequestValid {
  val valid: RequestValid = new RequestValid {}
}

object HawkAuthenticate {
  /**
    * Parse a Hawk `Authorization` request header.
    */
  def parseRawRequestAuthHeader(header: RawAuthenticationHeader): Option[RequestAuthorisationHeader] =
    RequestAuthorisationHeaderParser.parseAuthHeader(header)

  /**
    * Authenticate an incoming request using Hawk.
    */
  def authenticateRequest(
      credentials: Credentials,
      context: ValidatableRequestContext,
      method: ValidationMethod,
      leeway: Duration
  ): Either[HawkError, RequestValid] =
    for {
      _ <- validateTime(credentials, context, method, leeway)
      _ <- validateMac(credentials, context, method)
    } yield RequestValid.valid

  /**
    * Authenticate an outging response into a form suitable for adding to a `Server-Authorization` header.
    */
  def authenticateResponse(credentials: Credentials, payload: Option[PayloadContext]): ServerAuthorisationHeader =
    throw new NotImplementedError

  /**
    * Constructs an authorisation request header, suitable for sending to a server that uses Hawk authentication.
    */
  def hawkHeader(
      credentials: Credentials,
      context: RequestContext,
      method: ValidationMethod = PayloadValidationMethod,
      extendedData: Option[ExtendedData] = None): RequestAuthorisationHeader = {
    val time = Time.nowUtc
    val nonce = Nonce.generateNonce
    val payloadHash = context.payload.map(p => PayloadHash(base64Encode(p.data)))
    val mac = computeRequestMac(credentials, time, nonce, context, extendedData)
    RequestAuthorisationHeader(credentials.keyId, time, nonce, payloadHash, extendedData, mac)
  }
}
