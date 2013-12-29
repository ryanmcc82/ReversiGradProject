package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.Map;

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
      winner = reversi.getWinner(reversi.play(board));
      wins.put(winner, wins.get(winner) + 1);
      // strategy2=BLACK, strategy1=WHITE
      reversi = new Reversi(strategy2, strategy1);
      winner = reversi.getWinner(reversi.play(board));
      wins.put(winner, wins.get(winner) + 1);
    }
    return wins;
  }

  private Map<Player, Strategy> strategies;

  public Reversi(Strategy blackStrategy, Strategy whiteStrategy) {
    this.strategies = new HashMap<>();
    this.strategies.put(Player.BLACK, blackStrategy);
    this.strategies.put(Player.WHITE, whiteStrategy);
  }

  public Board play(Board board) {
    Board curr = board;
    while (!curr.isComplete()) {
      if (curr.getCurrentPossibleSquares().isEmpty()) {
        curr = curr.pass();
      } else {
        Player player = curr.getCurrentPlayer();
        Strategy strategy = this.strategies.get(player);
        Square square = strategy.chooseSquare(curr);
        curr = curr.play(square);
      }
    }
    return curr;
  }

  public Strategy getWinner(Board board) {
    return this.strategies.get(board.getWinner());
  }
}
