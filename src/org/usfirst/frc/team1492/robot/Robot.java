/*
 * Installing Eclipse with stuffs and things - REVIEW THIS ON TEAM LAPTOP
http://wpilib.screenstepslive.com/s/4485/m/13809/l/145002-installing-eclipse-c-java
*/

package org.usfirst.frc.team1492.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;


public class Robot extends SampleRobot {
    RobotDrive drive;
    Joystick stick;

    public Robot() {//         fl rl fr rl
    	drive = new RobotDrive(0, 1, 2, 3);
    	drive.setExpiration(0.1);
    	
        stick = new Joystick(0);
    }
    
    public void autonomous() {
    	drive.setSafetyEnabled(false);
    	
        //Auto Code
    }
    
    public void operatorControl() {
    	drive.setSafetyEnabled(true);
    	
        while (isOperatorControl() && isEnabled()) {
        	
        	//Operation Code
        	
            Timer.delay(0.005);
        }
    }
    
    public void test() {
    	drive.setSafetyEnabled(false);
    	
    }
}
