package org.usfirst.frc.team1492.robot;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;

public class TapeState {
	
	HashMap<Joystick, TapeJoystick> joysticks;

	public class TapeJoystick {
		double[] axes;
		boolean[] buttons;
		
		public TapeJoystick(){
			axes = new double[6];
			buttons = new boolean[12];
		}
	}

	public TapeJoystick getJoystick(Joystick realJoystick) {
		return joysticks.get(realJoystick);
	}

	public void change(Action action) {
		switch(action.type){
		case axis:{
			ActionAxis a = (ActionAxis)action;
			joysticks.get(a.realJoystick).axes[a.index] = a.value;
		}
			break;
		case button:{
			ActionButton a = (ActionButton)action;
			joysticks.get(a.realJoystick).buttons[a.index] = a.value;
		}
			break;
		default:
			break;
		
		}
	}

}
