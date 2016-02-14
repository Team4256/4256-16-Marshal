package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class Lifter {
	private static final double LIFTER_MOTOR_SPEED = .2;
	
	public CANTalon lifterLeft;
	public CANTalon lifterRight;
	
	public DigitalInput upperLimitSwitch;
	public DigitalInput lowerLimitSwitch;
	
	public double currentLifterSpeed = 0;
	
	public Lifter(int lifterLeftPort, int lifterRightPort, int upperLimitSwitchPort, int lowerLimitSwitchPort) {
		//Initialize motors
		lifterLeft = new CANTalon(lifterLeftPort);
		lifterRight = new CANTalon(lifterRightPort);
		
		//Initialize limit switches
		upperLimitSwitch = new DigitalInput(upperLimitSwitchPort);
		lowerLimitSwitch = new DigitalInput(lowerLimitSwitchPort);
		
		
	}
	
	private void set(double speed) {
		lifterLeft.set(speed);
		lifterRight.set(speed);
	}
	
	public void liftUp() {
		currentLifterSpeed = LIFTER_MOTOR_SPEED;
	}
	
	public void liftDown() {
		currentLifterSpeed = -LIFTER_MOTOR_SPEED;
	}
	
	public void update() {
		//Move if limit switch not active
		if((currentLifterSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentLifterSpeed && !upperLimitSwitch.get())) {
			set(currentLifterSpeed);
		}else{
			set(0);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
