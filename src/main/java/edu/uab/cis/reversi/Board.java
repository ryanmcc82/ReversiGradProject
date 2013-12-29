package edu.uab.cis.reversi;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

public class Board {
  private int size;
  private PMap<Square, Player> owners;
  private Player player;
  private PMap<Square, PSet<Square>> possibleMoves;

  public Board() {
    this(8);
  }

  public Board(int size) {
    this(size, getInitialOwners(size), Player.BLACK);
  }

  private Board(int size, PMap<Square, Player> owners, Player player) {
    if (size % 2 != 0 || size <= 2) {
      throw new IllegalArgumentException("Board size must be an even integer greater than 2");
    }
    this.size = size;
    this.owners = owners;
    this.player = player;
    this.possibleMoves = HashTreePMap.empty();
    // This is a brute force approach to determine the possible moves, just
    // going through all the squares, one at a time. It could be more efficient
    // in exactly which locations it checks
    Player opponent = this.player.opponent();
    int[][] directions =
        new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
            { -1, 1 } };
    for (int row = 0; row < this.size; ++row) {
      for (int column = 0; column < this.size; ++column) {
        if (this.owners.get(new Square(row, column)) == null) {
          PSet<Square> allCaptures = HashTreePSet.empty();
          for (int[] direction : directions) {
            int rowStep = direction[0];
            int columnStep = direction[1];
            int r = row + rowStep;
            int c = column + columnStep;
            PSet<Square> captures = HashTreePSet.empty();
            if (this.isValidSquare(r, c) && this.owners.get(new Square(r, c)) == opponent) {
              captures = captures.plus(new Square(r, c));
              r += rowStep;
              c += columnStep;
              while (this.isValidSquare(r, c) && this.owners.get(new Square(r, c)) == opponent) {
                captures = captures.plus(new Square(r, c));
                r += rowStep;
                c += columnStep;
              }
              if (this.isValidSquare(r, c) && this.owners.get(new Square(r, c)) == this.player) {
                allCaptures = allCaptures.plusAll(captures);
              }
            }
          }
          if (!allCaptures.isEmpty()) {
            this.possibleMoves = this.possibleMoves.plus(new Square(row, column), allCaptures);
          }
        }
      }
    }
  }

  private boolean isValidSquare(int row, int column) {
    return 0 <= row && row < this.size && 0 <= column && column < this.size;
  }

  private static PMap<Square, Player> getInitialOwners(int size) {
    PMap<Square, Player> owners = HashTreePMap.empty();
    int mid = size / 2;
    owners = owners.plus(new Square(mid - 1, mid - 1), Player.WHITE);
    owners = owners.plus(new Square(mid - 1, mid), Player.BLACK);
    owners = owners.plus(new Square(mid, mid - 1), Player.BLACK);
    owners = owners.plus(new Square(mid, mid), Player.WHITE);
    return owners;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.size, this.owners);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Board) {
      Board that = (Board) obj;
      return this.size == that.size && Objects.equals(this.owners, that.owners);
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 0; row < this.size; ++row) {
      for (int col = 0; col < this.size; ++col) {
        Player owner = this.owners.get(new Square(row, col));
        if (owner == Player.WHITE) {
          builder.append('W');
        } else if (owner == Player.BLACK) {
          builder.append('B');
        } else {
          builder.append('_');
        }
      }
      builder.append('\n');
    }
    return builder.toString();
  }

  /**
   * @return The number of rows (= the number of columns) in this Reversi board.
   */
  public int size() {
    return this.size;
  }

  /**
   * @return A mapping from squares to the players currently occupying them.
   */
  public Map<Square, Player> getSquareOwners() {
    return this.owners;
  }

  /**
   * @return True if no squares remain that can be played by either player,
   *         false otherwise.
   */
  public boolean isComplete() {
    return this.possibleMoves.isEmpty() && this.pass().possibleMoves.isEmpty();
  }

  /**
   * @return The player that gets to choose a square next.
   */
  public Player getCurrentPlayer() {
    return this.player;
  }

  /**
   * @return The possible valid moves that the current player may choose from.
   */
  public Set<Square> getCurrentPossibleSquares() {
    return this.possibleMoves.keySet();
  }

  /**
   * Places a game piece for the current player at the given square.
   * 
   * @param square
   *          The square where the current player would like to place their
   *          piece. Must be a valid play: there must not already be a piece
   *          there, and placing a piece there must capture at least one new
   *          piece for the current player.
   * @return The new board with the given piece played, with all pieces that
   *         were captured by this play now occupied by the player, and with the
   *         current player now set to the opponent.
   */
  public Board play(Square square) {
    Player existingPlayer = this.owners.get(square);
    if (existingPlayer != null) {
      String message = "A %s piece already exists at %s";
      throw new IllegalArgumentException(String.format(message, existingPlayer, square));
    }
    PSet<Square> captures = this.possibleMoves.get(square);
    if (captures == null) {
      String message = "%s will not capture any pieces if placed at (%d,%d)";
      throw new IllegalArgumentException(String.format(message, this.player, square));
    }
    PMap<Square, Player> newMoves = this.owners.plus(square, this.player);
    for (Square capture : captures) {
      newMoves = newMoves.plus(capture, this.player);
    }
    return new Board(this.size, newMoves, this.player.opponent());
  }

  /**
   * Passes the current player's turn. Only valid when there are no possible
   * capturing moves for the current player.
   * 
   * @return A new board with the same layout as the current one, but with the
   *         current player now set to the opponent.
   */
  public Board pass() {
    Set<Square> validNextMoves = this.getCurrentPossibleSquares();
    if (!validNextMoves.isEmpty()) {
      String message = "%s cannot pass since there are valid moves: %s";
      throw new IllegalArgumentException(String.format(message, this.player, validNextMoves));
    }
    return new Board(this.size, this.owners, this.player.opponent());
  }
}
