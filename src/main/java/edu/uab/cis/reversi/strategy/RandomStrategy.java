package edu.uab.cis.reversi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class RandomStrategy implements Strategy {

  @Override
  public Square getMove(Board board) {
    return chooseOne(board.getCurrentPossibleSquares());
  }

  protected Square chooseOne(Set<Square> squares) {
    List<Square> squareList = new ArrayList<>(squares);
    int index = new Random().nextInt(squareList.size());
    return squareList.get(index);
  }
}
