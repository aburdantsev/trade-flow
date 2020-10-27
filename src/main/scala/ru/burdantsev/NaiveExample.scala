package ru.burdantsev

import java.sql.Timestamp
import java.time.LocalDateTime

import ru.burdantsev.builder.NaiveBarsBuilder
import ru.burdantsev.stub.TradesGenerator

object NaiveExample extends App {
  val startTime = Timestamp.valueOf(LocalDateTime.now.minusHours(2)).getTime
  val tradesCount = 100
  val barSize = 2000
  val trades = TradesGenerator.generate(startTime, tradesCount).toList

  val builder = new NaiveBarsBuilder(barSize)
  val bars = builder.build(trades)
  bars.foreach(println)


}
