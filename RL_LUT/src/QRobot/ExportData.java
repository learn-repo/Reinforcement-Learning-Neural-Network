package QRobot;

import java.io.IOException;
import java.io.PrintWriter;

public class ExportData {
	int r[][];
	int res=0;
	
	
	public ExportData(int results[][], int resultCount) {
		
		r = results;
		res = resultCount;
		SaveResult(r, res);
		
	}
	
	
	public void SaveResult (int results[][], int resultCount) {
		
		 
		 PrintWriter pw = null;
		 try {
				pw = new PrintWriter("/Users/Dana/results.csv");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// PrintWriter pw = new PrintWriter(argFile);
	       StringBuilder sb = new StringBuilder();

	       for (int k = 0; k <resultCount; k++)
	       {
	          sb.append( results[k][0]); 
	           sb.append(',');
	           sb.append( results[k][1]);
	           sb.append('\n');
	       }
	       pw.write(sb.toString());
	       pw.close();
		
		
	}

}
