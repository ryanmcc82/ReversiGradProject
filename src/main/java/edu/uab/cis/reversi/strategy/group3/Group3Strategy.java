package edu.uab.cis.reversi.strategy.group3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.Strategy;

public class Group3Strategy implements Strategy {

    private long timeLimit;
    private TimeUnit timeunit;
    Square move;

    @Override
    public Square chooseSquare(Board board) {
        Square move;
        BitNode currentState = new BitNode(board);

        HashMap<BitNode, Square>  moveMap = BitNode.moveToSquare(board, currentState);
        search(currentState);
        BitNode choiceState = bestMove;
        move = moveMap.get(choiceState);
        if(move == null){
            if(moveMap.size() == 1||board.getCurrentPossibleSquares().size() == 1){
                return board.getCurrentPossibleSquares().iterator().next();
            }
            choiceState = currentState.getBestDMNewState();
            return  moveMap.get(choiceState);
        }
        return move;
    }

    BitNode requestedsSearchRoot;
    BitNode bestMove;


    public void search(BitNode board) {
        requestedsSearchRoot = board;
        if (requestedsSearchRoot != null) {
            int currentDepth = Long.bitCount(requestedsSearchRoot.occupied);

            int target = 3;
            if (currentDepth >= 64) {
                return;
            }

            Queue<BitNode> parentQueue = new LinkedList<>();
            Queue<BitNode> childQueue = new LinkedList<>();
            Queue<BitNode> endGameQue = new LinkedList<>();
            ArrayList bottomQueue = requestedsSearchRoot.getMovesAndResults();
            Queue<BitNode> temp;
            ArrayList<BitNode> tempA;
            BitNode tempN;
            childQueue.addAll(bottomQueue);
            if (timeLimit == 1000) {
                target = 4;
                if(bottomQueue.size()<5){
                    target = 5;
                    if(currentDepth>56){
                        target = 64 -  currentDepth;
                    }
                }
            }

            int targetDepth = currentDepth + target;
            if (64 - targetDepth < 0) {
                target = 64 -  currentDepth;
                targetDepth = 64;
            }
//            System.out.println("Start: "+childQueue.size() + " at " + currentDepth);
            do {//PopulateTree Loop
                temp = childQueue;
                childQueue = parentQueue;
                parentQueue = temp;
                while (!parentQueue.isEmpty()) {
                    tempN = parentQueue.poll();
                    tempA = tempN.getMovesAndResults();
                    if (tempA.size() == 0) {
                        endGameQue.add(tempN);
                    }
                    childQueue.addAll(tempA);
                }
                --target;
            } while (target > 0 && !childQueue.isEmpty());
//            System.out.println("Eval: "+childQueue.size() + " at " + targetDepth);
            while (!childQueue.isEmpty()) {//Evaluation Level
                BitNode evalNode = childQueue.poll();
                BitNode parent = evalNode.getParent();
                int score = evalNode.getBoardScore();
                if (parent == null) {
                    System.out.println(parent + ":" + evalNode + ":" + score);
                }

                parent.setChildScore(-score, evalNode, targetDepth);
                parentQueue.add(parent);
            }
            for (BitNode node : endGameQue) {
                node.setChildScore(node.getWiner(), null, 64);
            }
            while (!endGameQue.isEmpty()) {
                BitNode pushing;
                BitNode parent = endGameQue.poll();
                do {
                    pushing = parent;
                    parent = pushing.getParent();

                }
                while (parent.setChildScore(pushing.getHighestchildPathScore(), pushing, targetDepth) && parent != requestedsSearchRoot);
            }

            while (parentQueue.peek() != requestedsSearchRoot && parentQueue.size() != 0) {
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
            bestMove = requestedsSearchRoot.bestChild;
            return;
        }
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
        this.timeunit = unit;
        if(timeunit == TimeUnit.MILLISECONDS){
            timeLimit = time;
        }else {
            timeLimit = timeunit.toMillis(time);
        }
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
