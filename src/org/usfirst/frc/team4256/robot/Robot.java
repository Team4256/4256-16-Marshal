
package org.usfirst.frc.team4256.robot;



import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
	
	//Solenoid
	static DoubleSolenoid turretLifter = new DoubleSolenoid(0, 2, 3);
	static DoubleSolenoid winchStop = new DoubleSolenoid(0, 5, 6); // TODO
	
	//Relays
	static Relay light;
    
	//AI
	static Gyrometer4256 gyro = new Gyrometer4256(0, 0);

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
	
	static CANTalon lifterWinch = new CANTalon(15);
	
	
	//Systems
	static Intake intake;
//	static CANTalon turret = new CANTalon(15);
	static IntakeLifter intakeLifter;
	static Launcher shooter;
//	static Turret shooter;
	static ClimbingMech climbingMech;
 

	static NetworkTable visionTable;
	
	static Gamemode gamemode;
	static enum Gamemode {AUTONOMOUS, TELEOP};
	
	static DigitalInput frontLimitSwitch = new DigitalInput(2);
	
	static CameraServer camera = CameraServer.getInstance();  
	
	
	
	//static Launcher robotLauncher;
	
	public void robotInit() {
		{//Robot
			light = new Relay(0);
			visionTable = NetworkTable.getTable("SaltVision");
			
			drive = new Drive4256(wheelFrontLeft, wheelFrontRight, wheelBackLeft, wheelBackRight, 
					new DoubleSolenoid(0, 0, 1));
			shooter = new Launcher(shooterLeft, shooterRight, turretLifter);
			intake = new Intake(0, 5, 8, 0);
			intakeLifter = new IntakeLifter(intakeLifterLeft, intakeLifterRight, frontLimitSwitch);
			climbingMech = new ClimbingMech(lifterWinch, winchStop);
		}
		
		{//Camera
			camera.setQuality(100);
			camera.startAutomaticCapture("cam1");
		}
		
		{//SmartDashboard
			//SmartDashboard.putData("AutonomousObstacles", Obstacle.autonomousObstacles);
			//SmartDashboard.putData("ObstaclePosition", Obstacle.obstaclePosition);
			SmartDashboard.putNumber("AUTONOMOUS MODE", 103);
			SmartDashboard.putNumber("Position", 3);
			SmartDashboard.putNumber("NumberOfBalls", 1);
			SmartDashboard.putString("AUTONOMOUS MODE","AUTONOMOUS MODE");
			for(int i=0; i<Obstacle.autonomusObstacleDropDowns.length; i++) {
				Obstacle.obstaclePosition.addObject(""+(i+1), i+1);
				//SmartDashboard.putData("AutonomousObstacles"+(i+1), Obstacle.autonomusObstacleDropDowns[i]);
				//((Obstacle) Obstacle.autonomusObstacleDropDowns[i].getSelected()).position = i+1;
			}
		}
	}

	private static boolean autonomousThreadRunning = false;
