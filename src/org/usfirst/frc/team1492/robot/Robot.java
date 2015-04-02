package org.usfirst.frc.team1492.robot;

//import java.awt.dnd.Autoscroll;
import java.util.HashMap;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
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
	Solenoid pistonCenterSuspension;

	// PIDController PIDControllerLift;

	AnalogInput analogLift;

	DigitalInput digitalInLiftTop;
	DigitalInput digitalInLiftBottom;

	DigitalInput digitalInArmUp;
	DigitalInput digitalInArmDown;

	PowerDistributionPanel pdp;
	
	AnalogInput frontDistanceSensor;

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

	double[] liftPosPresets = { 0, .25, .5, .75, 1 };
	
	HashMap<Integer, String> autoModes = new HashMap<Integer, String>();
	
	final int autoModeNone = 0;
	final int autoModeToZone = 1;
	final int autoModeToZoneOver = 2;
	final int autoModeGrabToZone = 3;
	final int autoModeGrabToZoneOver = 4;
	/*final int autoModeDriveToAutoZone = 1;
	final int autoModeDriveToAutoZoneOverPlatform = 2;
	final int autoModeGrabCanAndDriveToAutoZone = 3;
	final int autoModeGrabCanAndDriveToAutoZoneOverScoringPlatform = 4;
	final int autoModeDriveIntoAutoZoneFromLandfill = 5;
	final int autoModeGrabCanOffStep = 6;
	final int autoModeGrabToteOrCanMoveBack = 7;
	final int autoModeGrabToteOrCanMoveBackOverScoringPlatform = 8;*/
	
	int autoMode = autoModeNone;

	Command autoCommand;
	SendableChooser autoChooser;

	public Robot() {
		motorLeft = new Talon(1);
		motorRight = new Talon(2);

		motorCenter = new Talon(0);

		motorLift = new Talon(4);
		motorArm = new Talon(3);

		pistonArmTilt1 = new DoubleSolenoid(1, 0);
		pistonArmTilt2 = new DoubleSolenoid(2, 3);
		pistonLiftWidth = new DoubleSolenoid(4, 5);
		pistonCenterSuspension = new Solenoid(6);

		analogLift = new AnalogInput(0);

		digitalInLiftTop = new DigitalInput(0);
		digitalInLiftBottom = new DigitalInput(1);

		digitalInArmUp = new DigitalInput(3);
		digitalInArmDown = new DigitalInput(2);
		//schuyler 480 526 1606

		pdp = new PowerDistributionPanel();
		
		frontDistanceSensor = new AnalogInput(1);
		
		//sonicVex = new Ultrasonic(5, 6);
		//sonicVex.setAutomaticMode(true);

		/*
		 * PIDControllerLift = new PIDController(0, 0, 0, analogLift,
		 * motorLift); PIDControllerLift.setInputRange(0, 1);
		 * PIDControllerLift.setOutputRange(0, .5); PIDControllerLift.disable();
		 */

		stickLeft = new Joystick(0);
		stickRight = new Joystick(1);
		stickAux = new Joystick(2);

		stickAuxLastButton = new boolean[stickAux.getButtonCount()];
		

		autoModes.put(autoModeNone, "None");
		autoModes.put(autoModeToZone, "Move to Zone");
		autoModes.put(autoModeToZoneOver, "Move over platform to Zone");
		autoModes.put(autoModeGrabToZone, "Grab and move to zone");
		autoModes.put(autoModeGrabToZoneOver, "Grab and move over platform to zone");
		/*autoModes.put(autoModeDriveToAutoZone, "(Tested) Drive to Auto Zone");
		autoModes.put(autoModeDriveToAutoZoneOverPlatform, "(Untested) Drive to Auto Zone Over Platform");
		autoModes.put(autoModeGrabCanAndDriveToAutoZone, "(Tested) Grab Can and drive to Auto Zone");
		autoModes.put(autoModeGrabCanAndDriveToAutoZoneOverScoringPlatform, "(Untested) Grab Can to and drive Auto Zone over Scoring Platform");
		autoModes.put(autoModeDriveIntoAutoZoneFromLandfill, "(Untested) Drive into Auto Zone from Landfill");
		autoModes.put(autoModeGrabCanOffStep, "(Untested) Grab Can off step");
		autoModes.put(autoModeGrabToteOrCanMoveBack, "(Untested) Grab Tote or can and move to auto zone");
		autoModes.put(autoModeGrabToteOrCanMoveBackOverScoringPlatform, "(Untested) Grab Tote or can and move to auto zone (driving over scoring platform)");
		*/
		autoChooser = new SendableChooser();
		autoChooser.addDefault("No Auto", 0);
		int i = 0;
		int counter = 0;
		while(counter < autoModes.size()){
			if(autoModes.containsKey(i)){
				autoChooser.addObject(autoModes.get(i), i);
				counter++;
			}
			i++;
		}
		SmartDashboard.putData("Auto Mode", autoChooser);
	}

	@Override
	public void autonomous() {

		autoMode = (Integer) autoChooser.getSelected();

		SmartDashboard.putString("Start Auto", autoModes.get(autoMode));
		SmartDashboard.putString("End Auto", "");
		
		double rotate90DegreeTime = .75; // Time it takes to rotate 90 degrees

		
		//pull up 5th wheel
		pistonCenterSuspension.set(false);
		
		
		switch(autoMode){
		
			case autoModeToZoneOver:
			case autoModeToZone:{
				
				//move forward
				setDriveMotors(.5, .5);
				Timer.delay(autoMode == autoModeGrabToZoneOver? .8 : .5); // Time it takes to be enclosed by the Auto Zone
				setDriveMotors(0, 0);

				Timer.delay(.5);
				
				//rotate right
				setDriveMotors(1, -1);
				Timer.delay(rotate90DegreeTime);
				setDriveMotors(0, 0);
				
				break;
			}
		
			case autoModeGrabToZoneOver:
			case autoModeGrabToZone: {
				//Start with claws open and almost touching can
				
				//close claws
				pistonLiftWidth.set(Value.kForward);
				Timer.delay(1);
				
				//lift up
				motorLift.set(.5);
				Timer.delay(1); // Time it takes to lift can
				motorLift.set(0);

				Timer.delay(.5);
				
				//move backward
				setDriveMotors(-.5, -.5);
				Timer.delay(autoMode == autoModeGrabToZoneOver? .8 : .5); // Time it takes to be enclosed by the Auto Zone
				setDriveMotors(0, 0);
				
				Timer.delay(.5);
				
				//rotate right
				setDriveMotors(1, -1);
				Timer.delay(rotate90DegreeTime);
				setDriveMotors(0, 0);
				break;
			}
		}

		SmartDashboard.putString("End Auto", autoModes.get(autoMode));
		SmartDashboard.putString("Start Auto", "");
		autoMode = autoModeNone;

	}

	@Override
	public void operatorControl() {
		/*CameraThread camThread = new CameraThread();
		camThread.start();
		SmartDashboard.putBoolean("Camera thread working", true);*/

		// PIDControllerLift.enable();

		while (isOperatorControl() && isEnabled()) {

			// SmartDashboard.putBoolean("CameraThread Running",
			// camThread.running);

			driveControl();
			manipulatorControl();

			SmartDashboard.putNumber("Total current draw", pdp.getTotalCurrent());
			SmartDashboard.putNumber("Voltage", pdp.getVoltage());

			Timer.delay(0.005);
		}

		// PIDControllerLift.disable();
		//camThread.finish();
	}

	@Override
	public void test() {

	}

	public void driveControl() {

		SETTING_hDriveDampening = SmartDashboard
				.getNumber("hDriveDampening", 5);

		double leftSide = -stickLeft.getAxis(AxisType.kY);
		double rightSide = -stickRight.getAxis(AxisType.kY);
		double h = farthestFrom0(stickLeft.getAxis(AxisType.kX),
				stickRight.getAxis(AxisType.kX));
		h = deadbandScale(h, .2);

		// h /= 2;
		if (stickRight.getRawButton(8)) {
			setDriveMotors(rightSide, rightSide, 0);
		} else {
			setDriveMotors(leftSide, rightSide, h);
		}

		if(stickLeft.getRawButton(1) || stickRight.getRawButton(1)) {
			pistonCenterSuspension.set(false);
		} else {
			pistonCenterSuspension.set(true);
		}
		
		double distanceValue = frontDistanceSensor.getValue();
		SmartDashboard.putNumber("Front Sensor Distance", distanceValue);
		
		//double rangeTotalInches = sonicVex.getRangeInches();
		//int rangePartFeet = (int)(rangeTotalInches / 12);
		//int rangePartInches = ((int)rangeTotalInches) % 12;
		//SmartDashboard.putString("Vex Range:", rangePartFeet+"' "+rangePartInches+"\"");
		
		//SmartDashboard.putNumber("Vex Range Feet", rangePartFeet);
		//SmartDashboard.putNumber("Vex Range Inches", rangePartInches);

		
	}

	public void manipulatorControl() {

		// Calibration:
		SETTING_motorLiftSpeed = ((-stickAux.getAxis(AxisType.kZ)) / 2) + .5;
		SmartDashboard.putNumber("motorLiftSpeed (auxStick)",
				SETTING_motorLiftSpeed);

		SETTING_armLiftSpeed = ((-stickRight.getAxis(AxisType.kZ)) / 2) + .5;
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
		if (stickAux.getRawButton(3) && digitalInLiftTop.get()) {
			liftSpeed = SETTING_motorLiftSpeed;
		}
		if (stickAux.getRawButton(2) && digitalInLiftBottom.get()) {
			liftSpeed = -SETTING_motorLiftSpeed;
		}
		motorLift.set(liftSpeed);
		//

		// Arm Up/Down

		double armSpeed = stickAux.getAxis(AxisType.kY) * SETTING_armLiftSpeed;
		
		 if ((!digitalInArmUp.get() && armSpeed < 0) || (!digitalInArmDown.get()
		 && armSpeed > 0)) { armSpeed = 0; }
		
		motorArm.set(armSpeed);

		// Lift Width in/out

		if (stickAux.getRawButton(4)) { // left out
			pistonLiftWidth.set(Value.kForward);
		}

		if (stickAux.getRawButton(5)) { // right in
			pistonLiftWidth.set(Value.kReverse);
		}

		// arm tilt

		// pistonArmTilt1.set(Value.kOff);
		// pistonArmTilt2.set(Value.kOff);

		if (stickAux.getRawButton(6)) { // tilt forward
			pistonArmTilt1.set(Value.kForward);
			pistonArmTilt2.set(Value.kForward);
		}

		if (stickAux.getRawButton(7)) { // tilt backward
			pistonArmTilt1.set(Value.kReverse);
			pistonArmTilt2.set(Value.kReverse);
		}

		//

		for (int i = 1; i < stickAuxLastButton.length; i++) {
			stickAuxLastButton[i] = stickAux.getRawButton(7);
		}
	}

	double deadbandScale(double input, double threshold) {
		return input > threshold ? (input - threshold) / (1 - threshold)
				: input < -threshold ? (input + threshold) / (1 - threshold)
						: 0;
	}

	double farthestFrom0(double a, double b) {
		return (Math.abs(a) > Math.abs(b)) ? a : b;
	}
	
	void setDriveMotors(double left, double right, double middle) {
		motorLeft.set(left);
		motorRight.set(-right);
		motorCenter.set(middle);
	}
	
	void setDriveMotors(double left, double right) {
		setDriveMotors(left, right, 0);
	}

}
