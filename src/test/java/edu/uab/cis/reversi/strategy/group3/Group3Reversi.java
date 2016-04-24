package edu.uab.cis.reversi.strategy.group3;

import com.google.common.collect.*;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A game of Reversi, played by two strategies.
 */
public class Group3Reversi {

  /**
   * The command-line options for the {@link Group3Reversi#main(String...)} method.
   */
  public interface Options {
    @Option(longName = "strategies",
            description = "Subclasses of edu.uab.cis.reversi.Strategy to play against each other")
    List<String> getStrategies();

    @Option(longName = "games",
            defaultValue = "10",
            description = "Number of games to play against each strategy")
    int getNumberOfGames();

    @Option(longName = "timeout",
            defaultValue = "100",
            description = "Maximum time allowed to a strategy for choosing a square")
    long getTimeout();

    @Option(longName = "timeout-unit",
            defaultValue = "MILLISECONDS",
            description = "Unit of the timeout, e.g. MILLISECONDS")
    TimeUnit getTimeoutUnit();

    @Option(longName = "debug",
            defaultValue = "true",
            description = "Prints out additional information that may be useful for debugging")
    boolean getDebug();
  }

  enum Result {
    WIN, LOSS, TIE, FAIL
  }

  /**
   * Runs a round-robin tournament over Reversi strategies.
   * 
   * @see Options for the available command-line options.
   */
  public static void main(String... args) throws Exception {
    Options options = CliFactory.parseArguments(Options.class, args);
    int nGames = options.getNumberOfGames();
    long timeout = options.getTimeout();
    TimeUnit timeoutUnit = options.getTimeoutUnit();

    // convert strategy names to class instances
    List<Strategy> strategies = Lists.newArrayList();
    for (String strategyName : options.getStrategies()) {
      Strategy strategy = Class.forName(strategyName).asSubclass(Strategy.class).newInstance();
      strategy.setChooseSquareTimeLimit(timeout, timeoutUnit);
      strategies.add(strategy);
    }
//    Warning expands exponentially and can cause program to exceed java heap should vary 2 vars at most
    boolean bookon = true; int mobWeight = 100; int cornerW = 810; int xSquare = 300; int cSquare = 40;int aSquare = 10; int parity = 30;
    VariableStrategy testA = new VariableStrategy(true,mobWeight,cornerW,xSquare,cSquare,aSquare,0,parity,0);
    testA.setChooseSquareTimeLimit(timeout, timeoutUnit);
    strategies.add(testA);
    for(mobWeight = 33; mobWeight < 36; mobWeight += 1){//init est: 1
      for(cornerW = 8100; cornerW < 8101; cornerW += 50){
        for(xSquare = 3000; xSquare < 3001; xSquare += 1){//init est: 31 or 30 NOTE performance is very close 20 - 35
          for(cSquare = 400; cSquare < 401; cSquare += 1){//init est: 4
            for(aSquare = 100; aSquare < 101; aSquare += 1){//init est 1
              for(parity = 30; parity<40; parity+=20 ) {
                VariableStrategy test = new VariableStrategy(true, 0, cornerW, xSquare, cSquare, aSquare, 0, parity, mobWeight);
                test.setChooseSquareTimeLimit(timeout, timeoutUnit);
                strategies.add(test);
              }
//              VariableStrategy test2 = new VariableStrategy(false,mobWeight,cornerW,xSquare,cSquare,aSquare,0,0,0);
//              test2.setChooseSquareTimeLimit(timeout, timeoutUnit);
//              strategies.add(test2);
            }
          }
        }
      }
    }

    // keep track of number of wins for each strategy
    final Map<Strategy, Multiset<Result>> results = Maps.newHashMap();
    for (Strategy strategy : strategies) {
      results.put(strategy, HashMultiset.<Result> create());
    }

    // Run N rounds, pairing each strategy with each other strategy. There will
    // actually be 2N games since each strategy gets to be both black and white
    Board board = new Board();
    for (int game = 0; game < nGames; ++game) {
      for (int i = 0; i < strategies.size(); ++i) {
        for (int j = 0; j < strategies.size(); ++j) {
          if (i != j) {
            Group3Reversi group3Reversi = new Group3Reversi(strategies.get(i), strategies.get(j), timeout, timeoutUnit);

            Strategy winner;
            try {
              winner = group3Reversi.getWinner(group3Reversi.play(board));
              if (winner != null) {
                results.get(winner).add(Result.WIN);
                for (Strategy strategy : group3Reversi.strategies.values()) {
                  if (strategy != winner) {
                    results.get(strategy).add(Result.LOSS);
                  }
                }
              } else {
                for (Strategy strategy : group3Reversi.strategies.values()) {
                  results.get(strategy).add(Result.TIE);
                }
              }
            } catch (StrategyTimedOutException e) {
              if (options.getDebug()) {
                e.printStackTrace(System.out);
              }
              results.get(e.getOpponentStrategy()).add(Result.WIN);
              results.get(e.getTimedOutStrategy()).add(Result.FAIL);
            }
          }
        }
      }
    }

    // rank strategies by number of wins
    System.out.printf("%4s\t%4s\t%4s\t%4s\n", "win", "loss", "tie", "fail");
    Ordering<Strategy> byWins = Ordering.natural().onResultOf(s -> results.get(s).count(Result.WIN)).reverse();
    for (Strategy strategy : byWins.sortedCopy(strategies)) {
      Multiset<Result> strategyResults = results.get(strategy);
      System.out.printf(
          "%4d\t%4d\t%4d\t%4d\t%s\t%s\n",
          strategyResults.count(Result.WIN),
          strategyResults.count(Result.LOSS),
          strategyResults.count(Result.TIE),
          strategyResults.count(Result.FAIL),
          strategy.getClass().getSimpleName(),
          strategy.toString());
    }

//    URL url = getClass().getResource("book.txt");
//    File file = new File(url.toURI());
//    File newDest = new File((dest.getPath().endsWith(File.separator) ? dest.getPath() : dest.getPath() + File.separator) + "VarStTournamentResutls.txt");
//    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//            new FileOutputStream("filename.txt"), "utf-8"))) {
//      writer.write("something");
//    }

  }

