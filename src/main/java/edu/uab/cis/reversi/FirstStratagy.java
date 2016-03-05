package edu.uab.cis.reversi;

import java.util.concurrent.TimeUnit;

public class FirstStratagy implements Strategy {

    long timelimit;
    @Override
    public Square chooseSquare(Board board) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        timelimit = time;
        // by default, do nothing
      }
    
    

}
