package edu.uab.cis.reversi;

public interface Strategy {
  public Square getMove(Player player, Board board);
}
