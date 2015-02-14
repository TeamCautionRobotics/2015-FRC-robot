package org.usfirst.frc.team1492.robot;

import edu.wpi.first.wpilibj.Joystick;

public abstract class Action {
	
	public long tick;
	public ActionType type = ActionType.none;
	
	
	public enum ActionType{
		none, button, axis;
	}
	
	public static class ActionJoystick extends Action{
		
		Joystick realJoystick;
		
	}
	
	
	

}