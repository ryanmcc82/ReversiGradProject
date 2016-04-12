package edu.uab.cis.reversi.strategy.group3;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class FirstStratagy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;

    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        Square move;
        
        int minMobility =  Integer.MAX_VALUE;
        Set<Square> moves = board.getCurrentPossibleSquares();
        for(Square moveP : moves){
            int mobility = board.play(moveP).getMoves().size();
            if (mobility < minMobility) {
                move = moveP;
                minMobility = mobility ;
            }
        }
       
        
        return null;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }
    
    

}
