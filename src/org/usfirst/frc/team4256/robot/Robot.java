
package org.usfirst.frc.team4256.robot;



import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
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
	
	//Relays
	static Relay light;
    
	//AI
	static Gyro4256 gyro = new Gyro4256(new AnalogInput(0));

	//Drive
	static Drive4256 drive;	
	static CANTalon wheelFrontLeft = new CANTalon(1);
	static CANTalon wheelBackLeft = new CANTalon(2);
	static CANTalon wheelFrontRight = new CANTalon(3);
	static CANTalon wheelBackRight = new CANTalon(4);
	
	//Systems
	static Turret turret;
	static Intake intake;
	static IntakeLifter intakeLifter;
//	static Launcher launcher;


	static NetworkTable visionTable;
	
	
	static Gamemode gamemode;
	static enum Gamemode {AUTONOMOUS, TELEOP};
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		{//Robot
			visionTable = NetworkTable.getTable("SaltVision");
			SmartDashboard.putBoolean("Motor Stop", false);

			drive = new Drive4256(wheelFrontLeft, wheelFrontRight, wheelBackLeft, wheelBackRight, 
					new DoubleSolenoid(0, 0, 1), new DoubleSolenoid(0, 2, 3));
			turret = new Turret(3, 7, 7, 8, new DoubleSolenoid(1, 2, 4), visionTable);
			intake = new Intake(4, 5, 8, 003/*unknown*/);
			intakeLifter = new IntakeLifter(7,8);
		}
		
		{//SmartDashboard
			SmartDashboard.putData("AutonomousObstacles", Obstacle.autonomousObstacles);
			SmartDashboard.putData("ObstaclePosition", Obstacle.obstaclePosition);

			for(int i=0; i<Obstacle.autonomusObstacleDropDowns.length; i++) {
				Obstacle.obstaclePosition.addObject(""+(i+1), i+1);
				SmartDashboard.putData("AutonomousObstacles"+(i+1), Obstacle.autonomusObstacleDropDowns[i]);
				((Obstacle) Obstacle.autonomusObstacleDropDowns[i].getSelected()).position = i+1;
			}
		}
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
	Toggle gearShiftToggle = new Toggle(xboxDriver, DBJoystick.BUTTON_LB);
	Toggle turretElevationToggle = new Toggle(xboxGun, DBJoystick.BUTTON_LB);
	Toggle toggleScissorLift = new Toggle(xboxGun, DBJoystick.BUTTON_Y);
	Toggle autoTrackingToggle = new Toggle(xboxGun, DBJoystick.BUTTON_START);
	public void teleopPeriodic() {
		gamemode = Gamemode.TELEOP;
		
		//Update systems
		turret.update();
		intake.update();
		intakeLifter.update();

		//Drive
		{
			double speedScale = (xboxDriver.getRawButton(DBJoystick.BUTTON_RB) ? .5 : .75);
			drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y)*speedScale, xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X)*speedScale);
			drive.gearShift(gearShiftToggle.getState());
		}

		//Turret
		{
			//Rotate manual
			if(xboxGun.axisPressed(DBJoystick.AXIS_LT)){
				turret.rotateLeftManual(); 
			}else if(xboxGun.axisPressed(DBJoystick.AXIS_RT)){
				turret.rotateRightManual();
			}
			
			//Rotate automatically
			if (xboxGun.getPOV() == DBJoystick.POV_WEST){
				turret.rotateLeftAutomatic();
			}else if (xboxGun.getPOV() == DBJoystick.POV_EAST){
				turret.rotateRightAutomatic();
			}
			
			//Shooter angle shift
			if (turretElevationToggle.getState()){
				turret.liftUp();
			}else {
				turret.liftDown();
			}
			
			//Toggle scissor lift
			if (toggleScissorLift.getState()){
				turret.liftUp();
			}else{
				turret.liftDown();
			}
			
			//Auto tracking toggle
			turret.isTracking = autoTrackingToggle.getState();
		}

		//Intake Lifter
		{
			//Lift up or down
			if(xboxDriver.axisPressed(DBJoystick.AXIS_LT)){
				intakeLifter.liftUpManual();
			}else if(xboxDriver.axisPressed(DBJoystick.AXIS_RT)){
				intakeLifter.liftDownManual();
			}else if (xboxDriver.getPOV() == DBJoystick.POV_SOUTH) {
				intakeLifter.liftDownAutomatic();
			}
		}
		
		//Intake
		{
			//Fire in high or low goal
			if (xboxGun.getRawButton(DBJoystick.BUTTON_RB)){
				intake.fireHigh();
			}else if (xboxGun.getRawButton(DBJoystick.BUTTON_X)){
				intake.fireLow();
			}
			
			//Intake ball
			if (xboxGun.getRawButton(DBJoystick.BUTTON_A)){
				intake.intakeIn();
			}
		}
		
		//SensorStop
//		if(stagingAreaSensor.get()){
//			stagingRollerLeft.set(0);
//			stagingRollerRight.set(0);
//		}
//		else {
//			stagingRollerLeft.set(1);
//			stagingRollerRight.set(1);
//		}
//		SmartDashboard.putBoolean("Motor Stop", stagingAreaSensor.get());
		
	}


	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
