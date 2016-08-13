package com.redbubble.hawk.validate

import cats.data.Xor
import com.redbubble.hawk.{HawkError, ValidationMethod}
import com.redbubble.hawk.HawkError
import com.redbubble.hawk.params.RequestContext

trait Validator[T] {
  def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, T]
}
