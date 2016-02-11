package org.usfirst.frc.team4256.robot;

public class Launcher {
	public ExtendedCANTalon motor1;
	public ExtendedCANTalon motor2;
	boolean isLaunching = false;
    long timeSinceLaunchStart;
	public Launcher(int motor1Port, int motor2Port) {
		motor1 = new ExtendedCANTalon(motor1Port);
		motor2 = new ExtendedCANTalon(motor2Port);
	}
	
	public void fire() {
		isLaunching = true;
		timeSinceLaunchStart = System.currentTimeMillis();
	}
	
	public void update(){
		if(System.currentTimeMillis() - timeSinceLaunchStart >= 500){
    		isLaunching = false;
    	}
		
		//isLaunching = false;
		if(isLaunching){
    		motor1.set(.75);
    		motor2.set(-.75);
    	//If false it will not do anything
    	}else{
    		motor1.set(0);
    		motor2.set(0);
    	}
	}
}
