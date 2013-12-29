package edu.uab.cis.reversi;

public interface Strategy {
  public Square chooseSquare(Board board);
}
