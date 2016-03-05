package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NavaxGyro extends AHRS {
	private double offsetAngle;
	private RangedDouble targetAngle;

	
	/**
	 * @param spi_port_id
	 * @param startAngle - The angle in degrees that the robot is facing. Should be 90 for forward.
	 */
	public NavaxGyro(edu.wpi.first.wpilibj.SerialPort.Port kmxp, double startAngle) {
		super(kmxp);
		
		offsetAngle = super.getAngle()-startAngle;
		targetAngle = new RangedDouble(new Range(0, 360), startAngle);
	}

	private void updateAngle() {
		targetAngle.setValue(getAngle());
	}
	
	public double getAngle() {
		return targetAngle.getNormalizedValueForContinous(super.getAngle()-offsetAngle);
	}
	
	public double getAngleDisplacementFrom(double targetAngle) {
		updateAngle();
		return this.targetAngle.getDisplacementFrom(targetAngle);
	}
	
	double deadband = .05;
	double minimumMagnitude = .6;
	public double getAngleDisplacementFromAngleAsMotorValue(double targetAngle/*, double deadband, double minimumMagnitude*/) {
		updateAngle();
		SmartDashboard.putNumber("angle displacement", this.targetAngle.getDisplacementFrom(targetAngle));
//		double motorValue = this.targetAngle.getDisplacementFrom(targetAngle)/180;
//		if(motorValue < 0) {
//			return -.6;
//		}else{
//			return .6;
//		}
		
		
//		return this.targetAngle.range.toRange(targetAngle, Range.MOTOR_RANGE)*.5;
		
		
		
		double motorValue = this.targetAngle.getDisplacementFrom(targetAngle)/180;
//		motorValue = Math.pow(motorValue, 2);//square input
		
		if(Math.abs(motorValue) < deadband) {
			return 0;
		}else{
			double motorMagnitude = motorValue*(1-minimumMagnitude) + Math.abs(motorValue)*minimumMagnitude;
			
			if(motorValue < 0) {
				return -motorMagnitude;
			}else{
				return motorMagnitude;
			}
		}
	}
	
	public double getGroundDisplacement() {
		return getAngleDisplacementFrom(targetAngle.getValue());
	}
}
