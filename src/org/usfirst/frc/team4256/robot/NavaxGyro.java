package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NavaxGyro extends AHRS {
	private RangedDouble targetAngle;

	
	/**
	 * @param spi_port_id
	 */
	public NavaxGyro(edu.wpi.first.wpilibj.SerialPort.Port kmxp) {
		super(kmxp);
		zeroYaw();
		targetAngle = new RangedDouble(new Range(0, 360), 0);
	}

	private void updateAngle() {
		targetAngle.setValue(getAngle());
	}
	
	public double getAngle() {
		return targetAngle.getNormalizedValueForContinous(super.getAngle());
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
		double motorValue = this.targetAngle.getDisplacementFrom(targetAngle)/180;
		if(motorValue < 0) {
			return -.6;
		}else{
			return .6;
		}
		
		
//		return this.targetAngle.range.toRange(targetAngle, Range.MOTOR_RANGE)*.5;
		
		
		
//		double motorValue = -this.targetAngle.getDisplacementFrom(targetAngle)/180;
//		motorValue = Math.pow(motorValue, 2);//square input
//		
//		if(Math.abs(motorValue) < deadband) {
//			return 0;
//		}else{
//			double motorMagnitude = Math.abs(motorValue)*(1-minimumMagnitude) + minimumMagnitude;
//			SmartDashboard.putNumber("motorMagnitude", motorMagnitude);
//			
//			if(motorValue < 0) {
//				return -motorMagnitude;
//			}else{
//				return motorMagnitude;
//			}
//		}
	}
	
	public double getGroundDisplacement() {
		return Math.sqrt(Math.pow(super.getDisplacementX(), 2)+Math.pow(super.getDisplacementZ(), 2));
//		return getAngleDisplacementFrom(targetAngle.getValue());
	}
}
