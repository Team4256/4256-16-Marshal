package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	
	public static final double ROLLER_IN_SPEED = 1;
	public static final double ROLLER_OUT_SPEED = 1;
	
	public State currentAction = State.nothing;
	public static enum State {loadTurret, intakeOut, intake, nothing}
	
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
		if (currentAction == State.intake || currentAction == State.loadTurret) {
			set(1);
//			Robot.shooter.start();
//			Robot.shooterLeft.set(.3);//temp
		}else if (currentAction == State.intakeOut) {
			set(-1);
//			Robot.shooter.stop();
//			Robot.shooterLeft.set(0);//temp
		}else{
			set(0);
//			Robot.shooter.stop();
//			Robot.shooterLeft.set(0);//temp
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
		currentAction = State.intakeOut;
	}
	
	public void loadTurret() {
//		this.firingStartTime = System.currentTimeMillis();
//		this.firingTotalTime = firingTime;
		currentAction = State.loadTurret;
	}
	
//	public void loadTurret() {
//		//fireHigh(FIRE_HIGH_TIME);
//		loadTurret(LOAD_TURRET_TIME);
//	}
	
	public void update() {
		if(currentAction == State.intake && stagingLimitSwitch.get()) {
			currentAction = State.nothing;
		}
		set();
		
	}
}

