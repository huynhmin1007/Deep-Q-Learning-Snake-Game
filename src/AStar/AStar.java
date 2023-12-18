package AStar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import deep_q_learning.Trainer;
import my_application.util.GameUtil;
import my_application.util.PositionUtil;
import my_application.viewhelper.Action;
import my_application.viewhelper.Direction;
import my_application.viewhelper.Position;

public class AStar {
	
	private int frameIterator;
	
	public int findPathToFood(ArrayList<Position> snake, Position food, int directionIndex) {
		
		Queue<Node> queue = new LinkedList<Node>();
		
		Node head = new Node(snake.get(0), directionIndex);
		queue.add(head);
		
		Set<Node> visited = new HashSet<Node>();
		
		while(!queue.isEmpty()) {
			Node current = queue.poll();
			
			Node forward = new Node(PositionUtil.getNextPositionForward(current.getPos(), current.getDirection()), 
					changeDirection(current.getDirection(), Action.MOVE_STRAIGHT), Action.getIndexOfAction(Action.MOVE_STRAIGHT));
			
			Node right = new Node(PositionUtil.getNextPositionRight(current.getPos(), current.getDirection()),
					changeDirection(current.getDirection(), Action.MOVE_RIGHT), Action.getIndexOfAction(Action.MOVE_RIGHT));
			
			Node left = new Node(PositionUtil.getNextPositionLeft(current.getPos(), current.getDirection()),
					changeDirection(current.getDirection(), Action.MOVE_LEFT), Action.getIndexOfAction(Action.MOVE_LEFT));
			
			ArrayList<Node> temp = new ArrayList<Node>(Arrays.asList(forward, right, left));
			
			for(Node node : temp) {
				if(!snake.contains(node.getPos()) && !node.getPos().isOutsideTheGameBounds() && !visited.contains(node)) {
					queue.add(node);
					node.setParent(current);
					visited.add(node);
				}
				if(node.getPos().equals(food)) {
					Node parent = node;
					
					while(parent.getParent() != null && !parent.getParent().equals(head)) {
						parent = parent.getParent();
					}

					return parent.getAction();
				}
			}
		}
		return -1;
	}
	
	public int findPathToTail(ArrayList<Position> snake, Position tail, int directionIndex) {
		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closeList = new ArrayList<Node>();
		
		Node head = new Node(snake.get(0).clone(), directionIndex);

		head.setH(distance(head.getPos(), tail));
		
		openList.add(head);
		
		while(!openList.isEmpty()) {
			Node current = null;
			int max = Integer.MIN_VALUE;
			
			for(Node node : openList) {
				if(max <= node.getF()) {
					max = node.getF();
					current = node;
				}
			}
			
			openList.remove(current);
			closeList.add(current);
			
			Node forward = new Node(PositionUtil.getNextPositionForward(current.getPos(), current.getDirection()), 
					changeDirection(current.getDirection(), Action.MOVE_STRAIGHT), Action.getIndexOfAction(Action.MOVE_STRAIGHT));
			
			Node right = new Node(PositionUtil.getNextPositionRight(current.getPos(), current.getDirection()),
					changeDirection(current.getDirection(), Action.MOVE_RIGHT), Action.getIndexOfAction(Action.MOVE_RIGHT));
			
			Node left = new Node(PositionUtil.getNextPositionLeft(current.getPos(), current.getDirection()),
					changeDirection(current.getDirection(), Action.MOVE_LEFT), Action.getIndexOfAction(Action.MOVE_LEFT));
			
			ArrayList<Node> temp = new ArrayList<Node>(Arrays.asList(forward, right, left));
			
			for(Node node : temp) {
				if(closeList.contains(node) || node.getPos().isOutsideTheGameBounds()) {
					continue;
				}
				
				if(!node.getPos().equals(tail) && snake.contains(node.getPos())) {
					continue;
				}
				
				if(!openList.contains(node)) {
					node.setParent(current);
					node.setG(current.getG() + GameUtil.TILE_SIZE);
					node.setH(distance(node.getPos(), tail));
					
					openList.add(node);
					
					if(node.getPos().equals(tail)) {
						while(node.getParent() != null && !node.getParent().equals(head)) {
							node = node.getParent();
						}
						
						return node.getAction();
					}
				}
			}
		}
		return -1;
	}
	
