package my_application.util;

import java.util.ArrayList;
import java.util.Arrays;
import my_application.viewhelper.Direction;
import my_application.viewhelper.Position;

public class StateUtil {
	
	public static int[] index;
	
	public static double[] buildState(Position[] snake, Position food, Direction direction) {
		int directionIndex = Direction.getIndexOfDirection(direction);
		index = new int[7];
		
		double[] checkFood = checkFood(snake[0], food, directionIndex);
		double[] checkBodyParts = checkBodyParts(snake, directionIndex);
		double[] checkTail = checkTail(snake, directionIndex);
		double[] checkNext = checkNextMove(snake, directionIndex);
		double[] checkWall = checkWall(snake[0], directionIndex);

		for(int i = 0; i < checkFood.length; i++) {
			boolean canMoveForward = checkBodyParts[i] >= snake.length - index[i];
			
			if(checkFood[i] != 0 && checkFood[i] != -1) {
				if(index[i] != 0) {
					if(canMoveForward) {
						checkFood[i] = 1;
					}
					else if(checkFood[i] < checkBodyParts[i] && checkBodyParts[i] != -1) {
						checkFood[i] = 1;
					}
					else checkFood[i] = 0;
				}
				else checkFood[i] = 1;
			}
			
			if(index[i] != 0) {
				if(!canMoveForward) {
					checkWall[i] = 0;
					checkBodyParts[i] = 1;
				}
				checkBodyParts[i] = 1 / checkBodyParts[i];
			}
		}
		
		double[] state = new double[checkFood.length + checkBodyParts.length + checkTail.length + checkNext.length + checkWall.length];

		System.arraycopy(checkFood, 0, state, 0, checkFood.length);
		System.arraycopy(checkBodyParts, 0, state, checkFood.length, checkBodyParts.length);
		System.arraycopy(checkTail, 0, state,  checkFood.length + checkBodyParts.length, checkTail.length);
		System.arraycopy(checkNext, 0, state, checkFood.length + checkBodyParts.length + checkTail.length, checkNext.length);
		System.arraycopy(checkWall, 0, state, checkFood.length + checkBodyParts.length + checkTail.length + checkNext.length, checkWall.length);
		
		return state;
	}
	
	public static double[] checkTail(Position[] snake, int direction) {
		Position tail = snake[snake.length - 1];
		Position head = snake[0];
		
		double[][] checkTail = new double[4][4];
		
		checkTail[0] = new double[] {
				tail.getY() < head.getY()? 1 : 0,
				tail.getX() > head.getX()? 1 : 0,
				tail.getY() > head.getY()? 1 : 0,
				tail.getX() < head.getX()? 1 : 0,
		};
		
		checkTail[1] = new double[] {
				tail.getX() > head.getX()? 1 : 0,
				tail.getY() > head.getY()? 1 : 0,
				tail.getX() < head.getX()? 1 : 0,
				tail.getY() < head.getY()? 1 : 0,
		};
		
		checkTail[2] = new double[] {
				tail.getY() > head.getY()? 1 : 0,
				tail.getX() < head.getX()? 1 : 0,
				tail.getY() < head.getY()? 1 : 0,
				tail.getX() > head.getX()? 1 : 0,
		};
		
		checkTail[3] = new double[] {
				tail.getX() < head.getX()? 1 : 0,
				tail.getY() < head.getY()? 1 : 0,
				tail.getX() > head.getX()? 1 : 0,
				tail.getY() > head.getY()? 1 : 0,
		};
		
		return checkTail[direction];
	}
	
	public static double[] checkNextMove(Position[] snake, int direction) {
		double[] checkNext = new double[3];
		ArrayList<Position> arr = new ArrayList<Position>(Arrays.asList(snake));
		
		Position nextHeadForward = PositionUtil.getNextPositionForward(snake[0], direction);
		if(nextHeadForward.isOutsideTheGameBounds() || (arr.contains(nextHeadForward) && !nextHeadForward.equals(snake[snake.length - 1]))) {
			checkNext[0] = 1;
		}
		
		Position nextHeadRight = PositionUtil.getNextPositionRight(snake[0], direction);
		if(nextHeadRight.isOutsideTheGameBounds() || (arr.contains(nextHeadRight) && !nextHeadRight.equals(snake[snake.length - 1]))) {
			checkNext[1] = 1;
		}
		
		Position nextHeadLeft = PositionUtil.getNextPositionLeft(snake[0], direction);
		if(nextHeadLeft.isOutsideTheGameBounds() || (arr.contains(nextHeadLeft) && !nextHeadLeft.equals(snake[snake.length - 1]))) {
			checkNext[2] = 1;
		}
		return checkNext;
	}
	
