package testdrivers;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Square;
import edu.uab.cis.reversi.strategy.group3.BitBoardNode;
import edu.uab.cis.reversi.strategy.group3.BitFirstStratagy;
import edu.uab.cis.reversi.strategy.group3.BoardBook;
import edu.uab.cis.reversi.strategy.group3.Book;

public class BitBoardDriver {

    public static final boolean TPRINT = true;
    static final long bitmask = 1;
    
    
    private static void bitBoardhashTable(){
        Board board = new Board();
        BitBoardNode bitBoard = new BitBoardNode(board);
        HashMap<BitBoardNode, Square> moveTable =  BitBoardNode.moveToSquare(board);
        ArrayList<BitBoardNode> moves = bitBoard.getMovesAndResults();
        System.out.println(moveTable);
        
        
    }
    
    private static void bitFirstStratagyTest(){
        Board board = new Board();
        BitFirstStratagy strategy = new BitFirstStratagy();
        Square  s = strategy.chooseSquare(board);
//        System.out.println(s);
        
        
    }
    
    
    
    
    
    private static void printMovesResutls(){
        Board board = new Board();
        BitBoardNode bitBoard = new BitBoardNode(board);
        System.out.println(bitBoard);
        System.out.println(board);
        ArrayList<BitBoardNode> moves = bitBoard.getMovesAndResults();
        int count = 0;
        for(BitBoardNode move: moves){
            System.out.println("************************************************\n" + count + " of " + moves.size());
            System.out.println(move.toString());
            count++;
        }
    }
    
    private static void printMoveResutls(){
        Board board = new Board();
        BitBoardNode bitBoard = new BitBoardNode(board);
        System.out.println(bitBoard);
        System.out.println(board);
        long moves = bitBoard.getLegalMoves();
        
        long move = Long.highestOneBit(moves);
        BitBoardNode result = bitBoard.play(move);
        printSBoard(move);
        System.out.println("\nResult:\n********************************************************");
        System.out.println(result);
    }

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
    
    private static void printRayArray(int i) {
        long test[] = BitBoardNode.rayArray[i];
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
        long movers = (sq23flag | sq44flag
                | sq34flag | sq45flag);
        long opp = (sq33flag | sq32flag
                | sq35flag | sq54flag | sq43flag);
        BitBoardNode searcher = new BitBoardNode(movers, opp);
        long moves = searcher.getLegalMoves();
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
    
    public static void testBoardToBit(){
        Board board = new Board();
        BitBoardNode bitBoard = new BitBoardNode(board);
        System.out.println(bitBoard);
        System.out.println(board);
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
    
    public static void roatation(){
        Book books  = new Book();
        Board[] boards = new Board[4];
        for(int i = 0; i< 4; i++){
            boards[i] = new Board();
        }
        BoardBook book = new BoardBook();
        boards = book.translateMoves(boards, 5, 4);
        int count = 0;
        for(Board bookl : boards){
            System.out.println(++count);
            System.out.println(bookl);
        }
        
        boards = book.translateMoves(boards, 3, 5);
        count = 0;
        for(Board bookl : boards){
            System.out.println(++count);
            System.out.println(bookl);
        }
        
    }

    public static void main(String[] args) {
//        testBoardToBit();
//        printRayArray(12);
//        printAgg();
//        searchMoves();
//        bitBoardhashTable();
//        printMovesResutls();
//        printMoveResutls();
//        bitFirstStratagyTest();
        roatation();
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
  public static final long sq00flag = 0b1000000000000000000000000000000000000000000000000000000000000000L;
//Warning
  // this
  // is
  // for
  // sign
  // bit

}