//	ALL AUTONOMOUS SPEEDS ARE INVERTED (ie. - IS FORWARD & + POSITIVE IS BACKWARDS) *SALTY DOES NOT KNOW WHY
	public void autonomousInit() {
		if(!autonomousThreadRunning) {
			AutoModes.exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
//					AutoModes.test();
					long startTime = System.currentTimeMillis();
					AutoModes.start();
					SmartDashboard.putNumber("Auto Run Time", System.currentTimeMillis()-startTime);
				}});
		}
	}

	

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
//		AutoModes.test();
		
	}
	
	public void teleopInit() {
		Robot.drive.fastGear();
		gearShiftToggle.state = true;
		intake.currentAction = Intake.State.nothing;
//		Robot.drive.enableBreakMode(false);
	}

	/**
	 * This function is called periodically during operator control
	 */
	static Toggle gearShiftToggle = new Toggle(xboxDriver, DBJoystick.BUTTON_LB);
	Toggle turretElevationToggle = new Toggle(xboxGun, DBJoystick.BUTTON_LB);
	//Toggle toggleScissorLift = new Toggle(xboxGun, DBJoystick.BUTTON_Y);
	Toggle autoTrackingToggle = new Toggle(xboxGun, DBJoystick.BUTTON_START);
	//Toggle intakeInToggle = new Toggle (xboxGun, DBJoystick.BUTTON_A);
	Toggle shooterToggle = new Toggle(xboxGun, DBJoystick.BUTTON_Y);
	
	//Toggle lockHeading = new Toggle(xboxDriver, DBJoystick.BUTTON_Y);
	Toggle turretLifterToggle = new Toggle(xboxGun, DBJoystick.AXIS_LT, false);
	
	public void teleopPeriodic() {
		gamemode = Gamemode.TELEOP;
		SmartDashboard.putNumber("Current Tilt", gyro.getElevation());
		SmartDashboard.putNumber("Current Angle", gyro.getCurrentAngle());
		//Update systems
		light.set(Value.kForward);
		intake.update();
		intakeLifter.update();


		//Drive
		{
			drive.gearShift(gearShiftToggle.getState());
			double speedScale = (xboxDriver.getRawButton(DBJoystick.BUTTON_RB) ? .5 : 1.0);
			FILTER4256.headingCorrection(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y)*speedScale, .75*xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X)*speedScale, xboxDriver.getRawButton(DBJoystick.BUTTON_Y));
		}
		
		
		//Turret
		{
			//SmartDashboard.putBoolean("Are we in range?", Math.abs(Robot.visionTable.getNumber("TargetDistance", 0) - 112) < 8);
			if (xboxGun.getRawButton(DBJoystick.BUTTON_LB)) {
				drive.alignToTarget();
			}
			POLISH4256.shotAlignment(2.0, 2.0, xboxGun.getRawButton(DBJoystick.BUTTON_RB));
			
			//Toggle shooter motors
			if(shooterToggle.getState()) {
				shooter.start();
				SmartDashboard.putString("Shooter Wheels", "Spinning");
			}else{
				shooter.stop();
				SmartDashboard.putString("Shooter Wheels", "Stopped");
			}
			
			//Raise/lower shooter
			if(turretLifterToggle.getState()) {
//			if(xboxGun.getAxisToggleState(DBJoystick.AXIS_LT)) {
				shooter.raise();
				SmartDashboard.putString("Shooter Position", "Up");
			}else{
				shooter.lower();
				SmartDashboard.putString("Shooter Position", "Down");
			}
		}

		//Intake Lifter
		{
			//Lift up or down
			if(xboxDriver.axisPressed(DBJoystick.AXIS_LT)){
				intakeLifter.liftDownManual();
			}else if(xboxDriver.axisPressed(DBJoystick.AXIS_RT)){
				intakeLifter.liftUpManual();
			}else if (xboxDriver.getPOV() == DBJoystick.POV_NORTH) {
				intakeLifter.liftUpAutomatic();
			}else if (xboxDriver.getPOV() == DBJoystick.POV_SOUTH) {
				intakeLifter.liftDownAutomatic();
			}
			
			if(intakeLifter.frontLimitSwitch.get()) {
				SmartDashboard.putBoolean("Front limit", false);
			}else{
				SmartDashboard.putBoolean("Front limit", true);
			}
		}
		
		{//Intake
			//Intake in/out/fire/stop
			if (xboxGun.getRawButton(DBJoystick.BUTTON_A)){
				intake.intakeIn();
			}else if (xboxGun.getRawButton(DBJoystick.BUTTON_X)){
				intake.intakeOut();
			}else if (xboxGun.axisPressed(DBJoystick.AXIS_RT)){
				intake.loadTurret();
			}else{
				intake.stop();
			}
		}
		
		{//Climbing Mech
			//Start climbing mode
			if (xboxGun.getRawButton(DBJoystick.BUTTON_BACK)) {
				climbingMech.startClimbing();
			}
			
			//Climb
			if (climbingMech.isActive) {
				climbingMech.moveHook(xboxGun.getRawAxis(DBJoystick.AXIS_LEFT_Y));
			}
		}
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
