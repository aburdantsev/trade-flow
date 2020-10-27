package ru.burdantsev

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import ru.burdantsev.builder.AkkaBarsBuilder
import ru.burdantsev.stub.TradesGenerator

import scala.concurrent.ExecutionContextExecutor

object AkkaStreamsExample extends App {

  val startTime = Timestamp.valueOf(LocalDateTime.now.minusHours(2)).getTime
  val tradesCount = 100
  val barSize = 2000
  val trades = TradesGenerator.generate(startTime, tradesCount).toList

  implicit val system: ActorSystem = ActorSystem("trade-flow")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val barsBuilder = new AkkaBarsBuilder(barSize)
  val result = barsBuilder.build(Source(trades))
    .runWith(Sink.foreach(println))

  result.onComplete(_ => system.terminate())
}
