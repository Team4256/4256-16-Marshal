package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;

public class Gyro4256 extends AnalogGyro{
	//initiation
	double offset;
	double previousGoalAngle = 0;
	public Gyro4256(AnalogInput channel) {
		super(channel);
		// TODO Auto-generated constructor stub
	}
	
    //gets current angle
	public double getGyroAngle(double offset) {
		this.offset = offset;
		double current;
		if (getAngle() + offset < 0){
			current = 360 - (Math.abs(getAngle() + offset))%360;
		}else{
			current = (getAngle() + offset)%360;
		}
		return current;
    }
	public double getGyroAngle(){
		return getGyroAngle(offset);
	}
	
	//takes any number and returns a valid angle
	public double makeValidAngle(double angle){
		return (Math.abs(angle))%360;
	}
	
	//finds how many degrees the robot still has to turn in order to get to the goalAngle
    public double getRemainingDegrees(double goalAngle) {
    	goalAngle = makeValidAngle(goalAngle);
    	double part1;
    	double part2;
    	if(getGyroAngle() > 180){
    		part1 = 360 - getGyroAngle();
    	}else{
    		part1 = getGyroAngle();
    	}
    	if(goalAngle > 180){
    		part2 = 360 - goalAngle;
    	}else{
    		part2 = goalAngle;
    	}
    	
    	if((getGyroAngle() >= 180 && goalAngle <= 180) || (goalAngle >= 180 && getGyroAngle() <= 180)){
    		if(part1 + part2 >= 180){
    			return 360 - (part1 + part2);
    		}else{
    			return part1 + part2;
    		}
    	}else{
    		return Math.abs(part1 - part2);
    	}
    }
    
    //decides which rotation direction is quicker
    public double getGyroRoute(double goalAngle) {
    	goalAngle = makeValidAngle(goalAngle);
    	double motorDirection;
    	double adjustedEast = (goalAngle + 90)%360;
    	double adjustedSouth = (goalAngle + 180)%360;
    	if ((getGyroAngle() > goalAngle && getGyroAngle() < adjustedEast) ||
    			(getGyroAngle() > adjustedEast && getGyroAngle() < adjustedSouth)){
    		motorDirection = -1;
    	}else{
    		motorDirection = 1;
    	}
    	return motorDirection;
    }
    
    //returns the rotation value that must be put into arcadeDrive to get to the angle
    public double rotateToAngle(double goalAngle){
    	double xValue = getRemainingDegrees(goalAngle);
    	double xSign = getGyroRoute(goalAngle);
//    	if (goalAngle != previousGoalAngle){
//    		//a = getRemainingDegrees(goalAngle)/2;
//    		xValue = getRemainingDegrees(goalAngle)/720;
//    		xSign = getGyroRoute(goalAngle);
//    	}
    	double speed = (.8*xValue*xSign)+1;
    	return speed;
    }
    
    //returns the rotation value that must be put into arcadeDrive to increment by the angle
    public double rotateByDegrees(double goalDegrees){
    	double goalAngle = (getGyroAngle() + goalDegrees)%360;
    	return rotateToAngle(goalAngle);
    }
}