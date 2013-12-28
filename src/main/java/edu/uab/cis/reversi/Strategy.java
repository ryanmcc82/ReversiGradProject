package edu.uab.cis.reversi;

public interface Strategy {
  public Square getMove(Board board);
}
