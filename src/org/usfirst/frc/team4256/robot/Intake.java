package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	
	public static final double ROLLER_IN_SPEED = 1;
	public static final double ROLLER_OUT_SPEED = 1;
	
	public State currentAction = State.nothing;
	public static enum State {loadTurret, intakeOut, intake, nothing}
	
	boolean shouldHaveBall = false;
	
	public VictorSP intakeRoller;
	
	public DigitalInput stagingLimitSwitch;
	
	public Intake(int intakeRollerPort, int stagingLeftPort, int stagingRightPort, int stagingLimitSwitchPort) {
		intakeRoller = new VictorSP(intakeRollerPort);
		stagingLimitSwitch = new DigitalInput(stagingLimitSwitchPort);
	}
	
	private void set(double intakeDirection) {
		intakeRoller.set(ROLLER_OUT_SPEED*intakeDirection);
	}
	
	private void set() {
		if (currentAction == State.intake || currentAction == State.loadTurret
				|| (shouldHaveBall && !stagingLimitSwitch.get())) {
			set(1);
		}else if (currentAction == State.intakeOut) {
			set(-1);
		}else{
			set(0);
		}
	}
	
	public void stop() {
		if (currentAction != State.intake) {
			currentAction = State.nothing;
		}
	}
	
	public void intakeIn() {
			currentAction = State.intake;
	}
	
	public void intakeOut() {
		shouldHaveBall = false;
		currentAction = State.intakeOut;
	}
	
	public void loadTurret() {
		shouldHaveBall = false;
		currentAction = State.loadTurret;
	}
	
	public void update() {
//		if(shouldHaveBall && !stagingLimitSwitch.get()) {
//			currentAction = State.intake;
//		}
		
		if(currentAction == State.intake && stagingLimitSwitch.get()) {
			shouldHaveBall = true;
			currentAction = State.nothing;
		}
		set();
		
	}
}

