package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Launcher {
	private final static double TURRET_MAX_SPEED = .5;
	private int direction = 1;
	
	public DBJoystick xboxGun;
	public CANTalon turretRotator;
	public CANTalon shootingWheel;
	
	public DoubleSolenoid scissorLift;	
	
	public NetworkTable visionTable;
	
	public boolean isInManualMode = false;
	public boolean isLaunching = false;
	public long timeSinceLaunchStart;
    
	public Launcher(int turretRotatorPort, int shootingWheelPort, DoubleSolenoid scissorLift, NetworkTable visionTable) {
		turretRotator = new ExtendedCANTalon(turretRotatorPort);
		shootingWheel = new ExtendedCANTalon(shootingWheelPort);
		this.scissorLift = scissorLift;
		this.visionTable = visionTable;
	}
	
	
	public void liftUp() {
		scissorLift.set(DoubleSolenoid.Value.kForward);
	}
	
	public void liftDown() {
		scissorLift.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void clockwiseTurret(){
		turretRotator.set(TURRET_MAX_SPEED);
	}
	
	public void counterClockwiseTurret(){
		turretRotator.set(TURRET_MAX_SPEED);
	}
	public void turretStop () {
		turretRotator.set(0);
	}
	
	
	public void aimRotator() {//TargetX, TargetY, TargetWidth, TargetHeight, ImageWidth, ImageHeight
		double targetX = visionTable.getNumber("TargetX", 0);
		double imageWidth = visionTable.getNumber("ImageWidth", 0);
		
		turretRotator.set(TURRET_MAX_SPEED*(targetX*2/imageWidth-1));
	}
	
	public void fire() {
		isLaunching = true;
		timeSinceLaunchStart = System.currentTimeMillis();
	}
	
	public void update() {
		if(!isInManualMode) {
			aimRotator();
		}
		
		if(System.currentTimeMillis() - timeSinceLaunchStart >= 500){
    		isLaunching = false;
    	}
		
		if(isLaunching){
    		shootingWheel.set(-.75);
    	}else{
    		shootingWheel.set(0);
    	}
	}
}
