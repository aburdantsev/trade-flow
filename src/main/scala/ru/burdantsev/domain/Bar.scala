package ru.burdantsev.domain

import java.sql.Timestamp
import java.time.Instant

sealed trait Bar {

  def contains(trade: Trade): Boolean

  def addTrade(trade: Trade, barSize: Long): Bar = this match {
    case EmptyBar => FilledBar(barStartTime(trade.time, barSize), barSize, trade.price, trade.price, trade.price, trade.price)
    case FilledBar(st, bs, o, h, l, c) => FilledBar(
      startTime = st,
      barSize = bs,
      o,
      math.max(trade.price, h),
      math.min(trade.price, l),
      trade.price
    )
  }

  private def barStartTime(tradeTime: Long, barSize: Long): Long =
    tradeTime - tradeTime % barSize
}

object Bar {
  def empty: Bar = EmptyBar
}

case object EmptyBar extends Bar {
  override def contains(trade: Trade): Boolean = true
}

case class FilledBar(startTime: Long = Long.MinValue,
                     barSize: Long = Long.MaxValue,
                     o: Double = Double.MinValue,
                     h: Double = Double.MinValue,
                     l: Double = Double.MaxValue,
                     c: Double = Double.MinValue) extends Bar {

  override def contains(trade: Trade): Boolean = trade.time >= this.startTime && trade.time < this.startTime + this.barSize

  override def toString: String =
    s"Bar(${Timestamp.from(Instant.ofEpochMilli(startTime))}, $barSize, O: $o, H: $h, L: $l, C: $c)"
}