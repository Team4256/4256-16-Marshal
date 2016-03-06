package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drive4256 {
	public static final double DRIVE_TURN_OFFSET = -.08;
	
	RobotDrive robotDrive;
	DoubleSolenoid gearShifter;
	Double lockedAngle;
//	DoubleSolenoid rightGearShifter;


	public Drive4256(CANTalon LFMotor, CANTalon RFMotor, CANTalon LBMotor, CANTalon RBMotor, DoubleSolenoid gearShifter) {
		//when shifters are in, robot is on fast gear; when shifters are out, robot is on slow gear

		LBMotor.changeControlMode(CANTalon.TalonControlMode.Follower);
		LBMotor.set(LFMotor.getDeviceID());

		RBMotor.changeControlMode(CANTalon.TalonControlMode.Follower);
		RBMotor.set(RFMotor.getDeviceID());
		
		LFMotor.setInverted(true);
		LBMotor.setInverted(true);
		this.robotDrive = new RobotDrive(LFMotor/*, LBMotor*/, RFMotor/*, RBMotor*/);

		this.gearShifter = gearShifter;
//		this.rightGearShifter = rightGearShifter;
	}

	public void arcadeDrive(double moveValue, double rotateValue) {
		if (lockedAngle != null) {
			robotDrive.arcadeDrive(moveValue, rotateValue + Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(lockedAngle) - DRIVE_TURN_OFFSET);
		}else {
			robotDrive.arcadeDrive(moveValue, rotateValue - DRIVE_TURN_OFFSET);
		}
	}

	public void gearShift(boolean gear) {
		//if (leftGearShifter.get() == DoubleSolenoid.Value.kForward){
		if (gear) {
			fastGear();
		}else{
			slowGear();
		}
	}

	public void slowGear() {
		gearShifter.set(DoubleSolenoid.Value.kForward);
//		rightGearShifter.set(DoubleSolenoid.Value.kForward);
	}

	public void fastGear() {
		gearShifter.set(DoubleSolenoid.Value.kReverse);
//		rightGearShifter.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void lockAngle(boolean updateAngle) {
		if(updateAngle && lockedAngle == null) {
			lockedAngle = Robot.gyro.getAngle();
		}else{
			lockedAngle = null;
		}
	}
}
