package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class NavaxGyro extends AHRS {
	private float tareAngle = 0;
	private float protectedZoneStart; //Angles increase as the numbers on a clock increase. This value should be the first protected angle encountered by a minute hand which starts at 12:00.
	private final float protectedZoneSize; //This value should be the number of degrees the minute hand must travel before reaching the end of the protected section.
    private RangedDouble targetAngle;
	
	public NavaxGyro(final float protectedZoneStart, final float protectedZoneSize) {
		super(SerialPort.Port.kMXP, SerialDataType.kProcessedData, (byte)46);
		reset();
		this.protectedZoneStart = protectedZoneStart;
		this.protectedZoneSize = protectedZoneSize%360 < 360 ? protectedZoneSize%360 : 0;
        targetAngle = new RangedDouble(new Range(0, 360), 0, true);
	}
	private double lastLegalDirection = 1.0;
	private double lastMeasuredAngle = 0.0;
	private double lastMeasuredAcceleration = 0.0;
	private double lastMeasuredRate = 0.0;
	private long lastMeasuredRateTime = System.currentTimeMillis();
	/**
	 * This function modifies the input to create a value between 0 and 359
	**/
	private static float validateAngle(final float angle) {
		if (angle < 0) {
			return (360 - (Math.abs(angle)%360) < 360) ? 360 - (Math.abs(angle)%360) : 0;
		}else {
			return (angle%360 < 360) ? angle%360 : 0;
		}
	}
	/**
	 * This function finds the shortest path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	private static float findPath(float startAngle, float endAngle) {
		startAngle = validateAngle(startAngle);
		endAngle = validateAngle(endAngle);
		float pathVector = endAngle - startAngle;
		if (Math.abs(pathVector) > 180) {
			pathVector = Math.abs(pathVector) - 360;
		}if (endAngle - startAngle < -180) {
			pathVector = -pathVector;
		}return pathVector;
	}
	/**
	 * This function returns a valid and legal version of the input.
	**/
	private float legalizeAngle(float angle) {
		angle = validateAngle(angle);
		protectedZoneStart = validateAngle(protectedZoneStart);
		float protectedZoneEnd = validateAngle(protectedZoneStart + protectedZoneSize);
		if (findPath(protectedZoneStart, angle) >= 0.0 && findPath(protectedZoneStart, angle) <= protectedZoneSize) {
			angle = Math.abs(findPath(angle, protectedZoneStart)) <= Math.abs(findPath(angle, protectedZoneEnd)) ? protectedZoneStart : protectedZoneEnd;
		}return angle;
	}
	/**
	 * This function tares the gyrometer at the specified angle. It accepts both -'s and +'s.
	**/
	public void setTareAngle(final float tareAngle) {
		this.tareAngle = validateAngle(this.tareAngle + tareAngle);
		this.protectedZoneStart = validateAngle(this.protectedZoneStart + tareAngle);
	}
	/**
	 * This function returns the current angle based on the tare angle.
	**/
	public float getCurrentAngle() {
		if (isCalibrating()) {
			return (float)lastMeasuredAngle;
		}lastMeasuredAngle = getAngle();
		float currentAngle;
		if (0 <= validateAngle(getFusedHeading()) && validateAngle(getFusedHeading()) <= tareAngle) {
			currentAngle = 360 - tareAngle + validateAngle(getFusedHeading());
		}else {
			currentAngle = validateAngle(getFusedHeading()) - tareAngle;
		}return validateAngle(currentAngle);
	}
	/**
	 * This function returns the path to the border that is nearest to the specified angle.
	**/
	public float findBorderPath(final float startAngle) {
		float borderPath = findPath(startAngle, protectedZoneStart);
		if (Math.abs(borderPath) > Math.abs(findPath(startAngle, protectedZoneStart + protectedZoneSize))) {
			borderPath = findPath(startAngle, protectedZoneStart + protectedZoneSize);
		}return borderPath;
	}
	/**
	 * This function finds the shortest legal path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public float findLegalPath(float startAngle, float endAngle) {
		startAngle = legalizeAngle(startAngle);
		endAngle = legalizeAngle(endAngle);
		float legalPathVector = findPath(startAngle, endAngle);
		float borderPath = findBorderPath(startAngle);
		if ((Math.abs(borderPath) < Math.abs(legalPathVector) && Math.signum(legalPathVector) == Math.signum(borderPath))
		|| (borderPath == 0 && Math.signum(legalPathVector) == Math.signum(findPath(startAngle, protectedZoneStart + protectedZoneSize/2)))) {
			legalPathVector = 360*Math.signum(-legalPathVector) + legalPathVector;
		}return legalPathVector;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public float getCurrentPath(float endAngle) {
		endAngle = legalizeAngle(endAngle);
		final float currentAngle = getCurrentAngle();
		float currentPathVector = findPath(currentAngle, endAngle);
		boolean legal = legalizeAngle(currentAngle) == currentAngle;
		if (legal) {
			currentPathVector = findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function returns true if the distance between the current angle and the last measured angle is greater than the tolerance.
	 * Otherwise, it returns false.
	**/
	public boolean isRotating(final double tolerance) {//TODO could re-implement using getRate()
		if (Math.abs(lastMeasuredAngle - getAngle()) >= tolerance && !isCalibrating()) {
			lastMeasuredAngle = getAngle();
			return true;
		}else if (!isCalibrating()) {
			lastMeasuredAngle = getAngle();
		}
		return false;
	}
	
	public double getAcceleration() {//if I update too often my change in rate will be too low
		if (System.currentTimeMillis() - lastMeasuredRateTime >= 200) {//TODO adjust this value to change how often a gets updated
			lastMeasuredAcceleration = (getRate()*1000.0 - lastMeasuredRate)/(System.currentTimeMillis() - lastMeasuredRateTime);
			lastMeasuredRate = getRate()*1000.0;
			lastMeasuredRateTime = System.currentTimeMillis();
		}return lastMeasuredAcceleration;
	}
	
	public float getElevation() {
		return getRoll();
	}
    
    public double motorizeCurrentPath(double endAngle) {
        updateAngle();
        SmartDashboard.putNumber("angle displacement", (double)getCurrentPath((float)endAngle));
        double motorValue = (double)getCurrentPath((float)endAngle)/90;
        motorValue = Math.abs(motorValue) > 1 ? Math.signum(motorValue) : motorValue;
        return motorValue;
    }
    
    private void updateAngle() {
        targetAngle.setValue(getRawAngle());
    }
}