	public static double[] checkFood(Position snakeHead, Position food, int direction) {
		int headX = snakeHead.getX();
		int headY = snakeHead.getY();
		
		int foodX = food.getX();
		int foodY = food.getY();
		
		double[] distanceToFood = new double[7];
		
		if(isFoundForward(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceForward(snakeHead, food, direction);
			distanceToFood[0] = distance;
			if(distance == 0) distanceToFood[0] = -1;
		}
		else if(isFoundForwardRight(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceDigonal(snakeHead, food, direction);
			distanceToFood[1] = distance;
		}
		else if(isFoundRight(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceBeside(snakeHead, food, direction);
			distanceToFood[2] = distance;
		}
		else if(isFoundBackRight(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceDigonal(snakeHead, food, direction);
			distanceToFood[3] = distance;
		}
		else if(isFoundBackLeft(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceDigonal(snakeHead, food, direction);
			distanceToFood[4] = distance;
		}
		else if(isFoundLeft(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceBeside(snakeHead, food, direction);
			distanceToFood[5] = distance;
		}
		else if(isFoundForwardLeft(headX, headY, foodX, foodY, direction)) {
			double distance = PositionUtil.distanceDigonal(snakeHead, food, direction);
			distanceToFood[6] = distance;
		}
		
		return distanceToFood;
	}
	
	public static double[] checkWall(Position snakeHead, int direction) {
		double[] isCollisionWall = new double[7];
		Arrays.fill(isCollisionWall, 1.0);
		
		if(snakeHead.isOutsideTheGameBounds()) {
			isCollisionWall[0] = -1;
		}

		return isCollisionWall;
	}
	
	public static double[] checkBodyParts(Position[] snake, int direction) {
		int headX = snake[0].getX();
		int headY = snake[0].getY();
		
		double[] distanceToParts = new double[7];
		Arrays.fill(distanceToParts, Integer.MAX_VALUE);
		
		for(int i = 1; i < snake.length; i++) {
			
			Position part = snake[i];
			
			int partX = part.getX();
			int partY = part.getY();
			
			if(isFoundForward(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceForward(snake[0], part, direction);
				if(distance < distanceToParts[0]) {
					distanceToParts[0] = distance;
					index[0] = i;
					if(distanceToParts[0] == 0) distanceToParts[0] = -1;
				}
			}
			else if(isFoundForwardRight(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceDigonal(snake[0], part, direction);
				if(distance < distanceToParts[1]) {
					distanceToParts[1] = distance;
					index[1] = i;
				}
			}
			else if(isFoundRight(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceBeside(snake[0], part, direction);
				if(distance < distanceToParts[2]) {
					distanceToParts[2] = distance;
					index[2] = i;
				}
			}
			else if(isFoundBackRight(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceDigonal(snake[0], part, direction);
				if(distance < distanceToParts[3]) {
					distanceToParts[3] = distance;
					index[3] = i;
				}
			}
			else if(isFoundBackLeft(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceDigonal(snake[0], part, direction);
				if(distance < distanceToParts[4]) {
					distanceToParts[4] = distance;
					index[4] = i;
				}
			}
			else if(isFoundLeft(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceBeside(snake[0], part, direction);
				if(distance < distanceToParts[5]) {
					distanceToParts[5] = distance;
					index[5] = i;
				}
			}
			else if(isFoundForwardLeft(headX, headY, partX, partY, direction)) {
				double distance = PositionUtil.distanceDigonal(snake[0], part, direction);
				if(distance < distanceToParts[6]) {
					distanceToParts[6] = distance;
					index[6] = i;
				}
			}
		}
		return Arrays.stream(distanceToParts).map(v -> v == Integer.MAX_VALUE? 0 : v).toArray();
	}
	
	public static boolean isOnDiagonal(int headX, int headY, int objX, int objY) {
		int size = GameUtil.TILE_SIZE;
		return Math.abs((objX - headX)/size) == Math.abs((objY - headY)/size);
	}
	
	public static boolean isFoundForward(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX == headX && objY <= headY,
				objX >= headX && objY == headY,
				objX == headX && objY >= headY,
				objX <= headX && objY == headY
		};
		return isFound[direction];
	}
	
	public static boolean isFoundForwardRight(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX > headX && objY < headY,
				objX > headX && objY > headY,
				objX < headX && objY > headY,
				objX < headX && objY < headY
		};
		return isFound[direction] && isOnDiagonal(headX, headY, objX, objY);
	}
	
	public static boolean isFoundRight(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX > headX && objY == headY,
				objX == headX && objY > headY,
				objX < headX && objY == headY,
				objX == headX && objY < headY
		};
		return isFound[direction];
	}
	
	public static boolean isFoundBackRight(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX > headX && objY > headY,
				objX < headX && objY > headY,
				objX < headX && objY < headY ,
				objX > headX && objY < headY
		};
		return isFound[direction] && isOnDiagonal(headX, headY, objX, objY);
	}
	
	public static boolean isFoundBackLeft(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX < headX && objY > headY,
				objX < headX && objY < headY,
				objX > headX && objY < headY,
				objX > headX && objY > headY
		};
		return isFound[direction] && isOnDiagonal(headX, headY, objX, objY);
	}
	
	public static boolean isFoundLeft(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX < headX && objY == headY,
				objX == headX && objY < headY,
				objX > headX && objY == headY,
				objX == headX && objY > headY
		};
		return isFound[direction];
	}
	
	public static boolean isFoundForwardLeft(int headX, int headY, int objX, int objY, int direction) {
		boolean[] isFound = {
				objX < headX && objY < headY,
				objX > headX && objY < headY,
				objX > headX && objY > headY,
				objX < headX && objY > headY
		};
		return isFound[direction] && isOnDiagonal(headX, headY, objX, objY);
	}
}