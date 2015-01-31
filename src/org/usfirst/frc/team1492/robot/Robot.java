package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PIDController;

public class Robot extends SampleRobot {
	Talon motorLeft;
	Talon motorRight;
	
	Talon motorCenter; // Motor controller for the middle of the H
	
	Talon motorLift;
	Talon motorArm;
	
	
	DoubleSolenoid pistonArmTilt;
	Solenoid pistonHand;
	Solenoid pistonLiftWidth;
	
	PIDController PIDControllerLift;
	
	AnalogInput analogLift;
	
	DigitalInput digitalInLiftTop;
	DigitalInput digitalInLiftBottom;
	
	DigitalInput digitalInArmUp;
	DigitalInput digitalInArmDown;
	
	
	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickAux;
	
	
	double motorLiftSpeed;
	double armLiftSpeed;
	
	int liftPos = 0;
	int liftPosMax = 4;
	int liftPosMin = 0;
	
	int armSpeed = 0;
	
	double[] liftPosPresets = {0, .25, .5, .75, 1};
	
	
	public Robot() {

		motorLeft = new Talon(1);
		motorRight = new Talon(3);
		
		motorCenter = new Talon(0);
		

		motorLift = new Talon(4);
		motorArm = new Talon(5);
		
		pistonArmTilt = new DoubleSolenoid(0, 1);
		pistonHand = new Solenoid(2);
		pistonLiftWidth = new Solenoid(3);
		
		analogLift = new AnalogInput(0);
		
		digitalInLiftTop = new DigitalInput(0);
		digitalInLiftBottom = new DigitalInput(1);
		
		digitalInArmUp = new DigitalInput(2);
		digitalInArmDown = new DigitalInput(3);
		
		
		PIDControllerLift = new PIDController(0, 0, 0, analogLift, motorLift);
		PIDControllerLift.setInputRange(0, 1);
		PIDControllerLift.setOutputRange(0, .5);
		PIDControllerLift.disable();
		
		
		
		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);

	}

	public void autonomous() {

	}

	public void operatorControl() {
		// CameraThread c = new CameraThread();
		
		PIDControllerLift.enable();

		while (isOperatorControl() && isEnabled()) {

			driveControl();
			manipulatorControl();

			Timer.delay(0.005);
		}
		
		PIDControllerLift.disable();

		// c.finish();
	}

	public void test() {

	}

	public void driveControl() {
		double leftSide = -stickLeft.getAxis(AxisType.kY);
		double rightSide = stickRight.getAxis(AxisType.kY);
		double horizontal = stickLeft.getAxis(AxisType.kX);
		horizontal = (horizontal > 0.2 || horizontal < -0.2) ? (horizontal-0.2) : 0;
		
		motorLeft.set(leftSide);

		motorRight.set(rightSide);

		motorCenter.set(horizontal);

	}

	public void manipulatorControl() {
		
		
		//Calibration:
		motorLiftSpeed = stickAux.getAxis(AxisType.kZ);
		SmartDashboard.putNumber("motorLiftSpeed (auxStick)", motorLiftSpeed);
		
		armLiftSpeed = stickRight.getAxis(AxisType.kZ);
		SmartDashboard.putNumber("armLiftSpeed (rightStick)", armLiftSpeed);
		//
		
		
		//Lift Up/Down
		if(stickAux.getRawButton(3)){//up
			liftPos ++;
		}
		if(stickAux.getRawButton(2)){//down
			liftPos --;
		}
		
		
		if(liftPos > liftPosMax){
			liftPos = liftPosMax;
		}
		if(liftPos < liftPosMin){
			liftPos = liftPosMin;
		}
		
		PIDControllerLift.setSetpoint(liftPosPresets[liftPos]);
		
		if((digitalInLiftTop.get() && liftPos==liftPosMax) || (digitalInLiftBottom.get() && liftPos==liftPosMin)){
			motorLift.set(0);
		}
		
		//
		
		
		//Arm Up/Down
		
		motorArm.set(stickAux.getAxis(AxisType.kY));
		
		if((digitalInArmUp.get() && armSpeed>0) || (digitalInArmDown.get() && armSpeed<0)){
			motorArm.set(0);
		}
		
		//
		
		
		//Lift Width in/out
		
		if(stickAux.getRawButton(4)){//left out
			pistonLiftWidth.set(true);
		}
		
		if(stickAux.getRawButton(5)){//right in
			pistonLiftWidth.set(false);
		}
		
		//
		
		//arm tilt
		
		pistonArmTilt.set(Value.kOff);
		
		if(stickAux.getRawButton(6)){//tilt forward
			pistonArmTilt.set(Value.kForward);
		}
		
		if(stickAux.getRawButton(7)){//tilt backward
			pistonArmTilt.set(Value.kReverse);
		}
		
		//
		
		//Hand
		
		pistonHand.set(stickAux.getRawButton(1));
		
		
		//
		
		
		

	}
}
