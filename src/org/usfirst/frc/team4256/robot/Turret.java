package org.usfirst.frc.team4256.robot;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class Turret {
	private static final double TURRET_MOTOR_SPEED = .2;
	
	public VictorSP turretMotor;
	
	public DigitalInput upperLimitSwitch;
	public DigitalInput lowerLimitSwitch;
	
	public double currentTurretSpeed = 0;
	public boolean isMovingAutomatically = false;
	
public Turret(int turretPort, int upperLimitSwitchPort, int lowerLimitSwitchPort){
		//Initialize motors
		turretMotor = new VictorSP(turretPort);
		
		
		//Initialize limit switches
	upperLimitSwitch = new DigitalInput(upperLimitSwitchPort);
	lowerLimitSwitch = new DigitalInput(lowerLimitSwitchPort);
		
	
	}
	
	private void set(double speed, boolean automatic) {
		currentTurretSpeed = speed;
		isMovingAutomatically = automatic;
		turretMotor.set(speed);
//		lifterRight.set(speed);
	}
	
	public void rotateLeft() {
		set(TURRET_MOTOR_SPEED, false);
	}
	
	public void rotateRight() {
		set(-TURRET_MOTOR_SPEED, false);
	}
	
//	public void liftUpAutomatic() {
//		set(TURRET_MOTOR_SPEED, true);
//	}
//	
//	public void liftDownAutomatic() {
//		set(-TURRET_MOTOR_SPEED, true);
//	}
	
	/**
	 * Updates the lifter.
	 * MUST be called before other lifter actions in teleop.
	 */
	public void update() {
		if(!isMovingAutomatically) {
			currentTurretSpeed = 0;
		}

		turretMotor.set(currentTurretSpeed);
//		Move if limit switch not active
		if((currentTurretSpeed < 0 && !lowerLimitSwitch.get()) || (0 < currentTurretSpeed && !upperLimitSwitch.get())) {
			set(currentTurretSpeed, false);
		}else{
			set(0, true);
//		}
	}
}
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

