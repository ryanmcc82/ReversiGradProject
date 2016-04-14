package edu.uab.cis.reversi;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSequence;
import org.pcollections.PSet;
import org.pcollections.TreePVector;
/**
 * A strategy for choosing where a player should play their next piece.
 */
public interface Strategy {
  /**
   * Determines where the current player should play their next piece. Some
   * methods that may be useful for defining such a strategy:
   * <ul>
   * <li>{@link Board#getCurrentPossibleSquares()}</li>
   * <li>{@link Board#getCurrentPlayer()}</li>
   * <li>{@link Board#getSquareOwners()}</li>
   * <li>{@link Board#getPlayerSquareCounts()}</li>
   * </ul>
   * 
   * @param board
   *          The current state of the Reversi board.
   * @return The square where the current player should play their next piece.
   */
  public Square chooseSquare(Board board);

  /**
   * Indicates to the strategy how much time will be allowed for each call to chooseSquare. If the
   * strategy takes longer than the allotted time, it will be considered to have lost the game.
   *
   * @param time The time allowed
   * @param unit The time unit of the time argument
   */
  default public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
    // by default, do nothing
  }
}


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
	 
	@Override
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
		
	    board.setMoves(newMoves);
		board.setSquareOwners(newOwners);
	    board.setPlayerSquareCounts(newPlayerSquareCounts);
			
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
 
 




