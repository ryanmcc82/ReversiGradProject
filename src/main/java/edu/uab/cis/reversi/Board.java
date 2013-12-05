package edu.uab.cis.reversi;

import java.util.Arrays;
import java.util.Objects;

public class Board {
	private int size;
	private Player[][] squares;

	public Board() {
		this(8);
	}

	public Board(int size) {
		this.size = size;
		this.squares = new Player[size][size];
		int mid = this.size / 2;
		this.squares[mid - 1][mid - 1] = Player.WHITE;
		this.squares[mid - 1][mid] = Player.BLACK;
		this.squares[mid][mid - 1] = Player.BLACK;
		this.squares[mid][mid] = Player.WHITE;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(this.squares);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			Board that = (Board) obj;
			return Objects.deepEquals(this.squares, that.squares);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (int row = 0; row < this.size; ++row) {
			for (int col = 0; col < this.size; ++col) {
				if (this.squares[row][col] == Player.WHITE) {
					builder.append('W');
				} else if (this.squares[row][col] == Player.BLACK) {
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

	public boolean isValidSquare(int row, int column) {
		return 0 <= row && row < this.size && 0 <= column && column < this.size;
	}

	public boolean isValidMove(Player player, int row, int column) {
		int[][] directions = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 },
				{ 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };
		Player other = player == Player.WHITE ? Player.BLACK : Player.WHITE;
		for (int[] direction : directions) {
			int rowStep = direction[0];
			int columnStep = direction[1];
			int r = row + rowStep;
			int c = column + columnStep;
			if (this.isValidSquare(r, c) && this.squares[r][c] == other) {
				r += rowStep;
				c += columnStep;
				while (this.isValidSquare(r, c) && this.squares[r][c] == other) {
					r += rowStep;
					c += columnStep;
				}
				if (this.isValidSquare(r, c) && this.squares[r][c] == player) {
					return true;
				}
			}
		}
		return false;
	}
}
