package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ClimbingMech {
	public static final double CLIMBING_WINCH_SPEED = -.75;
	
	public CANTalon climbingWinchLeft;
	public CANTalon climbingWinchRight;
	public DoubleSolenoid flinger;
	
	boolean isActive = false;
	
	public ClimbingMech(CANTalon climbingWinchLeft, CANTalon climbingWinchRight, DoubleSolenoid winchStop){
		this.climbingWinchLeft = climbingWinchLeft;
		this.climbingWinchRight = climbingWinchRight;
		this.flinger = winchStop;
	}
	
	public void startClimbing() {
		isActive = true;
		flinger.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void grabMech() {
		flinger.set(DoubleSolenoid.Value.kForward);
	}
	
	public void releaseMech() {
		flinger.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void raiseHook() {
		setHookSpeed(CLIMBING_WINCH_SPEED);
	}
	
	public void stopHook() {
		setHookSpeed(0);
	}
	
	private void setHookSpeed(double speed) {
		climbingWinchLeft.set(speed);
		climbingWinchRight.set(-speed);
	}
}
