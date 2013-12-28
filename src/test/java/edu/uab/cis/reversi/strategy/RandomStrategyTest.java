package edu.uab.cis.reversi.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class RandomStrategyTest {

  @Test
  public void testInitialBoard() {
    Board board = new Board();
    Strategy strategy = new RandomStrategy();
    Set<Square> expected =
        new HashSet<>(Arrays.asList(
            new Square(2, 3),
            new Square(3, 2),
            new Square(4, 5),
            new Square(5, 4)));
    Set<Square> actual = new HashSet<>();
    for (int i = 0; i < 100; ++i) {
      actual.add(strategy.getMove(board));
    }
    Assert.assertEquals(expected, actual);
  }

}
