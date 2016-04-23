package org.usfirst.frc.team4256.robot;

public class POLISH4256 {//things that are used regardless of the driver, yet are not specific to a physical object
	
	public static void shotAlignment(final double theoreticalTimeS, final double tolerance, final boolean enable) {
		final float goalAngle = Robot.gyro.getCurrentAngle() + (float)Robot.visionTable.getNumber("AngleDifferential", 0.0);
		Robot.drive.arcadeDriveNoSquare(0, FILTER4256.rotate(enable, goalAngle, theoreticalTimeS, tolerance));
	}
	
	public static void shotAlignment2(final double tolerance, final boolean enable) {
		float readyAngle = 0;
		final float doneAngle = Robot.gyro.getCurrentAngle() + (float)Robot.visionTable.getNumber("AngleDifferential", 0.0);
		if (Robot.gyro.getCurrentPath(doneAngle + 50) > Robot.gyro.getCurrentPath(doneAngle - 50)) {
			readyAngle = doneAngle + 50;
		}else {
			readyAngle = doneAngle - 50;
		}
		boolean ready = false;
		boolean stopped = false;
		boolean done = false;
		while(enable && (!ready || !done)) {
			if(!ready) {
				Robot.drive.arcadeDrive(0.0, 0.7*Math.signum((double)(Robot.gyro.getCurrentPath(readyAngle))));
				ready = Math.abs(Robot.gyro.getCurrentPath(readyAngle)) > tolerance ? false : true;
			}else if(ready && !done) {
				while(enable && !stopped && Robot.gyro.isRotating(2.0)){
					Robot.drive.arcadeDrive(0.0, 0.0);
				}
				stopped = true;
				//put code here for rotating approximately 50 degrees and then making minor last adjustments
				done = Math.abs(Robot.gyro.getCurrentPath(doneAngle)) > tolerance ? false : true;
			}
		}
	}
	
	public static void shotAlignment3(int button) {
		Float startAngle = null;
		Long startTime = null;
		while(Robot.xboxGun.getRawButton(button)) {
			if (!Robot.visionTable.getBoolean("TargetVisibility", false)) {
				Robot.drive.arcadeDrive(0.0, 0.8);
			}else if (Math.abs(Robot.visionTable.getNumber("AngleDifferential", 0.0)) > 2.0) {//make sure target offset is incorporated into the vision calculations for this angle
				if (startAngle == null) {startAngle = Robot.gyro.getCurrentAngle();}
				if (startTime == null) {startTime = System.currentTimeMillis()/1000;}
				float deltaAngle = 10 + Robot.gyro.getCurrentPath(startAngle);
				float deltaTime = (System.currentTimeMillis()/1000) - startTime;
				//use salt's correct motor value, between .8 and .45
				double rotateValue = deltaAngle/deltaTime > 0.5 ? 0.5 : (double)(deltaAngle/deltaTime);//may need to multiply time by ten or maybe even square it to slow down quickly enough
				Robot.drive.arcadeDrive(0.0, rotateValue*Math.signum(Robot.gyro.getCurrentPath((float)Robot.visionTable.getNumber("AngleDifferential", 0.0))));
			}
		}
	}
}
//TODO current based limit switches (ask for currentAmps and maxAmps, or maybe average amps, and return boolean for whether currentAmps are normal -- could also do changeIN thing)
//TODO documentation
//TODO change encoder sampling rate based on motor speed
//TODO emergency shutdown based on temperature
//TODO automatically try to retrieve ball