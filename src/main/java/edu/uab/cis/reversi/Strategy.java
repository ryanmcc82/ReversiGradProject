package edu.uab.cis.reversi;

import java.util.concurrent.TimeUnit;

/**
 * A strategy for choosing where a player should play their next piece.
 */
public interface Strategy {
  /**
   * Determines where the current player should play their next piece. Some
   * methods that may be useful for defining such a strategy:
   * <ul>
   * <li>{@link Board#getCurrentPossibleSquares()}</li>
   * <li>{@link Board#getCurrentPlayer()}</li>
   * <li>{@link Board#getSquareOwners()}</li>
   * <li>{@link Board#getPlayerSquareCounts()}</li>
   * </ul>
   * 
   * @param board
   *          The current state of the Reversi board.
   * @return The square where the current player should play their next piece.
   */
  public Square chooseSquare(Board board);

  /**
   * Indicates to the strategy how much time will be allowed for each call to chooseSquare. If the
   * strategy takes longer than the allotted time, it will be considered to have lost the game.
   *
   * @param time The time allowed
   * @param unit The time unit of the time argument
   */
  default public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
    // by default, do nothing
  }
}
