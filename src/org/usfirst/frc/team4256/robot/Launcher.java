package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Launcher {
	public static final double SHOOTER_SPEED = .95;
	
	CANTalon shooterLeft;
	CANTalon shooterRight;
	DoubleSolenoid turretLifter;
	
	public Launcher(CANTalon shooterLeft, CANTalon shooterRight, DoubleSolenoid turretLifter) {
		this.shooterLeft = shooterLeft;
		this.shooterRight = shooterRight;
		this.turretLifter = turretLifter;
		
		shooterRight.changeControlMode(CANTalon.TalonControlMode.Follower);
		shooterRight.set(shooterLeft.getDeviceID());
	}

	public void stop() {
		shooterLeft.set(0);
	}
	
	public void start() {
		shooterLeft.set(SHOOTER_SPEED);
	}
	
	public void fire() {
		start();
		lower();
	}
	
	public void raise() {
		turretLifter.set(DoubleSolenoid.Value.kForward);
	}
	
	public void lower() {
		turretLifter.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void align() {
		AutoModes.alignToTargetIncremental();
	}
}
