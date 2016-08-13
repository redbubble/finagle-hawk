package com.redbubble

import cats.data.Xor
import cats.data.Xor._
import shapeless.tag
import shapeless.tag.@@

package object hawk {
  type KeyId = String @@ KeyIdTag
  type Key = String @@ KeyTag
  type Nonce = String @@ NonceTag
  type PayloadHash = String @@ PayloadHashTag
  type ExtendedData = String @@ ExtendedDataTag
  type RawAuthenticationHeader = String @@ RawAuthenticationHeaderTag
  type HeaderKeyValue = String @@ HeaderKeyValueTag
  type HeaderKey = String @@ HeaderKeyTag
  type HeaderValue = String @@ HeaderValueTag
  type Base64Encoded = String @@ Base64EncodedTag

  val MustAuthenticateHttpHeader = "WWW-Authenticate"
  val AuthorisationHttpHeader = "Authorization"
  val HawkHeaderValuePrefix = "Hawk"

  def KeyId(s: String): @@[String, KeyIdTag] = tag[KeyIdTag](s)

  def Key(s: String): @@[String, KeyTag] = tag[KeyTag](s)

  def Nonce(s: String): @@[String, NonceTag] = tag[NonceTag](s)

  def PayloadHash(s: String): @@[String, PayloadHashTag] = tag[PayloadHashTag](s)

  def ExtendedData(s: String): @@[String, ExtendedDataTag] = tag[ExtendedDataTag](s)

  def RawAuthenticationHeader(s: String): @@[String, RawAuthenticationHeaderTag] = tag[RawAuthenticationHeaderTag](s)

  def HeaderKeyValue(s: String): @@[String, HeaderKeyValueTag] = tag[HeaderKeyValueTag](s)

  def HeaderKey(s: String): @@[String, HeaderKeyTag] = tag[HeaderKeyTag](s)

  def HeaderValue(s: String): @@[String, HeaderValueTag] = tag[HeaderValueTag](s)

  def Base64Encoded(s: String): @@[String, Base64EncodedTag] = tag[Base64EncodedTag](s)

  def errorXor[T](message: String): Xor[HawkError, T] = left(error(message))

  def error(message: String): HawkError = new HawkError(message)
}
