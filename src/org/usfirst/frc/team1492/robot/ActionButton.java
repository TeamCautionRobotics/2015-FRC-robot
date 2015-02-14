package org.usfirst.frc.team1492.robot;

import org.usfirst.frc.team1492.robot.Action.ActionJoystick;

import edu.wpi.first.wpilibj.Joystick;



public class ActionButton extends ActionJoystick{
	
	int index;
	boolean value;
	
	public ActionButton(long tick, Joystick realJoystick, int index, boolean value){
		this.tick = tick;
		this.type = ActionType.button;
		this.realJoystick = realJoystick;
		this.index = index;
		this.value = value;
	}
}