	public int aSearch(ArrayList<Position> snake, Position food, int directionIndex) {

		int actionToFood = findPathToFood(snake, food, directionIndex);

		if(actionToFood != -1) {
			ArrayList<Position> virtualSnake = (ArrayList<Position>) snake.clone();
			
			Position last = null;
			int dir = directionIndex;
			do {
				last = virtualSnake.get(virtualSnake.size() - 1);
				int action = findPathToFood(virtualSnake, food, dir);
				if(action == -1) break;
				dir = changeDirection(dir, Action.getActionByIndex(action));
				move(virtualSnake, dir);
			}
			while(!virtualSnake.get(0).equals(food));
			
			virtualSnake.add(last);
			int actionToTail = findPathToTail(virtualSnake, virtualSnake.get(virtualSnake.size() - 1), dir);
			
			if(actionToTail != -1) {
				frameIterator = 0;
				return actionToFood;
			}

			if(++frameIterator < 100) {
				return findPathToTail(snake, snake.get(snake.size() - 1), directionIndex);
			}
			else {
				return actionToFood;
			}
		}
		else {
			Position tail = snake.get(snake.size() - 1);

			int actionToTail = findPathToTail(snake, tail, directionIndex);
			
			if(actionToTail == -1) {
				actionToTail = randomAction();
				int dir = changeDirection(directionIndex, Action.getActionByIndex(actionToTail));
				Position snakeHead = snake.get(0).clone();
				
				for(int i = 0; i <= 300; i++) {
					snakeHead = PositionUtil.getNextPosition(snakeHead, Direction.getDirectionByIndex(dir));
					
					if(snakeHead.isOutsideTheGameBounds()) break;
					
					if(!snakeHead.equals(tail) && snake.contains(snakeHead)) {
						break;
					}
					
					actionToTail = randomAction();
					dir = changeDirection(dir, Action.getActionByIndex(actionToTail));
					
					if(i == 300) return -1;
				}
				return actionToTail;
			}
			return actionToTail;
		}
	}
	
	public Position getTailTarget(ArrayList<Position> snake) {
		int length = snake.size() - 1;
		
		Position tail = snake.get(length);
		Position prevTail = snake.get(length - 1);
		
		Direction direction = Direction.UP;
		
		if(prevTail.getY() < tail.getY()) {
			direction = Direction.UP;
		}
		else if(prevTail.getX() > tail.getX()) {
			direction = Direction.RIGHT;
		}
		else if(prevTail.getY() > tail.getY()) {
			direction = Direction.DOWN;
		}
		else if(prevTail.getX() < tail.getX()) {
			direction = Direction.LEFT;
		}
		
		direction = Direction.getDirectionByIndex((Direction.getIndexOfDirection(direction) + 2) % 4);
		
		return PositionUtil.getNextPosition(tail, direction);
	}
	
	public int randomAction() {
		return new Random().nextInt(Action.values().length);
	}
	
	public int distance(Position src, Position des) {
		return Math.abs(src.getX() - des.getX()) + Math.abs(src.getY() - des.getY());
	}
	
	public void move(ArrayList<Position> snake, int directionIndex) {
		Position newHead = PositionUtil.getNextPosition(snake.get(0).clone(), Direction.getDirectionByIndex(directionIndex));
		Collections.rotate(snake, 1);
		snake.set(0, newHead);
	}
	
	public int changeDirection(int currentDirection, Action action) {
		switch(action) {
		case MOVE_RIGHT:
			currentDirection = (currentDirection + 1) % Direction.values().length;
			break;
		case MOVE_LEFT:
			currentDirection--;
			if(currentDirection < 0) {
				currentDirection = Direction.values().length - 1;
			}
			break;
		}
		return currentDirection;
	}
	
	public static void main(String[] args) throws InterruptedException {
		Trainer trainer = new Trainer();
		trainer.trainAStar(new AStar());
	}
}
