package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PIDController;

public class Robot extends SampleRobot {
	Talon motorL;
	Talon motorR;
	
	Talon motorCenter; // Motor controller for the middle of the H

	Talon motorElevator;
	
	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickAux;
	
	PIDController liftPIDController;
	AnalogInput liftAnalogIn;
	Talon liftMotor;
	
	
	public Robot() {

		motorL = new Talon(0);
		motorR = new Talon(1);
		
		motorCenter = new Talon(2);
		

		motorElevator = new Talon(3);

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);
		
		liftPIDController = new PIDController(0, 0, 0, liftAnalogIn, liftMotor);
		
		liftPIDController.disable();

	}

	public void autonomous() {

	}

	public void operatorControl() {
		// CameraThread c = new CameraThread();
		
		liftPIDController.enable();

		while (isOperatorControl() && isEnabled()) {

			driveControl();
			manipulatorControl();

			Timer.delay(0.005);
		}
		
		liftPIDController.disable();

		// c.finish();
	}

	public void test() {

	}

	public void driveControl() {
		double leftSide = -stickLeft.getAxis(AxisType.kY);
		double rightSide = stickRight.getAxis(AxisType.kY);
		double horizontal = (stickLeft.getAxis(AxisType.kX) + stickRight
				.getAxis(AxisType.kX)) / 2;

		motorL.set(leftSide);

		motorR.set(rightSide);

		motorCenter.set(horizontal);

	}

	public void manipulatorControl() {

		

	}
}
