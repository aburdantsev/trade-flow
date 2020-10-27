package ru.burdantsev.builder

import akka.NotUsed
import akka.stream.scaladsl.Source
import ru.burdantsev.domain._

class AkkaBarsBuilder(barSize: Long) {

  def build[A <: Trade](source: Source[A, NotUsed]): Source[Bar, NotUsed] =
    source
    .concat(Source.single(Trade.empty))
    .statefulMapConcat { () =>
      var resultBar: Bar = EmptyBar

      { trade: Trade =>
        if (trade == Trade.empty) {
          List(resultBar)
        } else if (resultBar.contains(trade)) {
          resultBar = resultBar.addTrade(trade, barSize)
          Nil
        } else {
          val result = resultBar
          resultBar = EmptyBar.addTrade(trade, barSize)
          List(result)
        }
      }
    }

}
