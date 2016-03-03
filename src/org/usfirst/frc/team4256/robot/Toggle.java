package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Toggle {
	private DBJoystick joystick;
	private int control;
	private boolean value = false;
	private boolean lastValue = false;
	boolean state = false;
	
	boolean controlIsButton;
	
	public Toggle(DBJoystick joystick, int control) {
		this(joystick, control, true);
	}
	
	public Toggle(DBJoystick joystick, int control, boolean controlIsButton) {
		this.joystick = joystick;
		this.control = control;
		this.controlIsButton = controlIsButton;
	}
	
	/**
	 * Updates and returns the button's toggle state
	 */
	public boolean getState() {
		return getState(joystick);
	}
	
	public boolean getState(DBJoystick j) {
		value = getRawValue(j);
		if(value && value!=lastValue)
			state = !state;
		lastValue = value;
		return state;
	}
	
	private boolean getRawValue(DBJoystick j) {
		if(controlIsButton) {
			return j.getRawButton(control);
		}else{
			return j.axisPressed(control);
		}
	}
	
	public void setButton(int button) {
		this.control = button;
	}
}
