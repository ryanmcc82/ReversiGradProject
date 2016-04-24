package edu.uab.cis.reversi.strategy.group3;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ryan on 4/15/2016.
 */
public class VariableDeepStrategy implements Strategy {

    public enum State {
        SEARCHING,
        NEWROOT,
        NEWGAME,
        WAITING,
        END
    }
    private long timeLimit;
    private TimeUnit timeunit;
    private Book openingBook;
    private boolean inBook = true;

    private int moblitlityWeight;
    private boolean bookON;
    private int cornerWeight;
    private int xSqWeight;
    private int cSqWeight;
    private int aSqWeight;
    private int stabilityWeight;
    private int parityWeight;
    private int extrWeight;

    public VariableDeepStrategy(boolean bookOn, int mobWeight, int cornerW, int xSquareWeight, int cSquareWeight, int aSquareWeight, int stableW, int parityW, int undeterminedW ){
        moblitlityWeight = mobWeight;
        bookON = bookOn;
        cornerWeight = cornerW;
        xSqWeight = xSquareWeight;
        cSqWeight = cSquareWeight;
        aSqWeight = aSquareWeight;
        stabilityWeight = stableW;
        parityWeight = parityW;
        extrWeight =  undeterminedW;
        this.openingBook = new Book();
    }
    
    
    /**
     * Determines where the current player should play their next piece. Some
     * methods that may be useful for defining such a strategy:
     * <ul>
     * <li>{@link Board#getCurrentPossibleSquares()}</li>
     * <li>{@link Board#getCurrentPlayer()}</li>
     * <li>{@link Board#getSquareOwners()}</li>
     * <li>{@link Board#getPlayerSquareCounts()}</li>
     * </ul>
     *
     * @param board The current state of the Reversi board.
     * @return The square where the current player should play their next piece.
     */
    @Override
    public Square chooseSquare(Board board) {
        List<Move> movesList = board.getMoves();
        if(movesList.size() < 3){
            inBook = true;
        }

        Square move;
        if (inBook && bookON) {
            move = openingBook.checkBook(movesList);
            if (move == null) {
                inBook = false;
            } else {
                return move;
            }
        }

        BitBoardNode currentState = new BitBoardNode(board);
        HashMap<BitBoardNode, Square> moveMap = BitBoardNode.moveToSquare7(board);

        BitBoardNode choiceState = getBestVarScoredMove(currentState);
        move = moveMap.get(choiceState);
        if(move == null) return Square.PASS;

        return move;
    }

    public BitBoardNode getBestVarScoredMove(BitBoardNode currentstate) {
        ArrayList<BitBoardNode> moveList = currentstate.getMovesAndResults();
        BitBoardNode bestMove = currentstate;
        int bestscore = Integer.MIN_VALUE;

        ArrayList<BitBoardNode> tiedBest = new ArrayList<BitBoardNode>(moveList.size());

        for (BitBoardNode bitBoard : moveList) {
            bitBoard.getLegalMoves();
            int moveScore = - bitBoard.getVarCornerScore(cornerWeight, xSqWeight, aSqWeight, cSqWeight,parityWeight,extrWeight )
                    - (moblitlityWeight * bitBoard.getMobility());

            if (moveScore > bestscore) {
                bestMove = bitBoard;
                bestscore = moveScore;
                tiedBest.clear();
            } else if (moveScore == bestscore) {
                tiedBest.add(bitBoard);
            }
        }
        if (tiedBest.isEmpty()) {
            return bestMove;
        }
        tiedBest.add(bestMove);
        return tiedBest.get((int) (tiedBest.size() * Math.random()));
    }

    /**
     * Indicates to the strategy how much time will be allowed for each call to chooseSquare. If the
     * strategy takes longer than the allotted time, it will be considered to have lost the game.
     *
     * @param time The time allowed
     * @param unit The time unit of the time argument
     */
    @Override
    public void setChooseSquareTimeLimit(long time, TimeUnit unit) {
        this.timeLimit = time;
        this.timeunit = unit;
    }

    @Override
    public String toString() {
        return "bookOn: " +  bookON +
        "\tcornerWeight: " + cornerWeight+
                "\txSquareWeight: " + xSqWeight +
                "\tcSquareWeight: " + cSqWeight +
                "\taSquareWeight: " + aSqWeight +
                "\tmobilityWeight: " + moblitlityWeight+
                "\tstabilityWeight: " + stabilityWeight+
                "\tparityWeight: " + parityWeight;
    }

    class SearchTree implements Runnable
    {
        BitBoardNode requestedsSearchRoot;
        BitBoardNode searchRoot;
        BitBoardNode bestMove;
        BitBoardNode searchNode;
        private volatile State searchState;


        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            while ( searchState!=State.END){
                 if(searchState == State.NEWROOT){
                    restartSearch();
                    search();
                 }else if(searchState == State.NEWGAME){
                     bestMove = null;
                     searchRoot = null;
                     //TODO clear any HashMaps
                     searchRoot = new BitBoardNode(new Board());
                     searchState = State.WAITING;
                 } else if(searchState == State.SEARCHING) {
                     search();
                 }
            }
        }

        private void search(){
            while(searchState == State.SEARCHING){

            }
        }

      /**
       * Called by Master thread to notify when a new search is to be started
       * @param newRoot new root
       * @return
       */
        public synchronized BitBoardNode resetSearch(BitBoardNode newRoot){
            requestedsSearchRoot = newRoot;
            searchState = State.NEWROOT;
            return bestMove;
        }

        public synchronized void clearSearch(){
            requestedsSearchRoot = null;
            searchState = State.NEWGAME;
        }

        public synchronized void clearSearchDone(){
            searchState = State.WAITING;
        }

        private synchronized void restartSearch(){
            searchRoot = requestedsSearchRoot;
            searchState = State.SEARCHING;
        }

        public synchronized BitBoardNode getBestMove(){
            return bestMove;
        }

        public synchronized void setBestMove(BitBoardNode best){
            bestMove = best;
        }

        public State getState(){
            return searchState;
        }





    }

}
