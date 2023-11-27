package my_application.util;

import my_application.viewhelper.Direction;
import my_application.viewhelper.Position;

public class StateUtil {
	
	public static double[] buildState(Position[] snake, Position food, Direction direction) {
		int directionIndex = Direction.getIndexOfDirection(direction);
		
		double[] checkDirection = checkDirection(directionIndex);
		double[] checkFood = checkFood(snake[0], food, directionIndex);
		double[] checkDanger = checkDanger(snake, directionIndex);
		
		double[] state = new double[checkDirection.length + checkFood.length + checkDanger.length];
		
		System.arraycopy(checkDirection, 0, state, 0, checkDirection.length);
		System.arraycopy(checkFood, 0, state, checkDirection.length, checkFood.length);
		System.arraycopy(checkDanger, 0, state, checkFood.length, checkDanger.length);
		
		return state;
	}
	
	public static double[] checkDirection(int directionIndex) {
		double[] directions = new double[4];
		directions[directionIndex] = 1;
		
		return directions;
	}
	
	public static double[] checkFood(Position snakeHead, Position food, int directionIndex) {
		int headX = snakeHead.getX();
		int headY = snakeHead.getY();
		
		int foodX = food.getX();
		int foodY = food.getY();
		
		double[] isFoundFood = new double[7];
		
		if(isFoundForward(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[0] = 1;
		}
		else if(isFoundForwardRight(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[1] = 1;
		}
		else if(isFoundRight(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[2] = 1;
		}
		else if(isFoundBackRight(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[3] = 1;
		}
		else if(isFoundBackLeft(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[4] = 1;
		}
		else if(isFoundLeft(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[5] = 1;
		}
		else if(isFoundForwardLeft(headX, headY, foodX, foodY, directionIndex)) {
			isFoundFood[6] = 1;
		}
		
		return isFoundFood;
	}
	
	public static double[] checkDanger(Position[] snake, int directionIndex) {
		double[] checkDangerParts = checkBodyParts(snake, directionIndex);
		double[] checkWall = checkWall(snake[0], directionIndex);
		
		double[] danger = new double[checkDangerParts.length];
		
		for(int i = 0; i < danger.length; i++) {
			danger[i] = Math.max(checkDangerParts[i], checkWall[i]);
		}
		return danger;
	}
	
	public static double[] checkWall(Position snakeHead, int directionIndex) {
		double[] isCollisionWall = new double[7];
		
		if(PositionUtil.getNextPositionForward(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[0] = 1;
		}
		if(PositionUtil.getNextPositionForwardRight(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[1] = 1;
		}
		if(PositionUtil.getNextPositionRight(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[2] = 1;
		}
		if(PositionUtil.getNextPositionBackRight(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[3] = 1;
		}
		if(PositionUtil.getNextPositionBackLeft(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[4] = 1;
		}
		if(PositionUtil.getNextPositionLeft(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[5] = 1;
		}
		if(PositionUtil.getNextPositionForwardLeft(snakeHead, directionIndex).isOutsideTheGameBounds()) {
			isCollisionWall[6] = 1;
		}
		
		return isCollisionWall;
	}
	
	public static double[] checkBodyParts(Position[] snake, int directionIndex) {
		int headX = snake[0].getX();
		int headY = snake[0].getY();
		
		double[] isFoundParts = new double[7];
		int count = 0;
		
		int size = GameUtil.TILE_SIZE;
		
		for(int i = 1; i < snake.length; i++) {
			
			if(count >= 7) break;
			
			Position part = snake[i];
			
			if(part == null) continue;
			
			int partX = part.getX();
			int partY = part.getY();
			
			if(isFoundForward(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[0] = 1;
			}
			else if(isFoundForwardRight(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[1] = 1;
			}
			else if(isFoundRight(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[2] = 1;
			}
			else if(isFoundBackRight(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[3] = 1;
			}
			else if(isFoundBackLeft(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[4] = 1;
			}
			else if(isFoundLeft(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[5] = 1;
			}
			else if(isFoundForwardLeft(headX, headY, partX, partY, directionIndex)) {
				count++;
				isFoundParts[6] = 1;
			}
		}
		return isFoundParts;
	}
	
	public static boolean isOnDiagonal(int headX, int headY, int objX, int objY) {
		int size = GameUtil.TILE_SIZE;
		return Math.abs((objX - headX)/size) == Math.abs((objY - headY)/size);
	}
	
	public static boolean isFoundForward(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX == headX && objY < headY,
				objX > headX && objY == headY,
				objX == headX && objY > headY,
				objX < headX && objY == headY
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundForwardRight(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX > headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY < headY && isOnDiagonal(headX, headY, objX, objY)
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundRight(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX > headX && objY == headY,
				objX == headX && objY > headY,
				objX < headX && objY == headY,
				objX == headX && objY < headY
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundBackRight(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX > headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY < headY && isOnDiagonal(headX, headY, objX, objY)
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundBackLeft(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX < headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY > headY && isOnDiagonal(headX, headY, objX, objY)
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundLeft(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX < headX && objY == headY,
				objX == headX && objY < headY,
				objX > headX && objY == headY,
				objX == headX && objY > headY
		};
		return isFound[directionIndex];
	}
	
	public static boolean isFoundForwardLeft(int headX, int headY, int objX, int objY, int directionIndex) {
		boolean[] isFound = {
				objX < headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY < headY && isOnDiagonal(headX, headY, objX, objY),
				objX > headX && objY > headY && isOnDiagonal(headX, headY, objX, objY),
				objX < headX && objY > headY && isOnDiagonal(headX, headY, objX, objY)
		};
		return isFound[directionIndex];
	}
}
