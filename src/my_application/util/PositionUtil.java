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
	
	public static double distanceForward(Position head, Position obj, int direction) {
		int size = GameUtil.TILE_SIZE;
		
		double[] distance = {
			(head.getY() - obj.getY()) / size,
			(head.getX() - obj.getX()) / size
		};
		return distance[direction % 2] != 0? Math.abs(distance[direction % 2]) : 0;
	}
	
	public static double distanceDigonal(Position head, Position obj, int direction) {
		int size = GameUtil.TILE_SIZE;
		double distance = 2 * (Math.abs(head.getY() - obj.getY()) / size);
		
		return distance != 0? distance : 0;
	}
	
	public static double distanceBeside(Position head, Position obj, int direction) {
		int size = GameUtil.TILE_SIZE;
		
		double[] distance = {
			(head.getX() - obj.getX()) / size,
			(head.getY() - obj.getY()) / size
		};
		return distance[direction % 2] != 0? Math.abs(distance[direction % 2]) : 0;
	}
	
	public static Position getNextPositionForward(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {0, -size}),
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size}),
			getNextPosition(headX, headY, new int[] {-size, 0})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionForwardRight(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionRight(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size}),
			getNextPosition(headX, headY, new int[] {-size, 0}),
			getNextPosition(headX, headY, new int[] {0, -size})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionBackRight(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionBackLeft(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, size}),
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionLeft(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, 0}),
			getNextPosition(headX, headY, new int[] {0, -size}),
			getNextPosition(headX, headY, new int[] {size, 0}),
			getNextPosition(headX, headY, new int[] {0, size})
		};
		return pos[direction];
	}
	
	public static Position getNextPositionForwardLeft(Position head, int direction) {
		int headX = head.getX();
		int headY = head.getY();
		
		int size = GameUtil.TILE_SIZE;
		
		Position[] pos = {
			getNextPosition(headX, headY, new int[] {-size, -size}),
			getNextPosition(headX, headY, new int[] {size, -size}),
			getNextPosition(headX, headY, new int[] {size, size}),
			getNextPosition(headX, headY, new int[] {-size, size})
		};
		return pos[direction];
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
	
	public static boolean isSnakeCloserToTail(Position snakePosition, Position tailPosition, Direction direction) {
		
		switch(direction) {
		case UP:
			// Đang đi lại gần thức ăn (đi từ dưới lên)
			return snakePosition.getY() > tailPosition.getY();
		case RIGHT:
			return snakePosition.getX() < tailPosition.getX();
			
		case DOWN:
			return snakePosition.getY() < tailPosition.getY();
			
		case LEFT:
			return snakePosition.getX() > tailPosition.getX();
		}
		return false;
	}
}