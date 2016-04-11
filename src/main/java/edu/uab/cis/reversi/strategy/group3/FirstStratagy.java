package edu.uab.cis.reversi.strategy.group3;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class FirstStratagy implements Strategy {

    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        Set<Square> moves = board.getCurrentPossibleSquares();
        
        moves.parallelStream().forEach((square) -> {
            
        });
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        timelimit = time;
        // by default, do nothing
      }
    
    

}
