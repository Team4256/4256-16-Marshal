package org.usfirst.frc.team4256.robot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;





public class AutoModes {
	public static final double ROBOT_SPEED = -.75;
	public static final double AUTO_LIFTER_UP_MOTOR_SPEED = .5;
	public static final double AUTO_LIFTER_DOWN_MOTOR_SPEED = 1;
	
	public static final double DISTANCE_CENTER_TO_BARRIER = 000;//TODO
	public static final double DISTANCE_ACROSS_BARRIER = 000;//TODO
	public static final double DISTANCE_DEFENCE_WIDTH = 50;//TODO add divider width, long enough
	
	public static final long TIMEOUT_DISTANCE_CENTER_TO_BARRIER = 5000;//TODO
	public static final long TIMEOUT_DISTANCE_ACROSS_BARRIER = 5000;//TODO
	public static final long TIMEOUT_DISTANCE_DEFENCE_WIDTH = 5000;//TODO
	
	public static long DISTANCE_BETWEEN_DEFENCES;//<---remove!!
	

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
	public static void test() {
//		alignToTargetIncremental();
		rotateToGyroPosition(270);
		Timer.delay(.5);
		rotateToGyroPosition(90);
		Timer.delay(.5);
		
//		AutoModes.syncIntakeLifterDown();
//		
		
//		rotateToGyroPosition(45);
//		Timer.delay(.5);
//		rotateToGyroPosition(270);
//		Timer.delay(.5);
//		rotateToGyroPosition(90);
		//moveForwardForDistance(ROBOT_SPEED, 36, 3000);
//		Obstacle.low_bar.crossBarrier(1);
		
//		oneBall(Obstacle.low_bar);
		
//		SmartDashboard.putNumber("speed", 0);
//		SmartDashboard.putNumber("time (s)", 5);
//		moveForwardForTime(SmartDashboard.getNumber("speed"), SmartDashboard.getNumber("time (s)")*1000);
	}
	
	public static void oneBall(Obstacle obstacleToCross) {
		//Prepare for barrier and target
		obstacleToCross.preCrossBarrier(1);
		//Hayden doesn't understand -> syncAimRotator();
		moveForwardForTime(ROBOT_SPEED, 6000);
	
		//Cross barrier
		obstacleToCross.crossBarrier(1);
		
//		//Drives to specifc target position and shoots
		alignToTargetIncremental();
//		driveWithinShotRangeAndShoot();
		
		//Fire
		Timer.delay(.5);
//		Robot.turret.fire();
		Timer.delay(.5);
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
	//------rotator sync------
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
	
	private static int intakeLifterCommandCurrentIndex = 0;
	private static void private_syncIntakeLifter(final double liftSpeed, final long timeoutMillis) {
		exeSrvc.execute(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				int intakeLifterCommandIndex = ++intakeLifterCommandCurrentIndex;
				
				//Start lifter
				while(System.currentTimeMillis()-startTime < timeoutMillis && 
						intakeLifterCommandIndex  == intakeLifterCommandCurrentIndex && inAutonomous()) {
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
	
	
	///////////////////MOVEMENT-functions//////////////////
	public static double currentTargetAngle = 90;//need current angle in gyro, move
	public static void rotateTimeBased(double turnSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(0, turnSpeed);
		}
		
		stop();
	}
	
	public static void rotateToGyroPosition(double angle) {
		currentTargetAngle = angle;
		double turnSpeed = 0;
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(Math.abs(Robot.gyro.getAngleDisplacementFrom(angle)) > 1 && inAutonomous()) {
			turnSpeed = Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(angle);
			Robot.drive.arcadeDrive(0, turnSpeed);
		}
		
		stop();
	}

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
	
	public static void moveForwardForTime(double driveSpeed, double d) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime < d && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, -.08);
		}
		
		stop();
	}
	
	public static void alignToTargetIncremental() {
		alignToTarget(.8, .15, .15);
		alignToTarget(.6, 0, 0);
		alignToTarget(.4, .15, .15);
		alignToTarget(.2, .15, .2);
		alignToTarget(.03, .1, .2);
	}
	
