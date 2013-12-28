package edu.uab.cis.reversi;

import java.util.Objects;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

public class Board {
  private int size;
  private PMap<Move, Player> moves;

  public Board() {
    this(8);
  }

  public Board(int size) {
    if (size % 2 != 0 || size <= 2) {
      throw new IllegalArgumentException("Board size must be an even integer greater than 2");
    }
    this.size = size;
    this.moves = HashTreePMap.empty();
    int mid = this.size / 2;
    this.moves = this.moves.plus(new Move(mid - 1, mid - 1), Player.WHITE);
    this.moves = this.moves.plus(new Move(mid - 1, mid), Player.BLACK);
    this.moves = this.moves.plus(new Move(mid, mid - 1), Player.BLACK);
    this.moves = this.moves.plus(new Move(mid, mid), Player.WHITE);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.size, this.moves);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Board) {
      Board that = (Board) obj;
      return this.size == that.size && Objects.equals(this.moves, that.moves);
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    for (int row = 0; row < this.size; ++row) {
      for (int col = 0; col < this.size; ++col) {
        Player player = this.getPlayer(row, col);
        if (player == Player.WHITE) {
          builder.append('W');
        } else if (player == Player.BLACK) {
          builder.append('B');
        } else {
          builder.append('_');
        }
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  public int size() {
    return this.size;
  }

  public Player getPlayer(int row, int column) {
    return this.moves.get(new Move(row, column));
  }

  public Board addMove(int i, int j, Player player) {
    Board board = new Board(this.size);
    board.moves = board.moves.plus(new Move(i, j), player);
    return board;
  }

  public boolean isValidSquare(int row, int column) {
    return 0 <= row && row < this.size && 0 <= column && column < this.size;
  }

  public boolean isValidMove(Player player, int row, int column) {
    int[][] directions =
        new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
            { -1, 1 } };
    Player other = player == Player.WHITE ? Player.BLACK : Player.WHITE;
    for (int[] direction : directions) {
      int rowStep = direction[0];
      int columnStep = direction[1];
      int r = row + rowStep;
      int c = column + columnStep;
      if (this.isValidSquare(r, c) && this.getPlayer(r, c) == other) {
        r += rowStep;
        c += columnStep;
        while (this.isValidSquare(r, c) && this.getPlayer(r, c) == other) {
          r += rowStep;
          c += columnStep;
        }
        if (this.isValidSquare(r, c) && this.getPlayer(r, c) == player) {
          return true;
        }
      }
    }
    return false;
  }
}
