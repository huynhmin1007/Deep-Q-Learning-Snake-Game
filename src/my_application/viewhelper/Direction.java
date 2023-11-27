package my_application.viewhelper;

import java.util.List;

public enum Direction {
	
	UP, RIGHT, DOWN, LEFT;
	
	private static final List<Direction> VALUES = List.of(values());
	
	public static Direction getDirectionByIndex(int index) {
		return VALUES.get(index);
	}
	
	public static int getIndexOfDirection(Direction direction) {
		return VALUES.indexOf(direction);
	}
}
