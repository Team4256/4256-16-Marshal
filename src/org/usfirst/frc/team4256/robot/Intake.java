package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public static final long LOAD_TURRET_TIME =1000;
	public static final long FIRE_LOW_TIME =500;
	
	public static final double ROLLER_IN_SPEED = 1;
	public static final double ROLLER_OUT_SPEED = 1;
	public static final double STAGING_IN_SPEED = .3;
	public static final double STAGING_OUT_SPEED = .3;
	public int direction = 1;
	
	public State currentAction = State.nothing;
	public static enum State {loadTurret, intakeOut, intake, nothing}
	public long firingTotalTime;
	public long firingStartTime;
	
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
	
	private void set(double intakeDirection, double stagingDirection) {
		intakeRoller.set(ROLLER_OUT_SPEED*intakeDirection);
		stagingLeft.set(STAGING_OUT_SPEED*stagingDirection);
		stagingRight.set(STAGING_OUT_SPEED*stagingDirection);
	}
	
	private void set() {
		if (currentAction == State.loadTurret) {
			set(0, 1);
		}else if (currentAction == State.intakeOut) {
			set(-1, -1);
		}else if (currentAction == State.intake) {
			set(1, 1);
		}else{
			set(0,0);
		}
	}
	public void stop() {
		currentAction = State.nothing;
	}
	public void intakeIn() {
//		if (System.currentTimeMillis() - startTime > totalTime) {
			currentAction = State.intake;
//		}
	}
	
	public void intakeOut(long firingTime) {
		this.firingTotalTime = firingTime;
		currentAction = State.intakeOut;
	}
	
	public void intakeOut() {
		intakeOut(FIRE_LOW_TIME);
	}
	
	public void loadTurret(long firingTime) {
		this.firingTotalTime = firingTime;
		currentAction = State.loadTurret;
	}
	
	public void loadTurret() {
		//fireHigh(FIRE_HIGH_TIME);
		loadTurret(LOAD_TURRET_TIME);
	}
	
	public void update() {
		if (currentAction == State.intake && stagingLimitSwitch.get()) {
//			direction = 0;
		}else {
//			direction = 1;
//			set();
		}
		
		if(System.currentTimeMillis() - firingStartTime > firingTotalTime && currentAction != State.intake) {
			currentAction = State.nothing;
		}
		
		set();
	}
}

