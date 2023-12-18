package my_application.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import deep_q_learning.State;
import my_application.util.PositionUtil;
import my_application.util.StateUtil;
import my_application.viewhelper.Action;
import my_application.viewhelper.Direction;
import my_application.viewhelper.GameMode;
import my_application.viewhelper.Position;

public class Environment extends SnakeGame {
	
	private int frameIterator;
	private double reward = 0;
	private boolean isEatedFood = false;
	
	private Position last;
	
	public Environment(GameMode mode) {
		super();
		
		switch(mode) {
		case HUMAN:
			break;
		case STEP:
			setFocusable(false);
			timer.stop();
			break;
		}
	}
	
	private State buildState() {
		return new State(StateUtil.buildState(getSnake().toArray(new Position[0]), food, currentDirection));
	}
	
	public State reset() {
		reward = 0;
		frameIterator = 0;
		initializeGame();
		return buildState();
	}
	
	public State step(final int actionIndex) {
		Action action = Action.getActionByIndex(actionIndex);

		if(isEatedFood) {
			placeFood();
			isEatedFood = false;
		}
		
		this.changeDirection(action);
		this.move();
		
		reward = calcReward();

		frameIterator++;
		if(frameIterator > 80 * snakeLength) {
			running = false;
			reward = -10;
		}
		
		return buildState();
	}
	
	public void changeDirection(Action action) {
		int indexOfDirection = Direction.getIndexOfDirection(currentDirection);
		
		switch(action) {
		case MOVE_RIGHT:
			indexOfDirection = (indexOfDirection + 1) % Direction.values().length;
			currentDirection = Direction.getDirectionByIndex(indexOfDirection);
			break;
		case MOVE_LEFT:
			indexOfDirection--;
			if(indexOfDirection < 0) {
				indexOfDirection = Direction.values().length - 1;
			}
			currentDirection = Direction.getDirectionByIndex(indexOfDirection);
			break;
		}
	}
	
	public double calcReward() {
		if(snake[0].isOutsideTheGameBounds()) {
			running = false;
			return -10;
		}
		
		long matches = Arrays.stream(snake)
				.filter(Objects::nonNull)
				.filter(pos -> pos.equals(snake[0]))
				.count();
		
		if(matches > 1) {
			running = false;
			return -10;
		}
		
		if(snake[0].equals(food)) {
			snakeLength++;
			frameIterator = 0;
			isEatedFood = true;
			
			if(snake[0].equals(last)) return -10;
			
			return 5;
		}

		if(PositionUtil.isSnakeCloserToTail(snake[0], getTail(), currentDirection)) {
			return 0.1;
		}
		
		return 0;
	}
	
	public Position getTail() {
		return snake[snakeLength - 1] != null? snake[snakeLength - 1] : snake[snakeLength - 2];
	}
	
	@Override
	public void move() {
		last = snake[snakeLength - 1];
		super.move();
		if(last == null) last = snake[snakeLength - 1];	}
	
	public double getReward() {
		return reward;
	}
	
	public boolean isDone() {
		return !running;
	}
	
	public int getFrameIterator() {
		return this.frameIterator;
	}
	
	public ArrayList<Position> getSnake() {
		if(snake[snakeLength - 1] == null) {
			ArrayList<Position> arr = Arrays.stream(snake).filter(Objects::nonNull).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
			arr.add(last);
			return arr;
			
		}
		return Arrays.stream(snake).filter(Objects::nonNull).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
	
	public Position getFood() {
		return this.food;
	}
	
	public int getDirection() {
		return Direction.getIndexOfDirection(currentDirection);
	}
}
