package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Defense {
	public DoubleSolenoid defensePlayer;
	//public DoubleSolenoid wall;
	
	public Defense(DoubleSolenoid defensePlayer) {
		this.defensePlayer = defensePlayer;
	}
	
	public void activate() {
		SmartDashboard.putString("Shooter Position", "Up");
		defensePlayer.set(DoubleSolenoid.Value.kForward);
	}
	
	public void deactivate() {
		SmartDashboard.putString("Shooter Position", "Down");
		defensePlayer.set(DoubleSolenoid.Value.kReverse);
	}
}
