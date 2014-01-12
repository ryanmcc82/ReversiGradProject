package edu.uab.cis.reversi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

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

  @Test
  public void testPlayMultiple() {
    Strategy strategy1 = new SequentialStrategy();
    Strategy strategy2 = new SequentialStrategy();
    Map<Strategy, Integer> wins = Reversi.playMultiple(new Board(), strategy1, strategy2, 10);
    Map<Strategy, Integer> expected = new HashMap<>();
    expected.put(strategy1, 5);
    expected.put(strategy2, 5);
    expected.put(null, 0);
    Assert.assertEquals(expected, wins);
  }

  @Test
  public void testSlowStrategyAlwaysLoses() {
    Strategy normal = new SequentialStrategy();
    Strategy slow = new NeverTerminatesStrategy();
    Map<Strategy, Integer> wins = Reversi.playMultiple(new Board(), normal, slow, 10);
    Map<Strategy, Integer> expected = new HashMap<>();
    expected.put(normal, 10);
    expected.put(slow, 0);
    expected.put(null, 0);
    Assert.assertEquals(expected, wins);
  }

  @Test
  public void testMain() throws Exception {
    // not a thorough test, but at least we ensure it throws no exceptions
    String strategy = SequentialStrategy.class.getName();
    Reversi.main("--strategy1", strategy, "--strategy2", strategy, "--games", "4");
  }
}
