import java.sql.Timestamp

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.burdantsev.builder.{AkkaBarsBuilder, NaiveBarsBuilder}
import ru.burdantsev.domain.Trade
import ru.burdantsev.stub.TradesGenerator

import scala.concurrent.Await
import scala.concurrent.duration._

class BarBuilderSpec extends AnyFlatSpec with should.Matchers {

  implicit val system: ActorSystem = ActorSystem("test-system")

  "Three trades" should "be built in TWO bars" in {
    val trades = List(
      Trade(Timestamp.valueOf("2020-10-27 09:42:23.604").getTime, 58.0),
      Trade(Timestamp.valueOf("2020-10-27 09:42:24.054").getTime, 58.09),
      Trade(Timestamp.valueOf("2020-10-27 09:42:24.323").getTime, 58.22)
    )
    val builder = new AkkaBarsBuilder(2000)
    val future = builder.build(Source(trades)).runWith(Sink.collection)
    val bars = Await.result(future, 2.seconds)
    bars.length should be(2)
  }

  "Three trades" should "be built in ONE bar" in {
    val trades = List(
      Trade(Timestamp.valueOf("2020-10-27 09:42:23.604").getTime, 58.0),
      Trade(Timestamp.valueOf("2020-10-27 09:42:24.054").getTime, 58.09),
      Trade(Timestamp.valueOf("2020-10-27 09:42:24.323").getTime, 58.22)
    )
    val builder = new AkkaBarsBuilder(5000)
    val future = builder.build(Source(trades)).runWith(Sink.collection)
    val bars = Await.result(future, 2.seconds)
    bars.length should be(1)
  }

  "Three trades" should "be built in THREE bars" in {
    val trades = List(
      Trade(Timestamp.valueOf("2020-10-27 09:42:23.604").getTime, 58.0),
      Trade(Timestamp.valueOf("2020-10-27 09:42:24.054").getTime, 58.09),
      Trade(Timestamp.valueOf("2020-10-27 09:42:25.323").getTime, 58.22)
    )
    val builder = new AkkaBarsBuilder(1000)
    val future = builder.build(Source(trades)).runWith(Sink.collection)
    val bars = Await.result(future, 2.seconds)
    bars.length should be(3)
  }

  "Akka and naive versions" should "return same result" in {
    val trades = TradesGenerator.generate(0, 100).toList
    val barSize = 2000

    val naiveBuilder = new NaiveBarsBuilder(barSize)
    val naiveBars = naiveBuilder.build(trades).toList

    val akkaBuilder = new AkkaBarsBuilder(barSize)
    val future = akkaBuilder.build(Source(trades)).runWith(Sink.collection)
    val akkaBars = Await.result(future, 2.seconds).toList

    naiveBars should contain theSameElementsInOrderAs akkaBars
  }

}
