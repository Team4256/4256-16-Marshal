package org.usfirst.frc.team4256.robot;

public class Timer {
	public static boolean delay(final double seconds) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime <= seconds*1000) {
			if(Robot.gamemode == Robot.Gamemode.TELEOP && 
					(Robot.xboxDriver.anyControlIsActive() || Robot.xboxGun.anyControlIsActive())) {
				AutoModes.runningAutoModeInTeleop = false;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Delays unless a joystick control is activated.
	 * Returns true if the joystick has been activated.
	 * @param timeoutSeconds - the SECONDS to delay.
	 * @param controller -  the controller to check actions for.
	 * @return if the controller is active
	 */
	public static boolean teleopDelay(double timeoutSeconds, DBJoystick controller) {
		long startTime = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-startTime <= timeoutSeconds*1000) {
			if(controller.anyControlIsActive()) {
				return true;
			}
		}
		
		return false;
	}
}
