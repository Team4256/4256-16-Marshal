package org.usfirst.frc.team4256.robot;


public class Range {
	public static final Range MOTOR_RANGE = new Range(-1, 1);
	
	public double max;
	public double min;
	
	private double range;
	
	public Range(double min, double max) {
		this.min = min;
		this.max = max;
		this.range = Math.abs(max-min);
	}
	
	public double getRange() {
		return range;
	}
	
	public double getCenter() {
		return min+range/2;
	}
	
	public double fromPercent(double percent) {
		return min+range*percent/100;
	}
	
	public boolean isWithinRange(double n) {
		return (min <= n&&n <= max);
	}
	
	public static boolean withinRange(double n, Range range) {
		return range.isWithinRange(n);
	}
}
