package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Obstacle {
	static SendableChooser autonomousObstacles = new SendableChooser();
	static SendableChooser obstaclePosition = new SendableChooser();
	static SendableChooser[] autonomusObstacleDropDowns = new SendableChooser[5];
//	static SendableChooser autonomusObstacle1 = new SendableChooser();
//	static SendableChooser autonomusObstacle2 = new SendableChooser();
//	static SendableChooser autonomusObstacle3 = new SendableChooser();
//	static SendableChooser autonomusObstacle4 = new SendableChooser();
//	static SendableChooser autonomusObstacle5 = new SendableChooser();
//	static Obstacle startingObstacle;
    static Obstacle[] obstacles = new Obstacle[5]; 
	
	final static Obstacle portcullis = new Obstacle("portcullis", Difficulty.hard, 1);
	final static Obstacle cheval_de_frise = new Obstacle("cheval_de_frise", Difficulty.hard, 1);
	final static Obstacle moat = new Obstacle("moat", Difficulty.simple, 2);
	final static Obstacle ramparts = new Obstacle("ramparts", Difficulty.hard, 2);
	final static Obstacle drawbridge = new Obstacle("drawbridge", Difficulty.impossible, 3);
	final static Obstacle sally_port = new Obstacle("sally_port", Difficulty.impossible, 3);
	final static Obstacle rock_wall = new Obstacle("rock_wall", Difficulty.simple, 4);
	final static Obstacle rough_terrain = new Obstacle("rough_terrain", Difficulty.simple, 4);
	//low bar should be only constant
	final static Obstacle low_bar = new Obstacle("low_bar", Difficulty.simple, 5);
	
	int position; 
	Difficulty difficulty;
	//why is there an int position that is hard coded?
	public Obstacle(String name, Difficulty difficulty, int position) {
		this.difficulty = difficulty;
		this.position = position;
		
		autonomousObstacles.addObject(name, this);
		
		for(int i=0; i<autonomusObstacleDropDowns.length; i++) {
			//Initialize sendable chooser
			if(autonomusObstacleDropDowns[i] == null) {
				autonomusObstacleDropDowns[i] = new SendableChooser();
			}
			
			//Add obstacle
			autonomusObstacleDropDowns[i].addObject(name, this);
		}
		
		if(autonomusObstacleDropDowns[position-1] == null) {
			autonomusObstacleDropDowns[position-1] = new SendableChooser();
		}
	}
	
	static enum Difficulty	{
		simple,
		hard,
		impossible
	}
	
	public static Obstacle getStartingObstacle() {
		return (Obstacle) autonomousObstacles.getSelected();
	}
	
	public Obstacle getBarrierAtPosition(int i) {
		return obstacles[i-1];
	}
	
	private double getObstacleDirection() {
		double direction;
		
		if(this == low_bar) {
			direction = -1;
		}else{
			direction = 1;
		}
		
		return direction;
	}
	
	private double getObstacleSpeed() {
		if(this == cheval_de_frise) {
			return -.75;
		}
		return -1;
	}
	
	
	public void preCrossBarrier(double direction) {
		if(this == cheval_de_frise) {
//			AutoModes.syncIntakeLifterDownHalf();
		}else{
			AutoModes.syncIntakeLifterDown();
			
			if(this != low_bar) {
				Timer.delay(1);
			}
		}
	}
	
	public void moveToBarrier(double direction) {
		direction *= getObstacleDirection();
		
//		AutoModes.moveForwardToRamp(direction, direction*AutoModes.ROBOT_SPEED, 3000);
		if (this == cheval_de_frise){
			AutoModes.moveForwardForTime(direction*getObstacleSpeed(), 800);
		}else{
			AutoModes.moveForwardForTime(direction*getObstacleSpeed(), 500);
		}
	}
	
	private void moveBarrierLength(double direction) {
		direction *= getObstacleDirection();
		
//		AutoModes.moveForwardOffRamp(direction*AutoModes.ROBOT_SPEED, 2000);
		AutoModes.moveForwardForTime(direction*getObstacleSpeed(), 1000);
//		AutoModes.moveForwardForDistance(direction*AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_ACROSS_BARRIER, AutoModes.TIMEOUT_DISTANCE_ACROSS_BARRIER);
	}
	
	public void crossBarrier(double direction) {
		if (difficulty == Difficulty.simple) {
			//Cross like normal
//			AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, 2000);
//			this.crossRampart();
			moveBarrierLength(direction);
		}else if (difficulty == Difficulty.hard) {
			if (this == portcullis) {
				AutoModes.moveToLimitSwitch(direction*getObstacleSpeed(), Robot.intakeLifter.frontLimitSwitch, 5000);//TODO change timeout to lower # if works
//				Robot.intakeLifter.liftUpAutomatic();
				//TODO cross like normal
//				AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, 2000);
			}else if (this == cheval_de_frise){
				//Push cheval_de_frise downro
				AutoModes.intakeLifterDown();
				//Cross
//				moveBarrierLength(direction);
				//TODO put in low gear  (CURRENTLY NOT SHIFTING)
				Robot.drive.slowGear();
				Timer.delay(1);
				AutoModes.moveForwardForTime(-direction*.5, 1500);
			}else if (this == ramparts){
				double SKEW_ANGLE = -10;
				long TOTAL_TIME = 2000;
				//Skew robot to start rampart
				double part1 = .2;
				AutoModes.currentTargetAngle = AutoModes.currentTargetAngle+SKEW_ANGLE;
				AutoModes.rotateTimeBased(direction*getObstacleSpeed(), 1, 700);
				AutoModes.moveForwardForTime(direction*getObstacleSpeed(), 1000);
//				AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, (long) (part1*TOTAL_TIME));
////				AutoModes.moveForwardForDistance(direction*AutoModes.ROBOT_SPEED, (part1/Math.cos(SKEW_ANGLE))*AutoModes.DISTANCE_ACROSS_BARRIER, ((long)part1)*AutoModes.TIMEOUT_DISTANCE_ACROSS_BARRIER);
//				
//				//Skew robot to start rampart
//				double part2 = 1-part1;
//				AutoModes.currentTargetAngle = AutoModes.currentTargetAngle-SKEW_ANGLE;
//				AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, (long) (part1*TOTAL_TIME));
////				AutoModes.moveForwardForDistance(direction*AutoModes.ROBOT_SPEED, part2*AutoModes.DISTANCE_ACROSS_BARRIER, ((long)part2)*AutoModes.TIMEOUT_DISTANCE_ACROSS_BARRIER);
			}
		}else if (this.difficulty == Difficulty.impossible) {
			//replace getStartingObstacle() with this
//			if(getStartingObstacle().position != 5 && getBarrierAtPosition(getStartingObstacle().position+1).difficulty == Difficulty.simple) {
//				AutoModes.rotateToGyroPosition(270);
//				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
//				AutoModes.rotateToGyroPosition(0);
//				getBarrierAtPosition(getStartingObstacle().position+1).crossBarrier(direction);
//			}else if(getStartingObstacle().position != 1 && getBarrierAtPosition(getStartingObstacle().position-1).difficulty == Difficulty.simple) {
//				AutoModes.rotateToGyroPosition(90);
//				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
//				AutoModes.rotateToGyroPosition(0);
//				getBarrierAtPosition(getStartingObstacle().position-1).crossBarrier(direction);
//			}
		}
	}
	
	public void moveFromObstacleToTarget(int position) {

		int targetIndex;
		if(position == 1) {
			targetIndex = 1;
		}else if(position == 5) {
			targetIndex = 3;
		}else{
			targetIndex = 2;
		}
				

		double direction = getObstacleDirection();
		
		AutoModes.moveForwardForTime(.8, 1200);//temp
		Timer.delay(.2);
		if (targetIndex == 1) {
			if(direction == -1) {
				AutoModes.rotateToGyroPosition(60);
			}else{
				AutoModes.rotateToGyroPosition(120);
			}
		}
		else if (targetIndex == 3) {
			if(direction == -1) {
				AutoModes.rotateToGyroPosition(-60);
			}else{
				AutoModes.rotateToGyroPosition(-120);
			}
		}
		else { //targetIndex == 2	
			//No rotate
		}		
			Timer.delay(.2);
	//		AutoModes.driveWithinShotRange();
			AutoModes.moveForwardForTime(.9, 800);//temp
			Timer.delay(.2);
		
	}
	

//	public static void crossRampart() {
//		long startTime = System.currentTimeMillis();
//		double startDisplacement = Robot.gyro.getGroundDisplacement();
//		
//		while(Math.abs(Robot.gyro.getGroundDisplacement()-startDisplacement) < distance &&
//				System.currentTimeMillis()-startTime < timeoutMillis && inAutonomous()) {
//			Robot.drive.arcadeDrive(driveSpeed, Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(currentAngle));
//		}
//		
//		stop();
//	}
}


