package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Turret {
	private static final double TURRET_MOTOR_SPEED = .2;
	private static final double SHOOTING_WHEEL_MOTOR_SPEED = .2;
	private static final double SCISSOR_LIFT_MOTOR_SPEED = .2;

	public CANTalon turretRotator;
	public CANTalon shootingWheelMotorLeft;
	public CANTalon shootingWheelMotorRight;
	
//	public CANTalon scissorLiftLeft;
//	public CANTalon scissorLiftRight;

	public NetworkTable visionTable;

	public DigitalInput upperLimitSwitch;
	public DigitalInput lowerLimitSwitch;
//	public DigitalInput scissorLiftUpperLimitSwitch;
//	public DigitalInput scissorLiftLowerLimitSwitch;

	public double currentTurretSpeed = 0;
	public double currentScissorLiftSpeed= 0;
	
	private boolean shouldMoveRotatorOnUpdateManually = false; //for manual mode
	public boolean isTracking = false;
	public boolean isMovingAutomatically = false;
	public boolean isLaunching = false;
	public long timeSinceLaunchStart;

	public Turret(int turretID, int shootingMotorLeftID, int shootingMotorRightID, int upperLimitSwitchPort, int lowerLimitSwitchPort, 
			/*int scissorLiftLeftID, int sissorLiftRightID, /*int scissorLiftUpperLimitSwitchPort, int scissorLiftLowerLimitSwitchPort,*/
			NetworkTable visionTable){
		//Initialize motors
		turretRotator = new CANTalon(turretID);
		shootingWheelMotorLeft = new CANTalon(shootingMotorLeftID);
		shootingWheelMotorRight = new CANTalon(shootingMotorRightID);
//		scissorLiftLeft = new CANTalon(scissorLiftLeftID);
//		scissorLiftRight = new CANTalon(sissorLiftRightID);
		
		
		
		//Initialize limit switches
		upperLimitSwitch = new DigitalInput(upperLimitSwitchPort);
		lowerLimitSwitch = new DigitalInput(lowerLimitSwitchPort);
//		scissorLiftUpperLimitSwitch = new DigitalInput(scissorLiftUpperLimitSwitchPort);
//		scissorLiftLowerLimitSwitch = new DigitalInput(scissorLiftLowerLimitSwitchPort);
		
		//Initialize vision table
		this.visionTable = visionTable;
		
		//Set scissor lift limit switches
//		scissorLiftRight.changeControlMode(CANTalon.TalonControlMode.Follower);
//		scissorLiftRight.set(scissorLiftLeft.getDeviceID());
//		scissorLiftLeft.enableLimitSwitch(true, true);
		
		//
	}

	public void aimRotatorToTarget() {//TargetX, TargetY, TargetWidth, TargetHeight, ImageWidth, ImageHeight
		try{
			double targetX = visionTable.getNumber("TargetX", 0);
			double imageWidth = visionTable.getNumber("ImageWidth", 0);

			turretRotator.set(TURRET_MOTOR_SPEED*(targetX*2/imageWidth-1));
		}catch(NullPointerException e) {
//			System.err.println("Target not found");
		}
	}

	public void fire() {
		isLaunching = true;
		timeSinceLaunchStart = System.currentTimeMillis();
	}

	public void liftUp() {
		currentScissorLiftSpeed= SCISSOR_LIFT_MOTOR_SPEED;
//		scissorLift.set(DoubleSolenoid.Value.kForward);
	}

	public void liftDown() {
		currentScissorLiftSpeed= -SCISSOR_LIFT_MOTOR_SPEED;
//		scissorLift.set(DoubleSolenoid.Value.kReverse);
	}

	private void setTurret(double speed, boolean automatic) {
		currentTurretSpeed = speed;
		isMovingAutomatically = automatic;
//		turretMotor.set(speed);
		shouldMoveRotatorOnUpdateManually = true;
		//	lifterRight.set(speed);
	}

	public void rotateLeftManual() {
		setTurret(-TURRET_MOTOR_SPEED, false);
	}

	public void rotateRightManual() {
		setTurret(TURRET_MOTOR_SPEED, false);
	}

	public void rotateLeftAutomatic() {
		setTurret(-TURRET_MOTOR_SPEED, true);
	}

	public void rotateRightAutomatic() {
		setTurret(TURRET_MOTOR_SPEED, true);
	}

	/**
	 * Updates the turret.
	 * MUST be called in teleop periodic.
	 */
	public void update() {
		//Rotator
		if(isTracking) {//Auto-tracking
			aimRotatorToTarget();
		}else{//Manual control
			if((!isMovingAutomatically && !shouldMoveRotatorOnUpdateManually) || //not moving automatically and no command to manually move turret
					((currentTurretSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentTurretSpeed && !upperLimitSwitch.get()))) {//limit switch pressed
				currentTurretSpeed = 0;
			}

			shouldMoveRotatorOnUpdateManually = false;
			turretRotator.set(currentTurretSpeed);
		}

		//Scissor lift
//		scissorLiftLeft.set(currentScissorLiftSpeed);
//		scissorLiftRight.set(currentScissorLiftSpeed);
//		
		{//Shooting wheel
			//Check firing time
			if(System.currentTimeMillis() - timeSinceLaunchStart >= 500){
				isLaunching = false;
			}

			//Fire
			if(isLaunching){
				shootingWheelMotorLeft.set(-SHOOTING_WHEEL_MOTOR_SPEED);
				shootingWheelMotorRight.set(-SHOOTING_WHEEL_MOTOR_SPEED);
			}else{
				shootingWheelMotorLeft.set(0);
				shootingWheelMotorRight.set(0);
			}
		}
	}
}
