package org.usfirst.frc.team1492.robot;

import org.usfirst.frc.team1492.robot.Action.ActionJoystick;

import edu.wpi.first.wpilibj.Joystick;



public class ActionAxis extends ActionJoystick{
	
	int index;
	double value;
	
	public ActionAxis(long tick, int joystickIndex, int index, double value){
		this.tick = tick;
		this.type = ActionType.axis;
		this.joystickIndex = joystickIndex;
		this.index = index;
		this.value = value;
	}
}