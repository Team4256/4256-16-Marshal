package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class AutoModes {
	public static final long DISTANCE_BETWEEN_DEFENCES = 0;

	static double ROBOT_SPEED = .3;
	
	public static int DEFENSE_DISTANCE = 0;
	public static int LOWBAR_DISTANCE = 0;
	public static int ROCKWALL_DISTANCE = 0;
	public static int ROUGHTERRAIN_DISTANCE = 0;
	public static int MOAT_DISTANCE = 0;
	public static int ONE_BALL_DISTANCE = 0;
	public static int START_DIS = 0;
	
//	static Obstacle startingBarrier;
//	static enum Obstacle {
//		portcullis, cheval_de_frise, //Category A
//		moat, ramparts, //Category B
//		drawbridge, sally_port, //Category C
//		rock_wall, rough_terrain, //Category D
//		 //Static
//	}
	
	public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	
	public static boolean inAutonomous() {
		return Robot.gamemode == Robot.Gamemode.AUTONOMOUS;
	}

	///////////////////MODES//////////////////

	public static void oneBall() {
		Robot.intakeLifter.liftDown();
		syncAimRotator();
		Obstacle.getStartingObstacle().crossBarrier();
		Robot.intakeLifter.liftUp();
		Timer.delay(500);
		Robot.launcher.fire();
	}
	
	public static void twoBall() {
		oneBall();
		Robot.intakeLifter.liftDown();
		moveForwardForTime(-ROBOT_SPEED, 2000);
		rotateToGyroPosition(270);
		moveForwardForTime(ROBOT_SPEED, 1000);
		moveForwardForTime(-ROBOT_SPEED, 1000);
		rotateToGyroPosition(0);
		oneBall();
	}
	
	
	
	
	///////////////////FUNCTIONS//////////////////
	private static boolean syncAimRotatorIsRunning = false;
	public static void syncAimRotator() {
		if(!syncAimRotatorIsRunning) {
			exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
					while(inAutonomous()) {
						Robot.launcher.aimRotator();
					}
				}});
		}
		
		syncAimRotatorIsRunning = true;
	}


	
	///////////////////MOVEMENT-conversions//////////////////
	private double FEET_PER_SECOND = 4;//TODO CALCULATE FEET PER SECOND
	public double DISTANCE_TO_TIME(double distanceInFeet) {
		return 1000*distanceInFeet/FEET_PER_SECOND;
	}
	
	public double DISTANCE_TO_TIME(double distanceInFeet, double speed) {
		//TODO plug in formula if time?
		throw new Error("DISTANCE_TO_TIME not implemented");
		//return -1;
	}
	
	
	///////////////////MOVEMENT-functions//////////////////
	public static double currentAngle = 0;//need current angle in gyro, move
	public static void rotateToGyroPosition(double angle) {
		currentAngle = angle;
		
		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
			Robot.gyro.rotateToAngle(angle);
		}
		
		stop();
	}

	public static void moveForwardForTime(double driveSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.rotateToAngle(currentAngle));
		}
		
		stop();
	}
	
	static long moveToLimitSwitch(double driveSpeed, DigitalInput limitSwitch, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		while(limitSwitch.get() && System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.rotateToAngle(currentAngle));
		}
		
		stop();
		return System.currentTimeMillis()-startTime;
	}
	
	public static void stop() {
		Robot.drive.arcadeDrive(0, 0);
	}
	
	
	
//	private static double accelerationFunctionCurrentSpeed = 0;
//	private static double stepTime = 0;
//	private static double speedIncrement = 0;
	public static void moveAccleration(double driveMaxSpeed, double driveMinSpeed, long timeoutMillis) {
		double speed = 0;
		long startTime = System.currentTimeMillis();
		double speedRange = driveMaxSpeed - driveMinSpeed;
//		double steps = (int) (driveMaxSpeed*timeoutMillis/stepTime);
		
		//Acceleration
		while(System.currentTimeMillis()-startTime < timeoutMillis/2 && inAutonomous()) {
			speed = 2*(System.currentTimeMillis()-startTime)/timeoutMillis;
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.rotateToAngle(currentAngle));
		}
		
		//Decelleration
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTime < timeoutMillis/2 && inAutonomous()) {
			speed = 1-2*(System.currentTimeMillis()-startTime)/timeoutMillis;
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.rotateToAngle(currentAngle));
		}
		
		stop();
	}
//	private double getStep(double startTime, double stepTime) {
//		return ((System.currentTimeMillis()-startTime)/stepTime);
//	}
	
	
//	private static class DriveSpeed {
//		public double moveValue;
//		public double rotateValue;
//		
//		public DriveSpeed(double moveValue, double rotateValue) {
//			this.moveValue = moveValue;
//			this.rotateValue = rotateValue;
//		}
//		
//		public void drive() {
//			Robot.drive.arcadeDrive(moveValue, rotateValue);
//		}
//	}
}
