package edu.uab.cis.reversi.strategy.group3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.uab.cis.reversi.Board;
import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class BoardBook {

    private HashMap<Board, Square> map;
    private Player player1 = Player.BLACK;
    private Player player2 = Player.WHITE;
    private boolean loaded = true;

    public BoardBook() {
        map = new HashMap<Board, Square>();
        try {
            readData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        printBook();
    }
    private void printBook(){
        for( Entry<Board, Square> entry: map.entrySet()){
            if (entry.getKey().size() < 4)
            System.out.println(entry);
        }
    }
    
    public Board[] translateMoves(Board[] boards, int x, int y){
        int xrl, yrl, xrev, yrev, xrrev, yrrev;
        xrev = y;
        yrev = x;
        xrl = 7 - x;
        yrl = 7 - y;
        xrrev = yrl;
        yrrev = xrl;
        
        boards[0] = boards[0].play(new Square(x, y));
        
        boards[1] = boards[1].play(new Square(xrev, yrev));
        
        boards[2] = boards[2].play(new Square(xrl, yrl));
        
        boards[3] = boards[3].play(new Square(xrrev, yrrev));
        

        return boards;
        
    }
 

    private void readData() throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("book.txt"));
            String temp = reader.readLine();
            while (temp != null) {
                String[] split = temp.split(" ");
                temp = reader.readLine();
                Board[] boards = new Board[4];
                for(int i = 0; i< 4; i++){
                    boards[i] = new Board();
                }
                
                translateMoves(boards,
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]));

                for (int i = 1; i <= ((split.length - 1) / 2); i++) {

                        
                            Square thisMove = new Square(
                                    Integer.parseInt(split[i * 2]),
                                    Integer.parseInt(split[i * 2 + 1]));
                            for(Board board: boards){
                                if (!map.containsKey(board)) {
                                    map.put((board), thisMove);
                                }
                            }
                           
                            boards = translateMoves(boards,
                                    Integer.parseInt(split[i * 2]),
                                    Integer.parseInt(split[i * 2 + 1]));
                        
                }
                temp = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            this.loaded = false;
            e.printStackTrace();

        }

    }

    public Square checkBook(Board history) {

        if (loaded && map.containsKey(history)) {
            return map.get(history);
        } else
            return null;

    }

}
