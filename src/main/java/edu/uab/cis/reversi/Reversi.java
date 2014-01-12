package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class Reversi {

  public static interface Options {
    @Option(longName = "strategies")
    public List<String> getStrategies();

    @Option(longName = "games", defaultValue = "10")
    public int getNumberOfGames();

    @Option(longName = "timeout", defaultValue = "100")
    public long getTimeout();

    @Option(longName = "timeout-unit", defaultValue = "MILLISECONDS")
    public TimeUnit getTimeoutUnit();
  }

  public static void main(String... args) throws Exception {
    Options options = CliFactory.parseArguments(Options.class, args);
    int nGames = options.getNumberOfGames();
    long timeout = options.getTimeout();
    TimeUnit timeoutUnit = options.getTimeoutUnit();

    // convert strategy names to class instances
    List<Strategy> strategies = Lists.newArrayList();
    for (String strategyName : options.getStrategies()) {
      strategies.add(Class.forName(strategyName).asSubclass(Strategy.class).newInstance());
    }

    // keep track of number of wins for each strategy
    Map<Strategy, Integer> wins = Maps.newHashMap();
    for (Strategy strategy : strategies) {
      wins.put(strategy, 0);
    }

    // Run N rounds, pairing each strategy with each other strategy. There will
    // actually be 2N games since each strategy gets to be both black and white
    Board board = new Board();
    for (int game = 0; game < nGames; ++game) {
      for (int i = 0; i < strategies.size(); ++i) {
        for (int j = i + 1; j < strategies.size(); ++j) {
          Strategy strategy1 = strategies.get(i);
          Strategy strategy2 = strategies.get(j);
          Reversi reversi;
          Strategy winner;

          // first game: strategy1=BLACK, strategy2=WHITE
          reversi = new Reversi(strategy1, strategy2, timeout, timeoutUnit);
          try {
            winner = reversi.getWinner(reversi.play(board));
          } catch (StrategyTimedOutException e) {
            winner = e.getOpponentStrategy();
          }
          if (winner != null) {
            wins.put(winner, wins.get(winner) + 1);
          }

          // second game: strategy2=BLACK, strategy1=WHITE
          reversi = new Reversi(strategy2, strategy1, timeout, timeoutUnit);
          try {
            winner = reversi.getWinner(reversi.play(board));
          } catch (StrategyTimedOutException e) {
            winner = e.getOpponentStrategy();
          }
          if (winner != null) {
            wins.put(winner, wins.get(winner) + 1);
          }
        }
      }
    }

    // rank strategies by number of wins
    Ordering<Strategy> byWins = Ordering.natural().onResultOf(Functions.forMap(wins)).reverse();
    for (Strategy strategy : byWins.sortedCopy(wins.keySet())) {
      System.out.printf("%4d\t%s\n", wins.get(strategy), strategy.getClass().getName());
    }
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
    executor.shutdownNow();
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
