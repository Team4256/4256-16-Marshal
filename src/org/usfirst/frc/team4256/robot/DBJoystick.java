package org.usfirst.frc.team4256.robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;
/**
 * DBJoystick = Deadband Joystick
 *
 */
public class DBJoystick extends Joystick {
	//Deadband?
	public static double DEADBAND = .2;
	public static double DETENT = 0.7;
	
	//XBox Axis
	public static int AXIS_LEFT_X = 0;
	public static int AXIS_LEFT_Y = 1;
	public static int AXIS_LT = 2;
	public static int AXIS_RT = 3;
	public static int AXIS_RIGHT_X = 4;
	public static int AXIS_RIGHT_Y = 5;
	
	//XBox Controls
	public static int BUTTON_A = 1;
	public static int BUTTON_B = 2;
	public static int BUTTON_X = 3;
	public static int BUTTON_Y = 4;
	public static int BUTTON_LB = 5;
	public static int BUTTON_RB = 6;
	public static int BUTTON_BACK = 7;
	public static int BUTTON_START = 8;
	public static int BUTTON_LEFT_STICK = 9;
	public static int BUTTON_RIGHT_STICK = 10;
	public static int BUTTON_XBOX = 11;
	public static int BUTTON_NORTH = 12;
	public static int BUTTON_SOUTH = 13;
	public static int BUTTON_WEST = 14;
	public static int BUTTON_EAST = 15;
	
	//D-Pad
	public static int POV_NORTH = 0;
	public static int POV_NORTH_EAST = 45;
	public static int POV_EAST = 90;
	public static int POV_SOUTH_EAST = 135;
	public static int POV_SOUTH = 180;
	public static int POV_SOUTH_WEST = 225;
	public static int POV_WEST = 270;
	public static int POV_NORTH_WEST = 315;
	

	
////	boolean toggleState = false;
////	boolean previousState = false;
//
//
////	boolean toggleState = false;
////	boolean previousState = false;
//
//	boolean toggleState[] = new boolean[getButtonCount()];
//	boolean previousState[] = new boolean[getButtonCount()];
	private HashMap<JoystickControl, Toggle> toggles = new HashMap<JoystickControl, Toggle>();

	double port;
	
	public DBJoystick(int port) {
		super(port);
		this.port = port;
	}
	
	protected DBJoystick(int port, int numAxisTypes, int numButtonTypes) {
		super(port, numAxisTypes, numButtonTypes);
	}

	@Override
	public double getRawAxis(int axis) {
		return deadband(super.getRawAxis(axis));
	}
	
	private boolean getToggleState(JoystickControl control) {
		//Add toggle if not created
		if(!toggles.containsKey(control)) {
			toggles.put(control, new Toggle(control.joystick, control.index, control.isButton()));
		}
		
		return toggles.get(control).getState();
	}
	
	public boolean getButtonToggleState(int button) {
		return getToggleState(new JoystickControl(this, JoystickControl.ControlType.Button, button));
	}
	
	public boolean getAxisToggleState(int axis) {
		return getToggleState(new JoystickControl(this, JoystickControl.ControlType.Axis, axis));
	}
	
//	public boolean getRawToggle(int whichButton){
//		return toggleState[whichButton];
//	}
//	
//	public void updateToggle() {
//		for (int i = 1; i < previousState.length; i++) {
//			boolean currentState = this.getRawButton(i);
//			if (currentState && (currentState != previousState[i])){
//				toggleState[i] = !toggleState[i];
//			}
//			previousState[i] = currentState;
//		}
//	}
	
	public boolean axisPressed(int axis) {
		return Math.abs(super.getRawAxis(axis)) > DETENT;
	}
	
	public double deadband(double input) {
		return (Math.abs(input) <= DEADBAND ? 0 : input);
	}
	
	public void rumble(float amount) {
		setRumble(RumbleType.kLeftRumble, amount);
    	setRumble(RumbleType.kRightRumble, amount);
	}

	public boolean anyControlIsActive() {
		//Check buttons (index starts at 1)
		for(int i = 1; i<=getButtonCount(); i++) {
			if(getRawButton(i)) {
				return true;
			}
		}
		
		//Check axis
		for(int i = 0; i<getAxisCount(); i++) {
			if(axisPressed(i)) {
				return true;
			}
		}
		
		//Check POV
		for(int i = 0; i<getPOVCount(); i++) {
			if(getPOV(i) == -1) {
				return true;
			}
		}
		
		return false;
	}
}


