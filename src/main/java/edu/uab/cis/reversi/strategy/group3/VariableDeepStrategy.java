package edu.uab.cis.reversi.strategy.group3;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

import java.util.*;
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
    private SearchTree tree;
    Thread thread;

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
        tree = new SearchTree();
        this.thread = new Thread(tree);
        thread.start();
    }

    public VariableDeepStrategy(){
        tree = new SearchTree();
        Thread thread = new Thread(tree);
        thread.start();
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
        long startTime = System.currentTimeMillis() - 700L;
//        System.out.println(startTime);
        BitNode currentState = new BitNode(board);
        tree.resetSearch(currentState);
        currentState = new BitNode(board);
        Square move;
        HashMap<BitNode, Square> moveMap = BitNode.moveToSquare7(board);
        BitNode choiceState = currentState.getBestDMNewState();
        move = moveMap.get(choiceState);
        if(board.getMoves().size()< 2){
            return move;
        }
        BitNode treeChoice;
        long elapsed= System.currentTimeMillis() - startTime;
        while(elapsed < timeLimit){
            elapsed = System.currentTimeMillis() - startTime;
        }
        treeChoice = tree.getBestMove();
        if(treeChoice != choiceState && treeChoice !=null){
            move = moveMap.get(treeChoice);
        }
        return move;
    }

//    public BitNode getBestVarScoredMove(BitNode currentstate) {
//        LinkedList<BitNode> moveList = currentstate.getMovesAndResults();
//        BitNode bestMove = currentstate;
//        int bestscore = Integer.MIN_VALUE;
//
//        ArrayList<BitNode> tiedBest = new ArrayList<BitNode>(moveList.size());
//
//        for (BitNode bitBoard : moveList) {
//            bitBoard.getLegalMoves();
//            int moveScore = - bitBoard.getVarCornerScore(cornerWeight, xSqWeight, aSqWeight, cSqWeight,parityWeight,extrWeight )
//                    - (moblitlityWeight * bitBoard.getMobility());
//
//            if (moveScore > bestscore) {
//                bestMove = bitBoard;
//                bestscore = moveScore;
//                tiedBest.clear();
//            } else if (moveScore == bestscore) {
//                tiedBest.add(bitBoard);
//            }
//        }
//        if (tiedBest.isEmpty()) {
//            return bestMove;
//        }
//        tiedBest.add(bestMove);
//        return tiedBest.get((int) (tiedBest.size() * Math.random()));
//    }

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

    @Override
    public void finalize(){
        tree.end();
    }

    class SearchTree implements Runnable
    {
        BitNode requestedsSearchRoot;
        BitNode searchRoot;
        BitNode bestMove;
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
            this.searchState = State.WAITING;
            while ( searchState != State.END){
//                System.out.println(searchState);
                 if(searchState == State.NEWROOT){
                    restartSearch();
                    search();
                 }else if(searchState == State.NEWGAME){
                     bestMove = null;
                     searchRoot = null;
                     //TODO clear any HashMaps
                     searchRoot = new BitNode(new Board());
                     searchState = State.WAITING;
                 } else if(searchState == State.SEARCHING) {
                     System.out.println("Searching");
                     search();
                 }
            }
        }

        private void search(){
            if(requestedsSearchRoot != null){
//                System.out.println("test");
                int currentDepth = Long.bitCount(searchRoot.occupied);
                int step= 5;
                int target = step;
                int targetDepth = currentDepth + target;
                if(64 - targetDepth < 0){target = targetDepth - 64; targetDepth = 64;}
                Queue<BitNode> parentQueue = new LinkedList<>();
                Queue<BitNode> childQueue = new LinkedList<>();
                Queue<BitNode> bottomQueue = requestedsSearchRoot.getMovesAndResults();
                Queue<BitNode> temp;
                while(searchState == State.SEARCHING){
                    childQueue.addAll(bottomQueue);
                    parentQueue.clear();
                    do {//PopulateTree Loop
                        temp = childQueue;
                        childQueue = parentQueue;
                        parentQueue = temp;
                        while (!parentQueue.isEmpty()) {
                            childQueue.addAll(parentQueue.poll().getMovesAndResults());
                        }
                        --target;
                    }while(target > 0 && searchState == State.SEARCHING);
//                    System.out.println("firstWhile");
                    bottomQueue.clear();
                    while(!childQueue.isEmpty()){//Evaluation Level
                        BitNode evalNode = childQueue.poll();
                        BitNode parent = evalNode.getParent();
                        int score = evalNode.getBoardScore();
                        if(parent == null){
                            System.out.println(parent + ":"+evalNode+":"+score);
                        }
//                        System.out.println(parent + ":"+evalNode+":"+score);
                        parent.setChildScore( score, evalNode, targetDepth);
                        parentQueue.add(parent);
                        bottomQueue.add(evalNode);
                    }
//                    System.out.println("secondWhile");
                    while(parentQueue.peek() != searchRoot && searchState == State.SEARCHING) {
                        temp = childQueue;
                        childQueue = parentQueue;
                        parentQueue = temp;
                        while (!childQueue.isEmpty()) {
                            BitNode evalNode = childQueue.poll();
                            BitNode parent = evalNode.getParent();
                            parent.setChildScore(evalNode.getHighestchildPathScore(), evalNode, targetDepth);
                            parentQueue.add(parent);
                        }
                    }
                    setBestMove(searchRoot.bestChild);
//                    System.out.println("searched: "+ (targetDepth - currentDepth));
                    step = (step)/2;
                    if(step == 0){target = 1;}else{target = step;}
                    if(targetDepth + target> 64){
                        target = 64 -  targetDepth;
                        targetDepth = 64;
                    }
                    else{
                        targetDepth = targetDepth + target;
                    }
                }
            }
        }

      /**
       * Called by Master thread to notify when a new search is to be started
       * @param newRoot new root
       * @return
       */
        public synchronized void resetSearch(BitNode newRoot){
//            System.out.println("reset");
            requestedsSearchRoot = newRoot;
            searchState = State.NEWROOT;
            bestMove = null;
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

        public synchronized BitNode getBestMove(){
            searchState = State.WAITING;
            return bestMove;
        }

        public synchronized void setBestMove( BitNode best){
            if(searchRoot == requestedsSearchRoot){
                bestMove = best;
            }
        }

        public State getState(){
            return searchState;
        }

        public synchronized void end(){
            System.out.println("END!!");
            searchState = State.END;
        }





    }

}

