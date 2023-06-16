import java.util.ArrayList;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Control {
	
	// Letter
	//
	//	left					right
	//
	//  0             x 
	//  |---------------------------|0  
	//	|							|     MotorA
	//	|							|		|
	//	|							| 		|
	//	|							| y		|
	//	|							|		|
	//	|							|	    V
	//	|							|
	//	|---------------------------|
	//
	//				MotorB
	//			-------------->
	//
	//

	
	EV3LargeRegulatedMotor motorY   = new EV3LargeRegulatedMotor (MotorPort.A);
	EV3LargeRegulatedMotor motorX   = new EV3LargeRegulatedMotor (MotorPort.B);
	EV3LargeRegulatedMotor motorPen = new EV3LargeRegulatedMotor (MotorPort.D);
	
	boolean isPenDown = true;
	
	//true = positive 
	//false = negative
	boolean prevDirection = true;
	
	
	private int y = 0;
	private int x = 0;
	
	//private int yMAx;
	//private int xMAx;
	
	public Control() {
		
		
		
	}
	
	//=================//
	
	public void penUp() {
		
		if (isPenDown) {
			motorPen.rotate(-40);
			isPenDown = !isPenDown;
		}
	}
	
	public void penDown() {
		
		if (!isPenDown) {
			motorPen.rotate(40);
			isPenDown = !isPenDown;
		}
	}
	
	//=================//
	
	public void nextX() {
		
		motorX.rotate(50);
		x++;
		
	}
	
	public void goToY(int yGoal) {

		int yDiff  = yGoal-y;
		
		//Prep Gears
		if (yDiff > 0) {			 //if movement is positive
			
			if (!prevDirection) motorY.rotate(50);
			prevDirection = true;
			
		} else if (yDiff < 0) {  	//if movement is negative
			
			if (prevDirection)  motorY.rotate(-50);
			prevDirection = false;
			
		}
		
		motorY.rotate(50*yDiff);
		y = yGoal;	

	}
	
	public void goToYAlt(int yGoal) {

		int yDiff  = yGoal-y;
		/*
		//Prep Gears
		if (yDiff > 0) {			 //if movement is positive
				
			if (!prevDirection) motorY.rotate(50);
			prevDirection = true;
					
		} else if (yDiff < 0) {  	//if movement is negative
					
			if (prevDirection)  motorY.rotate(-50);
			prevDirection = false;
					
		}*/
		
		if (yDiff > 0) {
			for (int i = 0; i < yDiff; i++) motorY.rotate(50);
		} else if (yDiff < 0) {
			for (int i = 0; i > yDiff; i--) motorY.rotate(-50);
		}
		y = yGoal;	

	}
	
	public void drawDot() {
		penDown();
		penUp();
	}
	
	//=================//
	
	public void printLine(int[] line) {
		
		penUp();
		
		//boolean firstNew = true;
		
		if (x % 2 == 0) {
			
			//motorY.rotate(-50);
		
			for (int i = 0; i < line.length; i++) {
			
				if (line[i] == 1) {
					
					//if ( i-y >= 0 && firstNew) {
					//	motorY.rotate(-40);
					//	firstNew = false;
					//}
					
					goToYAlt(i);
					drawDot();
				}
				//if (i == line.length-1) goToYAlt(i);
			}
		
		} else {
			
			//motorY.rotate(50);
			
			for (int i = line.length-1; i >= 0; i--) {
				
				if (line[i] == 1) {
					
					//if ( i-y <= 0 && firstNew) {
					//	motorY.rotate(40);
					//	firstNew = false;
					//}
					
					goToYAlt(i);
					drawDot();
				}
				//if (i == 0) goToYAlt(i);
			}
			
		}
		
		//firstNew = true;
		
		nextX();

	}
	
	public void printFull(ArrayList<int[]> imgData) {

		penUp();
		
		motorX.rotate(100);
		
		for (int line = 0; line < imgData.size(); line++) {

			printLine(imgData.get(line));
			
		}

	}
	
	public void testLines() {
		
		penUp();
		
		motorY.rotate(-50);
		motorY.rotate(-50);
		motorY.rotate(-50);
		
		//
		drawDot();
		for (int i = 0; i < 20; i++) motorY.rotate(-50);
		drawDot();
		
		nextX();
		nextX();
		motorY.rotate(40);
		for (int i = 0; i < 20; i++) motorY.rotate(50);
		motorY.rotate(-40);
		
		drawDot();
		motorY.rotate(-50*20);
		drawDot();
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

}
