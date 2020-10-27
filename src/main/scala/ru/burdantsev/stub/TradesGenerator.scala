package ru.burdantsev.stub

import ru.burdantsev.domain.Trade

object TradesGenerator {

  def generate(startTime: Long, n: Int): Seq[Trade] = {

    @scala.annotation.tailrec
    def gen(acc: Seq[Trade], n: Int): Seq[Trade] =
      if (n == 0) acc
      else acc match {
        case t :: _ =>
          val newAcc = t.generateNext +: acc
          gen(newAcc, n - 1)
      }
    gen(Seq(Trade.first(startTime)), n-1).reverse
  }

}
