
package edu.uab.cis.reversi.strategy.group3;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class BitBoardNode {

    BitBoardNode parent;
    ArrayList<BitBoardNode> parents;//note if using hashTable to cut down on searching could have multiple parents.
    ArrayList<BitBoardNode> children;
    final long moverPieces;
    final long opponentPieces;
    final long occupied; 
    final long unoccupied;
    long moves;
    int mobility;
    int score;
    boolean movesSearched = false;
    static final long bitmask = 1;

    /* Static References */
    /*********************************************************************************************************************************/
    // ###################
    // # O O O O O O O O #
    // # O X X X X X X O #
    // # O X X X X X X O #
    // # O X X X X X X O #
    // # O X X X X X X O #
    // # O X X X X X X O #
    // # O X X X X X X O #
    // # O O O O O O O O #
    // ###################
    public static final long patternEdges;
    static {
        Long temp = 0b11111111L | 0b11111111L << (56);
        for (int i = 1; i < 7; i++) {
            temp = temp | 0b10000001L << (8 * i);
        }
        patternEdges = temp;
    }
    
    public static final long patternCorners;
    static{
        Long temp = 0b10000001L| 0b10000001L <<(56);
        patternCorners = temp;
    }

    public static final long positionArray[];
    static {
        positionArray = new long[64];
        for (int i = 0; i < 64; i++) {
            positionArray[i] = 0b1L << i;
        }
    }

    public static final long ajacentArray[];
    static {
        ajacentArray = new long[64];
        for (int i = 0; i < 64; i++) {
            if ((0b1L << i & patternEdges) == 0) { // if its not on the edge get
                                                   // all 8 surounding positions
                ajacentArray[i] = 0b1L << (i + 1) | 0b1L << (i - 1)
                        | 0b111L << (i + 7) | 0b111L << (i - 9);
            } else if (i == 0) {// corner
                ajacentArray[i] = 0b1L << (1 + i) | 0b11L << (8 + i);
            } else if (i == 7) {// corner
                ajacentArray[i] = 0b1L << (i - 1) | 0b11L << (7 + i);
            } else if (i == 63) {// corner
                ajacentArray[i] = (0b1L << (i - 1)) | 0b11L << (i - 9);
            } else if (i == 56) {// corner
                ajacentArray[i] = 0b1L << (1 + i) | 0b11L << ( i - 8);
            }else if(i>56){//top Row - corners
                ajacentArray[i] = 0b101L << (i-1) | 0b111L << (i - 9);
            }else if (i< 7){// Bottom Row - corners
                ajacentArray[i] = 0b101L << (i-1) | 0b111L << (i + 7);
            }else if ((i + 1) % 8 == 0) {// left most column - corners
                ajacentArray[i] = 0b1L << (i - 1) | 0b11L << (i + 7)
                        | 0b11L << (i - 9);
            } else {// right most column - corners
                ajacentArray[i] = 0b1L << (i + 1) | 0b11L << (i + 8)
                        | 0b11L << (i - 8);
            }
        }
    }

    public static long getforwardSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0L;
        for (int i = 0; i < length; i++) {
            tempH = tempH | (temp << (9 * i));
        }
        return tempH;
    }

    public static long getTopforwardSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0L;
        for (int i = 1; i < length + 1; i++) {
            tempH = tempH | (temp << (63 - (9 * i)));
        }
        return tempH;
    }

    public static long getBackSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0b0L;
        for (int i = 0; i < (length); i++) {
            tempH = tempH | (temp << (7 * i));
        }
        return tempH;
    }

    public static long getTopBackSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0b0L;
        for (int i = 0; i < (length); i++) {
            tempH = tempH | (temp << (56 - (7 * i)));
        }
        return tempH;
    }

    public static final long vertRay;
    static {
        long temp = 0b1L;
        long tempH = 0b0L;
        for (int i = 0; i < 8; i++) {
            tempH = tempH | (temp << (8 * i));
        }
        vertRay = tempH;
    }

    public static long horizontalRay(int length) {
        long temp = 0b1L;
        long tempH = 0b0L;
        for (int i = 1; i < (length + 1); i++) {
            tempH = tempH | (temp << i);
        }
        return tempH;
    }

    /**
     * Translates the offset of each of the bits around a square
     * to the corresponding directional Ray array. 
     **/
    public static int translationArray[] = { 0, 1, 2, -1, -1, -1, -1, -1, 3,
            -1, 4, -1, -1, -1, -1, -1, 5, 6, 7 };

    /** 
     * Static array of rays from each ray in each direction.
     * Used to find Legal moves and what tiles to flip. 
     **/
    public static final long rayArray[][];
    static {
        rayArray = new long[64][8];
        for (int i = 0; i < 64; i++) {
            int leftRayLength = 7 - (i % 8);
            int rightRayLength = (i % 8);
            long leftUp = getforwardSlantRay(leftRayLength) << (i + 9);
            if(i + 9 > 63 )leftUp = 0b0L;
            rayArray[i][7] = leftUp;

            long up = vertRay << (i + 8);
            if(i > 63-8)up = 0b0L;
            rayArray[i][6] = up;

            long rightUp = getBackSlantRay(rightRayLength) << (i + 7);
            if(i + 7 > 62 )rightUp = 0b0L;
            rayArray[i][5] = rightUp;

            long left = horizontalRay(leftRayLength) << i;
            rayArray[i][4] = left;

            long right = horizontalRay(rightRayLength) << (i
                    - (rightRayLength + 1));
            if(i<8){right = horizontalRay(rightRayLength)>>>1;}
            rayArray[i][3] = right;

            long leftDown = getTopBackSlantRay(leftRayLength) >>> (63 - i);
            rayArray[i][2] = leftDown;

            long down = vertRay >>> (64 - i);
            if(i==0)down = 0b0L;
            rayArray[i][1] = down;

            long rightDown = getTopforwardSlantRay(rightRayLength) >>> (63 - i);
            rayArray[i][0] = rightDown;
        }

    }

    /*********************************************************************************************************************************/

    public BitBoardNode(long moverPieces, long opponentPieces){
        this.moverPieces = moverPieces;
        this.opponentPieces = opponentPieces;
        this.occupied = opponentPieces | moverPieces;
        this.unoccupied = ~occupied;
        
    }
    
