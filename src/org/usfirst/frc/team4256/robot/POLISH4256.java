package org.usfirst.frc.team4256.robot;

public class POLISH4256 {//things that are used regardless of the driver, yet are not specific to a physical object
	
	public static void shotAlignment(final double theoreticalTimeS, final double tolerance, final boolean enable) {
		final float goalAngle = Robot.gyro.getCurrentAngle() + (float)Robot.visionTable.getNumber("AngleDifferential", 0.0);
		Robot.drive.arcadeDriveNoSquare(0, FILTER4256.rotate(enable, goalAngle, theoreticalTimeS, tolerance));
	}
}
//TODO current based limit switches (ask for currentAmps and maxAmps, or maybe average amps, and return boolean for whether currentAmps are normal -- could also do changeIN thing)
//TODO documentation
//TODO change encoder sampling rate based on motor speed
//TODO emergency shutdown based on temperature
//TODO automatically try to retrieve ball