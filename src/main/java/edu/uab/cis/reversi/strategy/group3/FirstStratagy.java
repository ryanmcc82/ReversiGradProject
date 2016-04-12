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
    private Book openingBook;
    private boolean inBook = true;

    public FirstStratagy() {
        openingBook = new Book();
    }
    @Override
    public Square chooseSquare(Board board) {

        Square move;
        if (inBook) {
            move = openingBook.checkBook(board.getMoves());
            if (move == null) {
                inBook = false;
            } else
                return move;

        }
        move = Square.PASS;
        
        int minMobility =  Integer.MAX_VALUE;
        Set<Square> moves = board.getCurrentPossibleSquares();
        for(Square moveP : moves){
            int mobility = board.play(moveP).getCurrentPossibleSquares().size();
            if (mobility < minMobility) {
                move = moveP;
                minMobility = mobility ;
            }
        }
       
        
        return move;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }
    
    

}
