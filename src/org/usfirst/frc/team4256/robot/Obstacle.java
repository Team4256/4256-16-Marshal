package org.usfirst.frc.team4256.robot;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
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
	
	public void crossBarrier(double direction) {
//		Robot.intakeLifter.liftDownAutomatic();
		
		if (getStartingObstacle().difficulty == Difficulty.simple) {
			//Cross like normal
			Robot.intakeLifter.liftDownAutomatic();
			AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, 2000);
		}else if (getStartingObstacle().difficulty == Difficulty.hard) {
			if (getStartingObstacle() == portcullis) {
				Robot.intakeLifter.liftDownAutomatic();
//				AutoModes.moveToLimitSwitch(direction*AutoModes.ROBOT_SPEED, Robot.intake.middleLimitSwitch/*change limit switch*/, 3000);
//				Robot.intakeLifter.liftUpAutomatic();
				//TODO cross like normal
//				AutoModes.moveForwardForTime(direction*AutoModes.ROBOT_SPEED, 2000);
			}else if (getStartingObstacle() == cheval_de_frise){//!!!!!!!!!! NEED TO BE ON ONE SIDE TO START
				//Push cheval_de_frise down
				Robot.intakeLifter.liftUpAutomatic();
				//TODO go forward
				Timer.delay(1000);
				Robot.intakeLifter.liftDownAutomatic();
				//TODO cross like normal
			}
		}else if (getStartingObstacle().difficulty == Difficulty.impossible) {
			if(getStartingObstacle().position != 5 && getBarrierAtPosition(getStartingObstacle().position+1).difficulty == Difficulty.simple) {
				AutoModes.rotateToGyroPosition(270);
				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
				AutoModes.rotateToGyroPosition(0);
				getBarrierAtPosition(getStartingObstacle().position+1).crossBarrier(direction);
			}else if(getStartingObstacle().position != 1 && getBarrierAtPosition(getStartingObstacle().position-1).difficulty == Difficulty.simple) {
				AutoModes.rotateToGyroPosition(90);
				AutoModes.moveForwardForTime(AutoModes.ROBOT_SPEED, AutoModes.DISTANCE_BETWEEN_DEFENCES);
				AutoModes.rotateToGyroPosition(0);
				getBarrierAtPosition(getStartingObstacle().position-1).crossBarrier(direction);
			}
		}
	}
}


