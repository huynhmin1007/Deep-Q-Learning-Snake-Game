package my_application.viewhelper;

import java.util.List;

public enum Action {
	
	MOVE_STRAIGHT, MOVE_RIGHT, MOVE_LEFT;
	
	private static final List<Action> VALUES = List.of(values());
	
	public static Action getActionByIndex(int index) {
		return VALUES.get(index);
	}
	
	public static int getIndexOfAction(Action action) {
		return VALUES.indexOf(action);
	}
}
