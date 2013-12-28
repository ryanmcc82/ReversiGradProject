package edu.uab.cis.reversi;

public enum Player {
  WHITE, BLACK;

  public Player opponent() {
    switch (this) {
      case WHITE:
        return BLACK;
      case BLACK:
        return WHITE;
      default:
        throw new IllegalStateException("unexpected Player: " + this);
    }
  }
}
