package QRobot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

 /**
7 * This interface is common to both the Neural Net and LUT interfaces.
8 * The idea is that you should be able to easily switch the LUT
9 * for the Neural Net since the interfaces are identical.
10 * @date 20 June 2012
11 * @author sarbjit
12 *
1 */
 public interface CommonInterface {
 /**
16 * @param X The input vector. An array of doubles.
17 * @return The value returned by th LUT or NN for this input vector
18 */
 public double outputFor(double [] X);
 
 /**
22 * This method will tell the NN or the LUT the output
23 * value that should be mapped to the given input vector. I.e.
24 * the desired correct output value for an input.
25 * @param X The input vector
26 * @param argValue The new value to learn
27 * @return The error in the output for that input vector
28 */
 public double train(double [] X, double argValue);

 /**
32 * A method to write either a LUT or weights of an neural net to a file.
33 * @param argFile of type File.
34 
 * @throws FileNotFoundException */
 public void save(File argFile);

 /**
38 * Loads the LUT or neural net weights from file. The load must of course
39 * have knowledge of how the data was written out by the save method.
40 * You should raise an error in the case that an attempt is being
41 * made to load data into an LUT or neural net whose structure does not match
42 * the data in the file. (e.g. wrong number of hidden neurons).
43 * @throws IOException
44 */
 public void load(String argFileName) throws IOException;
 }