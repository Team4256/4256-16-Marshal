package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive4256 {
	public static final double DRIVE_TURN_OFFSET = 0;//.12 - scewed to left with new drive fixs
	public static final double DRIVE_TURN_BACKWARDS_OFFSET = 0;
	
	static RobotDrive robotDrive;
	DoubleSolenoid gearShifter;
	Double lockedAngle;
//	DoubleSolenoid rightGearShifter;

	CANTalon wheelFrontLeft;
	CANTalon wheelBackLeft;
	CANTalon wheelFrontRight;
	CANTalon wheelBackRight;
	
	boolean isAligning;
	boolean isSuperAligning;
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
		this.robotDrive.setSafetyEnabled(false);

		this.gearShifter = gearShifter;
//		this.rightGearShifter = rightGearShifter;
	}

	public void arcadeDrive(double moveValue, double rotateValue) {
		//Drive
		if(isSuperAligning) {
			superAlign(Robot.xboxGun);
		}else if(isAligning) {
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
		
		//Check if robot is within shooting range
		if(Robot.visionTable.getBoolean("TargetVisibility", false)) {
			//Check target alignment
			double targetRotationalOffset = Math.abs(Robot.visionTable.getNumber("TargetX", 0)-Robot.visionTable.getNumber("ImageWidth", -1)/2);
			SmartDashboard.putBoolean("Aligned", (targetRotationalOffset <= 6));
			
			//Check target distance
			Range shootingYRange;
			if(Robot.shooter.isRaised) {
				shootingYRange = Robot.shooter.shootingYRangeLong;
			}else{
				shootingYRange = Robot.shooter.shootingYRangeShort;
			}
			
			double targetVerticalOffset = Math.abs(Robot.visionTable.getNumber("TargetY", -1)-shootingYRange.getCenter());
			SmartDashboard.putBoolean("In range", (targetVerticalOffset <= shootingYRange.getRange()));
		}else{
			SmartDashboard.putBoolean("Aligned", false);
			SmartDashboard.putBoolean("In range", false);
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
		SmartDashboard.putString("Shifter Value", "High Gear");//switched for practice
		gearShifter.set(DoubleSolenoid.Value.kForward);
//		rightGearShifter.set(DoubleSolenoid.Value.kForward);
	}

	public void fastGear() {
		SmartDashboard.putString("Shifter Value", "Low Gear");//switched for robot
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
	
	public void superAlignToTarget() {
		isSuperAligning = true;
	}

	public void alignToTarget() {
		isAligning = true;
	}
	
	private void superAlign(DBJoystick controller) {
		if (moveToTarget(Robot.shooter.shootingYRangeShort)) {
			if (align(Robot.xboxGun)) {
				Robot.shooter.fire();
				isSuperAligning = false;
			}
		}
	}

	public boolean moveToTarget(Range shootingYRange) {
		double driveSpeed = Robot.visionTable.getNumber("TargetY", -1) >= shootingYRange.max-15 ? .65 : 1;
		double targetY = Robot.visionTable.getNumber("TargetY", -1);
//		long startTime = System.currentTimeMillis();
		
		//Drive until target is at the farthest shooting range
		if(targetY >= shootingYRange.max) {
			arcadeDrive(driveSpeed, AutoModes.capMaximumMotorValue(AutoModes.getTargetOffset(), .85));
		}else if(targetY >= shootingYRange.fromPercent(75)) {
			driveSpeed = .5;
			arcadeDrive(driveSpeed, AutoModes.capMaximumMotorValue(AutoModes.getTargetOffset(), .8));
		}else{
			return true;
		}
		return false;
	}
	
	private boolean align(DBJoystick controller) {
		alignToTarget(controller, .3, .12, .02);
		alignToTarget(controller, .2, .08, .02);
		alignToTarget(controller, .1, .08, .1);
		if(alignToTarget(controller, .03, .08, .2) && !Robot.gyro.isRotating()) {
			isAligning = false;
			return true;
		}
		return false;
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
		//Terminate if target not visible
		if(!Robot.visionTable.getBoolean("TargetVisibility", false)) {
			isAligning = false;
			isSuperAligning = false;
			return false;
		}
		
		//Get target offset
		targetOffset = AutoModes.getTargetOffset();
		
		//Rotate towards target center
		if(Math.abs(targetOffset) > accuracy) {
			targetOffset = AutoModes.getTargetOffset();
//			robotDrive.arcadeDrive(0, AutoModes.correctMotorValue(targetOffset, .45, .56));
			robotDrive.arcadeDrive(0, AutoModes.correctMotorValue(targetOffset, .48, .56));
			
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
	
	public static boolean teleopDelay(double timeoutSeconds, DBJoystick controller) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime <= timeoutSeconds*1000) {
			if(controller.anyControlIsActive()) {
				return true;
			}
		}
		
		return false;
	}
}
