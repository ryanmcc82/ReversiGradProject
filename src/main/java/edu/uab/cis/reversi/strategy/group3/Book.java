package edu.uab.cis.reversi.strategy.group3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;

public class Book {

    private HashMap<List<Move>, Square> map;
    private Player player1 = Player.BLACK;
    private Player player2 = Player.WHITE;
    private boolean loaded = true;

    public Book() {
        map = new HashMap<List<Move>, Square>();
        try {
            readData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void printBook() {
        for (Entry<List<Move>, Square> entry : map.entrySet()) {
            if (entry.getKey().size() < 4)
                System.out.println(entry);
        }
    }

    private void readData() throws IOException {
        try {
            URL url = getClass().getResource("book.txt");
            File file = new File(url.toURI());
            BufferedReader reader = new BufferedReader(new FileReader(
                    file));
//            BufferedReader reader = new BufferedReader(new FileReader(
//                    "book.txt"));
            String temp = reader.readLine();
            while (temp != null) {
                String[] split = temp.split(" ");
                temp = reader.readLine();
                ArrayList<Move> seq = new ArrayList<Move>();
                Move move1 = new Move(new Square(Integer.parseInt(split[0]),
                        Integer.parseInt(split[1])), player1);
                seq.add(move1);
                for (int i = 1; i <= ((split.length - 1) / 2); i++) {

                    if (i % 2 == 1) {
                        Square thisMove = new Square(
                                Integer.parseInt(split[i * 2]),
                                Integer.parseInt(split[i * 2 + 1]));
                        Move moveP2 = new Move(thisMove, player2);

                        if (!map.containsKey(seq)) {
                            map.put(new ArrayList<Move>(seq), thisMove);
                        }
                        seq.add(moveP2);
                    } else {
                        Square thisMove = new Square(

                        Integer.parseInt(split[i * 2]),
                                Integer.parseInt(split[i * 2 + 1]));
                        Move moveP1 = new Move(thisMove, player1);
                        if (!map.containsKey(seq)) {
                            map.put(new ArrayList<Move>(seq), thisMove);
                        }
                        seq.add(moveP1);
                    }

                }
                temp = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            this.loaded = false;
            e.printStackTrace();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Square checkBook(List<Move> history) {

        if (history.size() == 0) {
            return new Square(3, 2);
        } else if (loaded && map.containsKey(history)) {
            return map.get(history);
        } else
            return null;

    }

}
