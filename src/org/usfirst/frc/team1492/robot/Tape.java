package org.usfirst.frc.team1492.robot;

import java.util.ArrayList;

public class Tape {
	
	public ArrayList<Action> actionList = new ArrayList<Action>();
	public int currentAction = 0;

	public TapeState currentState;

	public void addAction(Action action) {
		
		actionList.add(action);
		currentState.change(action);
		
	}

	public void play(long tapeTick) {
		
		while(actionList.get(currentAction).tick < tapeTick){
			currentState.change(actionList.get(currentAction));
			currentAction++;
		}
		
	}

}
