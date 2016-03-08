package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Launcher {
	public static final double SHOOTER_SPEED = .95;
	
	CANTalon shooterLeft;
	CANTalon shooterRight;
	DoubleSolenoid turretLifter;
	
	boolean isRaised;
	
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
		isRaised = true;
		Robot.visionTable.putNumber("ShooterAngle", isRaised? 39.1 : 33);
		turretLifter.set(DoubleSolenoid.Value.kForward);
	}
	
	public void lower() {
		isRaised = false;
		Robot.visionTable.putNumber("ShooterAngle", isRaised? 39.1 : 33);
		turretLifter.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void align(DBJoystick controller) {
		alignToTarget(controller, .6, 0, 0);
		alignToTarget(controller, .3, .15, .2);
		alignToTarget(controller, .03, .1, .2);
	}
	

	public void alignToTarget(DBJoystick controller, double accuracy, double driveIncrementDelay, double pauseIncrementDelay) {
		double targetOffset = AutoModes.getTargetOffset();
		
		while(Math.abs(targetOffset) > accuracy) {
			targetOffset = AutoModes.getTargetOffset();
			Robot.drive.arcadeDrive(0, AutoModes.correctMotorValue(targetOffset, .55, .56));
			if(pauseIncrementDelay != 0) {
				if(teleopDelay(driveIncrementDelay, controller)) break;
				Robot.drive.arcadeDrive(0, 0);
				if(teleopDelay(pauseIncrementDelay, controller)) break;
			}
		}
	}
	
	/**
	 * Delays unless a joystick control is activated.
	 * Returns true if the joystick has been activated.
	 * @param delayMillis - the milliseconds to delay.
	 * @param controller -  the controller to check actions for.
	 * @return
	 */
	public static boolean teleopDelay(double delayMillis, DBJoystick controller) {
		long startTime = System.currentTimeMillis();
		Robot.gyro.resetDisplacement();
		
		while(System.currentTimeMillis()-startTime < delayMillis) {
			if(controller.anyControlIsActive()) {
				return true;
			}
		}
		
		return false;
	}
}
