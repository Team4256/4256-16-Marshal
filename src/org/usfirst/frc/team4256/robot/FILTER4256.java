package org.usfirst.frc.team4256.robot;

import java.util.HashMap;
import java.util.Map;

public class FILTER4256 {//things that are used to aid the driver, yet are not specific to a physical object
	private static Map<String, Boolean> previousStates = new HashMap<String, Boolean>();
	public static Map<String, Boolean> toggleStates = new HashMap<String, Boolean>();
	private static Map<String, Long> stickyTimes = new HashMap<String, Long>();
	private static float rotateAngle = 0;
	private static double rotateValue = 0.0;
	private static double rotateIncrement = 0.0;
	private static boolean previousStateR = false;
	
	public static boolean toggleize(final String key, final boolean currentState) {
		if (previousStates.get(key) == null) {
			previousStates.put(key, false);
		}if (toggleStates.get(key) == null) {
			toggleStates.put(key, false);
		}if (currentState && (currentState != previousStates.get(key))) {
			boolean toggleBool = !toggleStates.get(key);
			toggleStates.replace(key, toggleBool);
		}
		previousStates.replace(key, currentState);
		return toggleStates.get(key);
	}
	
	public static boolean viscousize(final String key, final boolean currentState, final double timeoutMS) {
		if (previousStates.get(key) == null) {
			previousStates.put(key, false);
		}if (currentState && (currentState != previousStates.get(key))) {
			stickyTimes.putIfAbsent(key, System.currentTimeMillis());
			stickyTimes.replace(key, System.currentTimeMillis());
		}
		previousStates.replace(key, currentState);
		if (stickyTimes.get(key) != null) {
			return System.currentTimeMillis() - stickyTimes.get(key) <= timeoutMS;
		}else {
			return false;
		}
	}
	/**
	 * This should take the place of arcadeDrive. Therefore, the move and rotate values should match what would otherwise be used for that.
	 * When enable becomes true, it will set the locked heading to the current heading.
	 * While enable remains true, it will adjust rotation speed to maintain the locked heading.
	 * Do not run this in the same loop under the same conditions as other functions that affect rotation speed.
	**/
	public static void headingCorrection(final double moveValue, final double rotateValue, final boolean enable) {
		if (enable) {
			Robot.drive.arcadeDriveNoSquare(moveValue*moveValue, rotate(enable, Robot.gyro.getCurrentAngle(), 0.5, 2.0));//TODO adjust these values experimentally
		}else {
			Robot.drive.arcadeDrive(moveValue, rotateValue);
		}
	}
	/**
	 * This returns the rotation value necessary to turn to the goalAngle based on acceleration calculations and data from the gyrometer.
	 * theoreticalTimeS is the time in seconds which is used in calculations, and tolerance is the accuracy of the turn in degrees.
	 * Do not run this in the same loop under the same conditions as other functions that affect rotation speed.
	 * Make sure to use the unsquared version of arcadeDrive.
	**/
	public static double rotate(final boolean enable, final float goalAngle, final double theoreticalTimeS, final double tolerance) {
		if (enable) {
			if (!previousStateR) {
				rotateAngle = goalAngle;
				rotateValue = 0.0;//TODO could be Math.signum(a or path)*minimumRotationSpeed
				rotateIncrement = 0.0;
			}
			if (Math.abs((double)Robot.gyro.getCurrentPath(rotateAngle)) > Math.abs(tolerance)) {
				double path = (double)Robot.gyro.getCurrentPath(rotateAngle);
				double a = 4.0*path/Math.pow(theoreticalTimeS, 2.0);//TODO do calculations that take into account my current speed rather than Vi of 0
				if (Math.abs((double)Robot.gyro.getCurrentPath(rotateAngle)) <= Math.abs(path)/2.0) {
					a = -a;
					rotateIncrement = -rotateIncrement;
				}
				if (Math.abs(Robot.gyro.getAcceleration()) - Math.abs(a) < -5.0) {
					rotateIncrement += Math.signum(a)*0.05;//TODO adjust this value experimentally
				}else if (Math.abs(Robot.gyro.getAcceleration()) - Math.abs(a) > 5.0) {
					rotateIncrement += Math.signum(-a)*0.05;
				}rotateValue += rotateIncrement;
			}
		}
		previousStateR = enable;
		return rotateValue;
	}
	
	public static double getCurrentPath_Motor(final float goalAngle) {
		double motorValue = (double)Robot.gyro.getCurrentPath(goalAngle)/90;
		motorValue = Math.abs(motorValue) > 1 ? Math.signum(motorValue)*1 : motorValue;
		return motorValue;
	}
}