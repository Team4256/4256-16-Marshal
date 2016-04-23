package org.usfirst.frc.team4256.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.usfirst.frc.team4256.robot.Robot.Gamemode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoModes {
	private static final double VELOCITY_7 = ((double) 170)/6000;//inches per millisecond at .7 speed on practice bot
	private static final double VELOCITY_9 = ((double) 196)/4000;//inches per millisecond at .7 speed on practice bot
	private static final double ANGULAR_VELOCITY_7 = .2;//degrees per millisecond at .7 speed on practice bot
	
	private static final double ROBOT_SPEED = -1;
	private static final Range TURN_SPEED_RANGE = new Range(.7, .9);
	private static final Range TURN_SLOW_SPEED_RANGE = new Range(.6, .95);
	public static final double LIFTER_UP_SPEED = .5;
	public static final double LIFTER_DOWN_SPEED = 1;
	
	public static final double DISTANCE_BETWEEN_OBSTACLES = 1346.2/25.4;
	public static final double DISTANCE_OBSTACLE_TO_WALL = 4873.645/25.4;
	public static final double LATERAL_DISTANCE_FIRST_OBSTACLE_TO_CENTER_TARGET = 3663.6008/25.4;
	public static final double ROBOT_DISTANCE_OFFSET = 50;
	
	public static final double RAMP_ANGLE = 6;//Actual angle is 12, but 6 should be enough

	
	public static ExecutorService exeSrvc = Executors.newCachedThreadPool();
	public static int startPosition;
	public static int goal;
	
	public static boolean inAutonomous() {
		return Robot.gamemode == Robot.Gamemode.AUTONOMOUS;
	}
	
	public static void showStatus(String status) {
		SmartDashboard.putString("Autonomous Stat", status);
	}
	
	///////////////////START//////////////////
	public static void start() {
		Robot.gamemode = Gamemode.AUTONOMOUS;
		Robot.gyro.reset();
		Robot.drive.slowGear();
		
		//Get SmartDashboard variables
		int numBalls = (int) SmartDashboard.getNumber("NumberOfBalls");
		int autoMode =  (int) SmartDashboard.getNumber("AUTONOMOUS MODE");
		int position = (int) SmartDashboard.getNumber("Position");
		goal = (int) SmartDashboard.getNumber("Goal");
		startPosition = (int) SmartDashboard.getNumber("Position");
		double speed = -1;

		//Start selected autonomous mode
		switch (autoMode) {
//		case 20: //Corner Shot and back through low bar
////			syncIntakeLifterDownSlight();
//			Robot.shooter.start();
//			Robot.shooter.raise();
//			Timer.delay(1);
//			Robot.intake.intakeRoller.set(Intake.ROLLER_IN_SPEED);
//			
//			syncIntakeLifterDown();
//			Robot.shooter.lower();
//			moveForwardForTime(.8, 250);
//			rotateToGyroPosition(90);
//			moveForwardForTime(1, 1000);
//			moveForwardForTime(.75, 1200);
//			break;
//		case 21:	//Low Bar far shot
//			startPosition = 1;
//			speed = -.75;
//			syncIntakeLifterDown();
//			AutoModes.moveForwardForTime(-speed, 800);
//			
//			//Cross (backwards)
//			AutoModes.moveForwardForTime(-speed, 1000);
//			
//			//Line up to target and fire
//			Robot.shooter.shooterLeft.set(1);
//			Robot.shooter.raise();
//			AutoModes.moveForwardForTime(-speed, 1200);
//			//{added
//			Timer.delay(.1);
//			rotateTimeBased(0, 1, 800); //400 msecs rotated 20 degrees
//			Timer.delay(.1);
//			fire();
//			Timer.delay(.1);
//			rotateTimeBased(0, -1, 800);
//			moveForwardForTime(speed, 2200);
//			break;
//		case 9:	//Low Bar
//			Robot.drive.slowGear();
//			speed = -.9;
//			moveForwardForTime(speed, DISTANCE_TO_TIME(44, speed));
//			break;
		case 100:	//Low Bar with gyro
			speed = .9;
			lowBar(speed);
			break;
		case 101:	//Portcullis with gyro
			speed = -.7;
			Robot.drive.fastGear();
			
			//Go to portcullis
			private_syncIntakeLifter(-1, 1000);
			Timer.delay(.3);
			moveForwardForTime(speed, 100);
			lastGroundElevation = Robot.gyro.getElevation();
			moveForwardForTime(speed, 600);
			Robot.intakeLifter.moveLifter(-.6);
			
			//Go under portcullis and lift
			speed = -.7;
			moveForwardForTime(speed, 1000);
			Robot.intakeLifter.moveLifter(1);
			
			//
			moveForwardForTime(-.2, 300);
			moveForwardForTime(speed, 3000-300);
			//
			//Go forward while lifting, cross barrier
			Robot.intakeLifter.moveLifter(0);
			
			//Drive to tower and fire
			speed = -.9;
			syncIntakeLifterDown();
			Robot.drive.slowGear();
			moveFromObstacleToTargetAndFire(speed);
			break;
		case 102:	//Cheval De Frise with gyro
			speed = -.9;
			
			//Mount cheval de frise
			//syncIntakeLifterDownSlight();
			Robot.drive.slowGear();
			moveForwardForTime(speed, DISTANCE_TO_TIME(10, speed));
			lastGroundElevation = Robot.gyro.getElevation();
			//moveForwardForTime(speed, DISTANCE_TO_TIME(41, speed));//changed during competition from 35
			moveForwardForTime(speed, DISTANCE_TO_TIME(52, speed));//guaranteed to have hit it
			Timer.delay(.2);
			moveForwardForTime(.5, 400);//back up a tiny bit to get ready for lifter
			intakeLifterDown();
			Timer.delay(.5);
			
			//Cross barrier
			speed = -1;
			private_intakeLifter(-LIFTER_DOWN_SPEED, 200);
			moveForwardForTime(speed, DISTANCE_TO_TIME(78, speed));
			
			//Drive to tower and fire
			speed = -.9;
			moveFromObstacleToTargetAndFire(speed);
			break;
		case 103:	//Rough terrain with gyro
			speed = .9;
			Robot.drive.fastGear();
			
			//Mount cheval de frise
//			syncIntakeLifterDownHalf();
			Robot.shooter.lower();
//			moveForwardToRamp(speed, new Range(100, 5000));
//			moveForwardForTime(speed, DISTANCE_TO_TIME(5, speed));
//			moveForwardOffRamp(speed, 2500);
			moveForwardForTime(speed, DISTANCE_TO_TIME(92.5, speed));//156 in slow gear//was 105 in high gear
			//Drive to tower and fire
			Timer.delay(.15);
			if(startPosition != 4) {
				syncLifterDownHalf();
			}
			speed = .9;
			Robot.drive.slowGear();
			moveFromObstacleToTargetAndFire(speed);
			break;
		case 104:	//RockWall with gyro
			speed = -1;
			
			//Mount cheval de frise
			Robot.drive.fastGear();//should be fast gear but backwards on practice robot
			syncIntakeLifterDownRockWall();
			Timer.delay(.5);
			moveForwardForTime(speed, DISTANCE_TO_TIME(100, speed));
			Timer.delay(.5);
			//Drive to tower and fire
			Robot.drive.slowGear();
			speed = -.9;
			moveFromObstacleToTargetAndFire(speed);
			break;
		case 105:	//Moat with gyro
			speed = .8;
			
			//Mount cheval de frise
//			syncIntakeLifterDownHalf();
			Robot.shooter.lower();
//			moveForwardToRamp(speed, new Range(100, 5000));
//			moveForwardForTime(speed, DISTANCE_TO_TIME(5, speed));
//			moveForwardOffRamp(speed, 2500);
			moveForwardForTime(speed, DISTANCE_TO_TIME(156, speed));
			//Drive to tower and fire
			syncIntakeLifterDown();
			speed = .9;
			moveFromObstacleToTargetAndFire(speed);
			break;
		case 111:	//Portcullis with gyro - no shot
			speed = -.7;
			Robot.drive.fastGear();
			
			//Go to portcullis
			private_syncIntakeLifter(-1, 1000);
			Timer.delay(.3);
			moveForwardForTime(speed, 100);
			lastGroundElevation = Robot.gyro.getElevation();
			moveForwardForTime(speed, 600);
			Robot.intakeLifter.moveLifter(-.6);
			
			//Go under portcullis and lift
			speed = -.7;
			moveForwardForTime(speed, 1000);
			Robot.intakeLifter.moveLifter(1);//maybe increase speed
			
			//Go forward while lifting, cross barrier
//			speed = -.55;
//			moveForwardOffRamp(speed, 8000);
			moveForwardForTime(speed, 3000);
			Robot.intakeLifter.moveLifter(0);
			
			//just drive a little more
			moveForwardForTime(speed, 300);
//			//Drive to tower and fire
//			speed = -.7;
//			syncIntakeLifterDown();
			break;
		case 112:	//Cheval De Frise with gyro - no shot
			speed = -.9;
			
			//Mount cheval de frise
//			syncIntakeLifterDownSlight();
			Robot.drive.slowGear();
			moveForwardForTime(speed, DISTANCE_TO_TIME(10, speed));
			lastGroundElevation = Robot.gyro.getElevation();
			//moveForwardForTime(speed, DISTANCE_TO_TIME(41, speed));//changed during competition from 35
			moveForwardForTime(speed, DISTANCE_TO_TIME(52, speed));//guaranteed to have hit it
			Timer.delay(.2);
			moveForwardForTime(.5, 450);//back up a tiny bit to get ready for lifter
			intakeLifterDown();
			Timer.delay(.5);
			
			//Cross barrier
			speed = -1;
			moveForwardForTime(speed, DISTANCE_TO_TIME(62, speed));
			Timer.delay(.1);
			moveForwardForTime(speed, DISTANCE_TO_TIME(16, speed));
			break;
		case 200:	//Low Bar with gyro, 2 ball with spy bot
			speed = .9;
			lowBar(speed);
			
			//Travel to incompetent robot in the corner to eat ball
			syncIntakeLifterDown();
			rotateToGyroPosition(120);
			syncIntakeBall(1200);
			moveForwardForTime(-speed, DISTANCE_TO_TIME(24, speed));
			rotateToGyroPosition(100);
			moveForwardForTime(-speed, DISTANCE_TO_TIME(22, speed));
			rotateToGyroPosition(85);
			moveForwardForTime(speed, DISTANCE_TO_TIME(30, speed));
			fire();
			break;
		default: //Corner Shot
			syncLifterDownSlight();
			Robot.shooter.start();
			Robot.shooter.raise();
			Timer.delay(2);
			Robot.intake.intakeRoller.set(Intake.ROLLER_IN_SPEED);
			break;

		}
	}
	
	public static void lowBar(double speed) {
		//Cross barrier
		syncIntakeLifterDown();
		Robot.shooter.lower();
		moveForwardToRamp(speed, new Range(100, 5000));
		moveForwardOffRamp(speed, 2500);
		
		//Go to tower
		syncIntakeLifterUpFull();
		moveForwardForTime(speed, DISTANCE_TO_TIME(90, speed));
		
//		moveForwardForTime(speed, DISTANCE_TO_TIME(60, speed));
//		moveForwardForTime(speed, .15, DISTANCE_TO_TIME(30, speed));
//		rotateToGyroPosition(45);

		while(Math.abs(Robot.gyro.getCurrentPath(45)) > 2 && inAutonomous()) {
			double turnSpeed = correctMotorValue(Robot.gyro.getCurrentPath(45)/180, TURN_SPEED_RANGE.min, TURN_SPEED_RANGE.max);
			Robot.drive.arcadeDrive(.45, turnSpeed);
		}stop();
		
		//Drive to target, align, fire
		Robot.shooter.shooterLeft.set(1);
		moveToTarget(speed, new Range(100, 4000), 30);
		syncIntakeLifterDown();
		alignAndFire();
	}
	
	public static void moveFromObstacleToTargetAndFire(double speed) {
		Robot.drive.slowGear();
		//Final angle to turn to based on robot orientation
		double finalAngleForLeftTarget = (speed<0? 180+60: 60);
		double finalAngleForCenterTarget = (speed<0? 180-5:0);
		double finalAngleForRightTarget = (speed<0? 180-60: -60);
		if (goal == 1) {//change for this one
			if(startPosition == 2) {//needs testing, should go to left goal
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -5);
				moveForwardForTime(speed, DISTANCE_TO_TIME(110, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForLeftTarget);
			}else if(startPosition == 3) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -20);
				moveForwardForTime(speed, DISTANCE_TO_TIME(112, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForLeftTarget);
			}else if(startPosition == 4) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -60);
				syncLifterDownHalf();
				moveForwardForTime(speed, DISTANCE_TO_TIME(90, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForLeftTarget);	
			}else if(startPosition == 5) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -80);
				moveForwardForTime(speed, DISTANCE_TO_TIME(100, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForLeftTarget);
			}
		}else if (goal == 2) {//good (from comp)
			if(startPosition == 2) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 60);//rotate to 60 degrees by turning clockwise
				moveForwardForTime(speed, DISTANCE_TO_TIME(90, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForCenterTarget);
			}else if(startPosition == 3) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 30);//rotate to 30 degrees by turning clockwise
				moveForwardForTime(speed, DISTANCE_TO_TIME(60, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForCenterTarget);
			}else if(startPosition == 4) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -20);//rotate to 340 degrees by turning counter-clockwise
				syncLifterDownHalf();
				moveForwardForTime(speed, DISTANCE_TO_TIME(60, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForCenterTarget);	
			}else if(startPosition == 5) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, -50);//rotate to 310 degrees by turning counter-clockwise
				moveForwardForTime(speed, DISTANCE_TO_TIME(78, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForCenterTarget);
			}
		}else if (goal == 3) {//test still, should go to right goal
			if(startPosition == 2) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 70);
				moveForwardForTime(speed, DISTANCE_TO_TIME(90, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForRightTarget);
			}else if(startPosition == 3) {
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 50);
				moveForwardForTime(speed, DISTANCE_TO_TIME(140, speed));
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForRightTarget);
			}else if(startPosition == 4) {
				if(speed > 0) {//means going backwards
					rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 25);
					syncLifterDownHalf();
					moveForwardForTime(speed, DISTANCE_TO_TIME(150, speed));
				}else {
					rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 20);
					moveForwardForTime(speed, DISTANCE_TO_TIME(100, speed));
				}
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForRightTarget);
			}else if(startPosition == 5) {
				if(speed > 0) {//means going backwards
					rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, .2);
					moveForwardForTime(speed, DISTANCE_TO_TIME(120, speed));
				}else {
					rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, 5);
					moveForwardForTime(speed, DISTANCE_TO_TIME(90, speed));
				}
				rotateToGyroPosition(TURN_SLOW_SPEED_RANGE, finalAngleForRightTarget);
			}
		}
		Robot.shooter.shooterLeft.set(1);
		//Timer.delay(.5);
		//Drive to target, align, fire
		speed = .7;
		moveToTarget(speed, new Range(0, 2000));
