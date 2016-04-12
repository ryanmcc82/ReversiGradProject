package edu.uab.cis.reversi.strategy.group3;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class BitFirstStratagy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;

    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        Square move;
        BitBoardNode currentState = new BitBoardNode(board);
        System.out.println("\nCurrent\n" + currentState);
        System.out.println(currentState.moverPieces + ":" + currentState.opponentPieces);
        HashMap<BitBoardNode, Square>  moveMap = BitBoardNode.moveToSquare7(board);
        
        BitBoardNode choiceState = currentState.getBestDoubleMobility();
        move = moveMap.get(choiceState);
        System.out.println(moveMap);
        System.out.println("\nchoice\n" + choiceState + "\n" + move);
        if(move == null) return Square.PASS;
       
        return move;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }
    
    

}
