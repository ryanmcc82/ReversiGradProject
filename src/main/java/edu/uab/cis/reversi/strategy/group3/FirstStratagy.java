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
    private static int id = 0;
    private int thisID;
    int testcount = 0;

    public FirstStratagy() {
        openingBook = new Book();
        this.thisID = uniqueID();
//        System.out.println("Done Loading: " + thisID);
    }
    
    public static int uniqueID(){
        int temp = id;
        ++id;
        return temp;
    }
    @Override
    public Square chooseSquare(Board board) {
        List<Move> movesList = board.getMoves();
        if(movesList.size() < 3){
            inBook = true;
        }
        testcount  = movesList.size();
        

        Square move;
        if (inBook) {
            move = openingBook.checkBook(movesList);
            if (move == null) {
//                System.out.println("BookDepth: " + testcount + " : Fail " + thisID);
//                System.out.println(movesList);
                inBook = false;
            } else {
//                System.out.println("BookDepth: " + testcount + " : " + move + " : " + thisID);
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
