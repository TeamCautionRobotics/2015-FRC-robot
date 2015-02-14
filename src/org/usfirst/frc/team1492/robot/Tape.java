package org.usfirst.frc.team1492.robot;

import java.util.ArrayList;

import org.usfirst.frc.team1492.robot.Action.ActionType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	public void saveTape(String name){
		saveTape(this, name);
	}
	
	public static void saveTape(Tape tape, String name) {
		String fileString = "";
		for(int i = 0; i < tape.actionList.size(); i++){
			Action action = tape.actionList.get(i);
			String line = "";
			
			line += action.tick;
			line += "," + action.type.ordinal();
			
			switch(action.type){
				case axis:{
					ActionAxis a = (ActionAxis)action;
					line += "," + a.joystickIndex;
					line += "," + a.index;
					line += "," + a.value;
				}
					break;
				case button:{
					ActionButton a = (ActionButton)action;
					line += "," + a.joystickIndex;
					line += "," + a.index;
					line += "," + a.value;
				}
					break;
				default:
					break;
			}
			
			line += "\n";
			fileString += line;
		}
		
		//TODO Try to save file
		SmartDashboard.putString("tape", fileString);
		
	}

	public static Tape loadTape(String name) {
		Tape tape = new Tape();
		
		String fileString = "";
		fileString = SmartDashboard.getString("tape"); // Load from dashboard
		//TODO try to load from file
		
		String[] lines = fileString.split("\n");
		for(int i=0;i<lines.length;i++){
			String[] parts = lines[i].split(",");
			long tick = Long.parseLong(parts[0]);
			int type = Integer.parseInt(parts[1]);
			Action action = null;
			
			switch(ActionType.values()[type]){
				case axis:{
					int joystickIndex = Integer.parseInt(parts[2]);
					int index = Integer.parseInt(parts[3]);
					double value = Double.parseDouble(parts[4]);
					action = new ActionAxis(tick, joystickIndex, index, value);
				}
					break;
				case button:{
					int joystickIndex = Integer.parseInt(parts[2]);
					int index = Integer.parseInt(parts[3]);
					boolean value = Boolean.parseBoolean(parts[4]);
					action = new ActionButton(tick, joystickIndex, index, value);
				}
					break;
				default:
					break;
			}
			
			tape.addAction(action);
			
		}
		
		
		return tape;
	}

}
