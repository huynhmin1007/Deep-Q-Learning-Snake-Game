package my_application.viewhelper;

import java.util.Objects;

import my_application.util.GameUtil;

public class Position {
	
	private int x, y;

	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y;
	}
	
	public boolean isOutsideTheGameBounds() {
		return x >= GameUtil.SCREEN_WIDTH || y >= GameUtil.SCREEN_HEIGHT || x < 0 || y < 0;
	}
	
	public Position clone() {
		return new Position(x, y);
	}
}
