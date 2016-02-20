
package org.usfirst.frc.team4256.robot;


import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.DoubleSolenoid;

//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;

import edu.wpi.first.wpilibj.RobotDrive;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
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
	static Lifter robotIntake = new Lifter(7, 8);
	static Turret robotTurret = new Turret(3, 8, 9);

	//Toggles
//	static Toggle scissorToggle = new Toggle(xboxGun, 4);
	
	//Drive
	static Drive4256 drive;

	static CANTalon wheelFrontLeft = new CANTalon(1);
	static CANTalon wheelBackLeft = new CANTalon(2);
	static CANTalon wheelFrontRight = new CANTalon(3);
	static CANTalon wheelBackRight = new CANTalon(4);
//	static CANTalon lifterTest = new CANTalon(7);
	//	static RobotDrive drive = new RobotDrive(1, 2, 3, 4);

	//Launch
//	static CANTalon turretRotator = new CANTalon(7);
//	static CANTalon shootingWheel = new CANTalon(8);
	//Intake
//	static CANTalon intakeLeft = new CANTalon(5);
//	static CANTalon intakeRight = new CANTalon(6);
	static VictorSP stagingRollerLeft = new VictorSP(0);
	static VictorSP stagingRollerRight = new VictorSP(2);
//	static VictorSP intakeRoller = new VictorSP(1);
	
	//Servos
//	static Servo servoX = new Servo(0);
//	static Servo servoY = new Servo(1);
//	static Camera cameraServos = new Camera(servoX, servoY, (int)6.9);
	
	//Solenoids
//	static Compressor compressor = new Compressor();
//	static AnalogInput PressureGauge = new AnalogInput(0);
//	static DoubleSolenoid gearShift = new DoubleSolenoid(0, 2, 4);
//	static DoubleSolenoid scissorLift = new DoubleSolenoid(1, 2, 4);
	
	//Relays
	static Relay light = new Relay(0);
	
	//DI

    static DigitalInput stagingAreaSensor = new DigitalInput(1);

//	static DigitalInput leftIntakeBumper = new DigitalInput(0);
//	static DigitalInput rightIntakeBumper = new DigitalInput(2);
	//AI
	static Gyro4256 gyro = new Gyro4256(new AnalogInput(0));


	static NetworkTable visionTable;
	
	static Lifter intakeLifter;
	static Intake intake;
	static Launcher launcher;
	
	static Gamemode gamemode;
	static enum Gamemode {AUTONOMOUS, TELEOP};
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		visionTable = NetworkTable.getTable("SaltVision");
		SmartDashboard.putBoolean("Motor Stop", false);

		drive = new Drive4256(wheelFrontLeft, wheelFrontRight, wheelBackLeft, wheelBackRight, new DoubleSolenoid(0, 0, 1), new DoubleSolenoid(0, 2, 3));

////		drive = new Drive4256(new RobotDrive(1, 2, 3, 4), null, null);
//		intakeLifter = new Lifter(7, 6, 000/*unknown*/, 001/*unknown*/);
////		
//		intake = new Intake(4, 5, 8, 003/*unknown*/);
//		launcher = new Launcher(7, 8, new DoubleSolenoid(5, 2, 4), visionTable);


		intakeLifter = new Lifter(7,8);

		
	}

	public void autonomousInit() {
		gamemode = Gamemode.AUTONOMOUS;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	Toggle t = new Toggle(xboxDriver, DBJoystick.BUTTON_LB);
	long timeSinceLaunchStart;
	public void teleopPeriodic() {

		
		//Drive
//		drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y), xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X));
//		if(xboxDriver.getRawButton(DBJoystick.BUTTON_LB)) {
//			drive.gearShift();
//		}
		
		//Intake Lifter

		intakeLifter.update();
		gamemode = Gamemode.TELEOP;
		
		if(xboxGun.axisPressed(DBJoystick.AXIS_LT)){
			robotTurret.rotateLeft(); 
		}
		if(xboxGun.axisPressed(DBJoystick.AXIS_RT)){
			robotTurret.rotateRight();
		}
		
		if(xboxDriver.axisPressed(DBJoystick.AXIS_LT)){
			robotIntake.lifterLeft.set(0.2); 
		}
		if(xboxDriver.axisPressed(DBJoystick.AXIS_RT)){
			robotIntake.lifterLeft.set(-0.2);
		}
		//Drive
		double speedScale = (xboxDriver.getRawButton(DBJoystick.BUTTON_RB) ? .5 : .75);
		drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y)*speedScale, xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X)*speedScale);

		
//		if(xboxDriver.getRawToggle(DBJoystick.BUTTON_LB)) {
//			drive.gearShift();
//		}
//		if(t.getState()){
//		}

		drive.gearShift(t.getState());
		
		//xboxDriver.updateToggle();
		//Intake Lifter
		if (xboxDriver.getRawAxis(DBJoystick.AXIS_LT) > .5) {
			intakeLifter.liftDownManual();
		}
		if (xboxDriver.getRawAxis(DBJoystick.AXIS_RT) > .5) {
			intakeLifter.liftUpManual();
		}
		if (xboxDriver.getPOV() == DBJoystick.SOUTH) {
			intakeLifter.liftDownAutomatic();
		}

		//Intake
//		if(xboxGun.getRawButton(DBJoystick.BUTTON_RB)) {
//			//stager up
//		}
//		if(xboxGun.getRawButton(DBJoystick.BUTTON_A)) {
//			intake.intakeIn();
//		}else if(xboxGun.getRawButton(DBJoystick.BUTTON_X)) {
//			intake.intakeOut();
//		}
//		
//		//Launcher

//		if(xboxGun.getRawButton(DBJoystick.AXIS_RT)) {
//			launcher.clockwiseTurret();
//		}else if(xboxGun.getRawButton(DBJoystick.AXIS_LT)) {
//			launcher.counterClockwiseTurret();
//		}else{
//			launcher.turretStop();
//		}
		//Gyro
		//gyro.rotateToAngle(SmartDashboard.getNumber("goalAngle"))
		
		//ScissorLift
//		if(scissorToggle.getState()) {
//			scissorLift.set(DoubleSolenoid.Value.kForward);
//		}else{
//			scissorLift.set(DoubleSolenoid.Value.kReverse);
		
		//SensorStop
		if(stagingAreaSensor.get()){
			stagingRollerLeft.set(0);
			stagingRollerRight.set(0);
		}
		else {
			stagingRollerLeft.set(1);
			stagingRollerRight.set(1);
		}
		SmartDashboard.putBoolean("Motor Stop", stagingAreaSensor.get());
		SmartDashboard.putString("test", "test");
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
