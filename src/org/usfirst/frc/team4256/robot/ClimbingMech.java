package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ClimbingMech {
	public CANTalon lifterWinch;
	public DoubleSolenoid winchStop;
	
	boolean isActive = false;
	
	public ClimbingMech(CANTalon lifterWinch, DoubleSolenoid winchStop){
		this.lifterWinch = lifterWinch;
		this.winchStop = winchStop;
	}
	
	public void startClimbing() {
		isActive = true;
		winchStop.set(DoubleSolenoid.Value.kForward);
	}
	
	public void moveHook(double raiseSpeed) {
		lifterWinch.set(raiseSpeed);
	}
	
	public void lockHook() {
		winchStop.set(DoubleSolenoid.Value.kReverse);
	}
}
