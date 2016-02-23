package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeLifter {
	private static final double LIFTER_MOTOR_SPEED = .2;
	
	public CANTalon lifterLeft;
	public CANTalon lifterRight;
	
//	public DigitalInput upperLimitSwitch;
//	public DigitalInput lowerLimitSwitch;
	
	public double currentLifterSpeed = 0;
	private boolean shouldMoveLifterOnUpdateManually = false; //for manual mode
	public boolean isMovingAutomatically = false;
	
//	public Lifter(int lifterLeftPort, int lifterRightPort, int upperLimitSwitchPort, int lowerLimitSwitchPort)
	public IntakeLifter(int lifterLeftID, int lifterRightID){
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
		shouldMoveLifterOnUpdateManually = true;
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
	 * MUST be called in teleop periodic.
	 */
	public void update() {
		if(!isMovingAutomatically && shouldMoveLifterOnUpdateManually) {
			currentLifterSpeed = 0;
		}
		
		shouldMoveLifterOnUpdateManually = false;
		lifterLeft.set(currentLifterSpeed);
		
//		Move if limit switch not active
//		if((currentLifterSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentLifterSpeed && !upperLimitSwitch.get())) {
//			set(currentLifterSpeed);
//		}else{
//			set(0);
//		}
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

