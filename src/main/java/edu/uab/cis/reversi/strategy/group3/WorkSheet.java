package edu.uab.cis.reversi.strategy.group3;

import java.util.List;
import java.util.Map;

import org.pcollections.PMap;
import org.pcollections.PSequence;
import org.pcollections.PSet;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class WorkSheet {

	class UpdateBoardState implements Runnable
	 {
		 private Thread T;
		 private String ThreadName;
		 private Board board;
		 private Move move;
	 
      UpdateBoardState(Board board, Move move)
	 {
		 this.board = board;
		 this.move = move;
	 }
	 

	public void run() {
		// TODO Auto-generated method stub
		List<Move> moves = board.getMoves();
		
		PSequence<Move> newMoves = ((PSequence<Move>) moves).plus(move);
		
		Square square = move.getSquare();
		Player player = move.getPlayer();
		
		Player Opponent = board.getCurrentPlayer().opponent();
		
		PMap<Square, Player> newOwners = (PMap<Square, Player>) board.getSquareOwners();
			
		newOwners.plus(square, player);
		PMap<Square, PSet<Square>> possiblesquares = board.getPossibleSquares();
				
		PSet<Square> captures = possiblesquares.get(square);
	    for (Square capture : captures) {
	      newOwners = newOwners.plus(capture, player);
	    }
	    
	    Map<Player, Integer> playerSquareCounts = board.getPlayerSquareCounts();
	    
	    
	    int playerSquareCount = playerSquareCounts.get(player) + captures.size() + 1;
	    int opponentSquareCount = playerSquareCounts.get(Opponent) - captures.size();
	    
	    PMap<Player, Integer> newPlayerSquareCounts = (PMap<Player, Integer>) board.getPlayerSquareCounts();
	    newPlayerSquareCounts = newPlayerSquareCounts.plus(board.getCurrentPlayer(), playerSquareCount);
	    newPlayerSquareCounts = newPlayerSquareCounts.plus(Opponent, opponentSquareCount);
		
//	    board.setMoves(newMoves);
//		board.setSquareOwners(newOwners);
//	    board.setPlayerSquareCounts(newPlayerSquareCounts);
			
	}
}

class ReadBoardState implements Runnable
{
	 private Thread T;
	 private String ThreadName;
	 private Board board;
	 
	 ReadBoardState(Board board)
	 {
		 this.board = board;
	 }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		
		
	}
}


//public void setMoves(PSequence<Move> M){
//    this.moves = M;
//}

//public void setSquareOwners(PMap<Square, Player> M)
//{
//    this.owners = M;
//}

}
