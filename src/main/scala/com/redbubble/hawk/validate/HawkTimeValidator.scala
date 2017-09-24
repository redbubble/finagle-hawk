package com.redbubble.hawk.validate

import com.redbubble.hawk.params.ValidatableRequestContext
import com.redbubble.hawk.util.Time.nowUtc
import com.redbubble.hawk.{HawkError, _}
import mouse.all._
import org.joda.time.Duration

trait TimeValid

object TimeValid {
  val valid: TimeValid = new TimeValid {}
}

object HawkTimeValidator {
  final val DefaultLeeway: Duration = Duration.standardMinutes(2)

  def validate(credentials: Credentials, context: ValidatableRequestContext, leeway: Duration): Either[HawkError, TimeValid] = {
    val delta = nowUtc.minus(context.clientAuthHeader.timestamp).getStandardSeconds
    (delta <= leeway.getStandardSeconds).either(error("Timestamp invalid"), TimeValid.valid)
  }
}
