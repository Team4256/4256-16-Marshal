
package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

Launcher robotLauncher;
	
ExtendedCANTalon horizShooter1;
ExtendedCANTalon horizShooter2;
	
DBJoystick xboxGun;

Compressor compressor;





















static DoubleSolenoid trigger;

static DoubleSolenoid accumulatorOne;
static DoubleSolenoid accumulatorTwo;
ExtendedCANTalon intakeMotor;

    public void robotInit() {
//   robotLauncher = new Launcher(0, 1); 
//   horizShooter1 = new ExtendedCANTalon(0);
//   horizShooter2 = new ExtendedCANTalon(0);
//    	
//   xboxGun = new DBJoystick(0);
//   
//   compressor = new Compressor();
//   
//   trigger = new DoubleSolenoid(0, 0, 1);
//   
//   accumulatorOne = new DoubleSolenoid(0, 0, 1); 
//   accumulatorTwo = new DoubleSolenoid(0, 0, 2); 
//   intakeMotor = new ExtendedCANTalon(0);
   			
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
