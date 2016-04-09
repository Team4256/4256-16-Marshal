package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Gyrometer4256 extends AHRS {
	private double tareAngle = 0.0;
	private RangedDouble targetAngle;
	private double offset = 0;
	private double protectedZoneStart; //Angles increase as the numbers on a clock increase. This value should be the first protected angle encountered by a minute hand which starts at 12:00.
	private final double protectedZoneSize; //This value should be the number of degrees the minute hand must travel before reaching the end of the protected section.
	
	public Gyrometer4256(edu.wpi.first.wpilibj.SerialPort.Port kmxp, final double protectedZoneStart, final double protectedZoneSize) {
		super(kmxp);
		targetAngle = new RangedDouble(new Range(0, 360), 0, true);
		this.protectedZoneStart = protectedZoneStart;
		this.protectedZoneSize = protectedZoneSize%360 < 360.0 ? protectedZoneSize%360 : 0.0;
	}
	private double lastLegalDirection = 1.0;
	private double lastMeasuredAngle = 0.0;
	private double lastMeasuredRate = 0.0;
	private long lastMeasuredRateTime = System.currentTimeMillis();
	/**
	 * This function modifies the input to create a value between 0 and 359.999...
	**/
	private static double validateAngle(final double angle) {
		if (angle < 0.0) {
			return (360.0 - (Math.abs(angle)%360) < 360.0) ? 360.0 - (Math.abs(angle)%360) : 0.0;
		}else {
			return (angle%360 < 360.0) ? angle%360 : 0.0;
		}
	}
	/**
	 * This function finds the shortest path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	private static double findPath(double startAngle, double endAngle) {
		startAngle = validateAngle(startAngle);
		endAngle = validateAngle(endAngle);
		double pathVector = endAngle - startAngle;
		if (Math.abs(pathVector) > 180.0) {
			pathVector = Math.abs(pathVector) - 360.0;
		}if (endAngle - startAngle < -180.0) {
			pathVector = -pathVector;
		}return pathVector;
	}
	/**
	 * This function returns a valid and legal version of the input.
	**/
	private double legalizeAngle(double angle) {
		angle = validateAngle(angle);
		protectedZoneStart = validateAngle(protectedZoneStart);
		double protectedZoneEnd = validateAngle(protectedZoneStart + protectedZoneSize);
		if (findPath(protectedZoneStart, angle) >= 0.0 && findPath(protectedZoneStart, angle) <= protectedZoneSize) {
			angle = Math.abs(findPath(angle, protectedZoneStart)) <= Math.abs(findPath(angle, protectedZoneEnd)) ? protectedZoneStart : protectedZoneEnd;
		}return angle;
	}
	/**
	 * This function returns an angle between 0 and 359.999... based on the gyrometer's readings.
	**/
	private double normalizeInput() {//TODO may need to change for AHRS
		return validateAngle(getAngle());
	}
	/**
	 * This function tares the gyrometer at the specified angle. It accepts both -'s and +'s.
	**/
	public void setTareAngle(final double tareAngle) {
		this.tareAngle = validateAngle(this.tareAngle + tareAngle);
		this.protectedZoneStart = validateAngle(this.protectedZoneStart + tareAngle);
	}
	/**
	 * This function returns the current angle based on the tare angle.
	**/
	public double getCurrentAngle() {
		double currentAngle;
		if (0.0 <= normalizeInput() && normalizeInput() <= tareAngle) {
			currentAngle = 360.0 - tareAngle + normalizeInput();
		}else {
			currentAngle = normalizeInput() - tareAngle;
		}return validateAngle(currentAngle);
	}
	/**
	 * This function returns the path to the border that is nearest to the specified angle.
	**/
	public double findBorderPath(final double startAngle) {
		double borderPath = findPath(startAngle, protectedZoneStart);
		if (Math.abs(borderPath) > Math.abs(findPath(startAngle, protectedZoneStart + protectedZoneSize))) {
			borderPath = findPath(startAngle, protectedZoneStart + protectedZoneSize);
		}return borderPath;
	}
	/**
	 * This function finds the shortest legal path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public double findLegalPath(double startAngle, double endAngle) {
		startAngle = legalizeAngle(startAngle);
		endAngle = legalizeAngle(endAngle);
		double legalPathVector = findPath(startAngle, endAngle);
		double borderPath = findBorderPath(startAngle);
		if ((Math.abs(borderPath) < Math.abs(legalPathVector) && Math.signum(legalPathVector) == Math.signum(borderPath))
		|| (borderPath == 0.0 && Math.signum(legalPathVector) == Math.signum(findPath(startAngle, protectedZoneStart + protectedZoneSize/2.0)))) {
			legalPathVector = 360.0*Math.signum(-legalPathVector) + legalPathVector;
		}return legalPathVector;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public double getCurrentPath(double endAngle) {
		endAngle = legalizeAngle(endAngle);
		final double currentAngle = getCurrentAngle();
		double currentPathVector = findPath(currentAngle, endAngle);
		boolean legal = legalizeAngle(currentAngle) == currentAngle;
		if (legal) {
			currentPathVector = findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360.0*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function returns true if the distance between the current angle and the last measured angle is greater than the tolerance.
	 * Otherwise, it returns false.
	**/
	public boolean isRotating(final double tolerance) {//TODO could re-implement using getRate()
		if (Math.abs(lastMeasuredAngle - getAngle()) >= tolerance) {
			lastMeasuredAngle = getAngle();
			return true;
		}else {
			lastMeasuredAngle = getAngle();
			return false;
		}
	}
	
	public double getAcceleration() {
		double a = (getRate()*0.001 - lastMeasuredRate)/(System.currentTimeMillis() - lastMeasuredRateTime);
		lastMeasuredRate = getRate()*0.001;
		lastMeasuredRateTime = System.currentTimeMillis();
		return a;
	}
	
	//BEGIN SALT CODE END HAYDEN CODE MAY NEED INTERGRATION-------------------------------------------------------------------------
	public void zeroYaw() {
		super.zeroYaw();
//		offset = super.getAngle();
	}
	
	public double getElevation() {
		return super.getRoll();
	}

	private void updateAngle() {
		targetAngle.setValue(getRawAngle());
	}
	
	private double getNormalizedAngle() {
		return targetAngle.getNormalizedValueForContinous(getRawAngle());
	}

	public double getRawAngle() {
		return super.getAngle()-offset;
	}
	
	public double getAngle() {
		updateAngle();
		return getNormalizedAngle();
	}
	
	public double getAngleDisplacementFrom(double targetAngle) {
		updateAngle();
		return this.targetAngle.getDisplacementFrom(targetAngle);
	}
	
	double deadband = .05;
	double minimumMagnitude = .4;
	public double getAngleDisplacementFromAngleAsMotorValue(double targetAngle/*, double deadband, double minimumMagnitude*/) {
		updateAngle();
		SmartDashboard.putNumber("angle displacement", this.targetAngle.getDisplacementFrom(targetAngle));
		double displacementAngle = this.targetAngle.getDisplacementFrom(targetAngle);
		double motorValue = displacementAngle/90;
		if(motorValue > 1) {
			return 1;
		}else if(motorValue < -1) {
			return -1;
		}
		return motorValue;
	}
	
	public double getGroundDisplacement() {
		return Math.sqrt(Math.pow(super.getDisplacementX(), 2)+Math.pow(super.getDisplacementZ(), 2));
//		return getAngleDisplacementFrom(targetAngle.getValue());
	}
}