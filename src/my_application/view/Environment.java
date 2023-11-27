package my_application.view;

import java.util.Arrays;
import java.util.Objects;

import deep_q_learning.State;
import my_application.util.PositionUtil;
import my_application.util.StateUtil;
import my_application.viewhelper.Action;
import my_application.viewhelper.Direction;
import my_application.viewhelper.GameMode;

public class Environment extends SnakeGame {
	
	private int frameIterator;
	private double reward = 0;
	
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
		return new State(StateUtil.buildState(snake, food, currentDirection));
	}
	
	public State reset() {
		reward = 0;
		frameIterator = 0;
		initializeGame();
		return buildState();
	}
	
	public State step(final int actionIndex) {
		Action action = Action.getActionByIndex(actionIndex);

		this.changeDirection(action);
		this.move();
		
		frameIterator++;
		if(frameIterator > 200) {
			running = false;
			reward = -10;
		}
		
		State state = buildState();
		
		reward = calcReward();

		return state;
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
			placeFood();
			frameIterator = 0;
			return 10;
		}
		
		if(PositionUtil.isSnakeCloserToFood(snake[0], food, currentDirection)) {
			return 0.2;
		}
		
		return -0.1;
	}
	
	public double getReward() {
		return reward;
	}
	
	public boolean isDone() {
		return !running;
	}
}
