package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Turret {
	private static final double TURRET_MOTOR_SPEED = .2;
	private static final double SHOOTING_WHEEL_MOTOR_SPEED = .2;

	public VictorSP turretMotor;
	public CANTalon shootingWheelMotor;

	public DoubleSolenoid scissorLift;

	public NetworkTable visionTable;

	public DigitalInput upperLimitSwitch;
	public DigitalInput lowerLimitSwitch;

	public double currentTurretSpeed = 0;
	private boolean shouldMoveRotatorOnUpdateManually = false; //for manual mode
	public boolean isMovingAutomatically = false;
	public boolean isLaunching = false;
	public long timeSinceLaunchStart;

	public Turret(int turretPort, int shootingWheelID, int upperLimitSwitchPort, int lowerLimitSwitchPort, DoubleSolenoid scissorLift, NetworkTable visionTable){
		//Initialize motors
		turretMotor = new VictorSP(turretPort);
		shootingWheelMotor = new CANTalon(shootingWheelID);

		//		//Initialize limit switches
		upperLimitSwitch = new DigitalInput(upperLimitSwitchPort);
		lowerLimitSwitch = new DigitalInput(lowerLimitSwitchPort);
		this.scissorLift = scissorLift;
		this.visionTable = visionTable;
	}

	public void aimRotator() {//TargetX, TargetY, TargetWidth, TargetHeight, ImageWidth, ImageHeight
		try{
			double targetX = visionTable.getNumber("TargetX", 0);
			double imageWidth = visionTable.getNumber("ImageWidth", 0);

			turretMotor.set(TURRET_MOTOR_SPEED*(targetX*2/imageWidth-1));
		}catch(NullPointerException e) {
//			System.err.println("Target not found");
		}
	}

	public void fire() {
		isLaunching = true;
		timeSinceLaunchStart = System.currentTimeMillis();
	}

	public void liftUp() {
		scissorLift.set(DoubleSolenoid.Value.kForward);
	}

	public void liftDown() {
		scissorLift.set(DoubleSolenoid.Value.kReverse);
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
		if((!isMovingAutomatically && !shouldMoveRotatorOnUpdateManually) || //not moving automatically and no command to manually move turret
				((currentTurretSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentTurretSpeed && !upperLimitSwitch.get()))) {//limit switch pressed
			currentTurretSpeed = 0;
		}

		shouldMoveRotatorOnUpdateManually = false;
		turretMotor.set(currentTurretSpeed);
		aimRotator();
		
		
		if(System.currentTimeMillis() - timeSinceLaunchStart >= 500){
			isLaunching = false;
		}

		if(isLaunching){
			shootingWheelMotor.set(-SHOOTING_WHEEL_MOTOR_SPEED);
		}else{
			shootingWheelMotor.set(0);
		}
	}
}
