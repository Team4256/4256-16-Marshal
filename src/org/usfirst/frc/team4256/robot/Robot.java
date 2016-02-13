
package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//Joysticks
	static DBJoystick xboxDriver = new DBJoystick(0);
	static DBJoystick xboxGun = new DBJoystick(1);
	
	//Drive
//	static CANTalon wheelFrontLeft;
//	static CANTalon wheelBackLeft;
//	static CANTalon wheelFrontRight;
//	static CANTalon wheelBackRight;
	static RobotDrive drive = new RobotDrive(1, 2, 3, 4);
	//Launch
	static CANTalon turretRotator = new CANTalon(7);
	static CANTalon shootingWheel = new CANTalon(8);
	//Intake
	static CANTalon intakeLeft = new CANTalon(5);
	static CANTalon intakeRight = new CANTalon(6);
	static VictorSP stagingRollerLeft = new VictorSP(0);
	static VictorSP stagingRollerRight = new VictorSP(0);
	static VictorSP intakeRoller = new VictorSP(1);
	
	//Solenoid
	static Compressor compressor;
	static DoubleSolenoid gearShift = new DoubleSolenoid(0, 2, 4);
	static DoubleSolenoid scissorLift = new DoubleSolenoid(1, 2, 4);
	
	static Relay light = new Relay(0);
	
	static DigitalInput stagingAreaSensor = new DigitalInput(0);
	static DigitalInput leftIntakeBumper = new DigitalInput(1);
	static DigitalInput rightIntakeBumper = new DigitalInput(2);
	
	static Servo servoX = new Servo(0);
	static Servo servoY = new Servo(1);
	static Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	static Toggle armToggle = new Toggle(null, 6);
	static Toggle hookToggle = new Toggle(null, 8);
//	Toggle intakeToggle = new Toggle(xboxgun, 8);
	Toggle lightToggle = new Toggle(xboxGun, 10);
	Toggle switchToggle = new Toggle(xboxDriver, 4);
	Toggle atToggle = new Toggle(xboxDriver, 9);
	//Toggle liftToggle = new Toggle(xboxgun, 4);
	
	AnalogInput PressureGauge = new AnalogInput(0);

	
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		
		cameraServos.maxY = 42;
    	//rightFront.setInversed(true);
    	//rightBack.setInversed(true);
    	PressureGauge.setAverageBits(10);
		compressor = new Compressor();
		
		SmartDashboard.getBoolean("toggle state", false);
	}

	public void autonomousInit() {

	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	long timeSinceLaunchStart;
	public void teleopPeriodic() {
		SmartDashboard.getBoolean("toggle state");
		SmartDashboard.putBoolean("toggle state", xboxGun.getRawToggle(3));
		//If button 0 pressed, motor will run at 75% speed
		//    	if(xboxGun.getRawButton(0)){
		//    		robotLauncher.fire();
		//    	}
		//    	robotLauncher.update();
		//    	
		//    	//shooter trigger 
		//    	if(xboxGun.getRawButton(1)){
		//    		trigger.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		//    		Timer.delay(.5);
		//    		trigger.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
		//    	
		//    	}
		//    		
		//    	//accumulator up 
		//    	if(xboxGun.getRawButton(2)){
		//    		accumulatorOne.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		//    		accumulatorTwo.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward);
		//  
		//    	}
		//    	//accumulator down
		//    	if(xboxGun.getRawButton(3)){
		//    		accumulatorOne.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
		//    		accumulatorTwo.set(edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse);
		//    	}
		//    	//bring ball  in 
		//    	if(xboxGun.getRawButton(4)){
		//    		intakeMotor.set(.75);
		//    
		//    	}
		//    	//bring ball out
		//    	if(xboxGun.getRawButton(5)){
		//    		intakeMotor.set(-.75);
		//    	}
	}


	//    	if(xboxGun.getRawButton(0)){
	//    		horizShooter1.set(.75);
	//    		horizShooter2.set(-.75);
	//    	}else{
	//    		horizShooter1.set(0);
	//    		horizShooter2.set(0);
	//    	}




	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
