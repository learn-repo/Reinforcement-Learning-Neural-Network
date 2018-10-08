package QRobot;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Math;
import java.text.DecimalFormat;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class QLearnRobotNN  extends AdvancedRobot 
{
	
	public  double alpha = 0.8;
	public  double gamma = 0.9;
	public  double bearing;
	public  double heading;
	public  double distance;
	public  double QuanBearing;
	public  double QuanHeading;
	public  double QuanDistance;
	public  double QuanX;
	public  double QuanY;
	public static double reward = 0;
	public static int nActions = 6;
	public static int nStates = 8*8*3;
	private static int winTimes = 0;
	public static double X [] = {8,8,3,6};
	public double stateAction[] = {0,0,0,0};
	public double stateActionP[] = {0,0,0,0};
	public double QValues [] = {0,0,0,0,0,0};
	public static int firstRun = 0;
	public static boolean Policy = false;
	public int run = 0;
	DecimalFormat df = new DecimalFormat("#.#");
	
	public double randomEpsilon = 0;
	public double Epsilon = 0.75;
	public int randomAction = 0;
	public  double Q =0;
	public  double Q2 = 0;
	public  int action1 = 0;
	public int action2 = 0;
	private static int resultCount =0;
	public double enemyBearing = 0;
	public static int sizeLUT = nStates*nActions;
	
	public static QLUT LUT = new QLUT(nStates,nActions, X);
	public static int results[][] = new int [100000][2];
	public static double QError [] = new double [100000];
	
	public static boolean continueRun = false;
	public static boolean IntermediateRewards = true;
	public static QNN NN = new QNN();
	 public  static double who[] = new double[8];
	   public static double wih [][] = new double[8][5];
	
	int turnDirection = 1;
	
	
	public void run(){
		
		if (firstRun ==0) {
		loadWeights();
		NN.setWeights(wih, who);
		}
		
		if (continueRun )
		loadLUT();
		
		for (int i = 0; i<100000; i++)
		{	
			results[i][0]=0; 
			results[i][1]=0; 

		}
		if (firstRun != 0 ) {
			
			 loadLUT();
			 loadData();
			
		}

		setBodyColor(Color.blue);
		setGunColor(Color.white);
		setRadarColor(Color.red);
	
		while (true)  {
			setTurnRight(10000);
			setMaxVelocity(5);
			ahead(10000);
			}
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		
		
		bearing = e.getBearing();
		distance = e.getDistance();
		heading = getHeading();

		QuanBearing = QuantifyBearing(bearing);
		QuanHeading = QuantifyHeading(heading);
		QuanDistance = QuantifyDistance(distance);
		
			stateAction[0] = QuanHeading;
			stateAction[1] = QuanBearing ;
			stateAction[2] = QuanDistance;
			stateAction[3] = 0;
			
			for(int i = 0; i < nActions; i++)
			{
				stateAction[stateAction.length -1] = i+1;
				QValues[i] = NN.outputFor(stateAction);
			}
			
			double  MaxQ = -1000000;
			int MaxQI = -10000000;
			for (int i =0; i< nActions ; i++) {
				
				if  ( QValues[i] > MaxQ ) {
					
					MaxQ = QValues[i];
					MaxQI  = i;
				}
			}		
			
			stateAction[stateAction.length-1] =MaxQI + 1;
			Q = LUT.outputFor(stateAction);	

			if(Policy && run ==0) //on-policy learning
			{
				randomEpsilon = Math.random();
				if(randomEpsilon < Epsilon)
				{
					
						randomAction = 0 + (int)(Math.random() * ((5 - 0) + 1));		
						ExecuteAction(randomAction);
						stateAction[stateAction.length -1] = randomAction+1;	
						action1 = randomAction+1;
						Q = LUT.outputFor(stateAction);
				}
				else {
					ExecuteAction(MaxQI);	
					stateAction[stateAction.length -1] = MaxQI+1;	
					action1 =  MaxQI+1;	
					Q = LUT.outputFor(stateAction);
				}
				run++;
			}
			
			else if (Policy && run !=0) { //on-policy learning

				bearing = e.getBearing();
				distance = e.getDistance();
				heading = getHeading();

				QuanBearing = QuantifyBearing(bearing);
				QuanHeading = QuantifyHeading(heading);
				QuanDistance = QuantifyDistance(distance);
				
					stateActionP[0] = QuanHeading;
					stateActionP[1] = QuanBearing ;
					stateActionP[2] = QuanDistance;
				
					stateActionP[3] = 0;
					
					for(int i = 0; i < nActions; i++)
					{
						stateActionP[stateAction.length -1] = i+1;
						QValues[i] = LUT.outputFor(stateActionP);
					}
					
					 MaxQ = -10000000;
					MaxQI = -10000000;
					for (int i =0; i< nActions ; i++) {
						
						if  ( QValues[i] > MaxQ ) {
							
							MaxQ = QValues[i];
							MaxQI  = i;
						}
					}
					
					randomEpsilon = Math.random();
					if(randomEpsilon < Epsilon)
					{
							randomAction = 0 + (int)(Math.random() * ((5 - 0) + 1));		
							ExecuteAction(randomAction);
							stateActionP[stateAction.length -1] = randomAction+1;			
							Q2 = LUT.outputFor(stateActionP);
							action2 = randomAction+1;		
							
					}
					else {
						ExecuteAction(MaxQI);
					stateActionP[stateAction.length-1] =MaxQI + 1;
					Q2= LUT.outputFor(stateActionP);	
					action2 = MaxQI + 1;		
					
					}
					
					Q = Q + alpha * (reward + gamma * Q2 - Q);	
					LUT.update(stateAction, Q);
					stateAction = stateActionP;
					action1 = action2;
					Q = LUT.outputFor(stateAction);
					reward = 0;
					run++;
						
			}

			/// end of on-policy
			
			else //off-policy learning
			{
				randomEpsilon = Math.random();
				if(randomEpsilon < Epsilon)
				{
						randomAction = 0 + (int)(Math.random() * ((5 - 0) + 1));			
						ExecuteAction(randomAction);
			
						
				}
				else {
					ExecuteAction(MaxQI);				
					
				}
				
				
			bearing = e.getBearing();
			distance = e.getDistance();
			heading = getHeading();

			QuanBearing = QuantifyBearing(bearing);
			QuanHeading = QuantifyHeading(heading);
			QuanDistance = QuantifyDistance(distance);
				
			stateActionP[0] = QuanHeading;
			stateActionP[1] = QuanBearing ;
			stateActionP[2] = QuanDistance;
			stateActionP[3] = 0;

			
			for(int i = 0; i < nActions; i++)
			{
				stateActionP[stateAction.length -1] = i+1;
				QValues[i] = NN.outputFor(stateActionP);
			}
			
			double  CMaxQ = -1000000;
			int CMaxQI = 0;
			
			for (int i =0; i< nActions ; i++) {
				
				if  ( QValues[i] > CMaxQ ) {
					
					CMaxQ = QValues[i];
					CMaxQI  = i;
				}
			}
			
			stateActionP[stateAction.length-1] =CMaxQI + 1;
			
			Q = Q + alpha * (reward + gamma * CMaxQ - Q);	
			NN.updateWeights(Q);
			stateAction = stateActionP;
			reward = 0;

			}
			scan();
		
	}
	

	
	public double QuantifyDistance (double d) {
		
		if  (d <= 100)
			return Double.valueOf(df.format(1 + (d/10)));
		
		else if (d <= 200)
			return Double.valueOf(df.format(2 + (d/20)));
		
		else
			return Double.valueOf(df.format(3 + (d/30)));

	}
	
	public double QuantifyHeading (double h) {
		
		if(Math.ceil(h/45)==0)
			return 1;
		else
			return Double.valueOf(df.format((Math.ceil(h/45))));
		
		
	}
	
public double QuantifyBearing (double b) {
	
	
	if(Math.ceil(b/45)+4==0)
		return 1;
	else
		return Double.valueOf(df.format((Math.ceil(b/45)+4)));
	
		
	}

public void shoot() {
	
	
	if (bearing >= 0) {
		turnDirection = 1;
	} else {
		turnDirection = -1;
	}
	turnRight(bearing);
	
	setFire(2);
	out.println("shoot");

}

public void follow() {
	
	
	turnRight(bearing);

out.println("chase");
if (bearing >= 0) {
	turnDirection = 1;
} else {
	turnDirection = -1;
}
turnRight(bearing);

setFire(2);

ahead(distance - 140);


}

public void goUp() {
	
	ahead(40);
	out.println("up");
	if (bearing >= 0) {
		turnDirection = 1;
	} else {
		turnDirection = -1;
	}
	setFire(2);
	turnRight(bearing);
	

	
}
public void goDown() {
	back(40);
	out.println("down");
	if (bearing >= 0) {
		turnDirection = 1;
	} else {
		turnDirection = -1;
	}
	setFire(2);
	turnRight(bearing);
	

	
	
}
public void goRight() {
	turnRight(45);
	out.println("right");
	if (bearing >= 0) {
		turnDirection = 1;
	} else {
		turnDirection = -1;
	}
	setFire(2);
	turnRight(bearing);
	
	
	
}
public void goLeft() {
	turnLeft(45);
	out.println("left");
	if (bearing >= 0) {
		turnDirection = 1;
	} else {
		turnDirection = -1;
	}
	setFire(3);
	turnRight(bearing);
	
	
}


public void ExecuteAction(int actionIndex) {
	
	switch(actionIndex) {
	
	case 0:
		goDown();
		break;
	case 1:
		goUp();
		break;
	case 2:
		shoot();
		break;
	case 3:
		goRight();
		break;
	case 4:
		goLeft();
		break;
	case 5:
		follow();
		break;
	
	}	
}
public void onDeath(DeathEvent e)
{

	reward -= 100;	
	firstRun++;
	saveData();
	saveLUT();
	 saveQvalue();
	
}

public void onRoundEnded(RoundEndedEvent ee) {
	int roundNumber = getRoundNum();

  if(roundNumber % 100 == 0) {
			
		
	results[resultCount][0] = roundNumber;
	results[resultCount][1] = winTimes;
	
	double x[] = new double [4];
	x[0] = 3;
	x[1] = 4;
	x[2] = 2;
	x[3] = 3;
	QError[resultCount] = NN.outputFor(x);
	resultCount++;
	winTimes = 0;
	}
	
	out.println(roundNumber);
	out.println("win number " + winTimes);
	
}

public void onBattleEnded(BattleEndedEvent ee) {
	
	out.println("end");
	saveFinal();
	
}



public void saveData() {
	
	
PrintStream w = null;
	
	try {
		w = new PrintStream(new RobocodeFileOutputStream(getDataFile("results.csv")));
		
			for (int i = 0; i< resultCount; i++) {
				
				w.println(results[i][0]);
				w.println(results[i][1]);
			}

		if (w.checkError()) {
			out.println("I could not write the results!");
		}
	} catch (IOException e) {
		out.println("IOException trying to write results: ");
		e.printStackTrace(out);
	} finally {
		if (w != null) {
			w.close();
		}
	}
}


public void saveFinal() {
	
	
PrintStream w = null;
	
	try {
		w = new PrintStream(new RobocodeFileOutputStream(getDataFile("Finalresults.csv")));
		
			for (int i = 0; i< resultCount; i++) {
				
				w.println(results[i][1]);
				
			}

		if (w.checkError()) {
			out.println("I could not write the results!");
		}
	} catch (IOException e) {
		out.println("IOException trying to write results: ");
		e.printStackTrace(out);
	} finally {
		if (w != null) {
			w.close();
		}
	}

	
}


public void saveQvalue() {
	
	
PrintStream w = null;
	
	try {
		w = new PrintStream(new RobocodeFileOutputStream(getDataFile("Q.csv")));
		
			for (int i = 0; i< resultCount; i++) {
				
				w.println(QError[i]);
				
			}

		if (w.checkError()) {
			out.println("I could not write the results!");
		}
	} catch (IOException e) {
		out.println("IOException trying to write results: ");
		e.printStackTrace(out);
	} finally {
		if (w != null) {
			w.close();
		}
	}

	
}

public void loadData() {
	
	try {
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new FileReader(getDataFile("results.csv")));

			for (int i = 0; i< resultCount; i++) {
				
			results[i][0] = Integer.parseInt(reader.readLine());
			results[i][1] = Integer.parseInt(reader.readLine());
				
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

public void loadWeights() {
	
	try {
		BufferedReader reader = null;
		try {
			// Read file "count.dat" which contains 2 lines, a round count, and a battle count
			reader = new BufferedReader(new FileReader(getDataFile("weights.csv")));
			for (int k = 0; k <=NN.NoHiddenNeuron; k++){

				who[k] =  Double.parseDouble(reader.readLine());
	        }

	        for (int j = 0; j < NN.NoHiddenNeuron; j++) {
	            for (int k = 0; k < NN.NoInputNeuron + 1; k++) {

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

public void loadLUT() {
	
	try {
		BufferedReader reader = null;
		try {
			// Read file "count.dat" which contains 2 lines, a round count, and a battle count
			reader = new BufferedReader(new FileReader(getDataFile("LUT.csv")));

			for (int i =0; i< sizeLUT; i++) {

				LUT.LUTT[i] = Double.parseDouble(reader.readLine());
		
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

public void saveLUT() {
	
	
PrintStream w = null;
	
	try {
		w = new PrintStream(new RobocodeFileOutputStream(getDataFile("LUT.csv")));

			
			for (int i =0; i< sizeLUT; i++) {

				w.println(LUT.LUTT[i]); 
		
			}
	
		if (w.checkError()) {
			out.println("I could not save the LUT!");
		}
	} catch (IOException e) {
		out.println("IOException trying to save: ");
		e.printStackTrace(out);
	} finally {
		if (w != null) {
			w.close();
		}
	}
	
}

public void onWin(WinEvent e)
{

	reward += 100;
	winTimes++;
	firstRun++;
	
	saveLUT();
	saveData();
	 saveQvalue();

	
}

public void onHitWall(HitWallEvent e)
{
	
	if(IntermediateRewards) {
	reward += -8;}
}
	

public void onHitByBullet(HitByBulletEvent e)
{		
	if(IntermediateRewards) {
	reward -= 25;}
}

public void onBulletHit(BulletHitEvent e)
{
	if(IntermediateRewards) {
	reward += 10;}
}

public void onBulletMissed(BulletMissedEvent e)
{ 
	if(IntermediateRewards) {
	reward -= 8;}
}


	public void onHitRobot(HitRobotEvent e) {
		
		if(IntermediateRewards) {
		reward += 20;}
	
	}




}
