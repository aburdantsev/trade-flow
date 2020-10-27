package ru.burdantsev.domain

import java.sql.Timestamp
import java.time.Instant

import scala.util.Random

case class Trade(time: Long, price: Double) {

  def generateNext: Trade = {
    val coeff = Random.nextInt(100).toDouble / 10000

    val nextPrice = if (Random.nextBoolean)
      this.price * (1 + coeff)
    else
      this.price * (1 - coeff)

    Trade(
      this.time + Random.nextInt(500),
      (nextPrice * 100).ceil / 100
    )
  }

  override def toString: String = s"Trade(${Timestamp.from(Instant.ofEpochMilli(time))}, $price)"
}

object Trade {
  def first(startTime: Long): Trade = Trade(startTime, Random.nextInt(100).toDouble)

  val empty: Trade = Trade(Long.MinValue, Double.MinValue)
}