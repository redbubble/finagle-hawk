package com.redbubble.hawk.validate

import cats.syntax.either._
import com.redbubble.hawk.params.ValidatableRequestContext
import com.redbubble.hawk.validate.Maccer.validateAndComputeRequestMac
import com.redbubble.hawk.{HawkError, ValidationMethod, _}
import mouse.all._

trait MacValid

object MacValid {
  val valid: MacValid = new MacValid {}
}

object HawkMacValidator {
  def validate(credentials: Credentials, context: ValidatableRequestContext, method: ValidationMethod): Either[HawkError, MacValid] =
    validateAndComputeRequestMac(credentials, context, method).flatMap { computedMac =>
      validateMac(computedMac, context.clientAuthHeader.mac)
    }.leftMap(e => error(s"Request MAC does not match computed MAC: ${e.getMessage}"))

  private def validateMac(computedMac: MAC, providedMac: MAC): Either[HawkError, MacValid] =
    (computedMac == providedMac).either(error("Request MAC does not match computed MAC"), MacValid.valid)
}
