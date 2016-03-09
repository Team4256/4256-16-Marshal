package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.usfirst.frc.team4256.robot.Robot.Gamemode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;





public class AutoModes {
	private static final double ROBOT_SPEED = -1;
	public static final double AUTO_LIFTER_UP_MOTOR_SPEED = .5;
	public static final double AUTO_LIFTER_DOWN_MOTOR_SPEED = 1;
	
	public static final double DISTANCE_CENTER_TO_BARRIER = 000;//TODO
	public static final double DISTANCE_ACROSS_BARRIER = 000;//TODO
	public static final double DISTANCE_DEFENCE_WIDTH = 50;//TODO add divider width, long enough
	
	public static final long TIMEOUT_DISTANCE_CENTER_TO_BARRIER = 5000;//TODO
	public static final long TIMEOUT_DISTANCE_ACROSS_BARRIER = 5000;//TODO
	public static final long TIMEOUT_DISTANCE_DEFENCE_WIDTH = 5000;//TODO
	
	public static final double RAMP_ANGLE = 8;//Actual angle is 12, but 6 should be enough

	
	public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	
	public static boolean inAutonomous() {
		return Robot.gamemode == Robot.Gamemode.AUTONOMOUS;
	}
	
	public static void showStatus(String status) {
		SmartDashboard.putString("Autonomous Stat", status);
	}
	
