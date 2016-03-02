
package org.usfirst.frc.team4256.robot;



import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DoubleSole2noid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.SerialPort;
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

	//Compressor
	static Compressor compressor = new Compressor();
	
	//Relays
	static Relay light;
	
	//Limit Switch
//	static DigitalInput portcullisLimitSwitch = new DigitalInput(0);
    
	//AI
	//static Gyro4256 gyro = new Gyro4256(new AnalogInput(0));
	static NavaxGyro gyro = new NavaxGyro(SerialPort.Port.kMXP, 90);

	//Drive
	static Drive4256 drive;	
	static CANTalon wheelFrontLeft = new CANTalon(11);
	static CANTalon wheelBackLeft = new CANTalon(12);
	static CANTalon wheelFrontRight = new CANTalon(13);
	static CANTalon wheelBackRight = new CANTalon(14);
	
	static CANTalon intakeLifterLeft = new CANTalon(18);
	static CANTalon intakeLifterRight = new CANTalon(19);
	
	static CANTalon shooterLeft = new CANTalon(21);
	static CANTalon shooterRight = new CANTalon(20);
	
	//Systems
//	static Turret turret;
	
	static Intake intake;
	static CANTalon turret = new CANTalon(15);
	static IntakeLifter intakeLifter;
	static Turret shooter;
 

	static NetworkTable visionTable;
	
	
	static Gamemode gamemode;
	static enum Gamemode {AUTONOMOUS, TELEOP};
	
//	static DigitalInput stagingLimitSwitch = new DigitalInput(0);
	
	static CameraServer camera = CameraServer.getInstance();
	
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		{//Robot
			visionTable = NetworkTable.getTable("SaltVision");
			light = new Relay(0);
			SmartDashboard.putBoolean("Motor Stop", false);
			//Slave 
			wheelBackLeft.changeControlMode(CANTalon.TalonControlMode.Follower);
			wheelBackLeft.set(wheelFrontLeft.getDeviceID());

			wheelBackRight.changeControlMode(CANTalon.TalonControlMode.Follower);
			wheelBackRight.set(wheelFrontRight.getDeviceID());

			drive = new Drive4256(wheelFrontLeft, wheelFrontRight, wheelBackLeft, wheelBackRight, 
					new DoubleSolenoid(0, 0, 1), new DoubleSolenoid(0, 2, 3));
			//			turret = new Turret(5, 10, 11, 3, 4, 
			//					6, 7, visionTable);
			intake = new Intake(0, 5, 8, 0);
			
//			shooterLeft.changeControlMode(CANTalon.TalonControlMode.Follower);
//			shooterLeft.set(shooterRight.getDeviceID());
			
//			shooter = new Turret(0,0,0,0,0,visionTable);


			intakeLifter = new IntakeLifter(intakeLifterLeft, intakeLifterRight);
		}
		camera.setQuality(100);
		camera.startAutomaticCapture("cam1");
		

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
		AutoModes.test();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
