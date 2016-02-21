package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.RobotDrive;
public class AutoModes {
	private static double ROBOT_SPEED = .3;
	
	public static int DEFENSE_DISTANCE = 0;
	public static int LOWBAR_DISTANCE = 0;
	public static int ROCKWALL_DISTANCE = 0;
	public static int ROUGHTERRAIN_DISTANCE = 0;
	public static int MOAT_DISTANCE = 0;
	public static int ONE_BALL_DISTANCE = 0;
	public static int START_DIS = 0;
	
	static Obstacle startingBarrier;
	static enum Obstacle {
		portcullis, cheval_de_frise, //Category A
		moat, ramparts, //Category B
		drawbridge, sally_port, //Category C
		rock_wall, rough_terrain, //Category D
		low_bar //Static
	}
	
	public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	
	public static boolean inAutonomous() {
		return Robot.gamemode == Robot.Gamemode.AUTONOMOUS;
	}

	///////////////////MODES//////////////////

	public static void oneBall(boolean fromPortcullis) {
		Robot.intakeLifter.liftDownAutomatic();
		syncAimRotator();
		if(startingBarrier == Obstacle.portcullis) {
			moveToLimitSwitch(ROBOT_SPEED, Robot.intake.middleLimitSwitch/*change limit switch*/, 3000);
			Robot.intakeLifter.liftUpAutomatic();
			moveForwardForTime(ROBOT_SPEED, 2000);
		}else{
			moveForwardForTime(ROBOT_SPEED, 2000);
		}
		Robot.intakeLifter.liftUpAutomatic();
		Timer.delay(500);
		Robot.turret.fire();
	}
	
	public static void twoBall() {
		//oneBall();
		Robot.intakeLifter.liftDownAutomatic();
		moveForwardForTime(-ROBOT_SPEED, 2000);
		rotateToGyroPosition(270);
		moveForwardForTime(ROBOT_SPEED, 1000);
		moveForwardForTime(-ROBOT_SPEED, 1000);
		rotateToGyroPosition(0);
		//oneBall();
	}
	
	
	
	
	///////////////////FUNCTIONS//////////////////
	private static boolean syncAimRotatorIsRunning = false;
	public static void syncAimRotator() {
		if(!syncAimRotatorIsRunning) {
			exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
					while(inAutonomous()) {
						Robot.turret.aimRotator();
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
