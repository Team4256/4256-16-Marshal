package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ClimbingMech {
	public CANTalon lifterWinch;
	public DoubleSolenoid lock;
	
	boolean isActive = false;
	
	public ClimbingMech(CANTalon lifterWinch, DoubleSolenoid winchStop){
		this.lifterWinch = lifterWinch;
		this.lock = winchStop;
	}
	
	public void startClimbing() {
		isActive = true;
		lock.set(DoubleSolenoid.Value.kForward);
	}
	
	public void moveHook(double raiseSpeed) {
		lifterWinch.set(raiseSpeed);
	}
	
	public void lockHook() {
		lock.set(DoubleSolenoid.Value.kReverse);
	}
}
