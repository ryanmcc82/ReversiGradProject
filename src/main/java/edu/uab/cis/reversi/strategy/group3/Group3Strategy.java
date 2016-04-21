package edu.uab.cis.reversi.strategy.group3;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group3Strategy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;
    private Book openingBook;
    private boolean inBook = true;

    public Group3Strategy(){
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

    @Override
    public String toString() {
        return "bookOn: " +  true +
                "\tcornerWeight: " + BitBoardNode.CORNERW+
                "\txSquareWeight: " + BitBoardNode.XSQUAREW +
                "\tcSquareWeight: " + BitBoardNode.CSQUAREW +
                "\taSquareWeight: " + BitBoardNode.ASQUAREW +
                "\tmobilityWeight: " + BitBoardNode.MOBILITYW+
                "\tstabilityWeight: " + BitBoardNode.SABILITYW+
                "\tparityWeight: " + BitBoardNode.PARITY;
    }
}
