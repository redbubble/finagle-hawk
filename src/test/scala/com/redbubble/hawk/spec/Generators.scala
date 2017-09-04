package com.redbubble.hawk.spec

import com.redbubble.hawk.util.Time._
import com.redbubble.hawk.util.{Millis, Seconds, Time}
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}

trait Generators {
  private lazy val now = nowUtc.asDateTime

  final val genHexChar: Gen[Char] =
    Gen.oneOf('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  final val genTime: Gen[Time] = Gen.chooseNum(now.getMillis, now.plusYears(100).getMillis).map(ms => Time(Millis(ms)))
  final val genMillis: Gen[Millis] = genTime.map(_.millis)
  final val genSeconds: Gen[Seconds] = genTime.map(_.asSeconds)

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)

  implicit def arbTime: Arbitrary[Time] = Arbitrary(genTime)

  final val genDateTime: Gen[DateTime] = genMillis.map(m => utcTime(m).asDateTime)

  implicit def arbDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)

  final def genHexCharsOfLength(n: Int): Gen[List[Char]] = for {x <- Gen.listOfN(n, genHexChar)} yield x

  final def genHexOfLength[T](n: Int)(f: String => T): Gen[T] = genHexCharsOfLength(n).map(cs => f(cs.mkString))
}

object Generators extends Generators
