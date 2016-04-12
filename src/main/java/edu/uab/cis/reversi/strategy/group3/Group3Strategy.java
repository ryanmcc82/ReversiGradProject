package edu.uab.cis.reversi.strategy.group3;

import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group3Strategy implements Strategy {
    private long timeLimit;
    private TimeUnit timeunit;
    
    public Group3Strategy(){
        
    }

    @Override
    public Square chooseSquare(Board board) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }

}
