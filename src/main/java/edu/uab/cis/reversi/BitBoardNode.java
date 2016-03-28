package edu.uab.cis.reversi;

public class BitBoardNode {

    long boardWhite;
    long boardBlack;
    int flagBottomRightCorner;
    static final long bitmask = 1;
    public static final boolean TPRINT = true;

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

    public static void printwhole(long wBoard, long bBoard, int cornerFlags) {
        if (TPRINT) {
            StringBuffer sb = new StringBuffer();
            char tchar;

            sb.append("###################\n#");
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {

                    tchar = ((bitmask & wBoard) == bitmask) ? 'W'
                            : (((bitmask & bBoard) == bitmask) ? 'B' : 'X');
                    // ^bitwise and test for 1 in right most bit
                    sb.append(' ');
                    sb.append(tchar);
                    wBoard = wBoard >>> 1; // bit shift right fill with 0
                    bBoard = bBoard >>> 1; // bit shift right fill with 0
                }
                sb.append(" #");
                sb.append("\n");
                sb.append("#");
            }
            sb.append("##################");
            System.out.println(sb.toString());
        }

    }
    
    public void getMoves(Long opponent, Long movers){
        Long allPossible = -1L;
        Long occupied = opponent | movers; //keep this allocated
       Long unoccupied = allPossible ^ occupied;
       occupied.
       Long tempOpp = opponent;
       Long searchBit = Long.highestOneBit(tempOpp);
       Long surrounding;
       Long moves = 0L;
       
       if(Long.bitCount(occupied) < 32){//Note May should skip this comparison and just default to one or the other
           while(searchBit != 0L){
               surrounding = ajacentArray[Long.numberOfTrailingZeros(searchBit)];
               
               tempOpp = tempOpp ^ searchBit;
               searchBit = Long.highestOneBit(tempOpp);
           }
       }
//       int  set = opponent.
       Long possibles = 
       Long.highestOneBit(opponent);
    }

    public static void main(String args[]) {

        long tMax = -1L;
        long tMin = Long.MIN_VALUE;
        long test[] = {tMax, (sq11flag | sq00flag | sq22flag | sq33flag | sq44flag), patternEdges };
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
    public static final long sq00flag = 0b1000000000000000000000000000000000000000000000000000000000000000L;// Warning this is for sign bit
  

    //###################
    //# O O O O O O O O #
    //# O X X X X X X O #
    //# O X X X X X X O #
    //# O X X X X X X O #
    //# O X X X X X X O #
    //# O X X X X X X O #
    //# O X X X X X X O #
    //# O O O O O O O O #
    //###################
      public static final long patternEdges;
//      = (sq01flag
//              |sq00flag
//              |sq02flag
//              |sq03flag
//              |sq04flag
//              |sq05flag
//              |sq06flag
//              |sq07flag
//              |sq70flag
//              |sq71flag
//              |sq72flag
//              |sq73flag
//              |sq74flag
//              |sq75flag
//              |sq76flag
//              |sq77flag
//              |sq10flag
//              |sq20flag
//              |sq30flag
//              |sq40flag
//              |sq50flag
//              |sq60flag
//              |sq67flag
//              |sq57flag
//              |sq47flag
//              |sq37flag
//              |sq27flag
//              |sq17flag
//              );
      
      static {
          Long temp = 0b11111111L | 0b11111111L<<(56);
          for(int i = 1 ; i < 7; i++){
              temp = temp | 0b10000001L<<(8*i);
          }
          patternEdges = temp;
      }

      public static final long positionArray[];
      static{
          positionArray = new long[64];
          for(int i = 0; i<64; i++){
              positionArray[i]= 0b1L<<i;
          }
      }    
      
      public static final long ajacentArray[];
      
      static {
          ajacentArray = new long[64];
          for(int i = 0; i < 64; i++){
              if((0b1L<<i & patternEdges) == 0){ //if its not on the edge get all 8 surounding positions
                  ajacentArray[i] = 0b1L<<(i+1) | 0b1L<<(i-1)| 0b111L<<(i+7) | 0b111L<<(i-9);
              }else if(i==0){//corner
                  ajacentArray[i]= 0b1<<(1+i) | 0b11L<<(8+i);
              }else if(i==7){//corner
                  ajacentArray[i]= 0b1<<(i-1) | 0b11L<<(6+i);
              }else if(i==63){//corner
                  ajacentArray[i]= 0b1<<(i-1) | 0b11L<<(i-9);
              }else if(i==56){//corner
                  ajacentArray[i]= 0b1<<(1+i) | 0b11L<<(8+i);
              }else if((i+1)%8 ==0){//left most column - corners
                  ajacentArray[i]= 0b1L<<(i-1)| 0b11L<<(i+6) | 0b11L<<(i-9);
              }else {//right most column - corners
                  ajacentArray[i]= 0b1L<<(i+1)| 0b11L<<(i+7) | 0b11L<<(i-7);
              }
          }
          
      }
}
