
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
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
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
	static DoubleSolenoid flinger = new DoubleSolenoid(0, 5, 6); // TODO
	
	//Relays
	static Relay light;
    
	//AI
	static NavaxGyro gyro = new NavaxGyro(SerialPort.Port.kMXP);

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
	
	static CANTalon climbingWinchLeft = new CANTalon(15);
	static CANTalon climbingWinchRight = new CANTalon(16);
	
	
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
	
	static TargetPID targetPID;
	int climbingSafety = 0;
	
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
			climbingMech = new ClimbingMech(climbingWinchLeft, climbingWinchRight, flinger);
			//PID
			targetPID = new TargetPID("Target PID", 0.8, 0.01, 5.0);
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
			SmartDashboard.putNumber("Goal", 2);
			SmartDashboard.putString("AUTONOMOUS MODE","AUTONOMOUS MODE");
			for(int i=0; i<Obstacle.autonomusObstacleDropDowns.length; i++) {
				Obstacle.obstaclePosition.addObject(""+(i+1), i+1);
				//SmartDashboard.putData("AutonomousObstacles"+(i+1), Obstacle.autonomusObstacleDropDowns[i]);
				//((Obstacle) Obstacle.autonomusObstacleDropDowns[i].getSelected()).position = i+1;
			}
		}
		climbingMech.grabMech();
	}

	private static boolean autonomousThreadRunning = false;
//	ALL AUTONOMOUS SPEEDS ARE INVERTED (ie. - IS FORWARD & + POSITIVE IS BACKWARDS) *SALTY DOES NOT KNOW WHY
	public void autonomousInit() {
		if(!autonomousThreadRunning) {
			AutoModes.exeSrvc.execute(new Runnable() {
				@Override
				public void run() {
					AutoModes.test();
//					long startTime = System.currentTimeMillis();
//					AutoModes.start();
//					SmartDashboard.putNumber("Auto Run Time", System.currentTimeMillis()-startTime);
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
		shifterToggle_Driver.state = true;
		intake.currentAction = Intake.State.nothing;
		climbingMech.grabMech();
//		Robot.drive.enableBreakMode(false);
	}

	/**
	 * This function is called periodically during operator control
	 */
	static Toggle shifterToggle_Driver = new Toggle(xboxDriver, DBJoystick.BUTTON_LB);
	Toggle shotMotorToggle_Gunner = new Toggle(xboxGun, DBJoystick.BUTTON_Y);
	Toggle shotAngleToggle_Gunner = new Toggle(xboxGun, DBJoystick.AXIS_LT, false);
	Toggle climbToggle_Gunner = new Toggle(xboxGun, DBJoystick.BUTTON_START);
	
	public void teleopPeriodic() {
		gamemode = Gamemode.TELEOP;
		light.set(Value.kForward);
		intake.update();
		intakeLifter.update();
		SmartDashboard.putNumber("Elevation", gyro.getElevation());
		SmartDashboard.putNumber("Angle", gyro.getRawAngle());
		SmartDashboard.putNumber("Goal Distance", Robot.visionTable.getNumber("TargetDistance", 0));
		SmartDashboard.putNumber("Goal Angle", Robot.visionTable.getNumber("AngleDifferential", 0));	
		
		{//drive
			double speedScale = (xboxDriver.getRawButton(DBJoystick.BUTTON_RB) ? .5 : 1.0);
			drive.arcadeDrive(xboxDriver.getRawAxis(DBJoystick.AXIS_LEFT_Y)*speedScale, .75*xboxDriver.getRawAxis(DBJoystick.AXIS_RIGHT_X)*speedScale);
			drive.gearShift(shifterToggle_Driver.getState());
		}

		{//shooting
			if(xboxGun.getRawButton(DBJoystick.BUTTON_LB)) {//toggle old alignment
				drive.alignToTarget();
			}
			if(xboxGun.getRawButton(DBJoystick.BUTTON_RB)) {//run new alignment
	    		targetPID.enable();
				drive.arcadeDrive(0.0, targetPID.getOutput());//TODO have it get in range automatically using inches
	    	}
			
			if(shotMotorToggle_Gunner.getState()) {//toggle shot motors
				shooter.start();
				SmartDashboard.putString("Shooter Wheels", "Spinning");
			}else{
				shooter.stop();
				SmartDashboard.putString("Shooter Wheels", "Stopped");
			}
			
			if(shotAngleToggle_Gunner.getState()) {//toggle shot angle
				shooter.raise();
				SmartDashboard.putString("Shooter Position", "Up");
			}else{
				shooter.lower();
				SmartDashboard.putString("Shooter Position", "Down");
			}
		}

		{//intake lifter
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
		
		{//intake
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
		
		{//climbing
			if (xboxGun.getRawButton(DBJoystick.BUTTON_BACK)) {//count the number of times that back has been pressed
				climbingSafety++;
			}
			if (climbingSafety == 2) {//if I have hit it twice, then release the mechanism and get ready to climb
				climbingMech.startClimbing();
			}
			if (climbingSafety > 2 && DBJoystick.viscousize("climbing piston", xboxGun.getRawButton(DBJoystick.BUTTON_BACK), 500)) {
				climbingMech.releaseMech();//after the first two presses, the piston will remain out while the button is held down, then go back in after 500ms
			}else if (climbingSafety > 2){
				climbingMech.grabMech();
			}
			if (climbToggle_Gunner.getState()) {//reel it in
				climbingMech.raiseHook();
			}else{
				climbingMech.stopHook();
			}
			//TODO need a way to toggle the "start climbing" solenoid while in the pit
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
    	LiveWindow.run();
    	SmartDashboard.putBoolean("Pid State", xboxGun.getRawButton(DBJoystick.BUTTON_RB));
    	
    	if(xboxGun.getRawButton(DBJoystick.BUTTON_RB)) {
    		targetPID.enable();
			double power = targetPID.getOutput();//*(getTargetOffset() < 0 ? -1 : 1);
			SmartDashboard.putNumber("Power", power);
			drive.arcadeDrive(0.0, power);
    	}
	}

}
