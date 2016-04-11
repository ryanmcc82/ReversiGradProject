package edu.uab.cis.reversi;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.LongStream;

import testdrivers.BitBoardDriver;

public class BitBoardNode {

    long moverPieces;
    long opponentPieces;
    long moves;
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
        long tempH = 0;
        for (int i = 0; i < length; i++) {
            tempH = tempH | (temp << (9 * i));
        }
        return tempH;
    }

    public static long getTopforwardSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0;
        for (int i = 1; i < length + 1; i++) {
            tempH = tempH | (temp << (63 - (9 * i)));
        }
        return tempH;
    }

    public static long getBackSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0;
        for (int i = 0; i < (length); i++) {
            tempH = tempH | (temp << (7 * i));
        }
        return tempH;
    }

    public static long getTopBackSlantRay(int length) {
        long temp = 0b1L;
        long tempH = 0;
        for (int i = 0; i < (length); i++) {
            tempH = tempH | (temp << (56 - (7 * i)));
        }
        return tempH;
    }

    public static final long vertRay;
    static {
        long temp = 0b1L;
        long tempH = 0;
        for (int i = 0; i < 8; i++) {
            tempH = tempH | (temp << (8 * i));
        }
        vertRay = tempH;
    }

    public static long horizontalRay(int length) {
        long temp = 0b1L;
        long tempH = 0;
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
            rayArray[i][7] = leftUp;

            long up = vertRay << (i + 8);
            rayArray[i][6] = up;

            long rightUp = getBackSlantRay(rightRayLength) << (i + 7);
            rayArray[i][5] = rightUp;

            long left = horizontalRay(leftRayLength) << i;
            rayArray[i][4] = left;

            long right = horizontalRay(rightRayLength) << i
                    - (rightRayLength + 1);
            rayArray[i][3] = right;

            long leftDown = getTopBackSlantRay(leftRayLength) >>> 63 - i;
            rayArray[i][2] = leftDown;

            long down = vertRay >>> 64 - i;
            rayArray[i][1] = down;

            long rightDown = getTopforwardSlantRay(rightRayLength) >>> 63 - i;
            rayArray[i][0] = rightDown;
        }

    }

    /*********************************************************************************************************************************/

    public BitBoardNode(long moverPieces, long opponentPieces){
        this.moverPieces = moverPieces;
        this.opponentPieces = opponentPieces;
        
    }
    
    public BitBoardNode(long moverPieces, long opponentPieces, long move){
        //TODO Make constructor
        this.moverPieces = moverPieces;
        this.opponentPieces = opponentPieces;
    }
    
    public BitBoardNode(Board boardObject){
        Map<Square, Player> owners = boardObject.getSquareOwners();
        this.moverPieces = owners.entrySet().stream().filter( e -> e.getValue().equals(boardObject.getCurrentPlayer()))
        .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.opponentPieces  = owners.entrySet().stream().filter( e -> !e.getValue().equals(boardObject.getCurrentPlayer()))
                .mapToLong(e -> squareToLong(e.getKey())).reduce(0b0L, (r, v) -> r | v);
        this.moves = getLegalMoves(moverPieces, opponentPieces);
    }
    
    private long squareToLong(Square square){
        int index = square.getColumn() + (8 * (square.getRow()));
        long lindex = 0b1L << index;
        return lindex;
    }
    
    public List<BitBoardNode> getMovesAndResults(){
        ArrayList<BitBoardNode> moves = new ArrayList<BitBoardNode>();
//        TODO combine get legal moves and getMoveResult into one operation.
//        Note it may be best to have both methods available to us.
        return moves;
    }
    
    public long getMoveResult(long movers, long opponent, long move){
       long moverResult = movers | move;
       long surroundingOpp = opponent & ajacentArray[Long.numberOfTrailingZeros(move)];
       long searchDirBit;
       long searchDirRay;
       long moverRayIntersect;
       long cancleDirRay;/**used to clear bits in SearchDirRay that occer after closestMoverBit */
       
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
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);//
                        }
                    }else{
                        closestMoverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex) {
                            cancleDirRay = rayArray[closestMoverIndex][translationArray[searchDirDiff + 9]];
                            moverResult = moverResult | (searchDirRay ^ cancleDirRay);//
                        }
                    }
                }
            
           
           
           surroundingOpp = surroundingOpp & ~searchDirBit;// zero's search
           // Direction
       }
       
       return  moverResult;
    }

    public long getLegalMoves( long movers, long opponent) {
        // Later in game its faster to look at just empty spaces.
        // Early in game might be faster to look at it from occupied spaces. or
        // weed out based on boarder squares
        long occupied = opponent | movers; // keep this allocated
        long unoccupied = ~occupied;

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
                            moves = moves |searchBit;//add square to valid Moves
                            break;//end search if found
                        }
                    }else{
                        closestMoverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        closestEmptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(closestMoverIndex < closestEmptyIndex) {
                            moves = moves |searchBit;//add square to valid Moves
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
        return moves;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        char tchar;
        
        long wBoard = this.moverPieces;
        BitBoardDriver.printSBoard(moverPieces);
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