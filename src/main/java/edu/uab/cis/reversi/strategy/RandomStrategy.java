package edu.uab.cis.reversi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class RandomStrategy implements Strategy {

  @Override
  public Square getMove(Board board) {
    List<Square> possibleMoves = new ArrayList<>(board.getPossibleMoves());
    int index = new Random().nextInt(possibleMoves.size());
    return possibleMoves.get(index);
  }
}
