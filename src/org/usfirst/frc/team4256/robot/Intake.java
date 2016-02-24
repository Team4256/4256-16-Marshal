package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public static final long LOAD_TURRET_TIME =1000;
	public static final long FIRE_LOW_TIME =500;
	
	public static final double ROLLER_IN_SPEED = .3;
	public static final double ROLLER_OUT_SPEED = .3;
	public static final double STAGING_IN_SPEED = .3;
	public static final double STAGING_OUT_SPEED = .3;
	public int direction = 1;
	
	public State currentAction = State.nothing;
	public static enum State {loadTurret, firingLow, intake, nothing}
	public long totalTime;
	public long startTime;
	
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
		if (currentAction == State.loadTurret) {
			intakeRoller.set(0);
			stagingLeft.set(-STAGING_OUT_SPEED*direction);
			stagingRight.set(STAGING_OUT_SPEED*direction);
		}else if (currentAction == State.loadTurret) {
			intakeRoller.set(-ROLLER_OUT_SPEED*direction);
			stagingLeft.set(STAGING_OUT_SPEED*direction);
			stagingRight.set(-STAGING_OUT_SPEED*direction);
		}else {
			intakeRoller.set(ROLLER_IN_SPEED*direction);
			stagingLeft.set(-STAGING_IN_SPEED*direction);
			stagingRight.set(STAGING_IN_SPEED*direction);
		}
	}
	
	public void intakeIn() {
		if (System.currentTimeMillis() - startTime > totalTime) {
			currentAction = State.intake;
//			loadTurret = false;
//			firingLow = false;
		}
	}
	
	public void fireLow(long firingTime) {
		this.totalTime = firingTime;
		currentAction = State.firingLow;
//		loadTurret = false;
//		firingLow = true;
	}
	
	public void fireLow() {
		fireLow(FIRE_LOW_TIME);
	}
	
	public void loadTurret(long firingTime) {
		this.totalTime = firingTime;
		currentAction = State.loadTurret;
//		loadTurret = true;
//		firingLow = false;
	}
	
	public void fireHigh() {
		//fireHigh(FIRE_HIGH_TIME);
		loadTurret(LOAD_TURRET_TIME);
	}
	
	public void update() {
		if (currentAction == State.intake && stagingLimitSwitch.get()) {
			direction = 0;
		}else {
			direction = 1;
		}
		
		if(System.currentTimeMillis() - startTime > totalTime) {
			currentAction = State.intake;
		}
		
		set();
	}
}

