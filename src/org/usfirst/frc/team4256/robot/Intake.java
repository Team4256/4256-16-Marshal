package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public static final double STAGING_MOTOR_SPEED = .3;
	public static final double INTAKE_ROLLER_MOTOR_SPEED = .3;
	public double direction = 0;
	
	public VictorSP stagingLeft;
	public VictorSP stagingRight; 
	public VictorSP intakeRoller;
	
	public DigitalInput middleLimitSwitch;
	
	public Intake(int stagingLeftPort, int stagingRightPort, int intakeRollerPort, int middleLimitSwitchPort) {
		stagingLeft = new VictorSP(stagingLeftPort);
		stagingRight = new VictorSP(stagingRightPort);
		intakeRoller = new VictorSP(intakeRollerPort);
		middleLimitSwitch = new DigitalInput(middleLimitSwitchPort);
	}
	
	private void set(double direction) {
		stagingLeft.set(direction*STAGING_MOTOR_SPEED);
		stagingRight.set(direction*STAGING_MOTOR_SPEED);
		intakeRoller.set(direction*INTAKE_ROLLER_MOTOR_SPEED);
	}
	
	public void intakeIn() {
		direction = 1;
	}
	
	public void intakeOut() {
		direction = -1;
	}
	
	public void update() {
		//Move if limit switch not active
		if((direction == -1 && !middleLimitSwitch.get())) {
			set(direction);
		}else{
			set(0);
		}
	}
}

