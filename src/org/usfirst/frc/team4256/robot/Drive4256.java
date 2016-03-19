package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive4256 {
	public static final double DRIVE_TURN_OFFSET = .12;
	public static final double DRIVE_TURN_BACKWARDS_OFFSET = 0;
	
	RobotDrive robotDrive;
	DoubleSolenoid gearShifter;
	Double lockedAngle;
//	DoubleSolenoid rightGearShifter;

	CANTalon wheelFrontLeft;
	CANTalon wheelBackLeft;
	CANTalon wheelFrontRight;
	CANTalon wheelBackRight;
	
	boolean isAligning;
	double targetOffset;

	public Drive4256(CANTalon LFMotor, CANTalon RFMotor, CANTalon LBMotor, CANTalon RBMotor, DoubleSolenoid gearShifter) {
		//when shifters are in, robot is on fast gear; when shifters are out, robot is on slow gear
		wheelFrontLeft = LFMotor;
		wheelBackLeft = LBMotor;
		wheelFrontRight = RFMotor;
		wheelBackRight = RBMotor;

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
		if(isAligning) {
			align(Robot.xboxGun);
		}else{
			//Get rotational offset (due to wheel resistance/friction)
			double driveTurnOffset = rotateValue;
			if(rotateValue < 0) {
				driveTurnOffset *= DRIVE_TURN_BACKWARDS_OFFSET;
			}else{
				driveTurnOffset *= DRIVE_TURN_OFFSET;
			}

			//Arcade drive. Lock angle if set.
			if (lockedAngle != null) {
				SmartDashboard.putString("Angle Lock Toggle", "Engaged");
				robotDrive.arcadeDrive(moveValue, rotateValue + 
						Robot.gyro.getAngleDisplacementFromAngleAsMotorValue(lockedAngle) + driveTurnOffset);
			}else {
				SmartDashboard.putString("Angle Lock Toggle", "Disengaged");
				robotDrive.arcadeDrive(moveValue, rotateValue + driveTurnOffset);
			}
		}
	}
	
	public void enableBreakMode(boolean brake) {
		wheelBackLeft.enableBrakeMode(brake);
		wheelBackRight.enableBrakeMode(brake);
		wheelFrontLeft.enableBrakeMode(brake);
		wheelFrontRight.enableBrakeMode(brake);
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
		SmartDashboard.putString("Shifter Value", "Low Gear");
		gearShifter.set(DoubleSolenoid.Value.kForward);
//		rightGearShifter.set(DoubleSolenoid.Value.kForward);
	}

	public void fastGear() {
		SmartDashboard.putString("Shifter Value", "High Gear");
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

	public void alignToTarget() {
		isAligning = true;
	}
	
	private void align(DBJoystick controller) {
		//Align if target is farther from center
//		alignToTarget(controller, .6, 0, 0);
//		alignToTarget(controller, .58, .15, .15);
//		alignToTarget(controller, .3, .15, .06);
//		alignToTarget(controller, .1, .15, .08);
		
		
//		alignToTarget(controller, .6, 0, 0);
//		alignToTarget(controller, .58, .15, .15);
//		alignToTarget(controller, .2, .16, .02);
//		alignToTarget(controller, .1, .15, .04);
//		
//		//Stop alignment if the target has been aligned and the robot is not rotating
//		if(alignToTarget(controller, .03, .13, .1) && !Robot.gyro.isMoving()) {
//			isAligning = false;
//		}
//		alignToTarget(controller, .6, 0, 0);
//		alignToTarget(controller, .58, .1, .15);
//		alignToTarget(controller, .3, .1, .1);
		alignToTarget(controller, .3, .12, .02);
		alignToTarget(controller, .2, .08, .02);
		alignToTarget(controller, .1, .08, .1);
		if(alignToTarget(controller, .03, .08, .2) && !Robot.gyro.isRotating()) {
			isAligning = false;
		}
	}

	/**
	 * Will rotate the drive system towards the target.
	 * 
	 * @param controller - will terminate if this controller is activated
	 * @param accuracy - the distance of the target from the robot's center on a scale from 0 to 1
	 * @param driveIncrementDelay - the time to drive for in milliseconds (for moving in increments)
	 * @param pauseIncrementDelay - the time to stop after driving in milliseconds (for moving in increments)
	 * @return whether or not the target is within the specified accuracy
	 */
	private boolean alignToTarget(DBJoystick controller, double accuracy, double driveIncrementDelay, double pauseIncrementDelay) {
		targetOffset = AutoModes.getTargetOffset();
		
		if(Math.abs(targetOffset) > accuracy) {
			targetOffset = AutoModes.getTargetOffset();
			robotDrive.arcadeDrive(0, AutoModes.correctMotorValue(targetOffset, .45, .56));
			
			if(pauseIncrementDelay != 0) {
				isAligning = !teleopDelay(driveIncrementDelay, controller);
				if(!isAligning) return false;
				robotDrive.arcadeDrive(0, 0);
				isAligning = !teleopDelay(pauseIncrementDelay, controller);
			}
			
			return false;
		}else{
			return true;
		}
	}

	/**
	 * Delays unless a joystick control is activated.
	 * Returns true if the joystick has been activated.
	 * @param timeoutSeconds - the SECONDS to delay.
	 * @param controller -  the controller to check actions for.
	 * @return
	 */
	private static boolean teleopDelay(double timeoutSeconds, DBJoystick controller) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime <= timeoutSeconds*1000) {
			if(controller.anyControlIsActive()) {
				return true;
			}
		}
		SmartDashboard.putNumber("teleop delay", System.currentTimeMillis()-startTime);
		return false;
	}
}
