package org.usfirst.frc.team1492.robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;

public class TapeState {
	
	HashMap<Integer, TapeJoystick> joysticks = new HashMap<Integer, TapeJoystick>();
	long tick = 0;

	public class TapeJoystick {
		double[] axes;
		boolean[] buttons;
		
		public TapeJoystick(){
			axes = new double[6];
			buttons = new boolean[12];
		}
	}

	public TapeJoystick getJoystick(int realJoystick) {
		return joysticks.get(realJoystick);
	}

	public void change(Action action) {
		switch(action.type){
			case axis:{
				ActionAxis a = (ActionAxis)action;
				joysticks.get(a.joystickIndex).axes[a.index] = a.value;
			}
				break;
			case button:{
				ActionButton a = (ActionButton)action;
				joysticks.get(a.joystickIndex).buttons[a.index] = a.value;
			}
				break;
			default:
				break;
		}
	}

}
