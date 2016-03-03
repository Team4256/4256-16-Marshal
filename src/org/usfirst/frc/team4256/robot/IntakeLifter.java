package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class IntakeLifter {
	static final double LIFTER_MOTOR_SPEED = .4;
	
	public CANTalon lifterLeft;
	public CANTalon lifterRight;
	
//	public DigitalInput upperLimitSwitch = new DigitalInput(2);
//	public DigitalInput lowerLimitSwitch = new DigitalInput();
	
	public double currentLifterSpeed = 0;
	private boolean shouldMoveLifterOnUpdateManually = false; //for manual mode
	public boolean isMovingAutomatically = false;
	
//	public Lifter(int lifterLeftPort, int lifterRightPort, int upperLimitSwitchPort, int lowerLimitSwitchPort)
	public IntakeLifter(CANTalon lifterLeft, CANTalon lifterRight){
		//Initialize motors
		this.lifterLeft = lifterLeft;
		this.lifterRight = lifterRight;
	
		lifterLeft.setInverted(true);
		
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
		set(-LIFTER_MOTOR_SPEED, false);
	}
	
	public void liftDownManual() {
		set(LIFTER_MOTOR_SPEED, false);
//		Robot.intake.intakeIn();
	}
	
	public void liftUpAutomatic() {
		set(-LIFTER_MOTOR_SPEED, true);
	}
	
	public void liftDownAutomatic() {
		set(LIFTER_MOTOR_SPEED, true);
//		Robot.intake.intakeIn();
	}
	
	/**
	 * Updates the lifter.
	 * MUST be called in teleop periodic.
	 */
	public void update() {
		if(!isMovingAutomatically && !shouldMoveLifterOnUpdateManually) {
			currentLifterSpeed = 0;
		}
		
		shouldMoveLifterOnUpdateManually = false;
//		if(0 < currentLifterSpeed && !upperLimitSwitch.get()) {
			lifterLeft.set(currentLifterSpeed);
//		}
		
//		Move if limit switch not active
//		if((currentLifterSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentLifterSpeed && !upperLimitSwitch.get())) {
//			set(currentLifterSpeed);
//		}else{
//			set(0);
//		}
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

