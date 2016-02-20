package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public static final double ROLLER_IN_SPEED = .3;
	public static final double ROLLER_OUT_SPEED = .7;
	public static final double STAGING_IN_SPEED = .3;
	public static final double STAGING_OUT_SPEED = .7;
	public int stopper = 1;
	public boolean firingHigh = false;
	public boolean firingLow = false;
	
	public VictorSP intakeRoller;
	public VictorSP stagingLeft;
	public VictorSP stagingRight; 
	
	public DigitalInput stagingLimitSwitch;
	
	public Intake(int intakeRollerPort, int stagingLeftPort, int stagingRightPort, int stagingLimitSwitchPort) {
		intakeRoller = new VictorSP(intakeRollerPort);
		stagingLeft = new VictorSP(stagingLeftPort);
		stagingRight = new VictorSP(stagingRightPort);
		stagingLimitSwitch = new DigitalInput(stagingLimitSwitchPort);
	}
	
	private void set() {
		if (firingHigh) {
			intakeRoller.set(0);
			stagingLeft.set(-STAGING_OUT_SPEED*stopper);
			stagingRight.set(STAGING_OUT_SPEED*stopper);
		}else if (firingLow) {
			intakeRoller.set(-ROLLER_OUT_SPEED*stopper);
			stagingLeft.set(STAGING_OUT_SPEED*stopper);
			stagingRight.set(-STAGING_OUT_SPEED*stopper);
		}else {
			intakeRoller.set(ROLLER_IN_SPEED*stopper);
			stagingLeft.set(-STAGING_IN_SPEED*stopper);
			stagingRight.set(STAGING_IN_SPEED*stopper);
		}
	}
	
	public void intakeIn() {
		firingHigh = false;
		firingLow = false;
		set();
	}
	
	public void fireLow() {
		firingHigh = false;
		firingLow = true;
		set();
	}
	
	public void fireHigh() {
		firingHigh = true;
		firingLow = false;
		set();
	}
	
	public void update() {
		if (!firingHigh && !firingLow && stagingLimitSwitch.get()) {
			stopper = 0;
		}else {
			stopper = 1;
		}
	}
}

