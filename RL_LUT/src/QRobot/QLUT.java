package QRobot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;


public class QLUT implements LUTInterface{
	

	private int nheading;
	private int nbearing ;
	private int ndistance;

	private int naction;
	private int sizeOfLUT ;
	public int totalActions;
	public int totalStates;
	public int counter =0;
	public static double LUTT [];
	public static int indexLUT [][][][] = new int [8][8][3][6];
	DecimalFormat df = new DecimalFormat("#.##");
	

	
	public QLUT(int nStates, int nActions, double X [] ) {
		
		nbearing = (int) (X[0]);
		nheading = (int) (X[1]);
		ndistance = (int) (X[2]);
		naction = (int) (X[3]);
		totalActions = nActions;
		totalStates = nStates;

			initialiseLUT();
		
	}

	public void initialiseLUT() {
		sizeOfLUT = totalActions * totalStates;
		
		LUTT = new double[sizeOfLUT];
		
		for (int i =0; i<nbearing; i++) 
			for(int k =0; k<nheading; k++)
						for(int j = 0; j <ndistance; j++)
							for(int a = 0; a<naction; a++)
						{
						
						LUTT [counter]= 0;
						indexLUT [i][k][j][a] = counter;
						counter++;							
						}

	}
	
	public int indexFor(double [] X) { 

		int index = 0;
		index = indexLUT [(int)(X[0]-1)][(int)(X[1]-1)][(int)(X[2]-1)][(int)(X[3]-1)];
		return index;
		
	}
	
	 public double outputFor(double [] X) {
		 
		 return LUTT[indexFor(X)];
		
	 }
	 public double train(double [] X, double argValue) {
		 
		
		 
		 return 1;
	 
	 }
	 
	 public void update(double [] X, double argValue) {
		 
		 int index = indexFor(X);
		 double a = argValue;
		 double value = Double.valueOf(df.format(a));
		 LUTT[index] = value;
	 }
	 
	 
	 
	 public void save(File argFile) {
		 
		 PrintWriter pw = null;
		 try {
				pw = new PrintWriter(argFile);
			}
			catch (IOException e) {

				e.printStackTrace();
			}


	        StringBuilder sb = new StringBuilder();

	        for (int k = 0; k <sizeOfLUT; k++){

	           sb.append(LUTT[k]); 
	            sb.append('\n');
	        }
	        pw.write(sb.toString());
	        pw.close();
	 }
	 

	 
	 public void load(String argFileName) throws IOException{
		 

	        String csvFile = argFileName;
	        BufferedReader br = null;
	        String line = "";
	        int counter = 0;

	        try {

	            br = new BufferedReader(new FileReader(csvFile));
	            while ((line = br.readLine()) != null) {

	               LUTT[counter] = Double.parseDouble(line);
	             
	            }

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	 }

}