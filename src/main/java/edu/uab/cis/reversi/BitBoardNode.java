package edu.uab.cis.reversi;

public class BitBoardNode {

    long boardWhite;
    long boardBlack;
    int flagBottomRightCorner;
    static final long bitmask = 1;
    public static final boolean TPRINT = true;

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

    public static int translationArray[] = { 0, 1, 2, -1, -1, -1, -1, -1, 3,
            -1, 4, -1, -1, -1, -1, -1, 5, 6, 7 };

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

    public static void printBinary(long board) {
        if (TPRINT) {
            // String s = String.format("%063d", Long.toBinaryString(board));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < Long.numberOfLeadingZeros(board); i++) {
                sb.append('0');
            }

            sb.append(Long.toBinaryString(board));
            for (int i = 7; i >= 1; i--) {
                sb.insert(i * 8, ' ');
            }
            System.out.println(sb.toString());
            // System.out.println(s);
        }
    }

    public static void printSBoard(long board) {
        if (TPRINT) {
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

    public static void printwhole(long wBoard, long bBoard, long moves) {
        if (TPRINT) {
            StringBuffer sb = new StringBuffer();
            char tchar;

            for (int i = 0; i < 8; i++) {
                sb.insert(0, "//#");
                sb.insert(0, "\n");
                sb.insert(0, " #");
                for (int j = 0; j < 8; j++) {

                    tchar = ((bitmask & wBoard) == bitmask) ? 'O'
                            : (((bitmask & bBoard) == bitmask) ? 'X' 
                                    :(((bitmask & moves) == bitmask) ? '*' : '-'));
                    // ^bitwise and test for 1 in right most bit
                    sb.insert(0, tchar);
                    sb.insert(0, ' ');
                    wBoard = wBoard >>> 1; // bit shift right fill with 0
                    bBoard = bBoard >>> 1; // bit shift right fill with 0
                    moves = moves >>> 1;
                }

            }
            sb.insert(0, "//###################\n//#");
            sb.append("##################");
            System.out.println(sb.toString());
        }
    }

    public void moveResult(long move, long opponent, long movers) {

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
        int emptyIndex;
        int moverIndex;
        long searchDirRay;
        long moverRayIntersect;

        // if(Long.bitCount(occupied) < 32){//Note May should skip this
        // comparison and just default to one or the other
        while (searchBit != 0L) {
            surrounding = ajacentArray[Long.numberOfTrailingZeros(searchBit)];// gets surrounding squares from static table
            surroundingOpp = surrounding & opponent;

            while (surroundingOpp != 0L) {// if none of the surrounding squares are an opponent then its not a valid move

                searchDirBit = Long.lowestOneBit(surroundingOpp);
                squareIndex = Long.numberOfTrailingZeros(searchBit);
                searchDirDiff = Long.numberOfTrailingZeros(searchDirBit)
                        - squareIndex;// This diff lets us search in a diretion using bitshift.
                printSBoard(searchDirBit|searchBit);
                System.out.println("surrounding: " + squareIndex);
                printSBoard(surrounding);
                System.out.println(searchDirDiff + 9);
                searchDirRay = rayArray[squareIndex][translationArray[searchDirDiff + 9]];
                moverRayIntersect = searchDirRay & movers;
                if(moverRayIntersect!= 0L){// if mover has no pieces in ray path its not valid move.
                    if(searchDirDiff > 0){
                        moverIndex = Long.numberOfTrailingZeros(moverRayIntersect);
                        emptyIndex = Long.numberOfTrailingZeros(searchDirRay & unoccupied);
                        if(moverIndex < emptyIndex) moves = moves |searchBit;//add square to valid Moves
                    }else{
                        moverIndex = Long.numberOfLeadingZeros(moverRayIntersect);
                        emptyIndex = Long.numberOfLeadingZeros(searchDirRay & unoccupied);
                        if(moverIndex < emptyIndex) moves = moves |searchBit;//add square to valid Moves
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

    public static void main(String args[]) {
        BitBoardNode  searcher= new BitBoardNode();
        long movers = (sq23flag | sq54flag |sq77flag);
        long opp = (sq34flag | sq42flag );
        long moves = searcher.getLegalMoves(movers,opp);
        long tMax = -1L;
        long tMin = Long.MIN_VALUE;
        long test[] =
        {
                tMax,
                movers, opp, moves , sq77flag};
        int count = 0;

        
        for (long sq : test) {
            count++;
            System.out
                    .println("\n*********************************************************\n"
                            + count
                            + "\nNumber Of Bits Set: "
                            + Long.bitCount(sq));
            printBinary(sq);
            printSBoard(sq);
        }
        System.out.println("\n*********************************************************\n" + Long.bitCount(test[3]));
        printwhole(test[1], test[2], test[3]);
    }

    // ###################
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X O #
    // ###################
    public static final long sq77flag = 1;

    // ###################
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X X X #
    // # X X X X X X O X #
    // ###################
    public static final long sq76flag = 0b10L;
    public static final long sq75flag = 0b100L;
    public static final long sq74flag = 0b1000L;
    public static final long sq73flag = 0b10000L;
    public static final long sq72flag = 0b100000L;
    public static final long sq71flag = 0b1000000L;
    public static final long sq70flag = 0b10000000L;
    public static final long sq67flag = 0b100000000L;
    public static final long sq66flag = 0b1000000000L;
    public static final long sq65flag = 0b10000000000L;
    public static final long sq64flag = 0b100000000000L;
    public static final long sq63flag = 0b1000000000000L;
    public static final long sq62flag = 0b10000000000000L;
    public static final long sq61flag = 0b100000000000000L;
    public static final long sq60flag = 0b1000000000000000L;
    public static final long sq57flag = 0b10000000000000000L;
    public static final long sq56flag = 0b100000000000000000L;
    public static final long sq55flag = 0b1000000000000000000L;
    public static final long sq54flag = 0b10000000000000000000L;
    public static final long sq53flag = 0b100000000000000000000L;
    public static final long sq52flag = 0b1000000000000000000000L;
    public static final long sq51flag = 0b10000000000000000000000L;
    public static final long sq50flag = 0b100000000000000000000000L;
    public static final long sq47flag = 0b1000000000000000000000000L;
    public static final long sq46flag = 0b10000000000000000000000000L;
    public static final long sq45flag = 0b100000000000000000000000000L;
    public static final long sq44flag = 0b1000000000000000000000000000L;
    public static final long sq43flag = 0b10000000000000000000000000000L;
    public static final long sq42flag = 0b100000000000000000000000000000L;
    public static final long sq41flag = 0b1000000000000000000000000000000L;
    public static final long sq40flag = 0b10000000000000000000000000000000L;
    public static final long sq37flag = 0b100000000000000000000000000000000L;
    public static final long sq36flag = 0b1000000000000000000000000000000000L;
    public static final long sq35flag = 0b10000000000000000000000000000000000L;
    public static final long sq34flag = 0b100000000000000000000000000000000000L;
    public static final long sq33flag = 0b1000000000000000000000000000000000000L;
    public static final long sq32flag = 0b10000000000000000000000000000000000000L;
    public static final long sq31flag = 0b100000000000000000000000000000000000000L;
    public static final long sq30flag = 0b1000000000000000000000000000000000000000L;
    public static final long sq27flag = 0b10000000000000000000000000000000000000000L;
    public static final long sq26flag = 0b100000000000000000000000000000000000000000L;
    public static final long sq25flag = 0b1000000000000000000000000000000000000000000L;
    public static final long sq24flag = 0b10000000000000000000000000000000000000000000L;
    public static final long sq23flag = 0b100000000000000000000000000000000000000000000L;
    public static final long sq22flag = 0b1000000000000000000000000000000000000000000000L;
    public static final long sq21flag = 0b10000000000000000000000000000000000000000000000L;
    public static final long sq20flag = 0b100000000000000000000000000000000000000000000000L;
    public static final long sq17flag = 0b1000000000000000000000000000000000000000000000000L;
    public static final long sq16flag = 0b10000000000000000000000000000000000000000000000000L;
    public static final long sq15flag = 0b100000000000000000000000000000000000000000000000000L;
    public static final long sq14flag = 0b1000000000000000000000000000000000000000000000000000L;
    public static final long sq13flag = 0b10000000000000000000000000000000000000000000000000000L;
    public static final long sq12flag = 0b100000000000000000000000000000000000000000000000000000L;
    public static final long sq11flag = 0b1000000000000000000000000000000000000000000000000000000L;
    public static final long sq10flag = 0b10000000000000000000000000000000000000000000000000000000L;
    public static final long sq07flag = 0b100000000000000000000000000000000000000000000000000000000L;
    public static final long sq06flag = 0b1000000000000000000000000000000000000000000000000000000000L;
    public static final long sq05flag = 0b10000000000000000000000000000000000000000000000000000000000L;
    public static final long sq04flag = 0b100000000000000000000000000000000000000000000000000000000000L;
    public static final long sq03flag = 0b1000000000000000000000000000000000000000000000000000000000000L;
    public static final long sq02flag = 0b10000000000000000000000000000000000000000000000000000000000000L;
    public static final long sq01flag = 0b100000000000000000000000000000000000000000000000000000000000000L;
    public static final long sq00flag = 0b1000000000000000000000000000000000000000000000000000000000000000L;// Warning
                                                                                                            // this
                                                                                                            // is
                                                                                                            // for
                                                                                                            // sign
                                                                                                            // bit

}
