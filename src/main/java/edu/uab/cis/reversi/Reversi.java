package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

public class Reversi {

  public static interface Options {
    @Option(longName = "strategy1")
    public String getStrategy1();

    @Option(longName = "strategy2")
    public String getStrategy2();
  }

  public static void main(String[] args) {
    Options options = CliFactory.parseArguments(Options.class, args);
    Strategy strategy1 = loadStrategy(options.getStrategy1());
    Strategy strategy2 = loadStrategy(options.getStrategy2());
    Multiset<Strategy> wins = HashMultiset.create();
    Board board = new Board();
    for (int i = 0; i < 500; ++i) {
      Reversi reversi;
      reversi = new Reversi(strategy1, strategy2);
      wins.add(reversi.getWinner(reversi.play(board)));
      reversi = new Reversi(strategy2, strategy1);
      wins.add(reversi.getWinner(reversi.play(board)));
    }
    System.err.printf("Strategy 1: %4d\n", wins.count(strategy1));
    System.err.printf("Strategy 2: %4d\n", wins.count(strategy2));
    System.err.printf("Ties:       %4d\n", wins.count(null));
  }

  private static Strategy loadStrategy(String className) {
    try {
      Class<?> strategyClass = Class.forName(className);
      return strategyClass.asSubclass(Strategy.class).newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
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
