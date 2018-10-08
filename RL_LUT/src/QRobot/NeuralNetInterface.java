package QRobot;

import java.io.File;


public interface NeuralNetInterface extends CommonInterface{

final double bias = 1.0; // The input for each neurons bias weight

 /**
13 * Constructor. (Cannot be declared in an interface, but your implementation will need one)
14 * @param argNumInputs The number of inputs in your input vector
15 * @param argNumHidden The number of hidden neurons in your hidden layer. Only a single hidden layer is supported
16 * @param argLearningRate The learning rate coefficient
17 * @param argMomentumTerm The momentum coefficient
18 * @param argA Integer lower bound of sigmoid used by the output neuron only.
19 * @param arbB Integer upper bound of sigmoid used by the output neuron only.
20
21 public abstract NeuralNet (
22 int argNumInputs,
23 int argNumHidden,
24 double argLearningRate,
25 double argMomentumTerm,
26 double argA,
27 double argB );
28 */

 /**
31 * Return a bipolar sigmoid of the input X
32 * @param x The input
33 * @return f(x) = 2 / (1+e(-x)) - 1
34 */
 public double sigmoid(double x);

 /**
38 * This method implements a general sigmoid with asymptotes bounded by (a,b)
39 * @param x The input
40 * @return f(x) = b_minus_a / (1 + e(-x)) - minus_a
41 */
 public double customSigmoid(double x);

/**
45 * Initialize the weights to random values.
46 * For say 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
47 * Like wise for hidden units. For say 2 hidden units which are stored in an array.
48 * [0] & [1] are the hidden & [2] the bias.
49 * We also initialise the last weight change arrays. This is to implement the alpha term.
50 */
 public void initializeWeights();

 /**
54 * Initialize the weights to 0.
55 */
 public void zeroWeights();

} // End of public interface NeuralNetInterface