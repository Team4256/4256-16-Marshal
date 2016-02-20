package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drive4256 {
	RobotDrive robotDrive;
	DoubleSolenoid leftGearShifter;
	DoubleSolenoid rightGearShifter;

	public Drive4256(final int frontLeftMotor, final int rearLeftMotor, final int frontRightMotor,
		      final int rearRightMotor, DoubleSolenoid leftGearShifter, DoubleSolenoid rightGearShifter) {
		//when shifters are in, robot is on fast gear; when shifters are out, robot is on slow gear
		this.robotDrive = new RobotDrive(new CANTalon(frontLeftMotor), new CANTalon(rearLeftMotor), 
				new CANTalon(frontRightMotor), new CANTalon(rearRightMotor));
		this.leftGearShifter = leftGearShifter;
		this.rightGearShifter = rightGearShifter;
	}

	public void arcadeDrive(double moveValue, double rotateValue) {
		robotDrive.arcadeDrive(moveValue, rotateValue);
	}

	public void gearShift() {
		if (leftGearShifter.get() == DoubleSolenoid.Value.kForward){
			fastGear();
		}else{
			slowGear();
		}
	}

	public void slowGear() {
		leftGearShifter.set(DoubleSolenoid.Value.kForward);
		rightGearShifter.set(DoubleSolenoid.Value.kForward);
	}

	public void fastGear() {
		leftGearShifter.set(DoubleSolenoid.Value.kReverse);
		rightGearShifter.set(DoubleSolenoid.Value.kReverse);
	}
}
