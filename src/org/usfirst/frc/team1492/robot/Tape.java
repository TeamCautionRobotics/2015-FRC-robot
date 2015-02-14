package org.usfirst.frc.team1492.robot;

import java.util.ArrayList;

public class Tape {
	
	public ArrayList<Action> actionList = new ArrayList<Action>();

	public TapeState currentState;

	public void addAction(Action action) {
		
		actionList.add(action);
		currentState.change(action);
		
	}

}
