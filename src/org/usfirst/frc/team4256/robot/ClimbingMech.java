package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ClimbingMech {
	public static final double CLIMBING_WINCH_SPEED = 1;
	
	public CANTalon climbingWinch;
	public DoubleSolenoid flinger;
	
	boolean isActive = false;
	
	public ClimbingMech(CANTalon climbingWinch, DoubleSolenoid winchStop){
		this.climbingWinch = climbingWinch;
		this.flinger = winchStop;
	}
	
	public void startClimbing() {
		isActive = true;
		flinger.set(DoubleSolenoid.Value.kForward);
	}
	
	public void raiseHook() {
		climbingWinch.set(CLIMBING_WINCH_SPEED);
	}
	
	public void stopHook() {
		climbingWinch.set(0);
	}
}
