package testdrivers;

import edu.uab.cis.reversi.BitBoardNode;

public class BitBoardDriver {
    private static void printAgg(){
        long test[] = BitBoardNode.ajacentArray;
        int count = 0;

        for (long sq : test) {
            count++;
            System.out.println("\n*********************************************************\n"
                            + count + "\nNumber Of Bits Set: "
                            + Long.bitCount(sq));
            BitBoardNode.printBinary(sq);
            BitBoardNode.printSBoard(sq);
        }
    }
    
    private static void searchMoves(){
        BitBoardNode  searcher= new BitBoardNode();
        long movers = (BitBoardNode.sq23flag | BitBoardNode.sq44flag | BitBoardNode.sq34flag | BitBoardNode.sq45flag);
        long opp = (BitBoardNode.sq33flag | BitBoardNode.sq32flag | BitBoardNode.sq35flag 
                | BitBoardNode.sq54flag | BitBoardNode.sq43flag );
        long moves = searcher.getLegalMoves(movers,opp);
        long tMax = -1L;
        long tMin = Long.MIN_VALUE;
        long test[] =
        {
                tMax,
                movers, opp, moves };
        int count = 0;

        
        for (long sq : test) {
            count++;
            System.out
                    .println("\n*********************************************************\n"
                            + count
                            + "\nNumber Of Bits Set: "
                            + Long.bitCount(sq));
            BitBoardNode.printBinary(sq);
            BitBoardNode.printSBoard(sq);
        }
        System.out.println("\n*********************************************************\n" + Long.bitCount(test[3]));
        BitBoardNode.printwhole(test[1], test[2], test[3]);
    }
    
    
    public static void main(String[] args){
//        printAgg();
        searchMoves();
    }

}
