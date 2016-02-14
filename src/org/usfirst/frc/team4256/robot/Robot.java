
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
	static Drive4256 drive;
//	static CANTalon wheelFrontLeft;
//	static CANTalon wheelBackLeft;
//	static CANTalon wheelFrontRight;
//	static CANTalon wheelBackRight;
//	static RobotDrive drive = new RobotDrive(1, 2, 3, 4);
	//Launch
	static CANTalon turretRotator = new CANTalon(7);
	static CANTalon shootingWheel = new CANTalon(8);
	//Intake
	static Lifter intakeLifter;
	static Intake intake;
//	static CANTalon intakeLeft = new CANTalon(5);
//	static CANTalon intakeRight = new CANTalon(6);
//	static VictorSP stagingRollerLeft = new VictorSP(0);
//	static VictorSP stagingRollerRight = new VictorSP(0);
//	static VictorSP intakeRoller = new VictorSP(1);
	
	//Servos
	static Servo servoX = new Servo(0);
	static Servo servoY = new Servo(1);
	static Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	//Solenoids
	static Compressor compressor = new Compressor();
	static AnalogInput PressureGauge = new AnalogInput(0);
	static DoubleSolenoid gearShift = new DoubleSolenoid(0, 2, 4);
	static DoubleSolenoid scissorLift = new DoubleSolenoid(1, 2, 4);
	
	//Relays
	static Relay light = new Relay(0);
	
	//DI
	static DigitalInput stagingAreaSensor = new DigitalInput(0);
	static DigitalInput leftIntakeBumper = new DigitalInput(1);
	static DigitalInput rightIntakeBumper = new DigitalInput(2);
	

	
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		drive = new Drive4256(new RobotDrive(1, 2, 3, 4), new DoubleSolenoid(0, 2, 4), new DoubleSolenoid(1, 3, 5));
		intakeLifter = new Lifter(7, 6, 000/*unknown*/, 000/*unknown*/);
		intake = new Intake(4, 5, 8, 000/*unknown*/);
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
		drive.arcadeDrive(xboxDriver.getRawAxis(1), xboxDriver.getRawAxis(4));
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
