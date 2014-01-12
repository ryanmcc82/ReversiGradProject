package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class Reversi {

  public static interface Options {
    @Option(longName = "strategy1")
    public String getStrategy1();

    @Option(longName = "strategy2")
    public String getStrategy2();

    @Option(longName = "games", defaultValue = "1000")
    public int getNumberOfGames();

    @Option(longName = "timeout", defaultValue = "100")
    public long getTimeout();

    @Option(longName = "timeout-unit", defaultValue = "MILLISECONDS")
    public TimeUnit getTimeoutUnit();
  }

  public static void main(String... args) throws Exception {
    Options options = CliFactory.parseArguments(Options.class, args);
    Strategy strategy1, strategy2;
    strategy1 = Class.forName(options.getStrategy1()).asSubclass(Strategy.class).newInstance();
    strategy2 = Class.forName(options.getStrategy2()).asSubclass(Strategy.class).newInstance();
    int nGames = options.getNumberOfGames();
    Map<Strategy, Integer> wins = playMultiple(new Board(), strategy1, strategy2, nGames);
    System.err.printf(
        "%4d won by %s\n%4d won by %s\n%4d tied\n",
        wins.get(strategy1),
        options.getStrategy1(),
        wins.get(strategy2),
        options.getStrategy2(),
        wins.get(null));
  }

  public static Map<Strategy, Integer> playMultiple(
      Board board,
      Strategy strategy1,
      Strategy strategy2,
      int nGames) {
    Map<Strategy, Integer> wins = new HashMap<>();
    wins.put(strategy1, 0);
    wins.put(strategy2, 0);
    wins.put(null, 0);
    for (int i = 0; i < nGames / 2; ++i) {
      Reversi reversi;
      Strategy winner;
      // strategy1=BLACK, strategy2=WHITE
      reversi = new Reversi(strategy1, strategy2);
      try {
        winner = reversi.getWinner(reversi.play(board));
      } catch (StrategyTimedOutException e) {
        winner = e.getOpponentStrategy();
      }
      wins.put(winner, wins.get(winner) + 1);
      // strategy2=BLACK, strategy1=WHITE
      reversi = new Reversi(strategy2, strategy1);
      try {
        winner = reversi.getWinner(reversi.play(board));
      } catch (StrategyTimedOutException e) {
        winner = e.getOpponentStrategy();
      }
      wins.put(winner, wins.get(winner) + 1);
    }
    return wins;
  }

  private Map<Player, Strategy> strategies;

  private long timeout;

  private TimeUnit timeoutUnit;

  public Reversi(Strategy blackStrategy, Strategy whiteStrategy) {
    this(blackStrategy, whiteStrategy, 100, TimeUnit.MILLISECONDS);
  }

  public Reversi(Strategy blackStrategy, Strategy whiteStrategy, long timeout, TimeUnit timeoutUnit) {
    this.strategies = new HashMap<>();
    this.strategies.put(Player.BLACK, blackStrategy);
    this.strategies.put(Player.WHITE, whiteStrategy);
    this.timeout = timeout;
    this.timeoutUnit = timeoutUnit;
  }

  public Board play(Board board) throws StrategyTimedOutException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Board curr = board;
    while (!curr.isComplete()) {
      if (curr.getCurrentPossibleSquares().isEmpty()) {
        curr = curr.pass();
      } else {
        Player player = curr.getCurrentPlayer();
        final Strategy strategy = this.strategies.get(player);
        final Board boardForFuture = curr;
        Future<Square> future = executor.submit(new Callable<Square>() {
          @Override
          public Square call() throws Exception {
            return strategy.chooseSquare(boardForFuture);
          }
        });
        Square square;
        try {
          square = future.get(this.timeout, this.timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
          future.cancel(true);
          throw new StrategyTimedOutException(strategy, this.strategies.get(player.opponent()));
        }
        curr = curr.play(square);
      }
    }
    return curr;
  }

  public Strategy getWinner(Board board) {
    return this.strategies.get(board.getWinner());
  }

  public static class StrategyTimedOutException extends Exception {
    private static final long serialVersionUID = 1L;

    private Strategy timedOutStrategy;

    public Strategy getTimedOutStrategy() {
      return this.timedOutStrategy;
    }

    public Strategy getOpponentStrategy() {
      return this.opponentStrategy;
    }

    private Strategy opponentStrategy;

    public StrategyTimedOutException(Strategy timedOutStrategy, Strategy opponentStrategy) {
      this.timedOutStrategy = timedOutStrategy;
      this.opponentStrategy = opponentStrategy;
    }
  }
}
