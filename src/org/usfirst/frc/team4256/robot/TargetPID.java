package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TargetPID extends PIDSubsystem {
	public double MINIMUM_ROBOT_SPEED = .45;
	private double output;

	public TargetPID(double p, double i, double d, double period, double f) {
		super(p, i, d, period, f);
		init();
	}

	public TargetPID(double p, double i, double d, double period) {
		super(p, i, d, period);
		init();
	}

	public TargetPID(double p, double i, double d) {
		super(p, i, d);
		init();
	}

	public TargetPID(String name, double p, double i, double d, double f, double period) {
		super(name, p, i, d, f, period);
		init();
	}

	public TargetPID(String name, double p, double i, double d, double f) {
		super(name, p, i, d, f);
		init();
	}

	public TargetPID(String name, double p, double i, double d) {
		super(name, p, i, d);
		init();
	}
	
	private void init() {
		setAbsoluteTolerance(.03);
		LiveWindow.addSensor(getName(), getName(), getPIDController());
	}
	
	public double getOutput() {
		return output;
	}

	@Override
	protected double returnPIDInput() {
		double input = AutoModes.getTargetOffset();
		SmartDashboard.putNumber("Input", input);
		return input;
	}

	@Override
	protected void usePIDOutput(double output) {
		if(onTarget() && Robot.gyro.getRate() <= 1) {
			output = 0;
		}
		this.output = correctMotorValue(output, .3, 1);
	}
	

	public static double correctMotorValue(double motorValue, double minimumMagnitude, double maximumMagnitude) {
		double motorMagnitude = Math.abs(motorValue)*(maximumMagnitude-minimumMagnitude) + minimumMagnitude;
		return motorMagnitude*Math.signum(motorValue);
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
	}
}
