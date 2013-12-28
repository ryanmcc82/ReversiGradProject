package edu.uab.cis.reversi;

public interface Strategy {
  public Move getMove(Player player, Board board);
}
