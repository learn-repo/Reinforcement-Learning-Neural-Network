package QRobot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.PrintStream;

public class QNN implements NeuralNetInterface {

    public  neurons [] n = new neurons[14];
   
    double ErrorMin= 0.05;
    double Error = 1; 
    double momentum = 0.4;
    double learningRate = 0.01;
    double errors[] = new double[20000];
    int NoHiddenNeuron = 7;
    int NoInputNeuron = 4;
    int outputIndex = 0;
    int iterations = 20000;
    int trainLength = 1152;
    double outputs[] = new double[1152];
    double expectedOutputs[] = new double[1152];
    double input1[] = new double[1152];
    double input2[] = new double[1152];
    double input3[] = new double[1152];
    double input4[] = new double[1152];
    double who[] ;
    double wih [][] ;

    public QNN(){
    	
        who = new double[NoHiddenNeuron+1];
        wih = new double[NoHiddenNeuron][NoInputNeuron+1];
        
        outputIndex = NoHiddenNeuron + NoInputNeuron + 2;
     
        for (int i = 0; i <  NoInputNeuron + 1 ; i++)
        {
            n[i] = new neurons(NoHiddenNeuron); 
        }

        for (int i =  NoInputNeuron + 1; i < outputIndex; i++)
        {
            n[i] = new neurons(1); 
        }

        n[outputIndex] = new neurons(0); 
        
        n[0].setOutput(1); 
        n[5].setOutput(1);
    }

    public void loadWeights() {
    	
    	try {
    		BufferedReader reader = null;
    		try {
    		
    			reader = new BufferedReader(new FileReader("weights.csv"));
    			for (int k = 0; k <=NoHiddenNeuron; k++){

    				who[k] =  Double.parseDouble(reader.readLine());
    	        }

    	        for (int j = 0; j < NoHiddenNeuron; j++) {
    	            for (int k = 0; k < NoInputNeuron + 1; k++) {

    	            	wih[j][k] = Double.parseDouble(reader.readLine());
    	            }
    	        }

    		} finally {
    			if (reader != null) {
    				reader.close();
    			}
    		}
    	} catch (IOException e) {
    		
    		
    	} catch (NumberFormatException e) {
 	
    	}
    }
    public double sigmoid(double x){

        return ((2.0 / (1.0 +  (Math.exp(-x)))) - 1); //bipolar sigmoid
    }

    public double sigmoid2(double x){

        return 1.0 / (1.0 +  (Math.exp(-x)));
    }

    public void updateWeights(double expectedOutput){

        double yi = n[outputIndex].getOutput();
        double ci = expectedOutput;
        double deltaOutput = (ci - yi) * (0.5*((1+yi) * (1-yi))); //find delta for output layer

        double who[] = new double[NoHiddenNeuron+1]; //create weight values for hidden to output neurons (ho)
        double WeightChangeho[] = new double[NoHiddenNeuron+1];  //create weight change values for each neuron in hidden layer + bias

        for (int i = 0; i<=NoHiddenNeuron; i++){

            WeightChangeho[i]=  n[i+NoInputNeuron+1].getWeight(0) - n[i+NoInputNeuron+1].getPrevWeight(0); //current weight - previous weight
            who[i] =  n[i+NoInputNeuron+1].getWeight(0) + (momentum * WeightChangeho[i]) +
                    (deltaOutput*n[i+NoInputNeuron+1].getOutput()*learningRate);
            //= current weight + momentum* WeightChange + deltaOutputlayer* x(hidden) * learning rate
        }


        for (int j = NoInputNeuron+1; j<outputIndex; j++){
            n[j].setPrevWeight(0,n[j].getWeight(0));//update previous weight to current weight
            n[j].setWeight(0,who[j-(NoInputNeuron+1)]); //update current weight to new calculated weight

        }

        double WeightChangeih [][] = new double[NoHiddenNeuron][NoInputNeuron+1]; //create weight change values for each neuron in input layer + bias
        double wih [][] = new double[NoHiddenNeuron][NoInputNeuron+1]; //create weight values for input to hidden neurons (ih)

        for (int k=0; k<NoHiddenNeuron; k++){
            for (int m=0; m<NoInputNeuron+1; m++){

                WeightChangeih[k][m]= n[m].getWeight(k) - n[m].getPrevWeight(k); //current weight - previous weight
                wih [k][m] = n[m].getWeight(k) + (momentum *  WeightChangeih[k][m]) +
                        (deltaOutput*n[k+NoInputNeuron+2].getWeight(0)*0.5*(1+n[k+NoInputNeuron+2].getOutput())*(1-n[k+NoInputNeuron+2].getOutput())*
                                n[m].getOutput()*learningRate);
                //= current weight + momentum* WeightChange + deltaOutputlayer*0.5 *(1+h(i)*(1-h(i))* x(input) * learning rate
            }
        }

        for (int h = 0; h <NoInputNeuron+1; h++){
            for (int l = 0; l <NoHiddenNeuron; l++){

                n[h].setPrevWeight(l,n[h].getWeight(l));
                n[h].setWeight(l,wih[l][h]);
            }
        }
    }