	public static void alignToTarget(double accuracy, double driveIncrementDelay, double pauseIncrementDelay) {
		double targetOffset = 1;
		
		while(Math.abs(targetOffset) > accuracy && inAutonomous()) {
			double targetX = Robot.visionTable.getNumber("TargetX", 0);
			double imageWidth = Robot.visionTable.getNumber("ImageWidth", 0);
//			double targetDistance = Robot.visionTable.getNumber("TargetDistance", 0);
			
			targetOffset = (targetX*2/imageWidth-1);//33,40
			SmartDashboard.putNumber("target offset", targetOffset);
			Robot.drive.arcadeDrive(0, correctMotorTurnValue(targetOffset, .55, .56));
			if(pauseIncrementDelay != 0) {
				Timer.delay(driveIncrementDelay);
				Robot.drive.arcadeDrive(0, 0);
				Timer.delay(pauseIncrementDelay);
			}
		}
	}
	
	public static double correctMotorTurnValue(double motorValue, double minimumMagnitude, double maximumMagnitude) {
//		motorValue = Math.pow(motorValue, 1.5);//square input


		double motorMagnitude = Math.abs(motorValue)*(maximumMagnitude-minimumMagnitude) + minimumMagnitude;
		SmartDashboard.putNumber("motorMagnitude", motorMagnitude);

		if(motorValue < 0) {
			return -motorMagnitude;
		}else{
			return motorMagnitude;
		}
	}
	
//	public static ArrayList<Double> previousRates = new ArrayList<Double>();
//	public static int amountOfPreviousRates = 8;
//	public static double correctMotorTurnValueExperimental(double motorValue, double deadband, double minimumMagnitude, double maximumMagnitude) {
//		double angularVelocity = Robot.gyro.getRate();
//		
//		if(true){
//			return correctMotorTurnValue(motorValue, deadband, minimumMagnitude, maximumMagnitude)/angularVelocity;
//		}else{
//			previousRates.add(angularVelocity);
//			if(previousRates.size() > amountOfPreviousRates) {
//				previousRates.remove(0);
//			}
//			
//			return correctMotorTurnValue(motorValue, deadband, minimumMagnitude, maximumMagnitude)/previousRates.get(previousRates.size()-1);
//		}
//	}
	
//	public static double alignToTarget() {
//		double targetLongitude = Robot.visionTable.getNumber("TargetX", 0);
//		double imageCenter = Robot.visionTable.getNumber("ImageWidth", 0)/2;
//		double targetWidth = Robot.visionTable.getNumber("TargetWidth", 0);
//		double pixelSpeed = (targetLongitude - imageCenter)/imageCenter;
//		if (pixelSpeed < .4 && Math.abs(pixelSpeed*imageCenter) > targetWidth) {
//			pixelSpeed = .4;
//		}
//		if (Math.abs(pixelSpeed*imageCenter) > targetWidth) {
//			return pixelSpeed;
//		}else {
//			return 0;
//		}
//	}
	
//	public static final double SHOT_RANGE_INCHES = 100;
//	public static void driveWithinShotRangeAndShoot() {
//		double targetDistance = Robot.visionTable.getNumber("TargetDistance", 0);
//		double driveSpeed = 0;
//		if (alignToTarget() == 0) {
//			driveSpeed = .7;
//		}
//		while (targetDistance > SHOT_RANGE_INCHES) {
//			Robot.drive.arcadeDrive(driveSpeed, alignToTarget());
//		}
//		Robot.shooter.start();
////		Timer.delay(1);
////		Robot.shooter.fire();
//	}
	
//	public static void moveForwardToBall(double driveSpeed, long timeMillis)	{
//		long startTime = System.currentTimeMillis();
//		
//		while(System.currentTimeMillis()-startTime < timeMillis && inAutonomous())	{
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.rotateToAngle(currentAngle));
//			Robot.intake.intakeIn();
//		}
//		
//	}

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
	
