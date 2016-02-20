
package org.usfirst.frc.team4256.robot;


import org.usfirst.frc.team4256.robot.Obstacle.Difficulty;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
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
	
	//Toggles
//	static Toggle scissorToggle = new Toggle(xboxGun, 4);
	
	//Drive
	static Drive4256 drive;
//	static CANTalon wheelFrontLeft;
//	static CANTalon wheelBackLeft;
//	static CANTalon wheelFrontRight;
//	static CANTalon wheelBackRight;
//	static RobotDrive drive = new RobotDrive(1, 2, 3, 4);
	//Launch
//	static CANTalon turretRotator = new CANTalon(7);
//	static CANTalon shootingWheel = new CANTalon(8);
	//Intake
//	static CANTalon intakeLeft = new CANTalon(5);
//	static CANTalon intakeRight = new CANTalon(6);
//	static VictorSP stagingRollerLeft = new VictorSP(0);
//	static VictorSP stagingRollerRight = new VictorSP(0);
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
	static Relay light;
	
	//DI
//	static DigitalInput stagingAreaSensor = new DigitalInput(0);
//	static DigitalInput leftIntakeBumper = new DigitalInput(1);
//	static DigitalInput rightIntakeBumper = new DigitalInput(2);
	//AI
	static Gyro4256 gyro;


	static NetworkTable visionTable;
	
	static Lifter intakeLifter;
	static Intake intake;
	static Launcher launcher;
	
	static Gamemode gamemode;
	static enum Gamemode {AUTONOMOUS, TELEOP};
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
//		visionTable = NetworkTable.getTable("SaltVision");
		
//		light = new Relay(0);
//		gyro = new Gyro4256(new AnalogInput(0));
////		
//		drive = new Drive4256(0, 1, 2, 3, new DoubleSolenoid(0, 2, 4), new DoubleSolenoid(1, 2, 4));
////		drive = new Drive4256(new RobotDrive(1, 2, 3, 4), null, null);
//		intakeLifter = new Lifter(7, 6, 000/*unknown*/, 001/*unknown*/);
////		
//		intake = new Intake(4, 5, 8, 003/*unknown*/);
//		launcher = new Launcher(7, 8, new DoubleSolenoid(5, 2, 4), visionTable);
		
		SmartDashboard.putData("AutonomousObstacles", Obstacle.autonomousObstacles);
				
		Obstacle.obstaclePosition.addObject("1", 1);
		Obstacle.obstaclePosition.addObject("2", 2);
		Obstacle.obstaclePosition.addObject("3", 3);
		Obstacle.obstaclePosition.addObject("4", 4);
		Obstacle.obstaclePosition.addObject("5", 5);
		
		SmartDashboard.putData("ObstaclePosition", Obstacle.obstaclePosition);
		
		for(int i=0; i<Obstacle.autonomusObstacleDropDowns.length; i++) {
			SmartDashboard.putData("AutonomousObstacles"+(i+1), Obstacle.autonomusObstacleDropDowns[i]);
			((Obstacle) Obstacle.autonomusObstacleDropDowns[i].getSelected()).position = i+1;
		}
		
//		SmartDashboard.putData("AutonomousObstacles1", Obstacle.autonomusObstacle1);
//		SmartDashboard.putData("AutonomousObstacles2", Obstacle.autonomusObstacle2);
//		SmartDashboard.putData("AutonomousObstacles3", Obstacle.autonomusObstacle3);
//		SmartDashboard.putData("AutonomousObstacles4", Obstacle.autonomusObstacle4);
//		SmartDashboard.putData("AutonomousObstacles5", Obstacle.autonomusObstacle5);
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
	long timeSinceLaunchStart;
	public void teleopPeriodic() {
		//gamemode = Gamemode.TELEOP;
		
		//Drive
//		drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y), xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X));
//		if(xboxDriver.getRawButton(DBJoystick.BUTTON_LB)) {
//			drive.gearShift();
//		}
		
		//Intake Lifter
		
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
//		//Gyro
//		//gyro.rotateToAngle(SmartDashboard.getNumber("goalAngle"))
//		
//		//ScissorLift
////		if(scissorToggle.getState()) {
////			scissorLift.set(DoubleSolenoid.Value.kForward);
////		}else{
////			scissorLift.set(DoubleSolenoid.Value.kReverse);
////		}
//		
     }
	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
