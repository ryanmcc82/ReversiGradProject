package edu.uab.cis.reversi.strategy.group3;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class FirstStratagy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;
    private Book openingBook;
    private boolean inBook = true;
    int testcount = 0;

    public FirstStratagy() {
        openingBook = new Book();
    }
    @Override
    public Square chooseSquare(Board board) {
        List<Move> movesList = board.getMoves();
        if(movesList.size() < 1)inBook = true;

        Square move;
        if (inBook) {
            move = openingBook.checkBook(movesList);
            if (move == null) {
                System.out.println("BookDepth: " + testcount);
                inBook = false;
            } else {
                testcount++;
                return move;
            }
        }
        move = Square.PASS;

        int minMobility = Integer.MAX_VALUE;
        Set<Square> moves = board.getCurrentPossibleSquares();
        for (Square moveP : moves) {
            int mobility = board.play(moveP).getCurrentPossibleSquares().size();
            if (mobility < minMobility) {
                move = moveP;
                minMobility = mobility;
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