//    private BitBoardNode(long moverPieces, long opponentPieces, long move){
//        //TODO Make constructor
//        this.moverPieces = moverPieces;
//        this.opponentPieces = opponentPieces;
//        this.moves = moves;
//        this.occupied = opponentPieces | moverPieces;
//        this.unoccupied = ~occupied;
//    }
    
    public BitBoardNode(Board boardObject){
        Map<Square, Player> owners = boardObject.getSquareOwners();
        this.moverPieces = owners.entrySet().stream().filter( e -> e.getValue().equals(boardObject.getCurrentPlayer()))
        .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.opponentPieces  = owners.entrySet().stream().filter( e -> !e.getValue().equals(boardObject.getCurrentPlayer()))
                .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);        
        this.occupied = opponentPieces | moverPieces;
        this.unoccupied = ~occupied;
        getLegalMoves();
    }
    
    private long squareToLong(Square square){
        int index = square.getColumn() + (8 * (square.getRow()));
        long lindex = 0b1L << index;
        return lindex;
    }
    
    public ArrayList<BitBoardNode> getMovesAndResults(){
        if (children == null) {

            children = new ArrayList<BitBoardNode>();
            if(movesSearched){
                return populateMoves();
            }

            long tempUnOcc = unoccupied;
            long searchBit = Long.lowestOneBit(tempUnOcc);
            long surrounding;
            long surroundingOpp;
            this.moves = 0L;

            long searchDirBit;
            int searchDirDiff;
            int squareIndex;
            int closestEmptyIndex;
            int closestMoverIndex;
            long searchDirRay;
            long moverRayIntersect;
            long cancleDirRay;
            /**
             * used to clear bits in SearchDirRay that occer after
             * closestMoverBit
             */

            // if(Long.bitCount(occupied) < 32){//Note May should skip this
            // comparison and just default to one or the other
            while (searchBit != 0L) {
                squareIndex = Long.numberOfTrailingZeros(searchBit);
                surrounding = ajacentArray[squareIndex];// gets surrounding squares from static table
                surroundingOpp = surrounding & opponentPieces;
                long moverResult = moverPieces;
                while (surroundingOpp != 0L) {// if none of the surrounding squares are an opponent then its not a valid move

                    searchDirBit = Long.lowestOneBit(surroundingOpp);
                    searchDirDiff = Long.numberOfTrailingZeros(searchDirBit)
                            - squareIndex;// This diff lets us search in a diretion using bitshift.
                    searchDirRay = rayArray[squareIndex][translationArray[searchDirDiff + 9]];
                    moverRayIntersect = searchDirRay & moverPieces;
                    if (moverRayIntersect != 0L) {// if mover has no pieces in ray path its not valid move.
                        if (searchDirDiff > 0) {
                            closestMoverIndex = Long
                                    .numberOfTrailingZeros(moverRayIntersect);
                            closestEmptyIndex = Long
                                    .numberOfTrailingZeros(searchDirRay
                                            & unoccupied);
                            if (closestMoverIndex < closestEmptyIndex) {
                                cancleDirRay = rayArray[closestMoverIndex][translationArray[searchDirDiff + 9]];
                                moverResult = moverResult | searchBit
                                        | (searchDirRay ^ cancleDirRay);
                                this.moves = this.moves |searchBit;//add square to valid Moves
                            }
                        } else {
                            closestMoverIndex = Long
                                    .numberOfLeadingZeros(moverRayIntersect);
                            closestEmptyIndex = Long
                                    .numberOfLeadingZeros(searchDirRay
                                            & unoccupied);
                            if (closestMoverIndex < closestEmptyIndex) {
                                cancleDirRay = rayArray[63 - closestMoverIndex][translationArray[searchDirDiff + 9]];
                                moverResult = moverResult | searchBit
                                        | (searchDirRay ^ cancleDirRay);
                                this.moves = this.moves |searchBit;//add square to valid Moves
                            }
                        }
                    }

                    if (moverResult != moverPieces) {
                        long newOpponent = moverResult;
                        long newMover = (opponentPieces & moverResult)
                                ^ opponentPieces;
                        children.add(new BitBoardNode(newMover, newOpponent));
                    }
                    surroundingOpp = surroundingOpp & ~searchDirBit;// zero's
                                                                    // search
                                                                    // Direction
                }

                tempUnOcc = tempUnOcc ^ searchBit;// sets searched bit to zero
                searchBit = Long.lowestOneBit(tempUnOcc);// finds new
                                                         // bit(square) to
                                                         // search.
            }
        }
        movesSearched = true;
        mobility = Long.bitCount(moves);
        return children;
    }
    
    private ArrayList<BitBoardNode> populateMoves(){
        long tempMoves = this.moves;
        while(tempMoves != 0L){
            long tempMove = Long.highestOneBit(tempMoves);
            children.add(play(tempMove));
            tempMoves = tempMoves ^ tempMove;
        }
        return children;
    }
    
    public BitBoardNode play(long move){
       long movers = this.moverPieces;
       long opponent = this.opponentPieces; 
       long moverResult = movers | move;
       if(move == 0L) return this;
       long surroundingOpp = opponent & ajacentArray[Long.numberOfTrailingZeros(move)];
       long searchDirBit;
       long searchDirRay;
       long moverRayIntersect;
       long cancleDirRay;/**used to clear bits in SearchDirRay that occer after closestMoverBit */
       
       int closestEmptyIndex;
       int closestMoverIndex;
       
       /**searchDirDiff is a number between -9 and 9 its input into the translation 
        * Array returns a number 1-8 for the 8 directions next to a square that number
        * can then be used to get the group of squares(called searchDirRay) proceeding away from the search 
        * in that given direction */
       int searchDirDiff;
       int moveSquareIndex = Long.numberOfTrailingZeros(move);
       
       while (surroundingOpp != 0L) {
           
           searchDirBit = Long.lowestOneBit(surroundingOpp);
           
           searchDirDiff = Long.numberOfTrailingZeros(searchDirBit)
                   - moveSquareIndex;// This diff lets us search in a diretion using bitshift.
           searchDirRay = rayArray[moveSquareIndex][translationArray[searchDirDiff + 9]];
           moverRayIntersect = searchDirRay & movers;
           
            if(moverRayIntersect!= 0L){// if mover has no pieces in ray path its not valid move.
                    if(searchDirDiff > 0){
                        closestMoverIndex = Long.numberOfTrailingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfTrailingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex){
                            cancleDirRay = rayArray[closestMoverIndex][translationArray[searchDirDiff + 9]];
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);
//                            System.out.println("\nSearchDirBit;Ray;CancelRay\n");
//                            BitBoardDriver.printSBoard(searchDirBit);
//                            BitBoardDriver.printSBoard(searchDirRay);
//                            BitBoardDriver.printSBoard(cancleDirRay);
                        }
                    }else{
                        closestMoverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex) {
                            cancleDirRay = rayArray[63- closestMoverIndex][translationArray[searchDirDiff + 9]];
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);//

//                            System.out.println("\nElse:SearchDirBit;Ray;CancelRay\n");
//                            BitBoardDriver.printSBoard(searchDirBit|move);
//                            BitBoardDriver.printSBoard(searchDirRay);
//                            BitBoardDriver.printSBoard(cancleDirRay);
                            
                        }
                    }
                }
            
           
           
           surroundingOpp = surroundingOpp & ~searchDirBit;// zero's search
           // Direction
       }
       long newOpponent = moverResult;
       long newMover = (opponent & moverResult) ^ opponent;