  private Map<Player, Strategy> strategies;

  private long timeout;

  private TimeUnit timeoutUnit;

  /**
   * Creates a new Reversi game.
   * 
   * @param blackStrategy
   *          The strategy used to play the black pieces.
   * @param whiteStrategy
   *          The strategy used to play the white pieces.
   */
  public Group3Reversi(Strategy blackStrategy, Strategy whiteStrategy) {
    this(blackStrategy, whiteStrategy, 100, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates a new Reversi game.
   * 
   * @param blackStrategy
   *          The strategy used to play the black pieces.
   * @param whiteStrategy
   *          The strategy used to play the white pieces.
   * @param timeout
   *          The maximum time allowed to a strategy for choosing a square.
   * @param timeoutUnit
   *          The unit of the timeout, e.g. {@link TimeUnit#MILLISECONDS}
   */
  public Group3Reversi(Strategy blackStrategy, Strategy whiteStrategy, long timeout, TimeUnit timeoutUnit) {
    this.strategies = new HashMap<>();
    this.strategies.put(Player.BLACK, blackStrategy);
    this.strategies.put(Player.WHITE, whiteStrategy);
    this.timeout = timeout;
    this.timeoutUnit = timeoutUnit;
  }

  /**
   * Plays the strategies on the given Group3Reversi board.
   * 
   * @param board
   *          The board in its initial state.
   * @return The board after play is complete.
   * @throws StrategyTimedOutException
   *           If a strategy exceeds the alloted time to choose a square.
   */
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
        Future<Square> future = executor.submit(() -> strategy.chooseSquare(boardForFuture));
        try {
          Square square = future.get(this.timeout, this.timeoutUnit);
          curr = curr.play(square);
        } catch (Exception cause) {
          future.cancel(true);
          throw new StrategyTimedOutException(strategy, this.strategies.get(player.opponent()), cause);
        }
      }
    }
    executor.shutdownNow();
    return curr;
  }

  /**
   * Gets the winning strategy from a board.
   * 
   * This maps from the {@link Player}s of the board to the {@link Strategy}s of
   * the Group3Reversi game.
   * 
   * @param board
   *          A Group3Reversi board, after the game is complete.
   * @return The winning strategy.
   */
  public Strategy getWinner(Board board) {
    return this.strategies.get(board.getWinner());
  }

  /**
   * Identifies a strategy that exceeded the allotted time to choose a square.
   */
  public static class StrategyTimedOutException extends Exception {
    private static final long serialVersionUID = 1L;

    public StrategyTimedOutException(Strategy timedOutStrategy, Strategy opponentStrategy, Exception cause) {
      super(cause);
      this.timedOutStrategy = timedOutStrategy;
      this.opponentStrategy = opponentStrategy;
    }

    private Strategy timedOutStrategy;

    /**
     * @return The strategy that exceeded the allotted time.
     */
    public Strategy getTimedOutStrategy() {
      return this.timedOutStrategy;
    }

    /**
     * @return The strategy that was playing against the strategy that exceeded
     *         the allotted time.
     */
    public Strategy getOpponentStrategy() {
      return this.opponentStrategy;
    }

    private Strategy opponentStrategy;
  }
}
