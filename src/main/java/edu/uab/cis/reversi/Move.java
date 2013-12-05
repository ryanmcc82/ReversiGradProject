package edu.uab.cis.reversi;

import java.util.Objects;

public class Move {
	private int row, column;

	public Move(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.row, this.column);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move) {
			Move that = (Move) obj;
			return this.row == that.row && this.column == that.column;
		}
		return false;
	}

}
