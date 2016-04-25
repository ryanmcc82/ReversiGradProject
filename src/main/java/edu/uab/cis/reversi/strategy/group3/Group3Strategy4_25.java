package edu.uab.cis.reversi.strategy.group3;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Group3Strategy4_25 implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;
    private Book openingBook;
    private boolean inBook = true;


    public Group3Strategy4_25(){
        this.openingBook = new Book();
    }

    
    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        Square move;
        BitBoardNode currentState = new BitBoardNode(board);
        HashMap<BitBoardNode, Square>  moveMap = BitBoardNode.moveToSquare7(board);
        
        BitBoardNode choiceState = currentState.getBestDMNewState();
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
        return "bookOn: " +  false +
                "\tcornerWeight: " + BitBoardNode.CORNERW+
                "\txSquareWeight: " + BitBoardNode.XSQUAREW +
                "\tcSquareWeight: " + BitBoardNode.CSQUAREW +
                "\taSquareWeight: " + BitBoardNode.ASQUAREW +
                "\tmobilityWeight: " + 0+
                "\tDoubleMobWeight: " + BitBoardNode.DMOBILITYW +
                "\tstabilityWeight: " + BitBoardNode.SABILITYW+
                "\tparityWeight: " + BitBoardNode.PARITY;
    }
}
