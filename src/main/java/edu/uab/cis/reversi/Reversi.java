package edu.uab.cis.reversi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
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
    for (int i = 0; i < 500; ++i) {
      wins.add(getWinner(play(strategy1, strategy2)));
      wins.add(getWinner(play(strategy2, strategy1)));
    }
    System.err.printf("Strategy 1: %4d\n", wins.count(strategy1));
    System.err.printf("Strategy 2: %4d\n", wins.count(strategy2));
    System.err.printf("Ties:       %4d\n", wins.count(null));
  }

  public static Multiset<Strategy> play(Strategy blackStrategy, Strategy whiteStrategy) {
    // map players to strategies
    Map<Player, Strategy> strategies = new HashMap<>();
    strategies.put(Player.BLACK, blackStrategy);
    strategies.put(Player.WHITE, whiteStrategy);

    // play until the board is complete
    Board board = new Board();
    while (!board.isComplete()) {
      if (board.getPossibleMoves().isEmpty()) {
        board = board.pass();
      } else {
        Player player = board.getCurrentPlayer();
        Square square = strategies.get(player).getMove(board);
        board = board.addMove(square.getRow(), square.getColumn());
      }
    }

    // count the squares owned by each player
    Multiset<Strategy> squareCounts = HashMultiset.create();
    for (int row = 0; row < board.size(); ++row) {
      for (int column = 0; column < board.size(); ++column) {
        Player owner = board.getOwner(new Square(row, column));
        if (owner != null) {
          squareCounts.add(strategies.get(owner));
        }
      }
    }
    return squareCounts;
  }

  public static Strategy getWinner(Multiset<Strategy> squareCounts) {
    Strategy winner;
    int size = squareCounts.elementSet().size();
    if (size == 0) {
      throw new IllegalArgumentException("squareCounts cannot be empty");
    } else if (size == 1) {
      winner = squareCounts.elementSet().iterator().next();
    } else {
      Multiset<Strategy> sortedCounts = Multisets.copyHighestCountFirst(squareCounts);
      Iterator<Strategy> iterator = sortedCounts.elementSet().iterator();
      Strategy first = iterator.next();
      Strategy second = iterator.next();
      if (squareCounts.count(first) == squareCounts.count(second)) {
        winner = null;
      } else {
        winner = first;
      }
    }
    return winner;
  }

  private static Strategy loadStrategy(String className) {
    try {
      Class<?> strategyClass = Class.forName(className);
      return strategyClass.asSubclass(Strategy.class).newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
