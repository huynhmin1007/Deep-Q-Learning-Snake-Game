package my_application.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import my_application.util.GameUtil;
import my_application.util.PositionUtil;
import my_application.viewhelper.Action;
import my_application.viewhelper.Direction;
import my_application.viewhelper.Position;

public class SnakeGame extends JPanel implements ActionListener {
	
	protected transient Position[] snake = new Position[(GameUtil.SCREEN_WIDTH * GameUtil.SCREEN_HEIGHT) / GameUtil.TILE_SIZE];
	protected int snakeLength;
	protected Direction currentDirection = Direction.RIGHT;
	protected boolean running = true;
	
	protected transient Position food;
	
	protected final int DELAY = 40;
	protected Timer timer;
	
	public SnakeGame() {
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(GameUtil.SCREEN_WIDTH, GameUtil.SCREEN_HEIGHT));
		setFocusable(true);
		addKeyListener(new MyKeyAdapter());
		timer = new Timer(DELAY, this);
		timer.start();
		
		initializeGame();
	}
	
	public void setSize() {
		setPreferredSize(new Dimension(GameUtil.SCREEN_WIDTH, GameUtil.SCREEN_HEIGHT));
	}
	
	public void initializeGame() {
		snake = new Position[(GameUtil.SCREEN_WIDTH * GameUtil.SCREEN_HEIGHT) / GameUtil.TILE_SIZE];
		snakeLength = 3;
		
		int halfWidth = GameUtil.SCREEN_WIDTH / 2 ;
		int halfHeight = GameUtil.SCREEN_HEIGHT / 2 ;
		
		for(int i = 0; i < snakeLength; i++) {
			snake[i] = new Position(halfWidth - i * GameUtil.TILE_SIZE, halfHeight);
		}
		
		placeFood();
		running = true;
		currentDirection = Direction.RIGHT;
	}

	public void placeFood() {
		Random random = new Random();
		
		food = new Position(
				random.nextInt(GameUtil.SCREEN_WIDTH / GameUtil.TILE_SIZE) * GameUtil.TILE_SIZE, 
				random.nextInt(GameUtil.SCREEN_HEIGHT / GameUtil.TILE_SIZE) * GameUtil.TILE_SIZE);
		
		if(Arrays.asList(snake).contains(food)) {
			placeFood();
		}
	}
	
	public boolean isFoodEaten() {
		return food.equals(getHeadPosition());
	}

	public Position getHeadPosition() {
		return snake[0];
	}
	
	public boolean isOnGoing() {
		return running;
	}
	
	private void endGame() {
		running = false;
	}
	
	public boolean checkCollidingBodyParts(Position[] snakePosition) {
		long matches = Arrays.stream(snakePosition)
				.filter(Objects::nonNull)
				.filter(pos -> pos.equals(snakePosition[0]))
				.count();

		return matches > 1;
	}
	
	public boolean checkCollidingBodyParts(Position snakeHead) {
		long matches = Arrays.stream(snake)
				.filter(Objects::nonNull)
				.filter(pos -> pos.equals(snakeHead))
				.count();

		return matches > 0;
	}
	
	public void move() {
		// Di chuyen
		if(snakeLength - 1 >= 0) System.arraycopy(snake, 0, snake, 1, snakeLength - 1);
		
		snake[0] = PositionUtil.getNextPosition(snake[1], currentDirection);
	}
	
	public void changeDirection(Direction newDirection) {
		switch (newDirection) {
		 case UP:
             if (currentDirection == Direction.DOWN) break;
             currentDirection = newDirection;
             break;
         case RIGHT:
             if (currentDirection == Direction.LEFT) break;
             currentDirection = newDirection;
             break;
         case DOWN:
             if (currentDirection == Direction.UP) break;
             currentDirection = newDirection;
             break;
         case LEFT:
             if (currentDirection == Direction.RIGHT) break;
             currentDirection = newDirection;
             break;
		}
	}
	
	public int getScore() {
		return snakeLength - 3;
	}
	
	public void play() {
		move();
		if(isFoodEaten()) {
			snakeLength++;
			placeFood();
		}
		else {
			Position headPosition = getHeadPosition();
			running = !headPosition.isOutsideTheGameBounds();
			
			if(running && checkCollidingBodyParts(snake)) {
				endGame();
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		draw(g);
	}
	
	private void draw(Graphics g) {
		if(!running) {
			return;
		}
		
		// Score
		g.setColor(Color.RED);
		g.setFont(new Font("Ink Free", Font.BOLD, 28));
		FontMetrics metrics = getFontMetrics(g.getFont());
		String scoreDisplay = String.format("Score: %d", getScore());
		g.drawString(scoreDisplay, GameUtil.TILE_SIZE, g.getFont().getSize());
		
		// Draw food
		g.setColor(Color.RED);
		g.fillOval(food.getX(), food.getY(), GameUtil.TILE_SIZE, GameUtil.TILE_SIZE);
		
		// Draw snake
		g.setColor(Color.BLUE.brighter());
		for(int i = 1; i < snakeLength; i++) {
			Position pos = snake[i];
			if(pos != null) {				
				g.fill3DRect(pos.getX(), pos.getY(), GameUtil.TILE_SIZE, GameUtil.TILE_SIZE, true);
			}
		}
		
		g.setColor(Color.CYAN);
		g.fill3DRect(snake[0].getX(), snake[0].getY(), GameUtil.TILE_SIZE, GameUtil.TILE_SIZE, true);

		Toolkit.getDefaultToolkit().sync();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(isOnGoing()) {
			play();
		}
		else {
			initializeGame();
			timer.stop();
			timer.start();
		}
		repaint();
	}
	
	class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				changeDirection(Direction.UP);
				break;
			case KeyEvent.VK_RIGHT:
				changeDirection(Direction.RIGHT);
				break;
			case KeyEvent.VK_DOWN:
				changeDirection(Direction.DOWN);
				break;
			case KeyEvent.VK_LEFT:
				changeDirection(Direction.LEFT);
				break;
			}
		}
	}
}
