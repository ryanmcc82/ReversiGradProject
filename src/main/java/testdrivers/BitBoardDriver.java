package testdrivers;

import edu.uab.cis.reversi.BitBoardNode;

public class BitBoardDriver {

    public static final boolean TPRINT = true;
    static final long bitmask = 1;

    private static void printAgg() {
        long test[] = BitBoardNode.ajacentArray;
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
    }

    private static void searchMoves() {
        BitBoardNode searcher = new BitBoardNode();
        long movers = (BitBoardNode.sq23flag | BitBoardNode.sq44flag
                | BitBoardNode.sq34flag | BitBoardNode.sq45flag);
        long opp = (BitBoardNode.sq33flag | BitBoardNode.sq32flag
                | BitBoardNode.sq35flag | BitBoardNode.sq54flag | BitBoardNode.sq43flag);
        long moves = searcher.getLegalMoves(movers, opp);
        long expected = 0b0000000000000000001001100100001001100000001100000001100000000000L;
        if (TPRINT) {
            long test[] = { movers, opp, moves };
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
            System.out
                    .println("\n*********************************************************\n"
                            + Long.bitCount(test[2]));
            printwhole(test[0], test[1], test[2]);
        }
        System.out.println("Search: " + (moves == expected));
    }

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
                                    : (((bitmask & moves) == bitmask) ? '*'
                                            : '-'));
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

    public static void main(String[] args) {
        // printAgg();
        searchMoves();
    }

}
