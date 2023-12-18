package AStar;

import java.util.Objects;

import my_application.viewhelper.Position;

public class Node {
	
	private int G;
	private int H;
	private Node parent;
	private Position pos;
	private int action;
	private int direction;
	
	public Node(Position pos) {
		this.pos = pos;
	}
	
	public Node(Position pos, int direction, int action) {
		this.pos = pos;
		this.action = action;
		this.direction = direction;
	}
	
	public Node(Position pos, int direction) {
		this.pos = pos;
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	public int getF() {
		return G + H;
	}

	public int getG() {
		return G;
	}

	public void setG(int g) {
		G = g;
	}

	public int getH() {
		return H;
	}

	public void setH(int h) {
		H = h;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		return Objects.equals(pos, other.pos);
	}
}
