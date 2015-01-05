package edu.uab.cis.reversi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.uab.cis.reversi.strategy.baseline.RandomStrategy;

public class ReversiTest {

  static class SequentialStrategy implements Strategy {

    @Override
    public Square chooseSquare(Board board) {
      Set<Square> squares = board.getCurrentPossibleSquares();
      Ordering<Square> byPosition =
          Ordering.natural().<Integer> lexicographical().onResultOf(
              new Function<Square, List<Integer>>() {
                @Override
                public List<Integer> apply(Square input) {
                  return Arrays.asList(input.getRow(), input.getColumn());
                }
              });
      return byPosition.min(squares);
    }
  }

  static class NeverTerminatesStrategy implements Strategy {

    private boolean stop = false;

    @Override
    public Square chooseSquare(Board board) {
      while (!this.stop) {
        // simulate a strategy that never terminates
      }
      return null;
    }
  }

  @Test
  public void testPlay() throws Exception {
    Strategy blackStrategy = new SequentialStrategy();
    Strategy whiteStrategy = new SequentialStrategy();
    Reversi reversi = new Reversi(blackStrategy, whiteStrategy);
    Board board = reversi.play(new Board());
    Assert.assertTrue(board.isComplete());
    Assert.assertEquals(
        "WWWWWWWB\nWWWWWWBB\nWWWWWBWB\nWWWWBWWB\nWWWWWWWB\nWWWBWWWB\nWWWWBBWB\nBBBBBBWW\n",
        board.toString());
    Assert.assertEquals(Player.WHITE, board.getWinner());
    Assert.assertEquals(whiteStrategy, reversi.getWinner(board));
  }

  @Test(expected = Reversi.StrategyTimedOutException.class)
  public void testSlowStrategyIsTerminated() throws Exception {
    Strategy normal = new SequentialStrategy();
    Strategy slow = new NeverTerminatesStrategy();
    Reversi reversi = new Reversi(normal, slow);
    reversi.play(new Board());
  }

  @Test
  public void testMain() throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream oldOut = System.out;
    try {
      System.setOut(new PrintStream(output));
      Reversi.main(
          "--games",
          "2",
          "--strategies",
          RandomStrategy.class.getName(),
          SequentialStrategy.class.getName(),
          NeverTerminatesStrategy.class.getName());
    } finally {
      System.setOut(oldOut);
    }
    // check that NeverTerminatesStrategy fails 8 times
    String expected = String.format("8\t%s\n", NeverTerminatesStrategy.class.getSimpleName());
    Assert.assertTrue(output.toString().endsWith(expected));
  }
}
