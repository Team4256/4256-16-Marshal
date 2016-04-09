package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NavaxGyro extends AHRS {
	private RangedDouble targetAngle;
	private double offset = 0;

	
	/**
	 * @param spi_port_id
	 */
	public NavaxGyro(edu.wpi.first.wpilibj.SerialPort.Port kmxp) {
		super(kmxp);
		reset();
		zeroYaw();
		targetAngle = new RangedDouble(new Range(0, 360), 0, true);
	}
	
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