    public void initializeWeights()
    {

        for (int i = 0; i < NoInputNeuron+1; i++) //input neurons + bias initialization
        {
            double w[] = new double[NoHiddenNeuron]; 

            for (int j = 0; j<w.length; j++ ){
                w[j] = Math.random() * (1) - 0.5; //creates a random variable between -0.5 and +0.5
            }

            for (int k = 0; k <NoHiddenNeuron; k++){

                n[i].setWeight(k, w[k]); //initially, both current and prev weights are assigned the same value
                n[i].setPrevWeight(k, w[k]);

            }
        }

        for (int i = NoInputNeuron+1; i <outputIndex; i++) //hidden neurons + bias initialization
        {

            double w[] = new double[1]; //each neuron has 1 weight

            for (int j = 0; j<1; j++ ){
                w[j] = Math.random() * (0.5 +0.5) - 0.5; //creates a random variable between -0.5 and +0.5
            }
            n[i].setWeight(0,w[0]);//initially, both current and prev weights are assigned the same value
            n[i].setPrevWeight(0,w[0]);
        }
    }
    
    
    public void setWeights(double wihh[][], double whoo[]) {
    	
    	
    	for (int h = 0; h <NoInputNeuron+1; h++){
            for (int l = 0; l <NoHiddenNeuron; l++){

                n[h].setPrevWeight(l,wihh[l][h]);
                n[h].setWeight(l,wihh[l][h]);
                wih[l][h] = wihh[l][h];
            }
        }
    	
    	 for (int j = NoInputNeuron+1; j<outputIndex; j++){
             n[j].setPrevWeight(0,whoo[j-(NoInputNeuron+1)]);//update previous weight to current weight
             n[j].setWeight(0,whoo[j-(NoInputNeuron+1)]); //update current weight to new calculated weight
             who[j-(NoInputNeuron+1)] = whoo[j-(NoInputNeuron+1)];
         }
    	
    }
    public void zeroWeights(){}

    public double customSigmoid(double x){ return 0;}

    public double outputFor(double [] X){
        for (int i = 1; i <=X.length; i++){

            n[i].setOutput(X[i-1]);

        }

        double hsum[] = new double[NoHiddenNeuron];
        double hsumm = 0;
        for (int j = 0; j<NoHiddenNeuron; j++){
            hsumm = 0;

            for (int k = 0; k<= NoInputNeuron; k++) {

                hsumm = hsumm + n[k].getOutput() * n[k].getWeight(j); 
                //hidden neuron value, using bipolar sigmoid activation
            }
            n[j + NoInputNeuron +2].setOutput(sigmoid(hsumm));
        }
        double osum = 0;

        for (int i = NoInputNeuron +1; i<outputIndex; i++){
            osum = osum + n[i].getOutput()*n[i].getWeight(0);
        }

        n[outputIndex].setOutput(sigmoid(osum)); //output neuron value, using bipolar sigmoid activation

        return n[outputIndex].getOutput();
    }
    public double train(double [] X, double argValue){ return 0;}

    public void WriteToFile(String Filename, int rows, double e[]) throws FileNotFoundException{

        PrintWriter pw = new PrintWriter(new File(Filename));
        StringBuilder sb = new StringBuilder();

        for (int k = 0; k <rows +1; k++){

            sb.append(e[k]); //append error value, epoch value is not needed as we can make a counter with the length of errors
            sb.append('\n');
        }
        pw.write(sb.toString());
        pw.close();
    }

    public void save(File argFile){};
    public void load(String argFileName) throws IOException{}

    public void loadLUT() {

        try {
            BufferedReader reader = null;
            try {
              
                reader = new BufferedReader(new FileReader("LUTFinal2.csv"));

                for (int i =0; i< trainLength; i++) {

                    expectedOutputs[i] = Double.parseDouble(reader.readLine());
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {


        } catch (NumberFormatException e) {
        }
    }

    public void createInput(){

        int c =0;

        for (int i =0; i<8; i++)
            for(int k =0; k<8; k++)
                for(int j = 0; j <3; j++)
                    for(int a = 0; a<6; a++)
                    {

                        input1[c] = i+1;
                        input2[c] =k+1;
                        input3[c] = j+1;
                        input4[c] = a+1;
                        c++;
                    }
    }
    public double[] getInputs(int i){

        double [] x = new double[4];
        x[0] =input1 [i];
        x[1] = input2 [i];
        x[2] =input3 [i];
        x[3] = input4 [i];

        return x;

    }
}






