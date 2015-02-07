package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
	Talon motorLeft;
	Talon motorRight;

	Talon motorCenter; // Motor controller for the middle of the H

	Talon motorLift;
	Talon motorArm;

	DoubleSolenoid pistonArmTilt1;
	DoubleSolenoid pistonArmTilt2;
	DoubleSolenoid pistonLiftWidth;

	//PIDController PIDControllerLift;

	AnalogInput analogLift;

	DigitalInput digitalInLiftTop;
	DigitalInput digitalInLiftBottom;

	DigitalInput digitalInArmUp;
	DigitalInput digitalInArmDown;

	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickAux;
	boolean[] stickAuxLastButton;
	
	double SETTING_hDriveDampening;
	
	double SETTING_motorLiftSpeed;
	double SETTING_armLiftSpeed;

	int liftPos = 0;
	int liftPosMax = 4;
	int liftPosMin = 0;

	int armSpeed = 0;

	double[] liftPosPresets = { 0, .25, .5, .75, 1 };
	
	double hCurrent;

	public Robot() {
		motorLeft = new Talon(1);
		motorRight = new Talon(2);

		motorCenter = new Talon(0);

		motorLift = new Talon(4);
		motorArm = new Talon(3);

		pistonArmTilt1 = new DoubleSolenoid(0, 1);
		pistonArmTilt2 = new DoubleSolenoid(2, 3);
		pistonLiftWidth = new DoubleSolenoid(4, 5);

		analogLift = new AnalogInput(0);

		digitalInLiftTop = new DigitalInput(0);
		digitalInLiftBottom = new DigitalInput(1);

		digitalInArmUp = new DigitalInput(2);
		digitalInArmDown = new DigitalInput(3);

		/*
		PIDControllerLift = new PIDController(0, 0, 0, analogLift, motorLift);
		PIDControllerLift.setInputRange(0, 1);
		PIDControllerLift.setOutputRange(0, .5);
		PIDControllerLift.disable();
		*/

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);
		
		stickAuxLastButton = new boolean[stickAux.getButtonCount()];

	}

	public void autonomous() {

	}

	public void operatorControl() {
		// CameraThread c = new CameraThread();

		//PIDControllerLift.enable();

		while (isOperatorControl() && isEnabled()) {

			driveControl();
			manipulatorControl();

			Timer.delay(0.005);
		}

		//PIDControllerLift.disable();

		// c.finish();
	}

	public void test() {

	}

	public void driveControl() {
		
		SETTING_hDriveDampening = SmartDashboard.getNumber("hDriveDampening", 5);
		
		
		double leftSide = -stickLeft.getAxis(AxisType.kY);
		double rightSide = stickRight.getAxis(AxisType.kY);
		double hTarget = farthestFrom0(stickLeft.getAxis(AxisType.kX), stickRight.getAxis(AxisType.kX));
		hTarget = deadbandScale(hTarget, .2);
		hTarget /= 2;
		
		motorLeft.set(leftSide);

		motorRight.set(rightSide);

		motorCenter.set(hCurrent);

	}

	public void manipulatorControl() {

		// Calibration:
		SETTING_motorLiftSpeed = stickAux.getAxis(AxisType.kZ);
		SmartDashboard.putNumber("motorLiftSpeed (auxStick)", SETTING_motorLiftSpeed);

		SETTING_armLiftSpeed = stickRight.getAxis(AxisType.kZ);
		SmartDashboard.putNumber("armLiftSpeed (rightStick)", SETTING_armLiftSpeed);
		//
		
		//All the digital inputs:
		SmartDashboard.putBoolean("digitalInLiftTop ("+digitalInLiftTop.getChannel()+")", digitalInLiftTop.get());
		SmartDashboard.putBoolean("digitalInLiftBottom ("+digitalInLiftTop.getChannel()+")", digitalInLiftBottom.get());
		SmartDashboard.putBoolean("digitalInArmUp ("+digitalInArmUp.getChannel()+")", digitalInArmUp.get());
		SmartDashboard.putBoolean("digitalInArmDown ("+digitalInArmDown.getChannel()+")", digitalInArmDown.get());
		//
		
		//Lift Up/Down
		/* DISABLED PID
		if(stickAux.getRawButton(3) && !stickAuxLastButton[3]){//up
			liftPos ++;
		}
		if(stickAux.getRawButton(2) && !stickAuxLastButton[2]){//down
			liftPos --;
		}

		if (liftPos > liftPosMax) {
			liftPos = liftPosMax;
		}
		if (liftPos < liftPosMin) {
			liftPos = liftPosMin;
		}

		PIDControllerLift.setSetpoint(liftPosPresets[liftPos]);

		if ((digitalInLiftTop.get() && liftPos == liftPosMax)
				|| (digitalInLiftBottom.get() && liftPos == liftPosMin)) {
			motorLift.set(0);
		}
		*/
		
		//Instead of PID
		motorLift.set(0);
		if(stickAux.getRawButton(3) && !digitalInLiftTop.get()){
			motorLift.set(-SETTING_motorLiftSpeed);
		}
		if(stickAux.getRawButton(2) && !digitalInLiftBottom.get()){
			motorLift.set(SETTING_motorLiftSpeed);
		}
		//

		// Arm Up/Down

		motorArm.set(stickAux.getAxis(AxisType.kY) * SETTING_armLiftSpeed);

		if ((digitalInArmUp.get() && armSpeed > 0)
				|| (digitalInArmDown.get() && armSpeed < 0)) {
			motorArm.set(0);
		}

		// Lift Width in/out

		if (stickAux.getRawButton(4)) {	// left out
			pistonLiftWidth.set(Value.kForward);
		}

		if (stickAux.getRawButton(5)) {	// right in
			pistonLiftWidth.set(Value.kReverse);
		}

		// arm tilt

		pistonArmTilt1.set(Value.kOff);
		pistonArmTilt2.set(Value.kOff);

		if (stickAux.getRawButton(6)) {	// tilt forward
			pistonArmTilt1.set(Value.kForward);
			pistonArmTilt2.set(Value.kForward);
		}

		if (stickAux.getRawButton(7)) {	// tilt backward
			pistonArmTilt1.set(Value.kReverse);
			pistonArmTilt2.set(Value.kReverse);
		}
		
		//
		
		
		for(int i=1;i<stickAuxLastButton.length;i++){
			stickAuxLastButton[i] = stickAux.getRawButton(7);
		}
	}
	
	
	double deadbandScale(double input, double threshold){
		return input > threshold ? (input - threshold)/(1-threshold) : input < -threshold ? (input + threshold)/(1-threshold) : 0;
	}
	
	double farthestFrom0(double a, double b){
		if(Math.abs(a) > Math.abs(b)){
			return a;
		}else{
			return b;
		}
	}
	
	
	
}
