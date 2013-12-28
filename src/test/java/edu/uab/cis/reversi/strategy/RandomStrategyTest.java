package edu.uab.cis.reversi.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Strategy;

public class RandomStrategyTest {

  @Test
  public void testInitialBoard() {
    Board board = new Board();
    Strategy strategy = new RandomStrategy();
    Set<Move> expected =
        new HashSet<>(Arrays.asList(new Move(2, 3), new Move(3, 2), new Move(4, 5), new Move(5, 4)));
    Set<Move> actual = new HashSet<>();
    for (int i = 0; i < 100; ++i) {
      actual.add(strategy.getMove(Player.BLACK, board));
    }
    Assert.assertEquals(expected, actual);
  }

}
