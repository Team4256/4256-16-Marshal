package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotDrive;





public class AutoModes {
	static double ROBOT_SPEED = .3;

	
//	public static int DEFENSE_DISTANCE = 0;
//	public static int LOWBAR_DISTANCE = 0;
//	public static int ROCKWALL_DISTANCE = 0;
//	public static int ROUGHTERRAIN_DISTANCE = 0;
//	public static int MOAT_DISTANCE = 0;
//	public static int ONE_BALL_DISTANCE = 0;
//	public static int START_DIS = 0;
	
	public static long DISTANCE_BETWEEN_DEFENCES;
	

//	static Obstacle startingBarrier;
//	static enum Obstacle {
//		portcullis, cheval_de_frise, //Category A
//		moat, ramparts, //Category B
//		drawbridge, sally_port, //Category C
//		rock_wall, rough_terrain, //Category D
//		low_bar //Static
//	}

	
	public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	
	public static boolean inAutonomous() {
		return Robot.gamemode == Robot.Gamemode.AUTONOMOUS;
	}

	///////////////////MODES//////////////////

	public static void oneBall() {
		//Aim turret
		syncAimRotator();
		
		//Cross barrier
		Obstacle.getStartingObstacle().crossBarrier(1);
		
		//Fire
		Timer.delay(.5);
//		Robot.turret.fire();
		Timer.delay(.5);
	}

	public static void twoBall() {
		//Fire first ball
		oneBall();
		
		//Return to neutral zone
		Obstacle.getStartingObstacle().crossBarrier(-1);
//		moveForwardForTime(-ROBOT_SPEED, 2000);
		
		//Pick up ball
		rotateToGyroPosition(270);
		moveForwardForTime(ROBOT_SPEED, 1000);
		//TODO pick up ball
		
		//Return to barrier
		moveForwardForTime(-ROBOT_SPEED, 1000);
		rotateToGyroPosition(0);
		
		//Fire second ball
		oneBall();
	}
	
	///////////////////FUNCTIONS//////////////////
	//------rotator------
	private static boolean syncAimRotatorIsRunning = false;
	public static void syncAimRotator() {
		if(!syncAimRotatorIsRunning) {
			exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
					while(inAutonomous()) {
//						Robot.turret.aimRotatorToTarget();
					}
				}});
		}
		
		syncAimRotatorIsRunning = true;
	}
	
	//------intake lifter------
	public static void syncIntakeLifterUp() {
		private_syncIntakeLifter(IntakeLifter.LIFTER_MOTOR_SPEED, 2000);
	}
	
	public static void syncIntakeLifterDown() {
		private_syncIntakeLifter(-IntakeLifter.LIFTER_MOTOR_SPEED, 2000);
	}
	
	private static int intakeLifterCommandCurrentIndex = 0;
	private static void private_syncIntakeLifter(double liftSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		exeSrvc.execute(new Runnable() {
			@Override
			public void run() {
				int intakeLifterCommandIndex = intakeLifterCommandCurrentIndex++;
				
				while(System.currentTimeMillis()-startTime < timeoutMillis && 
						intakeLifterCommandIndex  == intakeLifterCommandCurrentIndex && inAutonomous()) {
					Robot.intakeLifter.lifterLeft.set(liftSpeed);
				}
			}});
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

	private static long moveToLimitSwitch(double driveSpeed, DigitalInput limitSwitch, long timeoutMillis) {

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
	
