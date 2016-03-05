package org.usfirst.frc.team4256.robot;

public class JoystickControl {
	public static enum ControlType {Button, Axis};
	
	public DBJoystick joystick;
	public ControlType type;
	public int index;
	
	public JoystickControl(DBJoystick joystick, ControlType type, int index) {
		this.joystick = joystick;
		this.type = type;
		this.index = index;
	}
	
	public boolean isButton() {
		return (type == ControlType.Button);
	}
}
