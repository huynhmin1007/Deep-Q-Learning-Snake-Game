package my_application.util;

import my_application.viewhelper.Direction;
import my_application.viewhelper.Position;

public class PositionUtil {

	public static Position getNextPosition(Position head, Direction direction) {
		if(direction == Direction.UP) {
			return new Position(head.getX(), head.getY() - GameUtil.TILE_SIZE);
		}
		
		if(direction == Direction.RIGHT) {
			return new Position(head.getX() + GameUtil.TILE_SIZE, head.getY());
		}
		
		if(direction == Direction.DOWN) {
			return new Position(head.getX(), head.getY() + GameUtil.TILE_SIZE);
		}
		
		if(direction == Direction.LEFT) {
			return new Position(head.getX() - GameUtil.TILE_SIZE, head.getY());
		}
		return null;
	}
	
	public static Position getNextPositionForward(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {0, -size}),
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size}),
			getNextPosition(headX, headY, new int[] {-size, 0})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionForwardRight(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionRight(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size}),
			getNextPosition(headX, headY, new int[] {-size, 0}),
			getNextPosition(headX, headY, new int[] {0, -size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionBackRight(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionBackLeft(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionLeft(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, 0}),
			getNextPosition(headX, headY, new int[] {0, -size}),
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPositionForwardLeft(Position head, int directionIndex) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size})
		};
		return pos[directionIndex];
	}
	
	public static Position getNextPosition(int headX, int headY, int[] vector) {
		return new Position(headX + vector[0], headY + vector[1]);
	}
	
	public static Position[] getNextSnakePosition(Position[] snake, Direction direction) {
		if(snake.length - 1 >= 0) System.arraycopy(snake, 0, snake, 1, snake.length - 1);
		
		snake[0] = PositionUtil.getNextPosition(snake[1], direction);
		
		return snake;
	}
	
	public static boolean isSnakeCloserToFood(Position snakePosition, Position foodPosition, Direction direction) {
		
		switch(direction) {
		case UP:
			// Đang đi lại gần thức ăn (đi từ dưới lên)
			return snakePosition.getY() > foodPosition.getY() && snakePosition.getX() == foodPosition.getX();
		case RIGHT:
			return snakePosition.getX() < foodPosition.getX() && snakePosition.getY() == foodPosition.getY();
			
		case DOWN:
			return snakePosition.getY() < foodPosition.getY() && snakePosition.getX() == foodPosition.getX();
			
		case LEFT:
			return snakePosition.getX() > foodPosition.getX() && snakePosition.getY() == foodPosition.getY();
		}
		return false;
	}
}
