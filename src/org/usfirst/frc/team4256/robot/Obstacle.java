package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	final static Obstacle ramparts = new Obstacle("ramparts", Difficulty.simple, 2);
	final static Obstacle drawbridge = new Obstacle("drawbridge", Difficulty.impossible, 3);
	final static Obstacle sally_port = new Obstacle("sally_port", Difficulty.impossible, 3);
	final static Obstacle rock_wall = new Obstacle("rock_wall", Difficulty.simple, 4);
	final static Obstacle rough_terrain = new Obstacle("rough_terrain", Difficulty.simple, 4);
	final static Obstacle low_bar = new Obstacle("low_bar", Difficulty.simple, 5);
	
	int position; 
	Difficulty difficulty;
	
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
//		autonomusObstacleDropDowns[position-1].addObject(name, this);
//		if(position == 1) {
//			autonomusObstacle1.addObject(name, this);
		
//		}else if(position == 2) {
//			autonomusObstacle2.addObject(name, this);
//		}
//		else if(position == 3) {
//			autonomusObstacle3.addObject(name, this);		
//		}
//		else if(position == 4) {
//			autonomusObstacle4.addObject(name, this);
//		}
//		else if(position == 5) {
//			autonomusObstacle5.addObject(name, this);
//		}
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
	
	public void crossBarrier() {
		if (getStartingObstacle().difficulty == Difficulty.simple) {
			AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, 2000);
		}else if (getStartingObstacle().difficulty == Difficulty.hard) {
			if (getStartingObstacle() == portcullis) {
				AutoModes.moveToLimitSwitch(AutoModes.ROBOT_SPEED, Robot.intake.middleLimitSwitch/*change limit switch*/, 3000);
				Robot.intakeLifter.liftUp();
				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, 2000);
			}else if (getStartingObstacle() == cheval_de_frise){
				throw new Error("crossing cheval_de_frise not programmed");
			}
		}else if (getStartingObstacle().difficulty == Difficulty.impossible) {
			if(getStartingObstacle().position != 5 && getBarrierAtPosition(getStartingObstacle().position+1).difficulty == Difficulty.simple) {
				AutoModes.rotateToGyroPosition(270);
				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
				AutoModes.rotateToGyroPosition(0);
				getBarrierAtPosition(getStartingObstacle().position+1).crossBarrier();
			}else if(getStartingObstacle().position != 1 && getBarrierAtPosition(getStartingObstacle().position-1).difficulty == Difficulty.simple) {
				AutoModes.rotateToGyroPosition(90);
				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
				AutoModes.rotateToGyroPosition(0);
				getBarrierAtPosition(getStartingObstacle().position-1).crossBarrier();
			}
		}
	}
}
