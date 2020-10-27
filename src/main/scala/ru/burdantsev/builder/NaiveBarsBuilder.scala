package ru.burdantsev.builder

import ru.burdantsev.domain.{FilledBar, Trade}

class NaiveBarsBuilder(barSize: Long) {

  def build(trades: Seq[Trade]): Seq[FilledBar] = {
    val res: Map[Long, Seq[Trade]] = trades.groupBy { trade =>
      barStartTime(trade.time)
    }
    res
      .map {
        case (startTime, trades) =>
          createBar(startTime, barSize, trades)
      }
      .toList
      .sortBy(_.startTime)
  }

  private def barStartTime(tradeTime: Long): Long =
    tradeTime - tradeTime % barSize

  private def createBar(startTime: Long,
                        barSize: Long,
                        trades: Seq[Trade]): FilledBar = {
    FilledBar(
      startTime,
      barSize,
      trades.minBy(_.time).price,
      trades.maxBy(_.price).price,
      trades.minBy(_.price).price,
      trades.maxBy(_.time).price
    )
  }

}
