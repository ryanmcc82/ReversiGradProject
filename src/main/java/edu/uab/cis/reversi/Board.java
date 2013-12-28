package edu.uab.cis.reversi;

import java.util.Objects;
import java.util.Set;

import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

public class Board {
  private int size;
  private PMap<Square, Player> moves;
  private Player nextPlayer;
  private PMap<Square, PSet<Square>> nextMoves;

  private Board(int size, PMap<Square, Player> moves, Player nextPlayer) {
    if (size % 2 != 0 || size <= 2) {
      throw new IllegalArgumentException("Board size must be an even integer greater than 2");
    }
    this.size = size;
    this.moves = moves;
    this.nextPlayer = nextPlayer;
    this.nextMoves = this.getNextValidMoves();
  }

  public Board() {
    this(8);
  }

  public Board(int size) {
    this(size, getInitialMoves(size), Player.BLACK);
  }

  private static PMap<Square, Player> getInitialMoves(int size) {
    PMap<Square, Player> moves = HashTreePMap.empty();
    int mid = size / 2;
    moves = moves.plus(new Square(mid - 1, mid - 1), Player.WHITE);
    moves = moves.plus(new Square(mid - 1, mid), Player.BLACK);
    moves = moves.plus(new Square(mid, mid - 1), Player.BLACK);
    moves = moves.plus(new Square(mid, mid), Player.WHITE);
    return moves;
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
        Player player = this.getOwner(row, col);
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

  public Player getOwner(int row, int column) {
    return this.moves.get(new Square(row, column));
  }

  public boolean isValidNextMove(int row, int column, Player player) {
    return this.nextMoves.get(new Square(row, column)) != null;
  }

  public Set<Square> getValidNextMoves(Player player) {
    return this.nextMoves.keySet();
  }

  public Board addMove(int row, int column, Player player) {
    if (player != this.nextPlayer) {
      String message = "%s cannot play - it is %s's turn";
      throw new IllegalArgumentException(String.format(message, player, this.nextPlayer));
    }
    Square move = new Square(row, column);
    Player existingPlayer = this.moves.get(move);
    if (existingPlayer != null) {
      String message = "A %s piece already exists at (%d,%d)";
      throw new IllegalArgumentException(String.format(message, existingPlayer, row, column));
    }
    if (!this.isValidNextMove(row, column, player)) {
      String message = "%s will not capture any pieces if placed at (%d,%d)";
      throw new IllegalArgumentException(String.format(message, player, row, column));
    }
    return new Board(this.size, this.moves.plus(move, player), player.opponent());
  }

  private PMap<Square, PSet<Square>> getNextValidMoves() {
    PMap<Square, PSet<Square>> validNextMoves = HashTreePMap.empty();
    Player opponent = this.nextPlayer.opponent();

    // this is a brute force approach, just going through all squares
    // it could be more efficient in exactly which locations it checks
    int[][] directions =
        new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
            { -1, 1 } };
    for (int row = 0; row < this.size; ++row) {
      for (int column = 0; column < this.size; ++column) {
        if (this.moves.get(new Square(row, column)) == null) {
          PSet<Square> allCaptures = HashTreePSet.empty();
          for (int[] direction : directions) {
            int rowStep = direction[0];
            int columnStep = direction[1];
            int r = row + rowStep;
            int c = column + columnStep;
            PSet<Square> captures = HashTreePSet.empty();
            if (this.isValidSquare(r, c) && this.getOwner(r, c) == opponent) {
              r += rowStep;
              c += columnStep;
              captures = captures.plus(new Square(r, c));
              while (this.isValidSquare(r, c) && this.getOwner(r, c) == opponent) {
                r += rowStep;
                c += columnStep;
                captures = captures.plus(new Square(r, c));
              }
              if (this.isValidSquare(r, c) && this.getOwner(r, c) == this.nextPlayer) {
                allCaptures = allCaptures.plusAll(captures);
              }
            }
          }
          if (!allCaptures.isEmpty()) {
            validNextMoves = validNextMoves.plus(new Square(row, column), allCaptures);
          }
        }
      }
    }
    return validNextMoves;
  }

  private boolean isValidSquare(int row, int column) {
    return 0 <= row && row < this.size && 0 <= column && column < this.size;
  }
}