	///////////////////START//////////////////
	public static void start() {
		Robot.gamemode = Gamemode.AUTONOMOUS;
		Robot.gyro.zeroYaw();
		Robot.visionTable.putNumber("ShooterAngle", Robot.shooter.isRaised? 39.1 : 33);

		//		int autoMode =  (int) SmartDashboard.getNumber("AutonomousObstacles");
		//		int position = (int) SmartDashboard.getNumber("ObstaclePosition");
		int numBalls = (int) SmartDashboard.getNumber("NumberOfBalls");
		int autoMode =  (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
		int position = (int) SmartDashboard.getNumber("Position");


//		AutoModes.oneBall(Obstacle.low_bar);
		AutoModes.test();
		//		drive.arcadeDrive(0, 1);

		switch (autoMode) {
//		case 0: //Portcullis 
//			AutoModes.oneBall(Obstacle.portcullis);
//			break;
		case 1: 	//Cheval De Frise 
			AutoModes.oneBall(Obstacle.cheval_de_frise);
			break;
		case 2: 	//Moat 
			AutoModes.oneBall(Obstacle.moat);
			break;
		case 3: 	//Ramparts 
			AutoModes.oneBall(Obstacle.ramparts);
			break;
//		case 4:     //Drawbridge 
//			AutoModes.oneBall(new Obstacle("drawbridge", Difficulty.impossible, position));
//			break;
//		case 5: 	//Sally Port 
//			AutoModes.oneBall(new Obstacle("sally_port", Difficulty.impossible, position));
//			break;
//		case 6:		//Rock Wall 
//			AutoModes.oneBall(new Obstacle("rock_wall", Difficulty.simple, position));
//			break;
		case 7:		//Rough Terrain
			AutoModes.oneBall(Obstacle.rough_terrain);
			break;
		case 8: //Corner Shot
			syncIntakeLifterDownSlight();
			Robot.shooter.start();
			Robot.shooter.raise();
			Timer.delay(1);
			Robot.intake.intakeRoller.set(1);
			break;
		default:	//Low Bar
		AutoModes.oneBall(Obstacle.low_bar);
		}
		//two ball eventually
	}

	///////////////////MODES//////////////////
	public static void test() {
//		Obstacle.low_bar.moveToBarrier(1);
//		driveWithinShotRange();
//		Robot.shooter.start();
//		Robot.shooter.raise();
//		fire();
		
		
//		alignToTargetIncremental();
//		rotateToGyroPosition(270);
//		Timer.delay(.5);
//		rotateToGyroPosition(90);
//		Timer.delay(.5);
		
//		AutoModes.syncIntakeLifterDown();
//		
		
//		rotateToGyroPosition(45);
//		Timer.delay(.5);
//		rotateToGyroPosition(270);
//		Timer.delay(.5);
//		rotateToGyroPosition(90);
		//moveForwardForDistance(ROBOT_SPEED, 36, 3000);
//		Obstacle.low_bar.crossBarrier(1);
//		AutoModes.moveForwardForTime(.75*AutoModes.ROBOT_SPEED, 800);
		oneBall(Obstacle.cheval_de_frise);
		
//		SmartDashboard.putNumber("speed", 0);
//		SmartDashboard.putNumber("time (s)", 5);
//		moveForwardForTime(SmartDashboard.getNumber("speed"), SmartDashboard.getNumber("time (s)")*1000);
	}
	
	public static void oneBall(Obstacle obstacleToCross) {
		//Prepare for barrier and target
		showStatus("Starting");
		obstacleToCross.preCrossBarrier(1);
		
		//Move to barrier
		showStatus("To obstacle");
		obstacleToCross.moveToBarrier(1);
	
		//Cross barrier
		showStatus("On obstacle");
		obstacleToCross.crossBarrier(1);
		
		//Prepare to fire
		Robot.shooter.start();
		Robot.shooter.raise();
		
		//Drive to target
		showStatus("To target");
//		obstacleToCross.moveFromObstacleToTarget(1);
		
		//Align and fire
//		showStatus("Align and FIRE");
//		alignAndFire();
	}

	public static void twoBall(Obstacle obstacleToCross) {
		//Fire first ball
		oneBall(obstacleToCross);
		
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
		oneBall(obstacleToCross);
	}
	
	///////////////////FUNCTIONS//////////////////
	//------fire------
	public static void alignAndFire() {
		//Align
		alignToTargetIncremental();
		fire();
	}
	
	public static void fire() {

		//Fire
		Robot.intake.intakeRoller.set(1);
	}
	
	//------rotator sync------
	private static boolean aimRotatorThreadRunning = false;
	public static void syncAimRotator() {
		if(!aimRotatorThreadRunning) {
			exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
					while(inAutonomous()) {
//						Robot.turret.aimRotatorToTarget();
					}
				}});
		}
		
		aimRotatorThreadRunning = true;
	}
	
	//------intake lifter sync------
	public static void syncIntakeLifterUp() {
		private_syncIntakeLifter(AUTO_LIFTER_UP_MOTOR_SPEED, 800);
	}
	
	public static void syncIntakeLifterDown() {
		private_syncIntakeLifter(-AUTO_LIFTER_DOWN_MOTOR_SPEED, 800);
	}
	
	public static void syncIntakeLifterDownHalf() {
		private_syncIntakeLifter(-AUTO_LIFTER_DOWN_MOTOR_SPEED, 400);
	}

	public static void syncIntakeLifterDownSlight() {
		private_syncIntakeLifter(-AUTO_LIFTER_DOWN_MOTOR_SPEED, 25);
	}

	private static int intakeLifterLastThreadIndex = 0;
	private static void private_syncIntakeLifter(final double liftSpeed, final long timeoutMillis) {
		exeSrvc.execute(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				int intakeLifterLocalThreadIndex = ++intakeLifterLastThreadIndex;
				
				//Start lifter
				while(System.currentTimeMillis()-startTime < timeoutMillis && 
						intakeLifterLocalThreadIndex  == intakeLifterLastThreadIndex && inAutonomous()) {
					Robot.intakeLifter.moveLifter(liftSpeed);
				}
				
				//Stop lifter
				Robot.intakeLifter.moveLifter(0);
			}});
	}
	
	//------intake lifter------
	public static void intakeLifterUp() {
		private_intakeLifter(AUTO_LIFTER_UP_MOTOR_SPEED, 800);
	}
	
	public static void intakeLifterDown() {
		private_intakeLifter(-AUTO_LIFTER_DOWN_MOTOR_SPEED, 800);
	}
	
	private static void private_intakeLifter(double liftSpeed, long timeoutMillis) {
		final long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.intakeLifter.lifterLeft.set(liftSpeed);
		}
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
	

	public static double correctMotorValue(double motorValue, double minimumMagnitude, double maximumMagnitude) {
//		motorValue = Math.pow(motorValue, 1.5);//square input


		double motorMagnitude = Math.abs(motorValue)*(maximumMagnitude-minimumMagnitude) + minimumMagnitude;
		SmartDashboard.putNumber("motorMagnitude", motorMagnitude);

		if(motorValue < 0) {
			return -motorMagnitude;
		}else{
			return motorMagnitude;
		}
	}
	
	///////////////////MOVEMENT-rotate//////////////////
	public static double currentTargetAngle = 90;//need current angle in gyro, move
	public static void rotateTimeBased(double driveSpeed, double turnSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, turnSpeed);
		}
		
		stop();
	}
	
	public static void rotateToGyroPosition(double angle) {
		currentTargetAngle = angle;
		double turnSpeed = 0;
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(Math.abs(Robot.gyro.getAngleDisplacementFrom(angle)) > 1 && inAutonomous()) {
//			turnSpeed = Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(angle);
			turnSpeed = correctMotorValue(Robot.gyro.getAngleDisplacementFrom(angle)/180, .4, .6);
			Robot.drive.arcadeDrive(0, turnSpeed);
		}
		
		stop();
	}
	

	///////////////////MOVEMENT-align//////////////////
	public static double getTargetOffset() {
		double targetOffset = 1;
		double targetX = Robot.visionTable.getNumber("TargetX", 0);
		double imageWidth = Robot.visionTable.getNumber("ImageWidth", 0);
//		double targetDistance = Robot.visionTable.getNumber("TargetDistance", 0);
		
		targetOffset = (targetX*2/imageWidth-1);//33,40
		SmartDashboard.putNumber("target offset", targetOffset);
		return targetOffset;
	}
	
	public static void alignToTargetIncremental() {
		alignToTarget(.8, .15, .15);
		alignToTarget(.6, 0, 0);
		alignToTarget(.4, .15, .15);
		alignToTarget(.2, .15, .2);
		alignToTarget(.03, .1, .2);
	}
	
	public static void alignToTarget(double accuracy, double driveIncrementDelay, double pauseIncrementDelay) {
		double targetOffset = getTargetOffset();
		
		while(Math.abs(targetOffset) > accuracy && inAutonomous()) {
			targetOffset = getTargetOffset();
			Robot.drive.arcadeDrive(0, correctMotorValue(targetOffset, .55, .56));
			if(pauseIncrementDelay != 0) {
				Timer.delay(driveIncrementDelay);
				Robot.drive.arcadeDrive(0, 0);
				Timer.delay(pauseIncrementDelay);
			}
		}
	}
	
	///////////////////MOVEMENT-drive//////////////////
	/**
	 * @deprecated
	 */
	public static void moveForwardForDistance(double driveSpeed, double distance, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		Robot.gyro.resetDisplacement();
		
		while(Math.abs(Robot.gyro.getGroundDisplacement()) < distance &&
				System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}
		
		stop();
	}
	
	public static void moveForwardForTime(double driveSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}
		
		stop();
	}
	
	//------elevation------
	public static double lastGroundElevation;
	public static void moveForwardToRamp(double direction, double driveSpeed, long timeoutMillis) {
		lastGroundElevation = Robot.gyro.getElevation();
		long startTime = System.currentTimeMillis();
		
		while(direction*(Robot.gyro.getElevation() - lastGroundElevation) >= RAMP_ANGLE-1 && 
				System.currentTimeMillis()-startTime < timeoutMillis  && inAutonomous()) {
			SmartDashboard.putNumber("Elevation", Robot.gyro.getElevation());
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(direction*driveSpeed, 0);
		}

		stop();
	}
	
	public static void moveForwardOffRamp(double driveSpeed, long timeoutMillis) {
		double startElevation = Robot.gyro.getElevation();
		long startTime = System.currentTimeMillis();
		
		//Wait for robot reach down ramp
		while(Math.abs(Robot.gyro.getElevation() - startElevation) <= 2*RAMP_ANGLE && System.currentTimeMillis()-startTime < timeoutMillis  && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}
		
		//Wait for robot to become level
		while(Robot.gyro.getElevation() - lastGroundElevation >= 1/*1 is max error*/ && System.currentTimeMillis()-startTime < timeoutMillis  && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}

		stop();
	}
	
	public static final double SHOT_RANGE_INCHES = 108.5;//112 was here for some reason
	public static void driveWithinShotRange() {
		double targetDistance = Robot.visionTable.getNumber("TargetDistance", 0);
		double speed = .7;
		while (targetDistance > SHOT_RANGE_INCHES) {
			speed = .7*targetDistance/(219 - SHOT_RANGE_INCHES);
			Robot.drive.arcadeDrive(correctMotorValue(speed, .4, .7), correctMotorValue(getTargetOffset(), 0, .25));
		}
//		Robot.shooter.start();
//		Timer.delay(1);
//		Robot.shooter.fire();
	}

	public static void moveToLimitSwitch(double driveSpeed, DigitalInput limitSwitch, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		while(limitSwitch.get() && System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
		}
		
		stop();
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
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
		}
		
		//Decelleration
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTime < timeoutMillis/2 && inAutonomous()) {
			speed = 1-2*(System.currentTimeMillis()-startTime)/timeoutMillis;
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
		}
		
		stop();
	}
}
	
