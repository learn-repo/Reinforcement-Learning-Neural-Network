package QRobot;

public class neurons {

    double output;
    double weights[] ;
    double PrevWeights[] ;



    public neurons(int numberOfWeights){

        weights = new double[numberOfWeights];
        PrevWeights = new double[numberOfWeights];
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double o){
        output = o;
    }


    public double getWeight(int index)
    {
        return weights[index];

    }

    public void setWeight(int index, double value)
    {
        weights[index] = value;

    }
    public void setPrevWeight(int index, double value)
    {
        PrevWeights[index] = value;

    }

    public double getPrevWeight(int index)
    {
        return PrevWeights[index];

    }

}
