package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	Talon motorLF;
	Talon motorLB;
	Talon motorRF;
	Talon motorRB;
	Talon motorCenter; // Motor controller for the middle of the H
	Talon motorElevator;
	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickAux;
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

	public Robot() {

		motorLF = new Talon(1);
		motorLB = new Talon(3);
		motorRF = new Talon(0);
		motorRB = new Talon(2);

		motorCenter = new Talon(5);

		motorElevator = new Talon(4);

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);

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
		// CameraThread c = new CameraThread();

		while (isOperatorControl() && isEnabled()) {

			driveControl();
			manipulatorControl();

			Timer.delay(0.005);
		}

		// c.finish();
	}

	public void test() {

	}

	public void driveControl() {
		double leftSide = -stickLeft.getAxis(AxisType.kY);
		double rightSide = stickRight.getAxis(AxisType.kY);
		double horizontal = (stickLeft.getAxis(AxisType.kX) + stickRight
				.getAxis(AxisType.kX)) / 2;

		motorLF.set(leftSide);
		motorRF.set(leftSide);

		motorLB.set(rightSide);
		motorRB.set(rightSide);

		motorCenter.set(horizontal);

	}

	public void manipulatorControl() {

		testSolenoid.set(stickRight.getRawButton(1));

		if (stickRight.getRawButton(4)) {
			cameraServoValue -= .05;
			if (cameraServoValue < 0) {
				cameraServoValue = 0;
			}
		}
		if (stickRight.getRawButton(5)) {
			cameraServoValue += .05;
			if (cameraServoValue > 1) {
				cameraServoValue = 1;
			}
		}

		cameraServo.set(cameraServoValue);

		// elevator limit switches not edge ones

		elevatorMaxSpeed = (stickAux.getAxis(AxisType.kZ) + 1) / 2;
		SmartDashboard.putNumber("elevatorMaxSpeed", elevatorMaxSpeed);

		SmartDashboard.putBoolean("!limitSwitchElevatorTop",
				!limitSwitchElevatorTop.get());
		SmartDashboard.putBoolean("!limitSwitchElevatorBottom",
				!limitSwitchElevatorBottom.get());
		SmartDashboard.putBoolean("!limitSwitchElevatorOne",
				!limitSwitchElevatorOne.get());

		if (!limitSwitchElevatorOne.get() /*
										 * || !limitSwitchElevatorTwo.get() ||
										 * !limitSwitchElevatorThree.get()
										 */) {
			elevatorSpeed = 0;
		}

		if (stickAux.getRawButton(3)) {
			elevatorSpeed = -elevatorMaxSpeed;
		}
		if (stickAux.getRawButton(2)) {
			elevatorSpeed = elevatorMaxSpeed;
		}

		if (!limitSwitchElevatorTop.get() || !limitSwitchElevatorBottom.get()) {
			elevatorSpeed = 0;
		}

		motorElevator.set(elevatorSpeed);

	}
}
