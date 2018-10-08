package QRobot;
/**
* Interface for the Look Up Table (LUT) Class
 * You should 'implement' this interface
 * @date 20 June 012
 * @author sarbjit
 *
9 */
 public interface LUTInterface extends CommonInterface {

 /**
 * Constructor. (You will need to define one in your implementation)
14 * @param argNumInputs The number of inputs in your input vector
15 * @param argVariableFloor An array specifying the lowest value of each variable in the input vector.
16 * @param argVariableCeiling An array specifying the highest value of each of the variables in the input vector.
17 * The order must match the order as referred to in argVariableFloor.
18 *
19 public LUT (
20 int argNumInputs,
21 int [] argVariableFloor,
22 int [] argVariableCeiling );
23 */

 /**
26 * Initialise the look up table to all zeros.
27 */
 public void initialiseLUT();

 /**
31 * A helper method that translates a vector being used to index the look up table
32 * into an ordinal that can then be used to access the associated look up table element.
33 * @param X The state action vector used to index the LUT
34 * @return The index where this vector maps to
35 */
 public int indexFor(double [] X);


 } // End of public interface LUT