//		alignToTargetIncremental();
//		Timer.delay(.2);
		alignAndFire();
	}
	

	
	
	///////////////////MODES//////////////////
	public static void test() {//TODO test marker
		Robot.gamemode = Gamemode.AUTONOMOUS;
		Robot.gyro.reset();
		Robot.drive.slowGear();
		
//		rotateTimeBased(0, .9, 6000);
		moveForwardForTime(.9, 4000);
//		moveForwardToRamp(.7, new Range(100, 50000));
//		Timer.delay(1);
//		moveForwardOffRamp(.7, 50000);
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
//		obstacleToCross.moveFromObstacleToTarget(position);
		
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
		Robot.intake.intakeRoller.set(Intake.ROLLER_IN_SPEED);
		
		//Stop intake
		Timer.delay(.6);
		Robot.intake.intakeRoller.set(0);
		Robot.shooter.shooterLeft.set(0);
//		exeSrvc.execute(new Runnable() {
//			@Override
//			public void run() {
//				Timer.delay(.75);
//				Robot.intake.intakeRoller.set(0);
//			}});
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

	//------intake sync------
	private static void syncIntakeBall(final long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		exeSrvc.execute(new Runnable() {
			@Override
			public void run() {
				while(!Robot.intake.stagingLimitSwitch.get() && 
						System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
					Robot.intake.intakeRoller.set(Intake.ROLLER_IN_SPEED);
				}
				
				Robot.intake.intakeRoller.set(0);
			}});
	}
	
	//------intake lifter sync------
	public static void syncIntakeLifterUp() {
		private_syncIntakeLifter(LIFTER_UP_SPEED, 600);
	}
	
	public static void syncIntakeLifterUpFull() {
		private_syncIntakeLifter(LIFTER_UP_SPEED, 1000);
	}
	
	public static void syncIntakeLifterDown() {
		private_syncIntakeLifter(-LIFTER_DOWN_SPEED, 800);
	}
	
	public static void syncIntakeLifterDownRockWall() {
		private_syncIntakeLifter(-LIFTER_DOWN_SPEED, 800);
	}
	
	public static void syncLifterDownHalf() {
		private_syncIntakeLifter(-LIFTER_DOWN_SPEED, 400);
	}

	public static void syncLifterDownSlight() {
		private_syncIntakeLifter(-LIFTER_DOWN_SPEED, 150);
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
		private_intakeLifter(LIFTER_UP_SPEED, 800);
	}
	
	public static void intakeLifterDown() {
		private_intakeLifter(-LIFTER_DOWN_SPEED, 700);
	}
	
	private static void private_intakeLifter(double liftSpeed, long timeoutMillis) {
		final long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.intakeLifter.lifterLeft.set(liftSpeed);
		}
		
		Robot.intakeLifter.lifterLeft.set(0);
	}
	
	
	
	
	
	///////////////////MOVEMENT-conversions//////////////////
	public static long DISTANCE_TO_TIME(double distanceInInches, double speed) {
		speed = Math.abs(speed);
		//TODO plug in formula or test more values
		if(speed == .7) {
			return (long) (distanceInInches/VELOCITY_7);
		}else if(speed == .9) {
			return (long) (distanceInInches/VELOCITY_9);
		}
		return (long) ((.7/speed)*distanceInInches/VELOCITY_7);
	}
	
	public static long DEGREES_TO_TIME(double degrees, double speed) {
		speed = Math.abs(speed);
		//TODO plug in formula or test more values
		if(speed == .7) {
			return (long) (degrees/ANGULAR_VELOCITY_7);
		}
		return (long) (degrees/ANGULAR_VELOCITY_7);
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
	
	public static double capMaximumMotorValue(double motorValue, double maximumMagnitude) {
		if(motorValue < -maximumMagnitude) {
			return -maximumMagnitude;
		}else if(motorValue > maximumMagnitude) {
			return maximumMagnitude;
		}else{
			return motorValue;
		}
	}
	
	///////////////////MOVEMENT-rotate//////////////////
	public static double currentTargetAngle = 0;//need current angle in gyro, move
	public static void rotateDegreesTimeBased(double driveSpeed, double turnSpeed, double degrees) {
		rotateTimeBased(driveSpeed, turnSpeed, DEGREES_TO_TIME(degrees, turnSpeed));
	}
	
	public static void rotateTimeBased(double driveSpeed, double turnSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, turnSpeed);
		}
		
		stop();
	}

	public static void rotateToGyroPosition(double angle) {
		rotateToGyroPosition(TURN_SPEED_RANGE, angle);
	}
	
	public static void rotateToGyroPosition(Range turnSpeedRange, double angle) {
		currentTargetAngle = angle;
		double turnSpeed = 0;
		
//		while(Robot.gyro.getAngle() < angle && inAutonomous()) {
		while(Math.abs(Robot.gyro.getCurrentPath((float)angle)) > 2 && inAutonomous()) {
//			turnSpeed = Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(angle);
			turnSpeed = correctMotorValue(Robot.gyro.getCurrentPath((float)angle)/180, turnSpeedRange.min, turnSpeedRange.max);
			SmartDashboard.putNumber("turnSpeed for Autonomous", turnSpeed);
			SmartDashboard.putNumber("goalAngle for Autonomous", angle);
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
//		alignToTarget(.8, .15, .15);
//		alignToTarget(.6, 0, 0);
//		alignToTarget(.4, .15, .15);
//		alignToTarget(.2, .15, .2);
//		alignToTarget(.03, .1, .2);
		
		do {
//			alignToTarget(.6, 0, 0);
//			alignToTarget(.58, .1, .15);
//			alignToTarget(.3, .1, .1);
//			alignToTarget(.03, .08, .2);
			
			alignToTarget(.3, .12, .02);
			alignToTarget(.2, .08, .02);
			alignToTarget(.1, .08, .1);
			alignToTarget(.03, .08, .2);
		}while(!Robot.gyro.isRotating());
	}
	
	public static void alignToTarget(double accuracy, double driveIncrementDelay, double pauseIncrementDelay) {
		double targetOffset = getTargetOffset();
		
		while(Math.abs(targetOffset) > accuracy && inAutonomous()) {
			targetOffset = getTargetOffset();
//			Robot.drive.arcadeDrive(0, correctMotorValue(targetOffset, .55, .56));
			Robot.drive.arcadeDrive(0, correctMotorValue(targetOffset, .45, .56));
			if(pauseIncrementDelay != 0) {
				Timer.delay(driveIncrementDelay);
				Robot.drive.arcadeDrive(0, 0);
				Timer.delay(pauseIncrementDelay);
			}
		}
	}
	
	///////////////////MOVEMENT-drive/////////////////
	public static void moveForwardForTime(double driveSpeed, long timeoutMillis) {
		moveForwardForTime(driveSpeed, 0, timeoutMillis);
	}
	
	public static void moveForwardForTime(double driveSpeed, double turnSpeed, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
//		Robot.drive.lockAngle(true);
		double angle = (float)Robot.gyro.getCurrentAngle();
		
		while(System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, turnSpeed);
		}
		
//		Robot.drive.lockAngle(false);
		stop();
	}
	
	//------elevation------
	public static double lastGroundElevation;
	public static void moveForwardToRamp(double driveSpeed, Range duration) {
		moveForwardToRamp(driveSpeed, duration, true);
	}
	
	public static void moveForwardToRamp(double driveSpeed, Range duration, boolean resetGroundElevation) {
		double direction = (driveSpeed<0? -1:1);
		moveForwardForTime(driveSpeed, (long) duration.min);
		
		if(resetGroundElevation) {
			lastGroundElevation = Robot.gyro.getElevation();
		}
		
		long startTime = System.currentTimeMillis();
		
//		while(direction*(Robot.gyro.getElevation() - lastGroundElevation) >= RAMP_ANGLE-1 && 
		while(Robot.gyro.getElevation()-lastGroundElevation <= direction*(RAMP_ANGLE) && 
				System.currentTimeMillis()-startTime < duration.getRange() && inAutonomous()) {
			SmartDashboard.putNumber("Elevation", Robot.gyro.getElevation());
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}

		stop();
	}
	
	public static void moveForwardOffRamp(double driveSpeed, long timeoutMillis) {
		double direction = (driveSpeed<0? -1:1);
		
		double startElevation = Robot.gyro.getElevation();
		long startTime = System.currentTimeMillis();
		
		//Wait for robot reach down ramp
		while(Robot.gyro.getElevation()-lastGroundElevation >= -direction*(RAMP_ANGLE) && 
				System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}
		
		//Wait for robot to become level
		while(Math.abs(Robot.gyro.getElevation() - lastGroundElevation) >= 1/*1 is max error*/ && 
				System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentTargetAngle));
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}

		stop();
	}

	public static void moveToTarget(double driveSpeed, Range duration) {
		moveToTarget(driveSpeed, duration, 15);
	}
	
	public static void moveToTarget(double driveSpeed, Range duration, double distToSlowFrom) {
		long startTime = System.currentTimeMillis();
		
		//Drive until robot sees target
		while((!Robot.visionTable.getBoolean("TargetVisibility", false) || 
				System.currentTimeMillis()-startTime < duration.min) && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, 0);
		}
		
		//Drive until target is at the farthest shooting range
		while(Robot.visionTable.getNumber("TargetY", -1) >= Robot.shooter.shootingYRangeShort.fromPercent(75)/*.max*/ && inAutonomous()) {
			//Slow when close
			if(Robot.visionTable.getNumber("TargetY", -1) <= Robot.shooter.shootingYRangeShort.max+distToSlowFrom) {
				driveSpeed = .5;
			}
			Robot.drive.arcadeDrive(driveSpeed, 0);//correctMotorValue(getTargetOffset(), 0, .35));//changed in competition from .55 to .35
		}
		stop();
	}

	public static void moveToLimitSwitch(double driveSpeed, DigitalInput limitSwitch, long timeoutMillis) {
		long startTime = System.currentTimeMillis();
		
		while(limitSwitch.get() && System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.motorizeCurrentPath(currentTargetAngle));
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
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.motorizeCurrentPath(currentTargetAngle));
		}
		
		//Decelleration
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTime < timeoutMillis/2 && inAutonomous()) {
			speed = 1-2*(System.currentTimeMillis()-startTime)/timeoutMillis;
			Robot.drive.arcadeDrive(speedRange*speed + driveMinSpeed, Robot.gyro.motorizeCurrentPath(currentTargetAngle));
		}
		
		stop();
	}
}
	
