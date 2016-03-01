package org.usfirst.frc.team4256.robot;

public class RangedDouble {
	public Range range;
	public Range deadZone = null;

	public boolean continuous;
	private double value;
	
	public RangedDouble(Range range, double value) {
		this(range, value, false);
	}
	
	public RangedDouble(Range range, double value, boolean continuous) {
		this.range = range;
		this.value = value;
		this.continuous = continuous;
	}
	
	
	public double getNormalizedValueForContinous(double n) {
		if(n < 0) {
			return getNormalizedNegativeValueForContinuous(n);
		}else{
			return range.min+n%range.getRange();
		}
	}
	
	private double getNormalizedNegativeValueForContinuous(double n) {
		n = range.min+range.max-Math.abs(n)%range.getRange();
		return n;
	}

	/**
	 * Gets the shortest displacement between the current value and the goal.
	 * @param goal - the angle to compare the current value to
	 * @return
	 */
	public double getDisplacementFrom(double goal) {
		double d = getNormalizedValueForContinous(goal)-value;//System.out.println(d);
		
		if(continuous) {
			if(deadZone != null) {
//				if((value <= deadZone.min && goal <= deadZone.min) || (deadZone.max <= value && deadZone.max <= goal)) {
				if((value <= deadZone.getCenter() && goal <= deadZone.getCenter()) 
						|| (deadZone.getCenter() <= value && deadZone.getCenter() <= goal)) {
					//Value and goal are on same side of dead zone
					return d;
				}else{
					//Value and goal are on opposite side of dead zone
					if(value < goal) {
						return -(range.max-goal + value-range.min);
					}else{
						return (range.max-value + goal-range.min);
					}
				}
			}else{
				if(Math.abs(d) > range.max/2) {
					d = getNormalizedNegativeValueForContinuous(d);

					//Correction when moving from quadrant I to quadrant IV
					if(value<range.max/4+range.min) {
						d = -d;
					}
				}else{
					d %= range.max;
				}
			}
		}
		
		return d;
	}

	public void goTowards(double goal) {
		increaseBy(getDisplacementFrom(goal)*2/range.getRange()*4);
	}
	
	public void increaseBy(double amount) {
		setValue(value+amount);
	}
	
	public void decreaseBy(double amount) {
		setValue(value-amount);
	}
	
	/**
	 * Converts a value relative to this range into another.
	 * 
	 * @param newRange - The new range to convert the value into
	 * @return the new value
	 */
	public double toRange(Range newRange) {
		return range.toRange(value, newRange);
	}


	/**
	 * @return the deadZone
	 */
	public Range getDeadZone() {
		return deadZone;
	}

	/**
	 * @param deadZone - the dead zone to set
	 */
	public void setDeadZone(Range deadZone) {
		this.deadZone = deadZone;
	}
	
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * If the value is too low, it is set to the lower limit.
	 * If the value is too high, it is set to the upper limit.
	 * If continuous, value is set to min+value%range.
	 * @param value - the value to set
	 */
	public void setValue(double newValue) {
		if(continuous) {
			value = getNormalizedValueForContinous(newValue);
		}else if(newValue < range.min) {
			value = range.min; //set as lower limit
		}else if(range.max < newValue) {
			value = range.max; //set as upper limit
		}else{
			this.value = newValue;
		}
	}
}
