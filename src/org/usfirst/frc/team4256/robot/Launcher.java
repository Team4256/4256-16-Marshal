package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class Launcher {
	public static final double SHOOTER_SPEED = .95;
	//The second number on the image is the y which is range
//	Range shootingYRangeShort = new Range(155, 170);//changed in Cinc competition from 150
//	Range shootingYRangeShort = new Range(168, 183);//was 165, 175
	Range shootingYRangeShort = new Range(150, 170);//practice robot blue gym 4/15/16
	Range shootingYRangeLong = new Range(187, 221);  //  This is the long shot (shooter up position)

//	Range shootingYRangeShort = new Range(146, 182);
//	Range shootingYRangeLong = new Range(187, 221);
	
	CANTalon shooterLeft;
	CANTalon shooterRight;
	DoubleSolenoid turretLifter;
	
	boolean isRaised;
	
	public Launcher(CANTalon shooterLeft, CANTalon shooterRight, DoubleSolenoid turretLifter) {
		this.shooterLeft = shooterLeft;
		this.shooterRight = shooterRight;
		this.turretLifter = turretLifter;
		
		changeControlMode(true);
//		SmartDashboard.putNumber("shooter speed", .95);
	}
	
	private void changeControlMode(boolean makeFollower) {
		if(makeFollower) {
			shooterRight.changeControlMode(CANTalon.TalonControlMode.Follower);
			shooterRight.set(shooterLeft.getDeviceID());
		}else{
			shooterRight.changeControlMode(CANTalon.TalonControlMode.Speed);
			shooterRight.set(0);
		}
	}
//	
	public void stop() {
		shooterLeft.set(0);
	}
	Toggle shooterToggle = new Toggle(Robot.xboxGun, DBJoystick.BUTTON_B);
	public void start() {
		changeControlMode(true);
//		double shooterScale = (shooterToggle.getState() ? 1 : SHOOTER_SPEED);
//		shooterLeft.set(SmartDashboard.getNumber("shooter speed", .95));
		shooterLeft.set(SHOOTER_SPEED);
//		SmartDashboard.putBoolean("Shooter Scale", shooterToggle.getState());
		
	}
	
	public void shootLeft() {
		changeControlMode(false);
		shooterLeft.set(.94);
		shooterRight.set(SHOOTER_SPEED);
	}
	
	public void fire() {
		start();
		lower();
	}
	
	public void raise() {
		isRaised = true;
		Robot.visionTable.putNumber("ShooterAngle", isRaised? 39.1 : 33);
		turretLifter.set(DoubleSolenoid.Value.kForward);
	}
	
	public void lower() {
		isRaised = false;
		Robot.visionTable.putNumber("ShooterAngle", isRaised? 39.1 : 33);
		turretLifter.set(DoubleSolenoid.Value.kReverse);
	}
}
