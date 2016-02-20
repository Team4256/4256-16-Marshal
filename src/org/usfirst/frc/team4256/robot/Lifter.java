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
	public boolean isMovingAutomatically = false;
	
//	public Lifter(int lifterLeftPort, int lifterRightPort, int upperLimitSwitchPort, int lowerLimitSwitchPort)
	public Lifter(int lifterLeftID, int lifterRightID){
		//Initialize motors
		lifterLeft = new CANTalon(lifterLeftID);
		lifterRight = new CANTalon(lifterRightID);
	
		
		lifterRight.changeControlMode(CANTalon.TalonControlMode.Follower);
		lifterRight.set(lifterLeft.getDeviceID());
		lifterLeft.enableLimitSwitch(true, true);
	}
	
	private void set(double speed, boolean automatic) {
		currentLifterSpeed = speed;
		isMovingAutomatically = automatic;
		lifterLeft.set(speed);
//		lifterRight.set(speed);
	}
	
	public void liftUpManual() {
		set(LIFTER_MOTOR_SPEED, false);
	}
	
	public void liftDownManual() {
		set(-LIFTER_MOTOR_SPEED, false);
	}
	
	public void liftUpAutomatic() {
		set(LIFTER_MOTOR_SPEED, true);
	}
	
	public void liftDownAutomatic() {
		set(-LIFTER_MOTOR_SPEED, true);
	}
	
	/**
	 * Updates the lifter.
	 * MUST be called before other lifter actions in teleop.
	 */
	public void update() {
		if(!isMovingAutomatically) {
			currentLifterSpeed = 0;
		}

		lifterLeft.set(currentLifterSpeed);
//		Move if limit switch not active
//		if((currentLifterSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentLifterSpeed && !upperLimitSwitch.get())) {
//			set(currentLifterSpeed);
//		}else{
//			set(0);
//		}
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

