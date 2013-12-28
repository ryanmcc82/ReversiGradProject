package edu.uab.cis.reversi;

import java.util.Objects;

public class Square {
  private int row, column;

  public Square(int row, int column) {
    super();
    this.row = row;
    this.column = column;
  }

  public int getRow() {
    return this.row;
  }

  public int getColumn() {
    return this.column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.column);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Square) {
      Square that = (Square) obj;
      return this.row == that.row && this.column == that.column;
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", this.row, this.column);
  }

}
