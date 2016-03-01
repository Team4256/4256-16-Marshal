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
	
	public boolean isWithinRange(double n) {
		return (min <= n&&n <= max);
	}
	
	/**
	 * Converts a value relative to this range into another.
	 * 
	 * @param value - The value to convert
	 * @param newRange - The new range to convert the value into
	 * @return the new value
	 */
	public double toRange(double value, Range newRange) {
		double newValue = newRange.min+(value-min)*newRange.getRange()*2/range-newRange.getRange()/2;
		
		//Force value into range if not in bounds
		if(newValue < newRange.min) {
			newValue = newRange.min;
		}else if(newRange.max < newValue) {
			newValue = newRange.max;
		}
		
		return newValue;
	}
}
