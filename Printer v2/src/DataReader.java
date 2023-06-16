import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {

	ArrayList<int[]> imgData = new ArrayList<int[]>();
	BufferedReader reader;

	public DataReader() {
		
		File dataFile = new File("imgData.txt");
		
		String line;
		
		
		//   ArrayList
		//  ---------->
		//
		//  0000000000	
		//	0011111100	|
		//	0010000100	| int[]
		//	0010000100	|
		//	0010000100	|
		//	0010000100	|
		//	0010000100	|
		//	0011111100	V
		//	0000000000
		//
		//
		
		
		try {
			
			reader = new BufferedReader(new FileReader(dataFile));
			
			while ((line = reader.readLine()) != null) {
				
		        int[] dataArray = new int[line.length()];
		        
		        //turn String into array of integers
				
		        for (int i = 0; i < line.length(); i++) dataArray[i] = Integer.parseInt(String.valueOf(line.charAt(i)));
		        
		        //add array to ArrayList
		        imgData.add(dataArray);
		        
		    }
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		
		
		
		
	}
	
}
