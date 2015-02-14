package org.usfirst.frc.team1492.robot;

import java.util.HashMap;

import org.usfirst.frc.team1492.robot.Action.ActionType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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

	// PIDController PIDControllerLift;

	AnalogInput analogLift;

	DigitalInput digitalInLiftTop;
	DigitalInput digitalInLiftBottom;

	DigitalInput digitalInArmUp;
	DigitalInput digitalInArmDown;

	Joystick stickLeft;
	Joystick stickRight;
	Joystick stickAux;
	//boolean[] stickAuxLastButton;

	double SETTING_hDriveDampening;

	double SETTING_motorLiftSpeed;
	double SETTING_armLiftSpeed;

	int liftPos = 0;
	int liftPosMax = 4;
	int liftPosMin = 0;

	double[] liftPosPresets = { 0, .25, .5, .75, 1 };
	
	HashMap<Integer, String> autoModes = new HashMap<Integer, String>();

	int autoModeNone = 0;
	int autoModeMoveForward = 1;
	int autoMode2 = 2;
	int autoMode3 = 3;
	
	int autoMode = autoModeNone;

	Command autoCommand;
	SendableChooser autoChooser;
	
	private boolean playingTape;
	private boolean recordingTape;
	private Tape currentTape = new Tape();
	long tapeTick;

	public Robot() {
		motorLeft = new Talon(1);
		motorRight = new Talon(2);

		motorCenter = new Talon(0);

		motorLift = new Talon(4);
		motorArm = new Talon(3);

		pistonArmTilt1 = new DoubleSolenoid(1, 0);
		pistonArmTilt2 = new DoubleSolenoid(2, 3);
		pistonLiftWidth = new DoubleSolenoid(4, 5);

		analogLift = new AnalogInput(0);

		digitalInLiftTop = new DigitalInput(0);
		digitalInLiftBottom = new DigitalInput(1);

		digitalInArmUp = new DigitalInput(2);
		digitalInArmDown = new DigitalInput(3);

		/*
		 * PIDControllerLift = new PIDController(0, 0, 0, analogLift,
		 * motorLift); PIDControllerLift.setInputRange(0, 1);
		 * PIDControllerLift.setOutputRange(0, .5); PIDControllerLift.disable();
		 */

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);

		//stickAuxLastButton = new boolean[stickAux.getButtonCount()];
		

		autoModes.put(autoModeNone, "None");
		autoModes.put(autoModeMoveForward, "Forward");
		autoModes.put(autoMode2, "Auto 2");
		autoModes.put(autoMode3, "Auto 3");

		autoChooser = new SendableChooser();
		autoChooser.addDefault("No Auto", 0);
		for(int i = 1; i < autoModes.size(); i++){
			autoChooser.addObject(autoModes.get(i), i);
		}
		SmartDashboard.putData("Auto Mode", autoChooser);
		
	}

	@Override
	public void autonomous() {

		autoMode = (Integer) autoChooser.getSelected();

		SmartDashboard.putString("Start Auto", autoModes.get(autoMode));
		SmartDashboard.putString("End Auto", "");
		
		if(autoMode == autoModeMoveForward){
			motorLeft.set(0.5);
			motorRight.set(0.5);
			Timer.delay(0.5);
			motorLeft.set(0);
			motorRight.set(0);
		}

		SmartDashboard.putString("End Auto", autoModes.get(autoMode));
		SmartDashboard.putString("Start Auto", "");
		autoMode = autoModeNone;

	}

	@Override
	public void operatorControl() {
		CameraThread camThread = new CameraThread();
		camThread.start();
		SmartDashboard.putBoolean("Camera thread working", true);

		// PIDControllerLift.enable();

		while (isOperatorControl() && isEnabled()) {

			// SmartDashboard.putBoolean("CameraThread Running",
			// camThread.running);
			
			
			if(!recordingTape && SmartDashboard.getBoolean("Record", false)){
				tapeTick = 0;
				currentTape.currentAction = 0;
			}
			recordingTape = SmartDashboard.getBoolean("Record", false);
			
			if(!playingTape && SmartDashboard.getBoolean("Play", false)){
				tapeTick = 0;
				currentTape.currentAction = 0;
			}
			playingTape = SmartDashboard.getBoolean("Play", false);
			
			
			if(playingTape){
				currentTape.play(tapeTick);
			}
			
			driveControl();
			manipulatorControl();
			tapeTick++;

			Timer.delay(0.005);
		}

		// PIDControllerLift.disable();
		camThread.finish();
	}

	@Override
	public void test() {
		
	}
	
	public double getAxis(Joystick stick, AxisType axis){
		if(playingTape){
			return currentTape.currentState.getJoystick(stick).axes[axis.value];
		} else if(recordingTape){
			if(currentTape.currentState.getJoystick(stick).axes[axis.value] != stick.getAxis(axis)){
				currentTape.addAction(new ActionAxis(tapeTick, stick, axis.value, stick.getAxis(axis)));
			}
		}
		return stick.getAxis(axis);
	}
	
	public boolean getButton(Joystick stick, int index){
		if(playingTape){
			return currentTape.currentState.getJoystick(stick).buttons[index];
		} else if(recordingTape){
			if(currentTape.currentState.getJoystick(stick).buttons[index] != stick.getRawButton(index)){
				currentTape.addAction(new ActionButton(tapeTick, stick, index, stick.getRawButton(index)));
			}
		}
		return stick.getRawButton(index);
	}

	public void driveControl() {

		SETTING_hDriveDampening = SmartDashboard
				.getNumber("hDriveDampening", 5);

		double leftSide = -getAxis(stickLeft, AxisType.kY);
		double rightSide = getAxis(stickRight, AxisType.kY);
		double h = farthestFrom0(getAxis(stickLeft, AxisType.kX), getAxis(stickRight, AxisType.kX));
		h = deadbandScale(h, .2);

		// h /= 2;

		motorLeft.set(leftSide);

		motorRight.set(rightSide);

		motorCenter.set(h);

	}

	public void manipulatorControl() {

		// Calibration:
		SETTING_motorLiftSpeed = ((-getAxis(stickAux, AxisType.kZ)) / 2) + .5;
		SmartDashboard.putNumber("motorLiftSpeed (auxStick)",
				SETTING_motorLiftSpeed);

		SETTING_armLiftSpeed = ((-getAxis(stickRight, AxisType.kZ)) / 2) + .5;
		SmartDashboard.putNumber("armLiftSpeed (rightStick)",
				SETTING_armLiftSpeed);
		//

		SmartDashboard.putNumber("AnalogLift PID (" + analogLift.getChannel()
				+ ")", analogLift.pidGet());

		// All the digital inputs:
		SmartDashboard.putBoolean(
				"digitalInLiftTop (" + digitalInLiftTop.getChannel() + ")",
				digitalInLiftTop.get());
		SmartDashboard.putBoolean(
				"digitalInLiftBottom (" + digitalInLiftBottom.getChannel()
						+ ")", digitalInLiftBottom.get());
		SmartDashboard.putBoolean(
				"digitalInArmUp (" + digitalInArmUp.getChannel() + ")",
				digitalInArmUp.get());
		SmartDashboard.putBoolean(
				"digitalInArmDown (" + digitalInArmDown.getChannel() + ")",
				digitalInArmDown.get());
		//

		// Lift Up/Down
		/*
		 * DISABLED PID if(stickAux.getRawButton(3) &&
		 * !stickAuxLastButton[3]){//up liftPos ++; }
		 * if(stickAux.getRawButton(2) && !stickAuxLastButton[2]){//down liftPos
		 * --; }
		 * 
		 * if (liftPos > liftPosMax) { liftPos = liftPosMax; } if (liftPos <
		 * liftPosMin) { liftPos = liftPosMin; }
		 * 
		 * PIDControllerLift.setSetpoint(liftPosPresets[liftPos]);
		 * 
		 * if ((digitalInLiftTop.get() && liftPos == liftPosMax) ||
		 * (digitalInLiftBottom.get() && liftPos == liftPosMin)) {
		 * motorLift.set(0); }
		 */

		// Instead of PID
		double liftSpeed = 0;
		if (getButton(stickAux, 3) && digitalInLiftTop.get()) {
			liftSpeed = SETTING_motorLiftSpeed;
		}
		if (getButton(stickAux, 2) && digitalInLiftBottom.get()) {
			liftSpeed = -SETTING_motorLiftSpeed;
		}
		motorLift.set(liftSpeed);
		//

		// Arm Up/Down

		double armSpeed = getAxis(stickAux, AxisType.kY) * SETTING_armLiftSpeed;
		/*
		 * if ((digitalInArmUp.get() && armSpeed > 0) || (digitalInArmDown.get()
		 * && armSpeed < 0)) { armSpeed = 0; }
		 */
		motorArm.set(armSpeed);

		// Lift Width in/out

		if (getButton(stickAux, 4)) { // left out
			pistonLiftWidth.set(Value.kForward);
		}

		if (getButton(stickAux, 5)) { // right in
			pistonLiftWidth.set(Value.kReverse);
		}

		// arm tilt

		// pistonArmTilt1.set(Value.kOff);
		// pistonArmTilt2.set(Value.kOff);

		if (getButton(stickAux, 6)) { // tilt forward
			pistonArmTilt1.set(Value.kForward);
			pistonArmTilt2.set(Value.kForward);
		}

		if (getButton(stickAux, 7)) { // tilt backward
			pistonArmTilt1.set(Value.kReverse);
			pistonArmTilt2.set(Value.kReverse);
		}

		//

		/*for (int i = 1; i < stickAuxLastButton.length; i++) {
			stickAuxLastButton[i] = stickAux.getRawButton(7);
		}*/
	}

	double deadbandScale(double input, double threshold) {
		return input > threshold ? (input - threshold) / (1 - threshold)
				: input < -threshold ? (input + threshold) / (1 - threshold)
						: 0;
	}

	double farthestFrom0(double a, double b) {
		return (Math.abs(a) > Math.abs(b)) ? a : b;
	}

}
