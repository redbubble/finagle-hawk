package com.redbubble.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import com.redbubble.hawk._
import com.redbubble.hawk.params.RequestContext
import com.redbubble.util.time.Time
import Time.nowUtc
import com.redbubble.hawk.{HawkError, ValidationMethod}
import org.joda.time.Duration

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  final val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, TimeValid] = {
    val delta = nowUtc.minus(context.clientAuthHeader.timestamp).getStandardSeconds
    (delta <= acceptableTimeDelta.getStandardSeconds).xor(error("Timestamp invalid"), new TimeValid {})
  }
}
