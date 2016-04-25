
package edu.uab.cis.reversi.strategy.group3;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class BitNode {

    BitNode parent;
//    ArrayList<BitNode> parents;//note if using hashTable to cut down on searching could have multiple parents.
    ArrayList<BitNode> children = null;

//    public ArrayList<BitNode> getParents() {return parents;}
//    public void setParents(ArrayList<BitNode> parents) {this.parents = parents;}
    public BitNode getParent() {return parent;}
    public void setParent(BitNode parent) {this.parent = parent;}

    public BitNode getBestChild() {
        return bestChild;
    }

    public void setBestChild(BitNode bestChild) {
        this.bestChild = bestChild;
    }

    public int getHighestchildPathScore() {
        return highestchildPathScore;
    }

    public boolean setChildScore(int childPathScore, BitNode child, int currentScoreDepth) {
//        System.out.print("Push?: " + childPathScore + ":" + highestchildPathScore);
        boolean swap = false;
        if(bestChild == null||highestchildPathScore > ( -1 * childPathScore)||currentScoreDepth>this.currentScoreDepth){
            bestChild = child;
            this.highestchildPathScore =  -1 * childPathScore;
            this.currentScoreDepth = currentScoreDepth;
            swap = true;
        }
        return swap;
    }

    int highestchildPathScore;
    BitNode bestChild;
    final long moverPieces;
    final long opponentPieces;
    final long occupied;
    final long unoccupied;
    long moves;
    int mobility;
    int currentScoreDepth;

    boolean movesSearched = false;
    static final long bitmask = 1;

    public static final int CORNERW = 8100;
    public static final int XSQUAREW = 3000;
    public static final int ASQUAREW = 100;
    public static final int CSQUAREW = 400;
    public static final int MOBILITYW = 100;
    public static final int DMOBILITYW = 33;
    public static final int SABILITYW = 0;
    public static final int PARITY = 30;
    public static final int WIN = Integer.MAX_VALUE / 2;
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

    public static final long xSquaresArray[];
    static {
        xSquaresArray = new long[64];
        for (int i = 0; i < 64; i++) {
            if (i == 0) {// corner
                xSquaresArray [i] = 0b10L << (8 + i);
            } else if (i == 7) {// corner
                xSquaresArray [i] = 0b1L << (7 + i);
            } else if (i == 63) {// corner
                xSquaresArray [i] =  0b1L << (i - 9);
            } else if (i == 56) {// corner
                xSquaresArray [i] = 0b10L << (i - 8);
            }else{
                xSquaresArray [i] = 0b0L;
            }
        }
    }

    public static final long cSquaresArray[];
    static {
        cSquaresArray = new long[64];
        for (int i = 0; i < 64; i++) {
            if (i == 0 |i == 7 | i == 63 | i == 56) {// corner
                cSquaresArray [i] = ajacentArray[i] ^ xSquaresArray [i];
            }else{
                cSquaresArray [i] = 0b0L;
            }
        }
    }

    public static final long aSquaresArray[];
    static {
        aSquaresArray = new long[64];
        for (int i = 0; i < 64; i++) {
            if (i == 0) {// corner
                aSquaresArray[i] = 0b1L << (2 + i) | 0b01L << (16 + i);
            } else if (i == 7) {// corner
                aSquaresArray[i] = 0b1L << (i - 2) | 0b01L << (16 + i);
            } else if (i == 63) {// corner
                aSquaresArray[i] = (0b1L << (i - 2)) | 0b1L << (i - 16);
            } else if (i == 56) {// corner
                aSquaresArray[i] = 0b1L << (2 + i) | 0b01L << ( i - 16);
            }else{
                aSquaresArray[i] = 0b0L;
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

    public BitNode(long moverPieces, long opponentPieces, BitNode parent){
        this.moverPieces = moverPieces;
        this.opponentPieces = opponentPieces;
        this.occupied = opponentPieces | moverPieces;
        this.unoccupied = ~occupied;
        this.parent = parent;

    }

    public BitNode(Board boardObject){
        Map<Square, Player> owners = boardObject.getSquareOwners();
        this.moverPieces = owners.entrySet().stream().filter( e -> e.getValue().equals(boardObject.getCurrentPlayer()))
        .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.opponentPieces  = owners.entrySet().stream().filter( e -> !e.getValue().equals(boardObject.getCurrentPlayer()))
                .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);        
        this.occupied = opponentPieces | moverPieces;
        children = null;
        this.unoccupied = ~occupied;
        getLegalMoves();
    }

    public BitNode(Board boardObject, int one){
        Map<Square, Player> owners = boardObject.getSquareOwners();
        this.moverPieces = owners.entrySet().stream().filter( e -> e.getValue().equals(boardObject.getCurrentPlayer()))
                .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.opponentPieces  = owners.entrySet().stream().filter( e -> !e.getValue().equals(boardObject.getCurrentPlayer()))
                .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.occupied = opponentPieces | moverPieces;
        this.unoccupied = ~occupied;
    }
    
    private static long squareToLong(Square square){
        int index = square.getColumn() + (8 * (square.getRow()));
        long lindex = 0b1L << index;
        return lindex;
    }
    
    public ArrayList<BitNode> getMovesAndResults(){
        if (children == null) {

            children = new ArrayList<BitNode>();
            if (movesSearched) {
                return populateMoves();
            } else {
                getLegalMoves();
                return populateMoves();
            }

//            long tempUnOcc = unoccupied;
//            long searchBit = Long.lowestOneBit(tempUnOcc);
//            long surrounding;
//            long surroundingOpp;
//            this.moves = 0L;
//
//            long searchDirBit;
//            int searchDirDiff;
//            int squareIndex;
//            int closestEmptyIndex;
//            int closestMoverIndex;
//            long searchDirRay;
//            long moverRayIntersect;
//            long cancleDirRay;
//            /**
//             * used to clear bits in SearchDirRay that occer after
//             * closestMoverBit
//             */
//
//            // if(Long.bitCount(occupied) < 32){//Note May should skip this
//            // comparison and just default to one or the other
//            while (searchBit != 0L) {
//                squareIndex = Long.numberOfTrailingZeros(searchBit);
//                surrounding = ajacentArray[squareIndex];// gets surrounding squares from static table
//                surroundingOpp = surrounding & opponentPieces;
//                long moverResult = moverPieces;
//                while (surroundingOpp != 0L) {// if none of the surrounding squares are an opponent then its not a valid move
//
//                    searchDirBit = Long.lowestOneBit(surroundingOpp);
//                    searchDirDiff = Long.numberOfTrailingZeros(searchDirBit)
//                            - squareIndex;// This diff lets us search in a diretion using bitshift.
//                    searchDirRay = rayArray[squareIndex][translationArray[searchDirDiff + 9]];
//                    moverRayIntersect = searchDirRay & moverPieces;
//                    if (moverRayIntersect != 0L) {// if mover has no pieces in ray path its not valid move.
//                        if (searchDirDiff > 0) {
//                            closestMoverIndex = Long
//                                    .numberOfTrailingZeros(moverRayIntersect);
//                            closestEmptyIndex = Long
//                                    .numberOfTrailingZeros(searchDirRay
//                                            & unoccupied);
//                            if (closestMoverIndex < closestEmptyIndex) {
//                                cancleDirRay = rayArray[closestMoverIndex][translationArray[searchDirDiff + 9]];
//                                moverResult = moverResult | searchBit
//                                        | (searchDirRay ^ cancleDirRay);
//                                this.moves = this.moves |searchBit;//add square to valid Moves
//                            }
//                        } else {
//                            closestMoverIndex = Long
//                                    .numberOfLeadingZeros(moverRayIntersect);
//                            closestEmptyIndex = Long
//                                    .numberOfLeadingZeros(searchDirRay
//                                            & unoccupied);
//                            if (closestMoverIndex < closestEmptyIndex) {
//                                cancleDirRay = rayArray[63 - closestMoverIndex][translationArray[searchDirDiff + 9]];
//                                moverResult = moverResult | searchBit
//                                        | (searchDirRay ^ cancleDirRay);
//                                this.moves = this.moves |searchBit;//add square to valid Moves
//                            }
//                        }
//                    }
//
//                    if (moverResult != moverPieces) {
//                        long newOpponent = moverResult;
//                        long newMover = (opponentPieces & moverResult)
//                                ^ opponentPieces;
//                        if(children.size()>40){
//                            System.out.println("childOverFlow");
//                        }
//                        children.add(new BitNode(newMover, newOpponent, this));
//                    }
//                    surroundingOpp = surroundingOpp & ~searchDirBit;// zero's
//                                                                    // search
//                                                                    // Direction
//                }
//
//                tempUnOcc = tempUnOcc ^ searchBit;// sets searched bit to zero
//                searchBit = Long.lowestOneBit(tempUnOcc);// finds new
//                                                         // bit(square) to
//                                                         // search.
//            }
//        }
//        movesSearched = true;
//        mobility = Long.bitCount(moves);
        }
        return children;

    }
    
    private ArrayList<BitNode> populateMoves(){
        if(children == null) {
            children = new ArrayList();
        }
        if( children.size() ==0) {
            long tempMoves = this.moves;
            while (tempMoves != 0L) {
                long tempMove = Long.highestOneBit(tempMoves);
                children.add(play(tempMove));
                tempMoves = tempMoves ^ tempMove;
            }
            if (children.size() == 0 && opponentPieces != 0L) {
                BitNode skipMove = new BitNode(opponentPieces, moverPieces, this);
                if (skipMove.getLegalMoves() != 0L) {
//                        System.out.println("skipp");
                    children.add(skipMove);
                }
            }
        }
        return children;
    }
    
    public BitNode play(long move){

       long moverResult = moverPieces | move;
       if(move == 0L) return new BitNode(opponentPieces, this.moverPieces, this);
       long surroundingOpp = opponentPieces & ajacentArray[Long.numberOfTrailingZeros(move)];
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
           moverRayIntersect = searchDirRay & moverPieces;
           
            if(moverRayIntersect!= 0L){// if mover has no pieces in ray path its not valid move.
                    if(searchDirDiff > 0){
                        closestMoverIndex = Long.numberOfTrailingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfTrailingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex){
                            cancleDirRay = rayArray[closestMoverIndex][translationArray[searchDirDiff + 9]];
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);
                        }
                    }else{
                        closestMoverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex) {
                            cancleDirRay = rayArray[63- closestMoverIndex][translationArray[searchDirDiff + 9]];
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);
                        }
                    }
                }
           surroundingOpp = surroundingOpp & ~searchDirBit;// zero's search Direction
       }

       long newOpponent = moverResult;
       long newMover = (opponentPieces & moverResult) ^ opponentPieces;
       return  new BitNode(newMover, newOpponent, this);
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

    public int getVarMobility(int w){
        return mobility * w;
    }
    
    public int getMobility(){
        return (MOBILITYW * mobility);
    }

    public int getParity(){
        return (Long.bitCount(moverPieces) - Long.bitCount(opponentPieces));
    }

    public int getWiner(){
        int score;
        int movers = Long.bitCount(moverPieces);
        int opps = Long.bitCount(opponentPieces);
        if(movers > opps){
            score = Integer.MIN_VALUE/2 - movers;
        }else{
            score = Integer.MAX_VALUE/2 + opps;
        }
        return score;
    }
    
    public static HashMap<BitNode, Square> moveToSquare(Board boardparent, BitNode node){
        HashMap<BitNode, Square> map = new HashMap<BitNode, Square>();
        for(Square square: boardparent.getCurrentPossibleSquares()){
            map.put(node.play(BitNode.squareToLong(square)), square);
        }
       
        return map;
    }

    public static HashMap<BitNode, Square> moveToSquare(Board boardparent, BitNode node, ArrayList<BitNode> list){
        HashMap<BitNode, Square> map = new HashMap<BitNode, Square>();
        for(Square square: boardparent.getCurrentPossibleSquares()){
            BitNode child = node.play(BitNode.squareToLong(square));
            map.put(child, square);
            list.add(child);
        }

        return map;
    }
    
    public int getDoubleMobility() {
       int oppmobility = Long.bitCount(play(0L).getLegalMoves());
        int numerator;
        int denominator = oppmobility + mobility;
        if(denominator == 0){
            return 0;
        }
        if(mobility > oppmobility) {
            numerator = mobility * -100;
        }else{
            numerator = oppmobility * 100;
        }
        return numerator/(oppmobility + mobility);

    }


    public int getBoardScore(){
        if(unoccupied == 0L){
            int moveScore = Long.bitCount(moverPieces);
            if (moveScore > 32){
                return (- WIN - moveScore);
            }else if (moveScore < 32){
                return ( WIN + Long.bitCount(opponentPieces));

            }else return 0;
        }
        int moveAndParityScore = PARITY * getParity() + (DMOBILITYW * getDoubleMobility());
        long emptyCorners = patternCorners & unoccupied;
        long xSquares = 0L;
        long cSquares = 0L;
        long aSquares = 0L;
        long cornerBit;
        while(emptyCorners != 0b0L){
            cornerBit = Long.highestOneBit(emptyCorners);
            xSquares = xSquares | xSquaresArray[Long.numberOfTrailingZeros(cornerBit)];
            cSquares = cSquares | cSquaresArray[Long.numberOfTrailingZeros(cornerBit)];
            aSquares = aSquares | aSquaresArray[Long.numberOfTrailingZeros(cornerBit)];
            emptyCorners = emptyCorners ^ cornerBit;
        }

        int moverscore = CORNERW * Long.bitCount(moverPieces & patternCorners)
                + (ASQUAREW  * Long.bitCount(moverPieces & aSquares))
                - (XSQUAREW * Long.bitCount(moverPieces & xSquares))
                - (CSQUAREW  * Long.bitCount(moverPieces & cSquares));

        int opponentscore = CORNERW * Long.bitCount(opponentPieces & patternCorners)
                + (ASQUAREW  * Long.bitCount(opponentPieces & aSquares))
                - (XSQUAREW * Long.bitCount(opponentPieces & xSquares))
                - (CSQUAREW  * Long.bitCount(opponentPieces & cSquares));
        return (moverscore - opponentscore - moveAndParityScore);
    }

    public long getStabilityScore(){
        long occupiedCorners = patternCorners & occupied;
        long currentStable = occupiedCorners; // | passed in
        if(currentStable == 0L){return 0;}
        long temp = currentStable;
        long currentUnstable = ~currentStable;
        long neighborsToExplore = 0L;
        long unexplored = occupied ^ currentStable;
        long workingSquare;
        while(temp != 0){// geting all stable squares neighbors(all stable squares have at least 1 stable neighbor)
            workingSquare = Long.highestOneBit(temp);
            neighborsToExplore = neighborsToExplore | (ajacentArray[Long.numberOfTrailingZeros(workingSquare)] & occupied);
            temp = temp ^ workingSquare;
        }
        neighborsToExplore = neighborsToExplore & currentUnstable;

        boolean verts;
        boolean forwardDiag;
        boolean backDiag;
        boolean horizontal;
        long team;
        while(neighborsToExplore !=0){
            workingSquare = Long.highestOneBit(neighborsToExplore);

            //subcase forpatterEdges

//            verts = (Long.bitCount() == 0);


            //if stable
            neighborsToExplore = neighborsToExplore | (ajacentArray[Long.numberOfTrailingZeros(workingSquare)]  & currentUnstable & unexplored);
            currentStable = currentStable |workingSquare;
            currentUnstable = currentUnstable ^ workingSquare;
            //
            unexplored = unexplored ^ workingSquare;
            neighborsToExplore = neighborsToExplore ^ workingSquare;
        }


        return (unexplored | ~occupied) ;
    }

    public BitNode getBestDMNewState() {
        BitNode currentstate = this;
        ArrayList<BitNode> moveList = this.getMovesAndResults();
        BitNode bestMove = currentstate;
        int bestscore = Integer.MIN_VALUE;

        ArrayList<BitNode> tiedBest = new ArrayList<BitNode>(moveList.size());

        for (BitNode bitBoard : moveList) {
            bitBoard.getLegalMoves();
            int moveScore = - bitBoard.getBoardScore();

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
    
    @Override
    public int hashCode(){
        int hashCode = (int)(this.moverPieces ^(this.moverPieces>>>32));
        hashCode = 31 * hashCode + (int)(this.opponentPieces ^(this.opponentPieces>>>32));
        return hashCode;
    }
    
    @Override
    public boolean equals(Object other){
        if ( this == other ) return true;
        if ( !(other instanceof BitNode) ) return false;
        BitNode node = (BitNode)other;
        return
                (this.opponentPieces == node.opponentPieces) && (this.moverPieces == node.moverPieces);
    }

    public String toLongs(){
        String s = moverPieces + ":" + opponentPieces;
        return s;
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

    public static void printSBoard(long board) {

            long tboard = board;
            StringBuffer sb = new StringBuffer();
            char tchar;

            for (int i = 0; i < 8; i++) {
                sb.insert(0, "//#");
                sb.insert(0, "\n");
                sb.insert(0, " #");
                for (int j = 0; j < 8; j++) {

                    tchar = ((bitmask & tboard) == 0) ? 'X' : 'O';
                    // ^bitwise and test for 1 in right most bit
                    sb.insert(0, tchar);
                    sb.insert(0, ' ');
                    tboard = tboard >>> 1; // bit shift right fill with 0
                }

            }
            sb.insert(0, "//###################\n//#");
            sb.append("##################");
            System.out.println(sb.toString());


    }

}
