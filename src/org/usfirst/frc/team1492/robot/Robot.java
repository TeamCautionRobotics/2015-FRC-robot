

package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends SampleRobot {
	Talon motorFL;
	Talon motorFR;
	Talon motorBL;
	Talon motorBR;
	Talon motorMid;
	Talon motorElevator;
	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickThree;
	Solenoid testSolenoid;
	
	Servo cameraServo;
	double cameraServoValue;
	
	DigitalInput limitSwitchElevatorTop;
	DigitalInput limitSwitchElevatorBottom;
	DigitalInput limitSwitchElevatorOne;
	DigitalInput limitSwitchElevatorTwo;
	DigitalInput limitSwitchElevatorThree;
	
	
	double elevatorSpeed = 0;
	double elevatorMaxSpeed = 1;
	double elevatorDir = 0;
	
	
	public Robot() {

		motorFL = new Talon(1);
		motorFR = new Talon(3);
		motorBL = new Talon(0);
		motorBR = new Talon(2);

		motorMid = new Talon(5);
		
		motorElevator = new Talon(4);

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickThree = new Joystick(2);

		testSolenoid = new Solenoid(0);
		
		cameraServo = new Servo(7);

		
		limitSwitchElevatorBottom = new DigitalInput(0);
		limitSwitchElevatorTop = new DigitalInput(1);
		
		limitSwitchElevatorOne = new DigitalInput(2);
		
		limitSwitchElevatorTwo = new DigitalInput(3);
		limitSwitchElevatorThree = new DigitalInput(4);
		 
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
		double leftSide = -stickLeft.getAxis(AxisType.kY);
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
			cameraServoValue -= .05;
			if(cameraServoValue<0){
				cameraServoValue = 0;
			}
		}
		if(stickRight.getRawButton(5)){
			cameraServoValue += .05;
			if(cameraServoValue>1){
				cameraServoValue = 1;
			}
		}

		cameraServo.set(cameraServoValue);
		
		//elevator limit switches not edge ones
		
		elevatorMaxSpeed = (stickThree.getAxis(AxisType.kZ)+1)/2;
		SmartDashboard.putNumber("elevatorMaxSpeed", elevatorMaxSpeed);
		
		SmartDashboard.putBoolean("limitSwitchElevatorTop", limitSwitchElevatorTop.get());
		SmartDashboard.putBoolean("limitSwitchElevatorBottom", limitSwitchElevatorBottom.get());
		
		
		if(limitSwitchElevatorOne.get() || limitSwitchElevatorTwo.get() || limitSwitchElevatorThree.get()){
			//elevatorSpeed = 0;
		}
		
		if(stickThree.getRawButton(3)){
			elevatorDir = -1;
		}
		if(stickThree.getRawButton(2)){
			elevatorDir = 1;
		}
		
		elevatorSpeed = elevatorDir*elevatorMaxSpeed;
		
		if(limitSwitchElevatorTop.get() || limitSwitchElevatorBottom.get()){
			elevatorSpeed = 0;
		}
		
		motorElevator.set(elevatorSpeed);
		
		
	}
}
