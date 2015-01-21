

package org.usfirst.frc.team1492.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.Joystick.ButtonType;

;

public class Robot extends SampleRobot {
	Talon motorFL;
	Talon motorFR;
	Talon motorBL;
	Talon motorBR;
	Talon motorMid;
	Joystick stickLeft;
	Joystick stickRight;
	Solenoid testSolenoid;
	
	Servo cameraServo;
	double cameraServoValue;

	public Robot() {

		motorFL = new Talon(0);
		motorFR = new Talon(1);
		motorBL = new Talon(2);
		motorBR = new Talon(3);
		motorMid = new Talon(4);

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);

		testSolenoid = new Solenoid(3);
		
		cameraServo = new Servo(7);
		
	}

	public void autonomous() {
		
	}

	public void operatorControl() {
		//CameraThread c = new CameraThread();

		while (isOperatorControl() && isEnabled()) {

			driveControl();
			manipulatorControl();

			Timer.delay(0.005);
		}
		
		//c.finish();
	}

	public void test() {

	}

	public void driveControl() {
		double leftSide = stickLeft.getAxis(AxisType.kY);
		double rightSide = stickRight.getAxis(AxisType.kY);
		double horizontal = (stickLeft.getAxis(AxisType.kX) + stickRight.getAxis(AxisType.kX)) / 2;

		motorFL.set(leftSide);
		motorBL.set(leftSide);

		motorFR.set(rightSide);
		motorBR.set(rightSide);

		motorMid.set(horizontal);

	}

	public void manipulatorControl() {

		if (stickRight.getRawButton(1)) {
			testSolenoid.set(true);
		} else {
			testSolenoid.set(false);
		}
		
		if(stickRight.getRawButton(4)){
			cameraServoValue -= .01;
		}
		if(stickRight.getRawButton(5)){
			cameraServoValue += .01;
		}
		
		cameraServo.set(cameraServoValue);
	}
}
