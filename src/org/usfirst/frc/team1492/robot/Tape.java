package org.usfirst.frc.team1492.robot;

import java.util.ArrayList;

public class Tape {
	
	public ArrayList<Action> actionList = new ArrayList<Action>();
	public int currentActionIndex = 0;

	public TapeState currentState;

	public void addAction(Action action) {
		if(action != null){
			actionList.add(action);
			currentState.change(action);
		}
		
	}

	public boolean play(long tapeTick) {
		
		if(currentActionIndex < actionList.size()){
			while(actionList.get(currentActionIndex).tick < tapeTick){
				currentState.change(actionList.get(currentActionIndex));
				currentActionIndex++;
			}
			return true;
		}else{
			return false;
		}
		
	}

}