//       System.out.println("\nNewOpponent\n");
//       BitBoardDriver.printSBoard(newOpponent);
//       System.out.println("\nNewMover\n");
//       BitBoardDriver.printSBoard(newMover);
       return  new BitBoardNode(newMover, newOpponent);
    }

    public long getLegalMoves() {
        // Later in game its faster to look at just empty spaces.
        // Early in game might be faster to look at it from occupied spaces. or
        // weed out based on boarder squares
        if(!movesSearched){
        long movers = this.moverPieces;
        long opponent = this.opponentPieces;
        long occupied = this.occupied; // keep this allocated
        long unoccupied = this.unoccupied;

        long tempUnOcc = unoccupied;
        long searchBit = Long.lowestOneBit(tempUnOcc);
        long surrounding;
        long surroundingOpp;
        long moves = 0L;

        long searchDirBit;
        int searchDirDiff;
        int squareIndex;
        int closestEmptyIndex;
        int closestMoverIndex;
        long searchDirRay;
        long moverRayIntersect;

        // if(Long.bitCount(occupied) < 32){//Note May should skip this
        // comparison and just default to one or the other
        while (searchBit != 0L) {
            squareIndex = Long.numberOfTrailingZeros(searchBit);
            surrounding = ajacentArray[squareIndex];// gets surrounding squares from static table
            surroundingOpp = surrounding & opponent;

            while (surroundingOpp != 0L) {// if none of the surrounding squares are an opponent then its not a valid move

                searchDirBit = Long.lowestOneBit(surroundingOpp);
                searchDirDiff = Long.numberOfTrailingZeros(searchDirBit)
                        - squareIndex;// This diff lets us search in a diretion using bitshift.
                searchDirRay = rayArray[squareIndex][translationArray[searchDirDiff + 9]];
                moverRayIntersect = searchDirRay & movers;
                if(moverRayIntersect!= 0L){// if mover has no pieces in ray path its not valid move.
                    if(searchDirDiff > 0){
                        closestMoverIndex = Long.numberOfTrailingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfTrailingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex){
                            this.moves = this.moves |searchBit;//add square to valid Moves
                            break;//end search if found
                        }
                    }else{
                        closestMoverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex) {
                            this.moves = this.moves |searchBit;//add square to valid Moves
                            break;//end search if found
                        }
                    }
                }

                surroundingOpp = surroundingOpp & ~searchDirBit;// zero's search
                                                                // Direction
            }

            tempUnOcc = tempUnOcc ^ searchBit;// sets searched bit to zero
            searchBit = Long.lowestOneBit(tempUnOcc);// finds new bit(square) to
                                                     // search.
        }
        
        }
        movesSearched = true;
        mobility = Long.bitCount(moves);
        return moves;
    }
    
    public int getMobility(){
        return mobility;
    }
    
    public static HashMap<BitBoardNode, Square> moveToSquare(Board boardparent){
        return 
        (HashMap<BitBoardNode, Square>) boardparent.getCurrentPossibleSquares().stream()
        .collect( Collectors.toMap( (Square square) -> new BitBoardNode(boardparent.play(square)),(Square square) -> square ));
    }
    
    public static HashMap<BitBoardNode, Square> moveToSquare7(Board boardparent){
        HashMap<BitBoardNode, Square> map = new HashMap<BitBoardNode, Square>();
        for(Square square: boardparent.getCurrentPossibleSquares()){
            map.put(new BitBoardNode(boardparent.play(square)), square);
        }
       
        return map;
    }
    
    public static BitBoardNode getBestDoubleMobility(
            ArrayList<BitBoardNode> moveList, BitBoardNode currentstate) {
        
        BitBoardNode bestMove = currentstate;
        int bestscore = Integer.MIN_VALUE;

        ArrayList<BitBoardNode> tiedBest = new ArrayList<BitBoardNode>(moveList.size());

        for (BitBoardNode bitBoard : moveList) {
            int mobilityScore = //Long.bitCount(bitBoard.play(0L).getLegalMoves())
                    - bitBoard.getMobility();

            if (mobilityScore > bestscore) {
                bestMove = bitBoard;
                bestscore = mobilityScore;
                tiedBest.clear();
            } else if (mobilityScore == bestscore) {
                tiedBest.add(bitBoard);
            }
        }
        if (tiedBest.isEmpty()) {
            return bestMove;
        }
        tiedBest.add(bestMove);
        return tiedBest.get((int) (tiedBest.size() * Math.random()));
    }
    
    public BitBoardNode getBestDoubleMobility() {
        BitBoardNode currentstate = this;
        ArrayList<BitBoardNode> moveList = this.getMovesAndResults();
        BitBoardNode bestMove = currentstate;
        int bestscore = Integer.MIN_VALUE;

        ArrayList<BitBoardNode> tiedBest = new ArrayList<BitBoardNode>(moveList.size());

        for (BitBoardNode bitBoard : moveList) {
            bitBoard.getLegalMoves();
//            System.out.println(bitBoard);
            int mobility = //Long.bitCount(bitBoard.play(0L).getLegalMoves())
                    - bitBoard.getMobility();

            if (mobility > bestscore) {
                bestMove = bitBoard;
                bestscore = mobility;
                tiedBest.clear();
            } else if (mobility == bestscore) {
                tiedBest.add(bitBoard);
            }
        }
        if (tiedBest.isEmpty()) {
            return bestMove;
        }
        tiedBest.add(bestMove);
        return tiedBest.get((int) (tiedBest.size() * Math.random()));
    }
    
    public int getCornerScore(){
        long emptyCorners = patternCorners & unoccupied;
        long dangerZones = 0L;
        long cornerBit;
        while(emptyCorners != 0b0L){
            cornerBit = Long.highestOneBit(emptyCorners);
            dangerZones = dangerZones | ajacentArray[Long.numberOfTrailingZeros(cornerBit)];
            emptyCorners = emptyCorners ^ cornerBit;
        }
        
        int moverscore = 25 * Long.bitCount(moverPieces & patternCorners) 
                - (15 * Long.bitCount(moverPieces & dangerZones)) ;
        
        int opponentscore = 25 * Long.bitCount(opponentPieces & patternCorners)-
                (15 * Long.bitCount(moverPieces & dangerZones));
        return moverscore - opponentscore;
    }
    
    public BitBoardNode getBestMobilityCorners() {
        BitBoardNode currentstate = this;
        ArrayList<BitBoardNode> moveList = this.getMovesAndResults();
        BitBoardNode bestMove = currentstate;
        int bestscore = Integer.MIN_VALUE;

        ArrayList<BitBoardNode> tiedBest = new ArrayList<BitBoardNode>(moveList.size());

        for (BitBoardNode bitBoard : moveList) {
            bitBoard.getLegalMoves();
//            System.out.println(bitBoard);
            int moveScore = - bitBoard.getCornerScore()
                    - bitBoard.getMobility();

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
    
//    public static BitBoardNode getBestShallow(ArrayList<BitBoardNode> moveList, BitBoardNode currentstate ){
//        BitBoardNode bestMove = currentstate;
//        int bestscore = 
//        
//        for(Square BitBoardNode : moveList){
//            int mobility = board.play(moveP).getCurrentPossibleSquares().size();
//            if (mobility < minMobility) {
//                move = moveP;
//                minMobility = mobility ;
//            }
//        }
//    }
//    
//    public static BitBoardNode getNextLevelMin(ArrayList<BitBoardNode> moveList, BitBoardNode currentstate ){
//        BitBoardNode bestMove = currentstate;
//        int bestscore = 
//        
//        for(Square BitBoardNode : moveList){
//            int mobility = board.play(moveP).getCurrentPossibleSquares().size();
//            if (mobility < minMobility) {
//                move = moveP;
//                minMobility = mobility ;
//            }
//        }
//    }
    
    @Override
    public int hashCode(){
        int hashCode = (int)(this.moverPieces ^(this.moverPieces>>>32));
        hashCode = 31 * hashCode + (int)(this.opponentPieces ^(this.opponentPieces>>>32));
        return hashCode;
    }
    
    @Override
    public boolean equals(Object other){
        if ( this == other ) return true;
        if ( !(other instanceof BitBoardNode) ) return false;
        BitBoardNode node = (BitBoardNode)other;
        return
                (this.opponentPieces == node.opponentPieces) && (this.moverPieces == node.moverPieces);
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        char tchar;
        
        long wBoard = this.moverPieces;
        long bBoard = this.opponentPieces;
        long tMoves = this.moves;

        for (int i = 0; i < 8; i++) {
            sb.insert(0, "//#");
            sb.insert(0, "\n");
            sb.insert(0, " #");
            for (int j = 0; j < 8; j++) {

                tchar = ((bitmask & wBoard) == bitmask) ? 'O'
                        : (((bitmask & bBoard) == bitmask) ? 'X'
                                : (((bitmask & tMoves) == bitmask) ? '*'
                                        : '-'));
                // ^bitwise and test for 1 in right most bit
                sb.insert(0, tchar);
                sb.insert(0, ' ');
                wBoard = wBoard >>> 1; // bit shift right fill with 0
                bBoard = bBoard >>> 1; // bit shift right fill with 0
                tMoves = tMoves >>> 1;
            }

        }
        sb.insert(0, "//###################\n//#");
        sb.append("##################");
        return sb.toString();
    }

}
