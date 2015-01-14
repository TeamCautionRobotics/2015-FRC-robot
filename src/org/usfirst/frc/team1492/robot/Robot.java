/*
 * Installing Eclipse with stuffs and things - REVIEW THIS ON TEAM LAPTOP
http://wpilib.screenstepslive.com/s/4485/m/13809/l/145002-installing-eclipse-c-java
*/

package org.usfirst.frc.team1492.robot;


import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Solenoid;;


public class Robot extends SampleRobot {
    RobotDrive drive;
    Joystick stickLeft;
    Joystick stickRight;
    Solenoid testSolenoid;
    Compressor compressor;

    public Robot() {//         fl bl fr br
    	drive = new RobotDrive(1, 3, 0, 2);
    	drive.setExpiration(0.1);
    	
        stickLeft = new Joystick(0);
        stickRight = new Joystick(1);
        
        testSolenoid = new Solenoid(3);
        
        compressor = new Compressor();
        compressor.setClosedLoopControl(true);
        //compressor.start();
    }
    
    public void autonomous() {
    	drive.setSafetyEnabled(false);
    	
    	drive.setLeftRightMotorOutputs(-.5, -.5);
    	Timer.delay(1);
    	drive.setLeftRightMotorOutputs(0, 0);
    	
        
    }
    
    public void operatorControl() {
    	drive.setSafetyEnabled(true);
    	
        while (isOperatorControl() && isEnabled()) {
        	
        	drive.tankDrive(stickRight, stickLeft);
        	
        	if(stickRight.getButton(ButtonType.kTrigger)){
        		testSolenoid.set(true);
        	}else{
        		testSolenoid.set(false);
        	}
        	
            Timer.delay(0.005);
        }
    }
    
    public void test() {
    	drive.setSafetyEnabled(false);
    	
    }
}
