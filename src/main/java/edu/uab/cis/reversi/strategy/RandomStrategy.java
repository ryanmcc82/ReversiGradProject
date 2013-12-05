package edu.uab.cis.reversi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Strategy;

public class RandomStrategy implements Strategy {

	@Override
	public Move getMove(Player player, Board board) {
		List<Move> possibleMoves = new ArrayList<>();
		for (int row = 0; row < board.size(); ++row) {
			for (int column = 0; column < board.size(); ++column) {
				if (board.isValidMove(player, row, column)) {
					possibleMoves.add(new Move(row, column));
				}
			}
		}
		int index = new Random().nextInt(possibleMoves.size());
		return possibleMoves.get(index);
	}
}