//	int autoMode =  (int) SmartDashboard.getNumber("AutonomousObstacles");
//	switch (autoMode) {
//	case 0:
//		Obstacle.getStartingObstacle().crossBarrier(1);
//		}
// will have to do case for each Obstacle?
	}

	/**
	 * This function is called periodically during operator control
	 */
	Toggle gearShiftToggle = new Toggle(xboxDriver, DBJoystick.BUTTON_LB);
	Toggle turretElevationToggle = new Toggle(xboxGun, DBJoystick.BUTTON_LB);
	Toggle toggleScissorLift = new Toggle(xboxGun, DBJoystick.BUTTON_Y);
	Toggle autoTrackingToggle = new Toggle(xboxGun, DBJoystick.BUTTON_START);
	//Toggle intakeInToggle = new Toggle (xboxGun, DBJoystick.BUTTON_A);
	Toggle shooterToggle = new Toggle (xboxGun, DBJoystick.AXIS_RT);
	Toggle shooterOverideToggle = new Toggle (xboxGun, DBJoystick.BUTTON_Y);
	public void teleopPeriodic() {
		gamemode = Gamemode.TELEOP;
		
		//Update systems
//		turret.update();
		light.set(Value.kForward);
		intake.update();
		intakeLifter.update();

		//Drive
		{
			double speedScale = (xboxDriver.getRawButton(DBJoystick.BUTTON_RB) ? .5 : 1.0);
			drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y)*speedScale, xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X)*speedScale);
			drive.gearShift(gearShiftToggle.getState());
		}
		

		//Turret
		{
//			//Rotate manual
//			if(xboxGun.axisPressed(DBJoystick.AXIS_LT)){
//				turret.rotateLeftManual(); 
//			}else if(xboxGun.axisPressed(DBJoystick.AXIS_RT)){
//				turret.rotateRightManual();
//			}
//			//Rotate manual (CAN)
//			if(xboxGun.axisPressed(DBJoystick.AXIS_LT)){
//				turret.set(-0.3); 
//			}else if(xboxGun.axisPressed(DBJoystick.AXIS_RT)){
//				turret.set(0.3);
//			}
			
//			//Rotate automatically
//			if (xboxGun.getPOV() == DBJoystick.POV_WEST){
//				turret.rotateLeftAutomatic();
//			}else if (xboxGun.getPOV() == DBJoystick.POV_EAST){
//				turret.rotateRightAutomatic();
//			}
			
			
			//Shooter angle shift
//			if (turretElevationToggle.getState()){
//				turret.liftUp();
//			}else {
//				turret.liftDown();
//			}
//			
//			//Toggle scissor lift
//			if (toggleScissorLift.getState()){
//				turret.liftUp();
//			}else{
//				turret.liftDown();
//			}
			if(shooterToggle.getState()) {
				shooterLeft.set(0.5);
			} else {
				shooterLeft.set(0);
			}
//			
//			//Auto tracking toggle
//			turret.isTracking = autoTrackingToggle.getState();
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
			if(xboxGun.getRawButton(DBJoystick.BUTTON_LB)) {
				shooterRight.set(0.5);
				shooterLeft.set(0.5);
			} else {
				shooterRight.set(0);
				shooterLeft.set(0);
			}
		}
		
		{//Intake
			//in and out
			if (xboxGun.getRawButton(DBJoystick.BUTTON_A)){
				intake.intakeIn();
				shooterLeft.set(.3);
				
			}else if (xboxGun.getRawButton(DBJoystick.BUTTON_X)){
				intake.intakeOut();
				shooterLeft.set(0);
			
				
			//high shot
			}else if (shooterToggle.getState()) {
				intake.loadTurret();
				shooterLeft.set(.5);
				//should have code in here so that it finishes no matter what (unless override is run), even if toggle state becomes false
				//should also have code in here that automatically makes the toggle state false when action is complete
				
			//shot override ** need to make so that state only gets changed if we are in the process of shooting. once shooting gets finished, this toggle needs to be reset to true
			//THIS MAY CAUSE PROBLEMS TONIGHT. COMENT OUT OVERRIDE IF ISSUES OCCUR
			}else if (shooterOverideToggle.getState()) {
				shooterLeft.set(.5);
			}else if (!shooterOverideToggle.getState()) {
				shooterLeft.set(0);
			
			}else{
				intake.stop();
				
				if (intake.currentAction != Intake.State.intake) {
					shooterLeft.set(0);
				}
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
		
		//Portcullis LimitSwitch
//		if(portcullisLimitSwitch.get()) {
//			//IntakeLifter is set to it's current state and speed
//			
//		}else{
//			intakeLifter.liftUpAutomatic();
//			AutoModes.moveForwardForTime(0, 0);
//			//drive forward autonomous function
//		}
//		
	}

	//Automated teleop variables
	public static AutomatedTeleopState automatedTeleopStage = AutomatedTeleopState.collectBall;
	public static enum AutomatedTeleopState {
			collectBall,
			crossBarrier,
			fire,
			returnToNeutralZone
		}
	
	/**
	 * This function should be toggled on button press to enter an autonomous loop when in teleop
	 */
	public void teleopAutomated() {
		//TODO exit if controller activated
		//Must be able to enter loop at any time
		//This can be done with an *enum for location, and *boolean declaring if the robot contains a ball
		
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
