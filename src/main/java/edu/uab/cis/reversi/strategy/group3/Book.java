package reversi;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pcollections.PSequence;

import edu.uab.cis.reversi.Move;
import edu.uab.cis.reversi.Player;
import edu.uab.cis.reversi.Square;
public class Book {

	private HashMap<PSequence<Move>,Square> map;
	private Player player1 = Player.BLACK;
    private Player player2 = Player.WHITE;
	public Book(){
		
	}
	
	
	
	private void readData() throws IOException{
		 try {
				BufferedReader reader = new BufferedReader(new FileReader("book.txt"));
				String temp = reader.readLine();
				while(temp != null){
					String[] split = temp.split(" ");
					temp = reader.readLine();
                    ArrayList<Move> seq = new ArrayList<Move>();
					for(int i = 1; i <= (split.length/2); i++){
					if(i < 2){
						Move move1 = new Move(new Square(Integer.parseInt(split[i-1]),Integer.parseInt(split[i]) ),player1);
                        seq.add(move1);
                    }else{
                    	if(i%2 == 0 ){
                    		Square thisMove = new Square(Integer.parseInt(split[i*2-1]),Integer.parseInt(split[i*2]) );
                    		Move move1 = new Move( thisMove,player2);
                    		if(!map.containsKey((PSequence<Move>) seq)){
                        		map.put((PSequence<Move>) seq, thisMove);	
                        		}
                            	seq.add(move1);
                    	}else{
                    			Square thisMove = new Square(Integer.parseInt(split[i*2-1]),Integer.parseInt(split[i*2+1]) );
                        		Move move1 = new Move(thisMove,player1);
                        		if(!map.containsKey((PSequence<Move>) seq)){
                        		map.put((PSequence<Move>) seq, thisMove);	
                        		}
                        		seq.add(move1);
                    	}
                    	
                    }
						
						
					}
					temp = reader.readLine();
				}
				
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		 
		 
	}

public Square checkBook(PSequence<Move> history){
	if(map.containsKey(history)){
		return map.get(history);
	}else return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
