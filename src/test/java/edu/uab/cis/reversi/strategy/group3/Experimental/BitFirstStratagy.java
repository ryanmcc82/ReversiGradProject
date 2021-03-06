package edu.uab.cis.reversi.strategy.group3.Experimental;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;
import edu.uab.cis.reversi.strategy.group3.BitBoardNode;
import edu.uab.cis.reversi.strategy.group3.Book;

public class BitFirstStratagy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;
    private Book openingBook;
    private boolean inBook = true;

    public BitFirstStratagy(){
        this.openingBook = new Book();
    }

    
    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        List<Move> movesList = board.getMoves();
        if(movesList.size() < 3){
            inBook = true;
        }

        Square move;
        if (inBook) {
            move = openingBook.checkBook(movesList);
            if (move == null) {
                inBook = false;
            } else {
                return move;
            }
        }
        
        BitBoardNode currentState = new BitBoardNode(board);
        HashMap<BitBoardNode, Square>  moveMap = BitBoardNode.moveToSquare7(board);
        
        BitBoardNode choiceState = currentState.getBestNewState();
        move = moveMap.get(choiceState);
        if(move == null) return Square.PASS;
       
        return move;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }
    
    

}
